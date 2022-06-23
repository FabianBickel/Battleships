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

    final private static int PLAYING_FIELD_SIZE = 10;
    final private static int[] SHIP_LENGTHS = { 1, 1, 1, 1, 2, 2, 2, 3, 3, 4 };
    final private static int SHOT_COUNT_TARGET = getShotCountTarget();

    PlayingField playingField;

    public static void main(String[] args) throws Exception {
        launch();
    }

    private static int getShotCountTarget() {
        int shotCountTarget = 0;
        for (int shipLength : SHIP_LENGTHS) {
            shotCountTarget += shipLength;
        }
        return shotCountTarget;
    }

    @Override
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
        Group root = new Group();
        getPlayingFieldWithShips();
        VBox vBoxInGame = new VBox();
        playingField.addPlayingFieldTo(vBoxInGame);
        vBoxInGame.getChildren().add(getButtonBar());
        root.getChildren().add(vBoxInGame);
        Scene scene = new Scene(root);
        return scene;
    }

    private void getPlayingFieldWithShips() throws TimeoutException {
        int i = 0;
        while (true) {            
            if (i >= 10) {
                throw new TimeoutException("Ship could not be placed. There are likely too many ships to be placed on the playing field.");
            }
                playingField = new PlayingField(800, 800, PLAYING_FIELD_SIZE, PLAYING_FIELD_SIZE);
                for (int shipLength : SHIP_LENGTHS) {
                    if (shipLength > PLAYING_FIELD_SIZE) {
                        throw new ArrayIndexOutOfBoundsException("Ships length cannot be higher than PLAYING_FIELD_SIZE");
                    }
                    Random random = new Random();
                    boolean isVertical = random.nextBoolean();
                    int colSpan = isVertical ? 1 : shipLength;
                    int rowSpan = isVertical ? shipLength : 1;
                    playingField.addShip(colSpan, rowSpan);
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