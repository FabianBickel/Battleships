import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeoutException;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    final private static int PLAYING_FIELD_SIZE = 9;
    final private static int SHIP_COUNT = 10;
    final private static ArrayList<ShipShape> SHIP_SHAPES = getShipShapes();
    final private static int SHOT_COUNT_TARGET = getShotCountTarget();

    private PlayingField playingField;

    public static void main(String[] args) throws Exception {
        launch();
    }

    private static ArrayList<ShipShape> getShipShapes() {
        ArrayList<ShipShape> shipShapes = new ArrayList<ShipShape>();
        shipShapes.add(ShipShape.DESTROYER);
        shipShapes.add(ShipShape.DESTROYER);
        shipShapes.add(ShipShape.DESTROYER);
        shipShapes.add(ShipShape.CRUISER);
        shipShapes.add(ShipShape.CRUISER);
        shipShapes.add(ShipShape.BATTLESHIP);
        shipShapes.add(ShipShape.SUBMARINE);
        shipShapes.add(ShipShape.CATAMARAN);
        shipShapes.add(ShipShape.CARRIER);
        shipShapes.add(ShipShape.ROCKET);
        return shipShapes;
    }

    private static int getShotCountTarget() {
        int shotCountTarget = 0;
        for (ShipShape shipShape : SHIP_SHAPES) {
            shotCountTarget += shipShape.getTileCount();
        }
        return shotCountTarget;
    }

    public void start(Stage stage) throws Exception {
        try {
            Scene scene = getScenePlay();
            configureStage(stage, scene);
        } catch (TimeoutException e) {
            System.out.println("Creating scene failed: \"" + e.getMessage() + '"');
            System.exit(-1);
        }
        stage.show();
    }

    private void configureStage(Stage stage, Scene scene) {
        stage.setTitle("Battleships");
        stage.setScene(scene);
        stage.setResizable(false);
    }

    private Scene getScenePlay() throws TimeoutException {
        Scene scene = getRootGroup();
        scene.getStylesheets().add("style.css");
        return scene;
    }

    private Scene getRootGroup() throws TimeoutException {
        getPlayingFieldWithShips();
        VBox vBoxInGame = new VBox();
        vBoxInGame.setFillWidth(true);
        playingField.addPlayingFieldTo(vBoxInGame);
        vBoxInGame.getChildren().add(getButtonBar());
        Scene scene = new Scene(vBoxInGame);
        return scene;
    }

    private void getPlayingFieldWithShips() throws TimeoutException {
        int attempt = 0;
        while (true) {
            if (attempt >= 10) {
                throw new TimeoutException(
                        "Ship could not be placed. There are likely too many ships to fit on the playing field.");
            }
            playingField = new PlayingField(800, 800, PLAYING_FIELD_SIZE, PLAYING_FIELD_SIZE);
            for (ShipShape shipShape : SHIP_SHAPES) {

                if (shipShape.getWidth() > PLAYING_FIELD_SIZE || shipShape.getHeight() > PLAYING_FIELD_SIZE) {
                    throw new ArrayIndexOutOfBoundsException("Ship cannot be bigget than the playing field.");
                }
                Random random = new Random();
                int orientation = random.nextInt(4);
                playingField.addShip(shipShape);
            }
            break;
        }
    }

    private ButtonBar getButtonBar() {
        ButtonBar buttonBar = new ButtonBar();
        Button buttonShoot = new Button("Shoot");
        buttonShoot.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
                playingField.shootLockedInTile();
            }
        });
        buttonBar.getButtons().add(buttonShoot);
        return buttonBar;
    }
}