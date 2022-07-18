package com.github.glowlux.driftjme.input;

import com.jme3.input.KeyInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.event.KeyInputEvent;
import com.github.glowlux.driftjme.FXContext;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.eclipse.fx.drift.DriftFXSurface;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * KeyInput that translates JavaFX input events to JME.
 * @author glowlux
 */
public class FXKeyInput implements KeyInput {

    private final FXContext fxContext;
    private final Stage stage;
    private final DriftFXSurface surface;

    private final Queue<KeyInputEvent> keyInputEvents = new ConcurrentLinkedQueue<>();

    private RawInputListener listener;
    private boolean initialised = false;

    public FXKeyInput(FXContext fxContext, Stage stage, DriftFXSurface surface) {
        this.fxContext = fxContext;
        this.stage = stage;
        this.surface = surface;
    }

    @Override
    public void initialize() {
        stage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            handleKeyInput(event, true);
        });

        stage.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            handleKeyInput(event, false);
        });

        initialised = true;
    }

    private void handleKeyInput(KeyEvent event, boolean pressed) {
        Integer[] jmeKeyCodes = FXKeyMap.toJmeKeyCodes(event.getCode());
        String text = event.getText();
        char keyChar = text.isEmpty() ? '\0' : text.charAt(0);

        for (int code : jmeKeyCodes) {
            KeyInputEvent keyInputEvent = new KeyInputEvent(code, keyChar, pressed, false);
            keyInputEvent.setTime(getInputTimeNanos());
            keyInputEvents.add(keyInputEvent);
        }
    }
    @Override
    public void update() {
        while (!keyInputEvents.isEmpty()) {
            listener.onKeyEvent(keyInputEvents.poll());
        }
    }

    @Override
    public void destroy() {

    }

    @Override
    public void setInputListener(RawInputListener listener) {
        this.listener = listener;
    }

    @Override
    public String getKeyName(int jmeKey) { // TODO
        return FXKeyMap.fromJmeKeyCode(jmeKey).getName();
    }

    @Override
    public boolean isInitialized() {
        return initialised;
    }

    @Override
    public long getInputTimeNanos() {
        return System.nanoTime();
    }
}
