package com.github.glowlux.driftjme.example;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.github.glowlux.driftjme.SimpleFXApplication;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.eclipse.fx.drift.DriftFXSurface;
import com.github.glowlux.driftjme.util.RightClickCameraAppState;

/**
 * Example of embedding JME into JavaFX.
 *
 * @author glowlux
 */
public class DriftTesting extends Application {
    private DriftFXSurface driftFXSurface;

    @Override
    public void start(Stage stage) {
        driftFXSurface = new DriftFXSurface();

        BorderPane borderPane = new BorderPane();
        borderPane.setBackground(null);
        borderPane.setPadding(new Insets(40));
        borderPane.setPrefSize(1024, 768);
        borderPane.setCenter(driftFXSurface);

        Scene scene = new Scene(borderPane);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.show();

        new Thread(() -> {
            TestJME app = new TestJME(driftFXSurface, stage);
            app.setShowSettings(false);
            app.start();
        }).start();

    }


    public class TestJME extends SimpleFXApplication {
        private Geometry boxGeom;

        public TestJME(DriftFXSurface surface, Stage stage) {
            super(surface, stage, false, false);
        }

        @Override
        public void simpleInitApp() {
            viewPort.setBackgroundColor(ColorRGBA.Black);

            flyCam.setEnabled(false); // disable the default flycam and replace it with a cam that does not lock the camera instantly.
            stateManager.attach(new RightClickCameraAppState());

            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", ColorRGBA.Orange);
            Box box = new Box(1, 1, 1);
            boxGeom = new Geometry("Box", box);
            boxGeom.setMaterial(mat);
            rootNode.attachChild(boxGeom);
        }

        @Override
        public void simpleUpdate(float tpf) {
            boxGeom.rotate(tpf * -1, tpf * -1, tpf * -1);
        }
    }
}

