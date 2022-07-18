/*
 * This file incorporates work covered by the following copyright and
 * permission notice:
     * Copyright (c) 2009-2021 jMonkeyEngine
     * All rights reserved.
     *
     * Redistribution and use in source and binary forms, with or without
     * modification, are permitted provided that the following conditions are
     * met:
     *
     * * Redistributions of source code must retain the above copyright
     *   notice, this list of conditions and the following disclaimer.
     *
     * * Redistributions in binary form must reproduce the above copyright
     *   notice, this list of conditions and the following disclaimer in the
     *   documentation and/or other materials provided with the distribution.
     *
     * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
     *   may be used to endorse or promote products derived from this software
     *   without specific prior written permission.
     *
     * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
     * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
     * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
     * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
     * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
     * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
     * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
     * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
     * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
     * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
     * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.github.glowlux.driftjme.util;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.audio.Listener;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.*;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

/**
 * Simple camera that requires right click to move camera, requires keyboard and mouse.
 *
 * @author glowlux
 */
public class RightClickCameraAppState extends AbstractAppState implements AnalogListener, ActionListener {

    protected InputManager inputManager;
    protected float moveSpeed = 5f;
    protected float rotationSpeed = 1f;
    protected float scrollSpeed = 0.8f;
    protected boolean canRotate = false;

    protected Vector3f initialUpVec;
    protected Camera camera;
    protected Listener listener;

    // final objects used for camera movement calculations, defined here so the object can be reused across multiple method calls. Treated as new.
    private final Vector3f cameraPositionCalc = new Vector3f();
    private final Vector3f velocityCalc = new Vector3f();
    private final Matrix3f rotationMatrixCalc = new Matrix3f();
    private final Quaternion cameraRotationCalc = new Quaternion();


    private static final class EditorCameraInputs {
        public final static String EDITOR_CAM_FORWARD = "EditorCamForward";
        public final static String EDITOR_CAM_BACKWARD = "EditorCamBackward";
        public final static String EDITOR_CAM_LEFT = "EditorCamLeft";
        public final static String EDITOR_CAM_RIGHT = "EditorCamRight";
        public final static String EDITOR_CAM_RISE = "EditorCamRise";
        public final static String EDITOR_CAM_LOWER = "EditorCamLower";
        public final static String EDITOR_CAM_ROTATE_TOGGLE = "EditorCamRotateToggle";
        public final static String EDITOR_CAM_ROTATE_LEFT = "EditorCamRotateLeft";
        public final static String EDITOR_CAM_ROTATE_RIGHT = "EditorCamRotateRight";
        public final static String EDITOR_CAM_ROTATE_UP = "EditorCamRotateUp";
        public final static String EDITOR_CAM_ROTATE_DOWN = "EditorCamRotateDown";
        public final static String EDITOR_CAM_SCROLL_FORWARD = "EditorCamScrollForward";
        public final static String EDITOR_CAM_SCROLL_BACKWARD = "EditorCamScrollBackward";
    }

    final private static String[] mappings = {
            EditorCameraInputs.EDITOR_CAM_FORWARD,
            EditorCameraInputs.EDITOR_CAM_BACKWARD,
            EditorCameraInputs.EDITOR_CAM_LEFT,
            EditorCameraInputs.EDITOR_CAM_RIGHT,
            EditorCameraInputs.EDITOR_CAM_RISE,
            EditorCameraInputs.EDITOR_CAM_LOWER,
            EditorCameraInputs.EDITOR_CAM_ROTATE_TOGGLE,
            EditorCameraInputs.EDITOR_CAM_ROTATE_LEFT,
            EditorCameraInputs.EDITOR_CAM_ROTATE_RIGHT,
            EditorCameraInputs.EDITOR_CAM_ROTATE_UP,
            EditorCameraInputs.EDITOR_CAM_ROTATE_DOWN,
            EditorCameraInputs.EDITOR_CAM_SCROLL_FORWARD,
            EditorCameraInputs.EDITOR_CAM_SCROLL_BACKWARD,

    };

    protected void rotateCamera(float value, Vector3f axis){
        if (!canRotate) return;
        rotationMatrixCalc.fromAngleNormalAxis(rotationSpeed * value, axis);

        Vector3f up = camera.getUp();
        Vector3f left = camera.getLeft();
        Vector3f dir = camera.getDirection();

        rotationMatrixCalc.mult(up, up);
        rotationMatrixCalc.mult(left, left);
        rotationMatrixCalc.mult(dir, dir);

        cameraRotationCalc.fromAxes(left, up, dir);
        cameraRotationCalc.normalizeLocal();

        camera.setAxes(cameraRotationCalc);
    }

    protected void moveCamera(float value, boolean sideways){
        velocityCalc.zero();
        cameraPositionCalc.set(camera.getLocation());

        if (sideways){
            camera.getLeft(velocityCalc);
        } else{
            camera.getDirection(velocityCalc);
        }
        velocityCalc.multLocal(value * moveSpeed);
        cameraPositionCalc.addLocal(velocityCalc);
        camera.setLocation(cameraPositionCalc);
    }

    protected void riseCamera(float value){
        velocityCalc.set(initialUpVec);
        velocityCalc.multLocal(value * moveSpeed);
        cameraPositionCalc.set(camera.getLocation());

        cameraPositionCalc.addLocal(velocityCalc);
        camera.setLocation(cameraPositionCalc);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        inputManager = app.getInputManager();
        camera = app.getCamera();
        listener = app.getListener();

        initialUpVec = camera.getUp().clone();

        inputManager.addMapping(EditorCameraInputs.EDITOR_CAM_FORWARD, new KeyTrigger(KeyInput.KEY_W), new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping(EditorCameraInputs.EDITOR_CAM_BACKWARD, new KeyTrigger(KeyInput.KEY_S), new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping(EditorCameraInputs.EDITOR_CAM_LEFT, new KeyTrigger(KeyInput.KEY_A), new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping(EditorCameraInputs.EDITOR_CAM_RIGHT, new KeyTrigger(KeyInput.KEY_D), new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping(EditorCameraInputs.EDITOR_CAM_RISE, new KeyTrigger(KeyInput.KEY_E));
        inputManager.addMapping(EditorCameraInputs.EDITOR_CAM_LOWER, new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addMapping(EditorCameraInputs.EDITOR_CAM_ROTATE_TOGGLE, new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        inputManager.addMapping(EditorCameraInputs.EDITOR_CAM_ROTATE_LEFT, new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping(EditorCameraInputs.EDITOR_CAM_ROTATE_RIGHT, new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping(EditorCameraInputs.EDITOR_CAM_ROTATE_UP, new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addMapping(EditorCameraInputs.EDITOR_CAM_ROTATE_DOWN, new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        inputManager.addMapping(EditorCameraInputs.EDITOR_CAM_SCROLL_FORWARD, new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        inputManager.addMapping(EditorCameraInputs.EDITOR_CAM_SCROLL_BACKWARD, new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        inputManager.addListener(this, mappings);

    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        listener.setLocation(camera.getLocation());
        listener.setRotation(camera.getRotation());
    }

    @Override
    public void cleanup() {
        super.cleanup();
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
        switch (name) {
            case EditorCameraInputs.EDITOR_CAM_FORWARD:
                moveCamera(value, false);
                break;
            case EditorCameraInputs.EDITOR_CAM_BACKWARD:
                moveCamera(-value, false);
                break;
            case EditorCameraInputs.EDITOR_CAM_LEFT:
                moveCamera(value, true);
                break;
            case EditorCameraInputs.EDITOR_CAM_RIGHT:
                moveCamera(-value, true);
                break;
            case EditorCameraInputs.EDITOR_CAM_RISE:
                riseCamera(value);
                break;
            case EditorCameraInputs.EDITOR_CAM_LOWER:
                riseCamera(-value);
                break;
            case EditorCameraInputs.EDITOR_CAM_ROTATE_LEFT:
                rotateCamera(value, initialUpVec);
                break;
            case EditorCameraInputs.EDITOR_CAM_ROTATE_RIGHT:
                rotateCamera(-value, initialUpVec);
                break;
            case EditorCameraInputs.EDITOR_CAM_ROTATE_UP:
                rotateCamera(-value, camera.getLeft());
                break;
            case EditorCameraInputs.EDITOR_CAM_ROTATE_DOWN:
                rotateCamera(value, camera.getLeft());
                break;
            case EditorCameraInputs.EDITOR_CAM_SCROLL_FORWARD:
                moveCamera(value * scrollSpeed, false);
                break;
            case EditorCameraInputs.EDITOR_CAM_SCROLL_BACKWARD:
                moveCamera(-value * scrollSpeed, false);
                break;
        }
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (EditorCameraInputs.EDITOR_CAM_ROTATE_TOGGLE.equals(name)) {
            if (isPressed) {
                inputManager.setCursorVisible(false);
                canRotate = true;
            } else {
                inputManager.setCursorVisible(true);
                canRotate = false;
            }
        }
    }
}
