import java.util.concurrent.TimeUnit;

import game.GameManager;
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
import tiles.*;

public class App extends Application {

    final private static int PLAYING_FIELD_SIZE = 10;
    final private static int TILE_SIZE = 48;

    private Rectangle lockedInTile;
    private int lockedInTileX = -1;
    private int lockedInTileY = -1;

    GridPane playingField;

    Group root;

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
        root = new Group();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("style.css");
        Pane pane = new Pane();
        playingField = getPlayingField();
        ButtonBar buttonBar = new ButtonBar();
        Button buttonShoot = new Button("Shoot");
        buttonShoot.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
                shootLockedInTile();
            }
        });

        VBox vBox = new VBox();

        buttonBar.getButtons().add(buttonShoot);
        vBox.getChildren().add(playingField);
        vBox.getChildren().add(buttonBar);
        pane.getChildren().add(vBox);
        root.getChildren().add(pane);
        return scene;
    }

    private GridPane getPlayingField() {
        if (playingField == null) {
            GridPane gridPane = new GridPane();
            gridPane.setGridLinesVisible(true);
            gridPane.setCursor(Cursor.CROSSHAIR);
            fillPlayingField(gridPane);
            return gridPane;
        } else {
            return playingField;
        }

    }

    private void fillPlayingField(GridPane gridPane) {
        for (int x = 0; x < PLAYING_FIELD_SIZE; x++) {
            for (int y = 0; y < PLAYING_FIELD_SIZE; y++) {
                Rectangle rect = getNewTile();
                gridPane.add(rect, x, y);
            }
        }
    }

    private Rectangle getNewTile() {
        Rectangle rect = new Rectangle();
        rect.setWidth(TILE_SIZE);
        rect.setHeight(TILE_SIZE);
        rect.getStyleClass().add("tile");
        rect.setOnMouseEntered(getEvHaTileHighlight());
        rect.setOnMouseExited(getEvHaTileReset());
        rect.setOnMouseClicked(getEvHaTileInteractWith());
        return rect;
    }

    private EventHandler<MouseEvent> getEvHaTileHighlight() {
        return new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
                markTile((Rectangle) arg0.getSource());
            }
        };
    }

    private EventHandler<MouseEvent> getEvHaTileReset() {
        return new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
                Rectangle source = (Rectangle) arg0.getSource();
                int x = (int) (GridPane.getColumnIndex(source));
                int y = (int) (GridPane.getRowIndex(source));
                if (!(lockedInTileX == x && lockedInTileY == y)) {
                    source.setStyle("-fx-fill: rgba(255, 255, 255, 0);");
                }
            }
        };
    }

    private EventHandler<MouseEvent> getEvHaTileInteractWith() {
        return new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
                Rectangle source = (Rectangle) arg0.getSource();
                int x = (int) (GridPane.getColumnIndex(source));
                int y = (int) (GridPane.getRowIndex(source));
                if (arg0.getClickCount() == 1) {
                    resetlockedInTile();
                    if (!(lockedInTileX == x && lockedInTileY == y)) {
                        lockInTile(source);
                    } else {
                        markTile(lockedInTile);
                        unlockLockedInTile();
                    }
                } else if (arg0.getClickCount() == 2) {
                    resetlockedInTile();
                    lockInTile(source);
                    shootLockedInTile();
                    unlockLockedInTile();
                }
            }
        };
    }

    private void lockInTile(Rectangle tile) {
        int x = (int) (GridPane.getColumnIndex(tile));
        int y = (int) (GridPane.getRowIndex(tile));
        lockedInTile = tile;
        lockedInTileX = x;
        lockedInTileY = y;
        Image img = new Image("crosshair.png");
        tile.setFill(new ImagePattern(img));
    }

    private void resetlockedInTile() {
        if (lockedInTile != null) {
            lockedInTile.setStyle("-fx-fill: rgba(255, 255, 255, 0);");
        }
    }

    private void unlockLockedInTile() {
        lockedInTile = null;
        lockedInTileX = -1;
        lockedInTileY = -1;
    }

    private void markTile(Rectangle tile) {
        tile.setStyle("-fx-fill: rgba(255, 255, 255, 0.25);");
    }

    private void shootLockedInTile() {
        if (lockedInTile != null) {
            shootTile(lockedInTile);
        }
    }

    private void shootTile(Rectangle tile) {
        Image image = new Image("explosion.gif");
        int column = GridPane.getColumnIndex(tile);
        int row = GridPane.getRowIndex(tile);
        double explosionX = column * TILE_SIZE + TILE_SIZE / 2 + playingField.getLayoutX();
        double explosionY = row * TILE_SIZE + TILE_SIZE / 2 + playingField.getLayoutY();
        double explosionRadius = TILE_SIZE;
        Circle explosion = new Circle(explosionX, explosionY, explosionRadius);
        explosion.setFill(new ImagePattern(image));
        root.getChildren().add(explosion);
        GameManager.shoot(column, row);
        resetlockedInTile();
        unlockLockedInTile();
    }
}