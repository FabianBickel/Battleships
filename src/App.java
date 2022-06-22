import java.util.Random;

import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class App extends Application {

    final private static int PLAYING_FIELD_SIZE = 10;
    final private static int TILE_SIZE = 48;
    final private static int[] SHIP_LENGTHS = {1, 1, 1, 1, 2, 2, 2, 3, 3, 4};

    PlayingField playingField;

    public static void main(String[] args) throws Exception {
        if (!(PLAYING_FIELD_SIZE < 20)) {
            throw new Exception("Playing field size must be smaller than 20.");
        }
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = getScenePlay();
        configureStage(stage, scene);
        stage.show();
    }

    private void configureStage(Stage stage, Scene scene) {
        stage.setTitle("Battleships");
        stage.setScene(scene);
        stage.setResizable(false);
    }

    private Scene getScenePlay() {
        Scene scene = getRootGroup();
        scene.getStylesheets().add("style.css");
        return scene;
    }

    private Scene getRootGroup() {
        Group root = new Group();
        playingField = new PlayingField(480, 480, PLAYING_FIELD_SIZE, PLAYING_FIELD_SIZE);
        fillPlayingFieldWithShips();
        VBox vBoxInGame = new VBox();
        playingField.addPlayingFieldTo(vBoxInGame);
        vBoxInGame.getChildren().add(getButtonBar());
        root.getChildren().add(vBoxInGame);
        Scene scene = new Scene(root);
        return scene;
    }

    private void fillPlayingFieldWithShips() {
        Random random = new Random();
        for (int shipLength : SHIP_LENGTHS) {
            boolean isVertical = random.nextBoolean();
            if (isVertical) {
                int column = random.nextInt(PLAYING_FIELD_SIZE);
                int row = random.nextInt(PLAYING_FIELD_SIZE - shipLength - 1);
                playingField.addShip(column, row, 1, shipLength);
            } else {
                int column = random.nextInt(PLAYING_FIELD_SIZE - shipLength - 1);
                int row = random.nextInt(PLAYING_FIELD_SIZE);
                playingField.addShip(column, row, shipLength, 1);
            }
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