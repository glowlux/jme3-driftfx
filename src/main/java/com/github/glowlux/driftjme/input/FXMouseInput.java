package com.github.glowlux.driftjme.input;

import com.jme3.cursors.plugins.JmeCursor;
import com.jme3.input.MouseInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.github.glowlux.driftjme.FXContext;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.robot.Robot;
import javafx.stage.Stage;
import org.eclipse.fx.drift.DriftFXSurface;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Translates JavaFX mouse input to JME mouse events.
 * Uses various workarounds to effectively handle locked/invisible mouse.
 *
 * @author glowlux
 */
public class FXMouseInput implements MouseInput {

    private final FXContext fxContext;
    private final Stage stage;
    private final DriftFXSurface surface;

    private final Queue<MouseMotionEvent> mouseMotionEvents = new ConcurrentLinkedQueue<>();
    private final Queue<MouseButtonEvent> mouseButtonEvents = new ConcurrentLinkedQueue<>();

    private final AtomicBoolean cursorVisible = new AtomicBoolean(true);
    private final AtomicBoolean cursorNeedsHiding = new AtomicBoolean(false);
    private final AtomicBoolean cursorNeedsUnhiding = new AtomicBoolean(false);
    private final AtomicBoolean needsLockPosition = new AtomicBoolean(false);

    private RawInputListener listener;

    private int mouseX;
    private int mouseY;
    private int mouseWheel;
    private int currentHeight;

    private boolean initialised = false;

    private final static Map<MouseButton, Integer> jfxToJmeButtonMapping = new HashMap<>();
    private static final int WHEEL_SCALE = 10; // scroll wheel scale.

    static {
        jfxToJmeButtonMapping.put(MouseButton.PRIMARY, BUTTON_LEFT);
        jfxToJmeButtonMapping.put(MouseButton.SECONDARY, BUTTON_RIGHT);
        jfxToJmeButtonMapping.put(MouseButton.MIDDLE, BUTTON_MIDDLE);
    }

    /**
     * Construct a MouseInput for translating JavaFX mouse input.
     * @param fxContext the current fxcontext
     * @param stage the javafx stage
     * @param surface the rendering surface.
     */
    public FXMouseInput(FXContext fxContext, Stage stage, DriftFXSurface surface) {
        this.fxContext = fxContext;
        this.stage = stage;
        this.surface = surface;
        Platform.runLater(() -> {
            Robot robot = new Robot();

            // To emulate GLFW_CURSOR_DISABLED (that is enabling infinite dragging of the mouse) we use the Robot to lock the mouse somewhere onto the surface and hide it.
            // This works reasonably well... but isn't fantastic. Unfortunately JavaFX doesn't give us too much more control over the mouse :(
            new AnimationTimer() { // HACK: hijack a javafx animation timer to check mouse inputs every frame.

                private double mouseLockScreenPositionX;
                private double mouseLockScreenPositionY;

                private int lastLocalMousePosX;
                private int lastLocalMousePosY;

                @Override
                public void handle(long now) {
                    if (!stage.isFocused()) return; // don't process mouse events usless focused...

                    // process cursor appearance
                    if (cursorNeedsHiding.compareAndSet(true, false)) {
                        surface.setCursor(Cursor.NONE);
                    }

                    Bounds surfaceScreenBounds = surface.localToScreen(surface.getBoundsInLocal());
                    double screenMouseX = robot.getMouseX();
                    double screenMouseY = robot.getMouseY();

                    int mouseLocalX, mouseLocalY;
                    int xDelta, yDelta;

                    if (cursorVisible.get() && isPositionOnSurface(screenMouseX, screenMouseY, surfaceScreenBounds)) {
                        // handle the visible cursor.
                        Point2D localCoords = surface.screenToLocal(screenMouseX, screenMouseY);

                        mouseLocalX = (int) Math.round(localCoords.getX());
                        mouseLocalY = currentHeight - (int) Math.round(localCoords.getY());
                        xDelta = mouseLocalX - lastLocalMousePosX;
                        yDelta = mouseLocalY - lastLocalMousePosY;

                        lastLocalMousePosX = mouseLocalX;
                        lastLocalMousePosY = mouseLocalY;
                        addMouseMoveEvent(mouseLocalX, mouseLocalY, xDelta, yDelta);

                    } else if (!cursorVisible.get()) {
                        // handle invisible cursor.

                        if (needsLockPosition.get()) { // ensure there's a position to lock the mouse to.
                            if (isPositionOnSurface(screenMouseX, screenMouseY, surfaceScreenBounds)) {
                                mouseLockScreenPositionX = screenMouseX;
                                mouseLockScreenPositionY = screenMouseY;
                            } else {
                                mouseLockScreenPositionX = surfaceScreenBounds.getCenterX();
                                mouseLockScreenPositionY = surfaceScreenBounds.getCenterY();
                            }

                            Point2D lockLocalBounds = surface.screenToLocal(lastLocalMousePosX, lastLocalMousePosY);
                            lastLocalMousePosX = (int) lockLocalBounds.getX();
                            lastLocalMousePosY = (int) lockLocalBounds.getY();

                            needsLockPosition.set(false);
                            return;
                        }
                        xDelta = (int) (screenMouseX - mouseLockScreenPositionX);
                        yDelta = (int) (screenMouseY - mouseLockScreenPositionY);

                        mouseLocalX = lastLocalMousePosX + xDelta;
                        mouseLocalY = lastLocalMousePosY + yDelta;

                        robot.mouseMove(mouseLockScreenPositionX, mouseLockScreenPositionY);
                        addMouseMoveEvent(mouseLocalX, mouseLocalY, xDelta, -yDelta);
                    }

                    // process cursor appearance
                    if (cursorNeedsUnhiding.compareAndSet(true, false)) {
                        robot.mouseMove(mouseLockScreenPositionX, mouseLockScreenPositionY);
                        surface.setCursor(Cursor.DEFAULT);
                    }
                }
            }.start();
        });

    }

    @Override
    public void initialize() {
        surface.setOnMousePressed(event -> { // handle button press
            MouseButtonEvent buttonEvent = new MouseButtonEvent(jfxToJmeButtonMapping.get(event.getButton()), true, mouseX, mouseY);
            buttonEvent.setTime(getInputTimeNanos());
            mouseButtonEvents.add(buttonEvent);
        });

        surface.setOnMouseReleased(event -> { // handle button release
            MouseButtonEvent buttonEvent = new MouseButtonEvent(jfxToJmeButtonMapping.get(event.getButton()), false, mouseX, mouseY);
            buttonEvent.setTime(getInputTimeNanos());
            mouseButtonEvents.add(buttonEvent);
        });

        surface.addEventHandler(ScrollEvent.ANY, event -> { // handle scrolling...
            double xOffset = event.getDeltaX() * WHEEL_SCALE;
            double yOffset = event.getDeltaY() * WHEEL_SCALE;
            mouseWheel += yOffset;
            final MouseMotionEvent mouseMotionEvent = new MouseMotionEvent(mouseX, mouseY, 0, 0, mouseWheel, (int) Math.round(yOffset));
            mouseMotionEvent.setTime(getInputTimeNanos());
            mouseMotionEvents.add(mouseMotionEvent);

        });
        initialised = true;
    }


    /**
     * Add a mouse move event
     * @param mouseLocalX the mouse local x position
     * @param mouseLocalY the mouse local y position
     * @param xDelta the change in x position
     * @param yDelta the change in y position
     */
    private void addMouseMoveEvent(int mouseLocalX, int mouseLocalY, int xDelta, int yDelta) {
        if (xDelta != 0 || yDelta != 0) {
            MouseMotionEvent motionEvent = new MouseMotionEvent(mouseLocalX, mouseLocalY, xDelta, yDelta, mouseWheel, 0);
            motionEvent.setTime(getInputTimeNanos());
            mouseMotionEvents.add(motionEvent);
            mouseX = mouseLocalX;
            mouseY = mouseLocalY;
        }
    }

    /**
     * Checks if a given screen coordinate is within the bounds of the rendering surface.
     * @param screenX the x coordinate in screen coordinates.
     * @param screenY the y coordinate in screen coordinates.
     * @param surfaceScreenBounds the bounds of the {@link DriftFXSurface} in screen coordinates
     * @return true if the position is within the surface
     */
    private static boolean isPositionOnSurface(double screenX, double screenY, Bounds surfaceScreenBounds) {
        return (screenX >= surfaceScreenBounds.getMinX() && screenX <= surfaceScreenBounds.getMaxX())
                && (screenY >= surfaceScreenBounds.getMinY() && screenY<= surfaceScreenBounds.getMaxY());
    }

    @Override
    public void setCursorVisible(boolean visible) {
        if (visible && !cursorVisible.get()) {
            // cursor is being made visible
            cursorNeedsHiding.set(false);
            cursorNeedsUnhiding.set(true);
        } else if (!visible && cursorVisible.get()) { // cursor is being invisible (or disabled)
            needsLockPosition.compareAndSet(false, true);
            cursorNeedsHiding.set(true);
            cursorNeedsUnhiding.set(false);
        }
        cursorVisible.set(visible);
    }

    @Override
    public int getButtonCount() {
        return 3; // assume 3, we can't query this from javafx.
    }

    @Override
    public void setNativeCursor(JmeCursor cursor) {
        throw new UnsupportedOperationException("Native cursors not supported on FXMouseInput");
    }

    /**
     * Set the current height of the viewport (used internally for mouse position calculations).
     * @param currentHeight
     */
    public void setCurrentHeight(int currentHeight) {
        this.currentHeight = currentHeight;
    }

    @Override
    public void update() {
        while (!mouseMotionEvents.isEmpty()) {
            listener.onMouseMotionEvent(mouseMotionEvents.poll());
        }

        while (!mouseButtonEvents.isEmpty()) {
            listener.onMouseButtonEvent(mouseButtonEvents.poll());
        }
    }

    @Override
    public void destroy() {}

    @Override
    public boolean isInitialized() {
        return initialised;
    }

    @Override
    public void setInputListener(RawInputListener listener) {
        this.listener = listener;
    }

    @Override
    public long getInputTimeNanos() {
        return System.nanoTime();
    }
}
