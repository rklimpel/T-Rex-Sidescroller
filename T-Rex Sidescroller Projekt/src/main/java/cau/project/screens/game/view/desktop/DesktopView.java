package main.java.cau.project.screens.game.view.desktop;

import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.Toolkit;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.effect.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import main.java.cau.project.*;
import main.java.cau.project.screens.game.view.GameView;
import main.java.cau.project.services.listeners.KeyboardListener;
import main.java.cau.project.services.listeners.MouseListener;
import main.java.cau.project.services.loader.CustomFontLoader;
import main.java.cau.project.services.loader.ImageLoader;

import java.util.ArrayList;


public class DesktopView extends GameView {

   @FXML
   Pane pane;
   @FXML
   BorderPane background;

   private Label lbl_hints = new Label();

   private Rectangle rectangle;

   private ImageView iV_player = new ImageView();

   private ImageLoader imageLoader;
   private CustomFontLoader customFontLoader;

   private Font customFont;

   private Boolean gameObjectsAsImages = R.gameobjectsAsImages;

   private int paneWidth;
   private int paneHeight;

   private double sunOpacity = 0;

   int playerWalkImage = 1;

   private ArrayList<ImageView> backgroundImages = new ArrayList<>();

   /**
    * Deskotp View Constructor
    * <p>
    * set a view variable to use if "this" is not possible
    * sets the View ID
    * set Keyboard and Mouse Listeners
    * Load Images and Fonts
    * Show Hints
    */
   public DesktopView() {

      view = this;
      setViewID(R.viewIdGameDesktop);

      if (Main.getMainView() == null || Main.getMainView().getViewID() != R.viewIdSplit) {
         Main.setMainView(this);
      }



      Platform.runLater(new Runnable() {
         @Override
         public void run() {

            paneWidth = (int) pane.getWidth();
            paneHeight = (int) (pane.getHeight());

            if (paneWidth == 0 || paneHeight == 0) {
               paneWidth = 800;
               paneHeight = 400;
            }

            if(gameObjectsAsImages){
               initBackgroundImages();
            }

            DesktopView.super.setController(paneWidth, paneHeight);

            new KeyboardListener(view);
            new MouseListener(view);
         }
      });

      imageLoader = new ImageLoader();
      imageLoader.load();
      customFontLoader = new CustomFontLoader();
      customFont = customFontLoader.load(R.fontPixel, 160);

      //creates the hints at when is called and space isn't pressed yet
      showHints();
   }

   private void initBackgroundImages() {
      for (int i = 0; i < 2; i++) {
         ImageView imageView = new ImageView();
         imageView.setImage(imageLoader.getImg_background());
         imageView.setFitWidth(paneWidth);
         imageView.setFitHeight(paneHeight);
         imageView.setX(0+(i*paneWidth));
         imageView.setY(0);
         backgroundImages.add(imageView);
         pane.getChildren().add(imageView);
      }
   }

   /**
    * Is called on GameModel Data changes
    * Creates a new "Frame" / Scene and creates all game components new
    */
   public void Update() {

      Platform.runLater(new Runnable() {
         @Override
         public void run() {

            pane.getChildren().clear();


            if (gameObjectsAsImages) {

               drawBackgroundImages();

               drawSun();

               drawBackgroundObjects();

               drawImagePowerup();

               drawImagePlatform();

               //Add Obstacles to Pane
               drawImageObstacles();

               //Add Player to Pane
               drawImagePlayer();

            } else {

               drawPowerup();

               drawPlatform();

               //Add Obstacles to Pane
               drawObstacles();

               //Add Player to Pane
               drawPlayer();
            }

            //Add One Big Ground
            drawGround();

            //Add Score Label to Pane
            drawScoreLabel();

            drawScorePopups();

            if (controller.gameModel.gameOver && controller.gameModel.trickJumpFail) {

               Label label = new Label();
               label.setText("Trickser failed!");
               label.setFont(customFont);

               FontLoader fontLoader = Toolkit.getToolkit().getFontLoader();
               float labelWidth = fontLoader.computeStringWidth(label.getText(), label.getFont());

               label.setLayoutX((pane.getWidth() / 2) - labelWidth / 2);
               label.setLayoutY(pane.getHeight() / 7 * 3);

               DropShadow dropShadow = new DropShadow(2,Color.BLACK);

               label.setEffect(dropShadow);

               pane.getChildren().add(label);

               //Main.stage.getScene().getRoot().setEffect(new GaussianBlur());
            }

            DesktopView.super.calcAndShowFPS();

         }
      });
   }

   private void drawScorePopups() {

      for (int i = 0; i < controller.getScorePopups().size(); i++) {
         Label label = new Label();
         label.setLayoutX(controller.getScorePopups().get(i).getX());
         label.setLayoutY(controller.getScorePopups().get(i).getY());
         label.setText(controller.getScorePopups().get(i).getText());
         label.setFont(new Font("Arial",25));
         label.setOpacity(controller.getScorePopups().get(i).getOpacity());

         pane.getChildren().add(label);
      }

   }

   private void drawSun() {
      if(sunOpacity != 1){
         sunOpacity += 0.001;
      }
      ImageView imageView = new ImageView();
      imageView.setImage(imageLoader.getImg_sun());
      imageView.relocate(0,0);
      imageView.setFitHeight(paneHeight);
      imageView.setFitWidth(paneWidth);
      imageView.setOpacity(sunOpacity);
      pane.getChildren().add(imageView);
   }

   private void drawBackgroundImages(){
      for (int i = 0; i < backgroundImages.size(); i++) {

         backgroundImages.get(i).setX(backgroundImages.get(i).getX()-0.2);

         if(backgroundImages.get(i).getX()+paneWidth<=0){
            backgroundImages.get(i).setX(paneWidth);
         }

         pane.getChildren().add(backgroundImages.get(i));
      }
   }

   private void drawBackgroundObjects() {

      for (int i = 0; i < controller.getBackgroundObjects().size(); i++) {
         ImageView imageView = new ImageView();
         imageView.setX(controller.getBackgroundObjects().get(i).getX());
        imageView.setY(controller.getBackgroundObjects().get(i).getY());
        imageView.setFitHeight(controller.getBackgroundObjects().get(i).getHeight());
        imageView.setFitWidth(controller.getBackgroundObjects().get(i).getWidth());
         imageView.setImage(imageLoader.getImg_cactus());

         pane.getChildren().add(imageView);
      }

   }

   /**
    * draw a single, not moving ground line over the whole pane width (at groundlvl)
    * add this ground rectangle to pane
    */
   private void drawGround() {

      Rectangle rectangle = new Rectangle();
      rectangle.relocate(controller.getGround().getX(), controller.getGround().getY());
      rectangle.setWidth(controller.getGround().getWidth());
      rectangle.setHeight(controller.getGround().getHeight());

      pane.getChildren().addAll(rectangle);
   }

   /**
    * create a new Image with obstacle values from gamecontroller -> gamemodell
    * add obstacle Image to pane
    * <p>
    * will be called for every obstacle in obstacle array list
    */
   private void drawImageObstacles() {

      DropShadow dropShadow = new DropShadow(3,Color.BLACK);

      for (int i = 0; i < controller.gameModel.getObstacles().size(); i++) {

         ImageView iv_Obstacle = new ImageView();

         switch (controller.getObstacles().get(i).getType()){
            case 0:
               iv_Obstacle.setImage(imageLoader.getImg_obstacle());
               break;
            case 1:
               iv_Obstacle.setImage(imageLoader.getImg_obstacle());
               break;
            case 2:
               iv_Obstacle.setImage(imageLoader.getImg_obstacleBottom());
               break;
            case 3:
               iv_Obstacle.setImage(imageLoader.getImg_obstacleAdlerAM());
               break;
            case 4:
               iv_Obstacle.setImage(imageLoader.getImg_obstacleBottom());
               break;
         }

         iv_Obstacle.relocate(controller.getObstacles().get(i).getX(),
                 controller.getObstacles().get(i).getY());
         iv_Obstacle.setFitHeight(controller.getObstacles().get(i).getHeight());
         iv_Obstacle.setFitWidth(controller.getObstacles().get(i).getWidth());

         iv_Obstacle.setEffect(dropShadow);

         pane.getChildren().addAll(iv_Obstacle);
      }
   }

   /**
    * create a new rectangle with obstacle values from gamecontroller -> gamemodell
    * add obstacle rectangle to pane
    * <p>
    * will be called for every obstacle in obstacle array list
    */
   private void drawObstacles() {

      for (int i = 0; i < controller.gameModel.getObstacles().size(); i++) {

         rectangle = new Rectangle(
                 controller.getObstacles().get(i).getX(),
                 controller.getObstacles().get(i).getY(),
                 controller.getObstacles().get(i).getWidth(),
                 controller.getObstacles().get(i).getHeight());

         pane.getChildren().addAll(rectangle);
      }
   }

   /**
    * create a new Image with the players values from gamecontroller -> gamemodel
    * add player Image to pane
    */
   private void drawImagePlayer() {

      DropShadow dropShadow = new DropShadow(7,Color.BLACK);

      if (controller.getPlayer().getCrouching()) {

         iV_player.setImage(imageLoader.getImg_playerCrouched());

      } else {

         if(controller.getPlayer().getJumping()){

            iV_player.setImage(imageLoader.getImg_jump());

         }else{

            switch (playerWalkImage){
               case 1:
                  iV_player.setImage(imageLoader.getImg_walking1());
                  break;
               case 2:
                  iV_player.setImage(imageLoader.getImg_walking2());
                  break;
               case 3:
                  iV_player.setImage(imageLoader.getImg_walking3());
                  break;
               case 4:
                  iV_player.setImage(imageLoader.getImg_walking4());
                  break;
            }
         }
      }

      iV_player.setRotate(controller.getPlayer().getRotation());
      iV_player.setFitWidth(controller.getPlayer().getWidth());
      iV_player.setFitHeight(controller.getPlayer().getHeight());
      iV_player.relocate(controller.getPlayer().getX(), controller.getPlayer().getY());

      iV_player.setEffect(dropShadow);

      pane.getChildren().addAll(iV_player);

   }

   /**
    * create a new rectangle with the players values from gamecontroller -> gamemodel
    * add player rectangle to pane
    */
   private void drawPlayer() {

      rectangle = new Rectangle(
              controller.getPlayer().getX(),
              controller.getPlayer().getY(),
              controller.getPlayer().getWidth(),
              controller.getPlayer().getHeight());

      rectangle.setRotate(controller.getPlayer().getRotation());
      rectangle.setFill(controller.getPlayer().getColor());
      rectangle.setStroke(Color.BLACK);
      rectangle.setStrokeWidth(2);
      rectangle.setStrokeLineCap(StrokeLineCap.ROUND);

      pane.getChildren().add(rectangle);

   }

   private void drawImagePowerup(){

      for (int i = 0; i < controller.gameModel.getPowerups().size(); i++) {

         ImageView iv_powerup = new ImageView();
         iv_powerup.setImage(imageLoader.getImg_powerupTaco());
         iv_powerup.relocate(controller.getPowerups().get(i).getX(),
                 controller.getPowerups().get(i).getY());
         iv_powerup.setFitHeight(controller.getPowerups().get(i).getHeight());
         iv_powerup.setFitWidth(controller.getPowerups().get(i).getWidth());

         pane.getChildren().addAll(iv_powerup);

         Circle circle = new Circle(controller.getPowerups().get(i).getX()+controller.getPowerups().get(i).getWidth()/2,
                 controller.getPowerups().get(i).getY()+controller.getPowerups().get(i).getHeight()/2,
                 controller.getPowerups().get(i).getHeight()/2);

         circle.setFill(Color.TRANSPARENT);
         circle.setStroke(Color.rgb(255,0,0,0.4));
         circle.setStrokeWidth(2);

         DropShadow dropShadow = new DropShadow(2,Color.BLACK);
         circle.setEffect(dropShadow);

         pane.getChildren().add(circle);
      }

   }

   private void drawPowerup(){

      for (int i = 0; i < controller.getPowerups().size(); i++) {

         rectangle = new Rectangle(
                 controller.getPowerups().get(i).getX(),
                 controller.getPowerups().get(i).getY(),
                 controller.getPowerups().get(i).getWidth(),
                 controller.getPowerups().get(i).getHeight());

         rectangle.setFill(controller.getPowerups().get(i).getColor());

         pane.getChildren().add(rectangle);

      }
   }

   private void drawImagePlatform(){

      for (int i = 0; i < controller.getPlatforms().size(); i++) {

         ImageView imageView = new ImageView();
         imageView.setImage(imageLoader.getImg_platform0());

         imageView.setX(controller.getPlatforms().get(i).getX());
         imageView.setY(controller.getPlatforms().get(i).getY());
         imageView.setFitWidth(controller.getPlatforms().get(i).getWidth());
         imageView.setFitHeight(controller.getPlatforms().get(i).getHeight());

         DropShadow ds = new DropShadow( 3, Color.BROWN);

         imageView.setEffect(ds);

         pane.getChildren().add(imageView);

      }

   }

   private void drawPlatform(){


      for (int i = 0; i < controller.getPlatforms().size(); i++) {

         rectangle = new Rectangle(
                 controller.getPlatforms().get(i).getX(),
                 controller.getPlatforms().get(i).getY(),
                 controller.getPlatforms().get(i).getWidth(),
                 controller.getPlatforms().get(i).getHeight());

         rectangle.setFill(controller.getPlatforms().get(i).getColor());

         pane.getChildren().add(rectangle);

      }

   }

   /**
    * Create a new Label, set Style and Position on Pane
    * get Score from GameController -> gamemodel and wirte score inside it
    * add this score label to pane
    */
   private void drawScoreLabel() {

      Label label = new Label();
      label.setText("Score: " + controller.getScore());
      label.setFont(customFont);

      FontLoader fontLoader = Toolkit.getToolkit().getFontLoader();
      float labelWidth = fontLoader.computeStringWidth(label.getText(), label.getFont());

      label.setLayoutX((pane.getWidth() / 2) - labelWidth / 2);
      label.setLayoutY(pane.getHeight() / 7);

      DropShadow dropShadow = new DropShadow(2,Color.BLACK);

      label.setEffect(dropShadow);

      pane.getChildren().add(label);
   }

   /**
    * Show Hints how to move the player with the Keyboard to the User
    * <p>
    * will automatically be destroyed on first update
    */
   private void showHints() {

      Platform.runLater(new Runnable() {
         @Override
         public void run() {

            lbl_hints.setText("Press Space to Jump \n Press Strg to crouch \n Press Shift to Trickjump ");
            lbl_hints.setFont(customFontLoader.load(R.fontPixel, 80));

            FontLoader fontLoader = Toolkit.getToolkit().getFontLoader();
            float btn_hintsWidth = fontLoader.computeStringWidth(lbl_hints.getText(), lbl_hints.getFont());

            lbl_hints.setLayoutX((pane.getWidth() / 2) - btn_hintsWidth / 2 / 3);
            lbl_hints.setLayoutY(pane.getHeight() / 2);

            pane.getChildren().addAll(lbl_hints);
         }
      });
   }

   public void init(Boolean gameObjectsAsImages) {
      this.gameObjectsAsImages = gameObjectsAsImages;
   }

   public void nextPlayerImage(){
      playerWalkImage+=1;
      if(playerWalkImage==5){
         playerWalkImage=1;
      }
   }
}