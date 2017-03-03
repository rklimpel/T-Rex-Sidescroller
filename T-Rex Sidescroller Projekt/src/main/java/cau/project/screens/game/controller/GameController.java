package main.java.cau.project.screens.game.controller;

import main.java.cau.project.R;
import main.java.cau.project.services.Helper;
import main.java.cau.project.Main;
import main.java.cau.project.View;
import main.java.cau.project.screens.game.model.GameModel;
import main.java.cau.project.screens.game.model.Obstacle;
import main.java.cau.project.screens.game.model.Player;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class GameController {

   private ArrayList<View> listeningViews = new ArrayList<>();

   public GameModel gameModel;
   Timer updateTimer;

   /**
    * Constructor adds calling view to Listener List and creates a new GameModel with default Size
    * if no gameModle instanz exists
    *
    * @param newListener
    */
   public GameController(View newListener) {
      this(newListener,600,300);
   }

   /**
    * Constructor adds calling view to listener List and creates a new GameModel with views requierments
    * if no gameModel Instanz exists
    *
    * @param newListener
    * @param paneWidth
    * @param paneHeight
    */
   public GameController(View newListener, int paneWidth, int paneHeight) {
      this.listeningViews.add(newListener);

      if (this.gameModel == null) {
         this.gameModel = new GameModel(this, paneWidth, paneHeight);
      }
   }

   private void startUpdater() {
      updateTimer = new Timer();
      TimerTask task = new TimerTask() {
         public void run() {
            update();
         }
      };
      updateTimer.scheduleAtFixedRate(task, 0, 10);
   }

   public void stopUpdater(){
      updateTimer.cancel();
      updateTimer.purge();
   }

   public void addListener(View newListener) {
      this.listeningViews.add(newListener);
   }

   /**
    * Update, Called by gamemodel on gametick
    * updates all listening views
    */
   public void update() {
      for (int i = 0; i < listeningViews.size(); i++) {
         listeningViews.get(i).Update();
      }
   }

   /**
    * one of the views called jump
    *
    * @param view
    */
   public void jump(View view) {

      //IF Game is not over but it is not Running start the game
      if (! gameModel.gameTimerEnabled && ! gameModel.gameOver) {
         gameModel.createPlayer();
         gameModel.startGameTimer();
         startUpdater();
      }

      //IF the Game is over (collision) space switches to menu
      else if (! gameModel.gameTimerEnabled && gameModel.gameOver) {
         quitGame(view);
      }

      //else space is there to jump
      else if (gameModel.gameTimerEnabled) {
         gameModel.jump();
      }
   }

   /**
    * one of the views called crouch
    */
   public void crouch() {
      gameModel.player.crouch();
   }

   /**
    * one of the views called crouch end
    */
   public void crouchEnd() {
      gameModel.player.crouchEnd();
   }

   /**
    * one of the views called quit game
    * set Score to Helper variable to access it from EndView
    * if there is no listening view left determinate game Controller and stop game models gameTimer
    * <p>
    * send exit request to the view that called quitGame
    *
    * @param view
    */
   public void quitGame(View view) {

      if (gameModel.gameOver) Helper.score = gameModel.getScore();

      for (int i = 0; i < listeningViews.size(); i++) {
         if (gameModel.gameOver) {
            view.exit(R.viewIdEnd);
         } else {
            view.exit(R.viewIdMenu);
         }
      }
      gameModel.stopGameTimer();

      listeningViews = null;
      gameModel = null;
      Main.gameController = null;

      stopUpdater();

   }

   /**
    * Gets Obstacles from Gamemodel
    *
    * @return
    */
   public ArrayList<Obstacle> getObstacles() {
      return gameModel.getObstacles();
   }

   /**
    * Gets player from gamemodel
    *
    * @return
    */
   public Player getPlayer() {
      return gameModel.getPlayer();
   }

   /**
    * Gets Score from gamemodel
    *
    * @return
    */
   public int getScore() {
      return gameModel.getScore();
   }


   public int getModelPaneWidth() {
      return gameModel.getPaneWidth();
   }

   public int getModelPaneHeight() {
      return gameModel.getPaneHeight();
   }
}