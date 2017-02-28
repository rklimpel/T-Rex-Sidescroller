package main.java.cau.project.game.view_desktop;

import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.Toolkit;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import main.java.cau.project.Helper;
import main.java.cau.project.Main;
import main.java.cau.project.R;
import main.java.cau.project.end.EndView;
import main.java.cau.project.game.GameModel;
import main.java.cau.project.menu.MenuView;

import java.io.*;




/**
 * Created by ricoklimpel on 23.02.17.
 */
public class GameController {

    //objects from FXML configuration
    @FXML Button btn_backToMenu;
    @FXML Pane pane;
    @FXML BorderPane background;

    private Label lbl_hints = new Label("Press Space to Jump \n Press Strg to Crouch ");

    private Rectangle rectangle;

    private ImageView imageView_player = new ImageView();
    private ImageView imageView_obstacle;

    private Image imagePlayer = null;
    private Image imagePlayerCrouched = null;
    private Image imageObstacle = null;

    //Remember related mvc classes
    private GameModel gameModel;
    private GameView gameView;

    private Boolean gameObjectsAsImages = R.gameobjectsAsImages;

    private Font customScoreFont;

    public GameController() {
        this.gameModel = new GameModel(this);
        loadCustomFont();
        //creates the hints at when is called and space isn't pressed yet
        showhints();

        loadImages();
    }

    private void loadImages() {

        try{
            imagePlayer = new Image("file:src/main/res/assets/player_mexiko.png");
            imagePlayerCrouched = new Image("file:src/main/res/assets/playerCrouched_mexiko.png");
            imageObstacle = new Image("file:src/main/res/assets/obstacle_mexiko.png");
        }catch(Exception e){
            System.out.println(e);
        }

    }

    /**
     * Leave Game, quit every running things and go back to Menu
     *
     * @throws IOException
     */
    public void OnClick_btn_backToMenu() throws IOException {
        startMenu();
    }

    /**
     * Go Back the menu (exit Game)
     * <p>
     * switches to Menu scene and stops the game Timer
     *
     * @throws IOException
     */
    public void startMenu() throws IOException {
        //Stop Game Timer
        gameModel.stopGameTimer();
        //Start Menu Scene
        new MenuView();
    }

    /**
     * This Methods gets and handles the Key event from GameView
     *
     * @param event
     */
    public void KeyEventHandler(KeyEvent event) {

        //On Space Pressed
        if (event.getCode() == KeyCode.SPACE) {
            //IF Game is not over but it is not Running start the game
            if (!gameModel.gameTimerEnabled && !gameModel.gameOver) {

                gameModel.createPlayer();
                gameModel.startGameTimer();
                // the hints vanish if game starts
                deleteHints();

            }
            //IF the Game is over (collision) space switches to menu
            else if (!gameModel.gameTimerEnabled && gameModel.gameOver) {

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            new EndView(gameModel.getScore());
                        } catch (IOException e) {
                            System.out.println("somehting failed...");
                        }
                    }
                });
            }
            //else space is there to jump
            else if(gameModel.gameTimerEnabled){
                gameModel.jump();
            }

            //On Escape Pressed:
        } else if (event.getCode() == KeyCode.ESCAPE) {
            try {
                startMenu();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (event.getCode() == KeyCode.CONTROL) {
            if (event.getEventType() == KeyEvent.KEY_PRESSED) {
                gameModel.player.crouch();
            }
        }
    }

    public void MouseEventHandler (MouseEvent mouseEvent){

        if(mouseEvent.getButton() == MouseButton.PRIMARY){

            //IF Game is not over but it is not Running start the game
            if (!gameModel.gameTimerEnabled && !gameModel.gameOver) {

                gameModel.createPlayer();
                gameModel.startGameTimer();
                // the hints vanish if game starts
                deleteHints();

            }
            //IF the Game is over (collision) space switches to menu
            else if (!gameModel.gameTimerEnabled && gameModel.gameOver) {

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            new EndView(gameModel.getScore());
                        } catch (IOException e) {
                            System.out.println("somehting failed...");
                        }

                    }
                });

            }
            //else primary mouse is there to jump
            else {
                gameModel.jump();
            }
        }
        else if(mouseEvent.getButton() == MouseButton.SECONDARY){
            gameModel.player.crouch();
        }
    }

    public void MouseClickReleased (MouseEvent event){
        if(event.getButton() == MouseButton.SECONDARY){
            gameModel.player.crouchEnd();
        }
    }


    /**
     * Is called on GameModel Data changes
     * e.g. in gametick method
     */
    public void Update() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                pane.getChildren().clear();

                if(gameObjectsAsImages){

                    addImageObastaclesToPane();

                    addImagePlayerToPane();

                }else{
                    //Add Obstacles to Pane
                    addObstaclesToPane();

                    //Add Player to Pane
                    addPlayerToPane();
                }

                //Add Score Label to Pane
                addScoreLabelToPane();

                //Add One Big Ground
                addGroundToPane();
            }
        });
    }

    private void addGroundToPane() {
        Rectangle rectangle = new Rectangle();
        rectangle.relocate(0,pane.getHeight()-R.groundLvL);
        rectangle.setWidth(pane.getWidth());
        rectangle.setHeight(R.groundSize);

        pane.getChildren().addAll(rectangle);
    }

    private void addImageObastaclesToPane() {

        for (int i = 0; i < gameModel.getObstacles().size(); i++) {

            imageView_obstacle = new ImageView();
            imageView_obstacle.setImage(imageObstacle);
            imageView_obstacle.relocate(gameModel.getObstacles().get(i).getX(),
                    gameModel.getObstacles().get(i).getY());
            imageView_obstacle.setFitHeight(gameModel.getObstacles().get(i).getHeight());
            imageView_obstacle.setFitWidth(gameModel.getObstacles().get(i).getWidth());

            pane.getChildren().addAll(imageView_obstacle);
        }
               

    }

    private void addImagePlayerToPane(){

        if(gameModel.player.getCrouching()){
            imageView_player.setImage(imagePlayerCrouched);
        }else{
            imageView_player.setImage(imagePlayer);
        }
        imageView_player.setFitWidth(gameModel.player.getWidth());
        imageView_player.setFitHeight(gameModel.player.getHeight());
        imageView_player.relocate(gameModel.player.getX(),gameModel.player.getY());

        pane.getChildren().addAll(imageView_player);


    }

    private void addObstaclesToPane() {
        for (int i = 0; i < gameModel.getObstacles().size(); i++) {

            rectangle = new Rectangle(
                    gameModel.getObstacles().get(i).getX(),
                    gameModel.getObstacles().get(i).getY(),
                    gameModel.getObstacles().get(i).getWidth(),
                    gameModel.getObstacles().get(i).getHeight());

            pane.getChildren().addAll(rectangle);
        }
    }

    private void addScoreLabelToPane() {
        Label label = new Label();
        label.setFont(new Font("Arial",50));
        label.setText("Score: " + gameModel.getScore());
        label.setFont(customScoreFont);

        FontLoader fontLoader = Toolkit.getToolkit().getFontLoader();
        float labelWidth = fontLoader.computeStringWidth(label.getText(), label.getFont());

        label.setLayoutX((pane.getWidth()/2)-labelWidth/2);
        label.setLayoutY(pane.getHeight()/5);

        pane.getChildren().add(label);
    }


    private void addPlayerToPane() {
        rectangle = new Rectangle(
                gameModel.player.getX(),
                gameModel.player.getY(),
                gameModel.player.getWidth(),
                gameModel.player.getHeight());

        rectangle.setRotate(gameModel.player.getRotation());
        rectangle.setFill(gameModel.player.getColor());
        rectangle.setStroke(Color.BLACK);
        rectangle.setStrokeWidth(2);
        rectangle.setStrokeLineCap(StrokeLineCap.ROUND);

        pane.getChildren().add(rectangle);
    }

    /**
     * Tells the GameController about his relatted gameView.
     * Could'nt be set in constructor because constructor is called by fxml configuration
     *
     * @param gameView
     */
    public void setView(GameView gameView) {
        this.gameView = gameView;
    }

    /**
     * getter for Pane Size (called by Model)
     *
     * @return
     */
    public int getPaneHeight() {
        if (pane != null) {
            return (int) pane.getHeight();
        }
        return 0;
    }

    /**
     * getter for Pane Size (called by Model)
     *
     * @return
     */
    public int getPaneWidth() {
        if (pane != null) {
            return (int) pane.getWidth();
        }
        return 0;
    }

    public void loadCustomFont() {
        customScoreFont = Helper.loadFont(200);
    }

    public void KeyReleasedHandler(KeyEvent event) {
        if (event.getCode() == KeyCode.CONTROL) {
            gameModel.player.crouchEnd();
        }
    }

    /**
     * hides the hints when game is running
     */
    public void deleteHints() {
        lbl_hints.setVisible(false);
    }

    /**
     * creates hints at the beginning of the game and centers it
     */
    public void create_Hints(){
        lbl_hints.setFont(Helper.loadFont(100));
        FontLoader fontLoader = Toolkit.getToolkit().getFontLoader();
        float  btn_hintsWidth = fontLoader.computeStringWidth(lbl_hints.getText(), lbl_hints.getFont());
        lbl_hints.setVisible(true);

        lbl_hints.setLayoutX((pane.getWidth() / 2) - btn_hintsWidth /2 /2);
        lbl_hints.setLayoutY(pane.getHeight()/5);
    }

    /**
     * shows hints and creates them
     */
    private void showhints() {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                create_Hints();
                pane.getChildren().addAll(lbl_hints);
            }
        });

    }
}