package com.github.glowlux.driftjme;

import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;
import javafx.stage.Stage;
import org.eclipse.fx.drift.DriftFXSurface;

/**
 * Use this instead of {@link SimpleApplication} when embedding JME in JavaFX.
 * Passes the surface and stage through to {@link FXContext}.
 *
 * @author glowlux
 */
public abstract class SimpleFXApplication extends SimpleApplication {

    private final DriftFXSurface surface;
    private Stage stage;
    private final boolean disableDebugOutput;
    private final boolean forceMainMemoryTransfer;

    public SimpleFXApplication(DriftFXSurface surface, Stage stage, boolean disableDebugOutput, boolean forceMainMemoryTransfer) {
        super();
        this.surface = surface;
        this.stage = stage;
        this.disableDebugOutput = disableDebugOutput;
        this.forceMainMemoryTransfer = forceMainMemoryTransfer;
    }

    @Override
    public void start() {
        if (settings == null) {
            setSettings(new AppSettings(true));
        }
        FXContext fxContext = new FXContext(surface, stage, disableDebugOutput, forceMainMemoryTransfer);
        context = fxContext;
        fxContext.setSystemListener(this);
        fxContext.create();

    }
}
