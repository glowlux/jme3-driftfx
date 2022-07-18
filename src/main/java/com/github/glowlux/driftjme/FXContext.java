/*
This project incorporates work covered by the following copyright and permission notice:

       Copyright (c) 2009-2021 jMonkeyEngine
       All rights reserved.

       Redistribution and use in source and binary forms, with or without
       modification, are permitted provided that the following conditions are
       met:

       * Redistributions of source code must retain the above copyright
         notice, this list of conditions and the following disclaimer.

       * Redistributions in binary form must reproduce the above copyright
         notice, this list of conditions and the following disclaimer in the
         documentation and/or other materials provided with the distribution.

       * Neither the name of 'jMonkeyEngine' nor the names of its contributors
         may be used to endorse or promote products derived from this software
         without specific prior written permission.

       THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
       "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
       TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
       PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
       CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
       EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
       PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
       PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
       LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
       NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
       SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 */

package com.github.glowlux.driftjme;

import com.github.glowlux.driftjme.input.FXKeyInput;
import com.github.glowlux.driftjme.input.FXMouseInput;
import com.jme3.input.JoyInput;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.TouchInput;
import com.jme3.lwjgl3.utils.APIUtil;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeSystem;
import com.jme3.system.NanoTimer;
import com.jme3.system.lwjgl.LwjglContext;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.eclipse.fx.drift.*;
import org.lwjgl.Version;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.Callback;

import java.nio.ByteBuffer;
import java.text.MessageFormat;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;

/**
 * A context that renders to a {@link DriftFXSurface}, thus embedding JME in JavaFX.
 *
 * @author gloxlux
 */
public class FXContext extends LwjglContext implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(FXContext.class.getName());

    private int width = 1024;
    private int height = 768;

    private final DriftFXSurface surface;
    private final Stage stage;

    private Swapchain swapChain;
    private Renderer fxRenderer;
    private TransferType txType;
    private long ctx;
    private Callback debugProc;
    private Thread mainThread;

    private final AtomicBoolean isCreated = new AtomicBoolean(false);
    private final AtomicBoolean shouldClose = new AtomicBoolean(false);

    private final FXMouseInput mouseInput;
    private final FXKeyInput keyInput;

    private final boolean disableDebugOutput;

    public FXContext(DriftFXSurface surface, Stage stage, boolean disableDebugOutput, boolean forceMainMemoryTransfer) {
        this.surface = surface;
        this.stage = stage;
        this.disableDebugOutput = disableDebugOutput;

        txType = StandardTransferTypes.MainMemory;
        if (!forceMainMemoryTransfer && StandardTransferTypes.IOSurface.isAvailable()) txType = StandardTransferTypes.IOSurface;
        else if (!forceMainMemoryTransfer && StandardTransferTypes.NVDXInterop.isAvailable()) txType = StandardTransferTypes.NVDXInterop;

        LOGGER.info(MessageFormat.format("Using DriftFX '{}' transfer type.", txType.toString()));
        mouseInput = new FXMouseInput(this, stage, surface);
        mouseInput.setCurrentHeight(height);
        keyInput = new FXKeyInput(this, stage, surface);
    }

    @Override
    protected void printContextInitInfo() {
        LOGGER.log(Level.INFO, "LWJGL {0} context running on thread {1}\n * Graphics Adapter: DriftFX {2}",
                APIUtil.toArray(Version.getVersion(), Thread.currentThread().getName(), "")); // TODO: Version for driftfx??
    }

    @Override
    public Type getType() {
        return Type.OffscreenSurface;
    }

    @Override
    public MouseInput getMouseInput() { //TODO
        return mouseInput;
    }

    @Override
    public KeyInput getKeyInput() { //TODO
        return keyInput;
    }

    @Override
    public JoyInput getJoyInput() { // TODO
        return null;
    }

    @Override
    public TouchInput getTouchInput() { // TODO
        return null;
    }

    @Override
    public void setTitle(String title) {} // nothing, set the title in the jfx application.

    @Override
    public void setAutoFlushFrames(boolean enabled) {
        throw new UnsupportedOperationException("Restart not configurable on FXContext.");
    }

    @Override
    public void create(boolean waitFor) {
        if (isCreated.get()) LOGGER.warning("create() called when display is already created!");
        mainThread = Thread.currentThread();
        Platform.runLater(() -> stage.setOnCloseRequest(event -> destroy(false)));
        run();
    }

    @Override
    public void restart() {
        throw new UnsupportedOperationException("Restart not supported on FXContext.");
    }

    @Override
    public void destroy(boolean waitFor) {
        shouldClose.set(true);
        // waitFor is ignored!
    }

    private void createContext(AppSettings settings) {
        ctx = org.eclipse.fx.drift.internal.GL.createSharedCompatContext(0);
        org.eclipse.fx.drift.internal.GL.makeContextCurrent(ctx);
        System.err.println("Context is " + ctx);

        fxRenderer = GLRenderer.getRenderer(surface);

        // TODO: Set framerate limit from settings.

    }

    private void destroyContext() {
        swapChain.dispose();
        swapChain = null;
    }

    /**
     * Run main loop.
     */
    private void runLoop() {
        Vec2i size = fxRenderer.getSize();

        if (swapChain == null || size.x != width || size.y != height) {
            System.err.println("(re)create swapchain");
            if (swapChain != null) {
                swapChain.dispose();
            }

            swapChain = fxRenderer.createSwapchain(new SwapchainConfig(size, 2, PresentationMode.MAILBOX, txType));

            width = size.x;
            height = size.y;
            listener.reshape(width, height);
            mouseInput.setCurrentHeight(height);
        }

        try {
            RenderTarget target = swapChain.acquire();

            int tex = GLRenderer.getGLTextureId(target);
            int depthTex = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, depthTex);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32F, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer)null);
            glBindTexture(GL_TEXTURE_2D, 0);

            int fb = glGenFramebuffers();


            glBindFramebuffer(GL_FRAMEBUFFER, fb);
            glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, tex, 0);
            glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, depthTex, 0);

            int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
            switch (status) {
                case GL_FRAMEBUFFER_COMPLETE: break;
                case GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT: System.err.println("INCOMPLETE_ATTACHMENT!"); break;
            }

            glViewport(0, 0, width, height);

            // Subclasses just call GLObjectManager clean up objects here
            // it is safe .. for now.
            if (renderer != null) {
                renderer.postFrame();
            }

            listener.update();

//            Sync.sync(60) // you could sync to 60 fps here, but the Sync class is often runtime only.

            glBindFramebuffer(GL_FRAMEBUFFER, 0);
            glDeleteFramebuffers(fb);
            glDeleteTextures(depthTex);

            swapChain.present(target);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Do LWJGL/DriftFX initialisation
     * @return returns true if initialisation was successful.
     */
    private boolean initInThread() {
        try {
            if (!JmeSystem.isLowPermissions()) {
                // Enable uncaught exception handler only for current thread
                Thread.currentThread().setUncaughtExceptionHandler((thread, thrown) -> {
                    listener.handleError("Uncaught exception thrown in " + thread.toString(), thrown);
                    if (shouldClose.get()) {
                        // listener.handleError() has requested the
                        // context to close. Satisfy request.
                        deinitInThread();
                    }
                });
            }

            timer = new NanoTimer();

            createContext(settings);
            printContextInitInfo();
            super.internalCreate();

            if (!disableDebugOutput) debugProc = GLUtil.setupDebugMessageCallback();
            isCreated.set(true);

        } catch (Exception e) {
            listener.handleError("Failed to create display", e);
            return false;
        }

        listener.initialize();
        return true;
    }

    /**
     * Clean up and close LWJGL.
     */
    private void deinitInThread() {
        listener.destroy();

        destroyContext();
        super.internalDestroy();

        LOGGER.fine("Display destroyed.");
    }


    @Override
    public void run() {
        if (listener == null) {
            throw new IllegalStateException("SystemListener is not set on context!"
                    + "Must set with JmeContext.setSystemListener().");
        }

        LOGGER.log(Level.FINE, "Using LWJGL {0}", Version.getVersion());

        if (!initInThread()) {
            LOGGER.log(Level.SEVERE, "Display initialization failed. Cannot continue.");
            return;
        }

        do {
            runLoop();
        } while (!shouldClose.get());

        if (debugProc != null) {
            debugProc.free();
        }

        deinitInThread();
    }
}
