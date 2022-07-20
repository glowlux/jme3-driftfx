# jme3-driftfx

Embed JMonkeyEngine within a JavaFX application. Full support for keyboard and mouse events.  

Great performance. Uses [DriftFX](https://github.com/eclipse-efx/efxclipse-drift) to effciently render JME to JavaFX directly.

## Example
```java
public class Example extends Application {
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

    public static void main(String[] args) {
        launch(Example.class, args);
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
```
# Command line args
You'll need the following command line arguments...
```bash
--module-path [ PATH TO JAVAFX LIBS HERE ]
--add-modules javafx.controls
--add-exports javafx.graphics/com.sun.javafx.sg.prism=ALL-UNNAMED 
--add-exports javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED 
--add-exports javafx.graphics/com.sun.javafx.util=ALL-UNNAMED 
--add-exports javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED 
--add-exports javafx.graphics/com.sun.javafx.geom=ALL-UNNAMED 
--add-exports javafx.graphics/com.sun.javafx.geom.transform=ALL-UNNAMED 
--add-exports javafx.graphics/com.sun.javafx.scene.text=ALL-UNNAMED 
--add-exports javafx.graphics/com.sun.javafx.font=ALL-UNNAMED 
--add-exports javafx.graphics/com.sun.prism.paint=ALL-UNNAMED 
--add-opens javafx.graphics/com.sun.prism=ALL-UNNAMED
--add-opens javafx.graphics/com.sun.prism.es2=ALL-UNNAMED 
--add-opens javafx.graphics/com.sun.prism.d3d=ALL-UNNAMED 
--add-opens javafx.graphics/com.sun.prism.impl=ALL-UNNAMED
```

#### Or if you're using gradle and the application plugin...
```gradle
run {
    def modules = sourceSets.main.runtimeClasspath.filter { jar -> jar.getName().contains('javafx-') } .getAsPath()
    jvmArgs.addAll(
            [
                    "--module-path", modules,
                    "--add-modules", "javafx.controls",
                    "--add-exports", "javafx.graphics/com.sun.javafx.sg.prism=ALL-UNNAMED",
                    "--add-exports", "javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED",
                    "--add-exports", "javafx.graphics/com.sun.javafx.util=ALL-UNNAMED",
                    "--add-exports", "javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED",
                    "--add-exports", "javafx.graphics/com.sun.javafx.geom=ALL-UNNAMED",
                    "--add-exports", "javafx.graphics/com.sun.javafx.geom.transform=ALL-UNNAMED",
                    "--add-exports", "javafx.graphics/com.sun.javafx.scene.text=ALL-UNNAMED",
                    "--add-exports", "javafx.graphics/com.sun.javafx.font=ALL-UNNAMED",
                    "--add-exports", "javafx.graphics/com.sun.prism.paint=ALL-UNNAMED",
                    "--add-opens", "javafx.graphics/com.sun.prism=ALL-UNNAMED",
                    "--add-opens", "javafx.graphics/com.sun.prism.es2=ALL-UNNAMED",
                    "--add-opens", "javafx.graphics/com.sun.prism.d3d=ALL-UNNAMED",
                    "--add-opens", "javafx.graphics/com.sun.prism.impl=ALL-UNNAMED",
            ],
    )
}
```

# Installation
### Gradle
```gradle
repositories {
	maven {url 'https://repo.eclipse.org/content/groups/efxclipse'}
	maven { url 'https://jitpack.io' }
}

dependencies {
	implementation 'com.github.glowlux:jme3-driftfx:1.0.0'
}
```

### Maven
```maven
<repositories>
	<repository>
		<id>efxclipse</id>
		<url>https://repo.eclipse.org/content/groups/efxclipse</url>
	</repository>
	<repository>
		<id>jitpack.io</id>
		<url>https://jitpack.io</url>
	</repository>
</repositories>
 
<dependency>
	<groupId>com.github.glowlux</groupId>
	<artifactId>jme3-driftfx</artifactId>
	<version>1.0.0</version>
</dependency>
 ```
 
 # Issues & Troubleshooting. 
 If JME is not rendering correctly... Try setting ``forceMainMemoryTransfer`` to ``true`` in the constructor of ``SimpleFXApplication``. 
 
 Other rendering issues are likely due to hardware and drivers, check [DriftFX's known issues](https://github.com/eclipse-efx/efxclipse-drift#known-issues).
 
Having another issue? log an issue with as much information as possible. Thank you soo much. 

### Known Issues
n/a
 
# License 
[License is MIT](https://github.com/glowlux/jme3-driftfx/blob/main/LICENSE)

 


