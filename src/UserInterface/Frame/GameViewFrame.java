package UserInterface.Frame;

import GameObject.MapPackage.Map;
import Management.GameManager;
import UserInterface.MenuPackage.Menu;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.nio.file.Paths;

public class GameViewFrame {

    private static final int PANE_WIDTH = 640;
    private static final int PANE_HEIGHT = 640;
    private static final int BUTTON_WIDTH = 250;
    private static final int BUTTON_HEIGTH = 5;
    private boolean returnCall = false;
    private Label gameFinished;
    private Label gameOver;
    private Label levelFinished;
    private Map map;
    private GameManager gameManager;
    private Button backToMenu;
    private Button nextLevel;
    private Stage gameView;

    public GameViewFrame(GameManager gameManager, int frame){
        this.map = map;
        this.gameManager = gameManager;
        gameFinished = new Label("Game Finished!\n" + "\nPlayer 1 Score: " + gameManager.getScores(0) + "\nPlayer 2 Score: " + gameManager.getScores(1) + "\nCongratulations!");//add score here
        gameOver = new Label("Game Over\n" + "Level: " + gameManager.getLevel() + "\nPlayer 1 Score: " + gameManager.getScores(0) + "\nPlayer 2 Score: " + gameManager.getScores(1) ); //add score here
        levelFinished = new Label("Level" + gameManager.getLevel() + " Finished\n");
        gameFinished.setId("labels1");
        gameOver.setId("labels1");
        levelFinished.setId("labels1");
        backToMenu = new Button("Menu");
        nextLevel = new Button( "Continue Next Level");
        gameView = new Stage();
        gameView.setResizable(false);
        StackPane pane = new StackPane();
        gameView.initModality(Modality.APPLICATION_MODAL);

        styleButton(backToMenu);
        styleButton(nextLevel);
        nextLevel.setOnAction( event -> {
            returnCall = true;
            gameView.close();
            gameManager.stopLoop();
            gameManager.endMapManager();
            gameManager.initiateNextLevel();
        });
        backToMenu.setOnAction( event -> {
            returnCall = true;
            gameView.close();
            gameManager.endMapManager();
            GameManager.endGameManagerInstance();
            Menu newGame = new Menu();
            Stage newStage = new Stage();
            try {
                newGame.start(newStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
            gameManager.closeActiveMapManager();

        });

        Image im = new Image(Paths.get("."+"/MediaFiles/metal.png").toUri().toString(), true);
        pane.setBackground(new Background(new BackgroundImage(im, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
        VBox answerBoxes = new VBox(10);
        if( frame == 0 )
            answerBoxes.getChildren().addAll(gameOver, backToMenu);
        else if( frame == 1)
            answerBoxes.getChildren().addAll(gameFinished, backToMenu);
        else if( frame == 2)
            answerBoxes.getChildren().addAll(levelFinished, nextLevel);
        answerBoxes.setAlignment(Pos.CENTER);
        pane.getChildren().add( answerBoxes);
        Scene frameScene = new Scene(pane, PANE_WIDTH, PANE_HEIGHT);
        String  style = getClass().getResource("../../style.css").toExternalForm();
        frameScene.getStylesheets().add(style);
        gameView.setScene(frameScene);
    }

    public void showGameView(){
        gameManager.stopLoop();
        gameView.show();
    }

    private void styleButton( Button returnButton) {
        returnButton.setId("glass-grey");
        returnButton.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGTH);


        returnButton.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                returnButton.setStyle("-fx-background-color:#c3c4c4;");
            }
        });
        returnButton.setOnMouseExited(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                returnButton.setStyle("-fx-background-color:\n" +
                            "        #dae7f3,\n" +
                            "        linear-gradient(#d6d6d6 50%, white 100%),\n" +
                            "        radial-gradient(center 50% -40%, radius 200%, #e6e6e6 45%, rgba(230,230,230,0) 50%);");
            }
        });
    }
        public boolean isReturnCall() {
            return returnCall;
        }
        public void setReturnCall(boolean returnCall) {
            this.returnCall = returnCall;
        }
}


