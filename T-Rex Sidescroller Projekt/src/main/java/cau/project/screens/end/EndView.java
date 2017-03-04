package main.java.cau.project.screens.end;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import main.java.cau.project.*;
import main.java.cau.project.services.LighthouseNetwork;
import main.java.cau.project.services.loader.CustomFontLoader;
import main.java.cau.project.services.Helper;
import main.java.cau.project.services.listeners.KeyboardListener;
import main.java.cau.project.services.SceneSwitcher;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class EndView extends View{

   int score;

   @FXML
   Label lbl_endscore;
   @FXML
   Label lbl_gameover;
   @FXML
   GridPane background;
   @FXML
   Label lbl_restart;
   @FXML
   Button btn_restart;

   CustomFontLoader customFontLoader = new CustomFontLoader();

   public EndView() {

      view = this;
      setViewID(R.viewIdEnd);

      score = Helper.score;

      Platform.runLater(new Runnable() {
         @Override
         public void run() {

            new KeyboardListener(view);

            setScoreLabel();
            setGameoverLabel();
            setRestartButton();

            background.setStyle("-fx-background-color: #000000;");
         }
      });

      if(Main.getMainView().getViewID()==R.viewIdGameLighthouse){
         Main.getLighthouseService().lighthouseEnd();
      }

   }


   private void setRestartButton() {
      btn_restart.setStyle("-fx-text-fill: grey; -fx-base: black;");
   }

   private void setGameoverLabel() {
      lbl_gameover.setFont(customFontLoader.load(R.fontPixel,100));
      lbl_gameover.setTextFill(Color.WHITE);
   }

   private void setScoreLabel() {
      lbl_endscore.setText("Endscore: " + score);
      lbl_endscore.setFont(customFontLoader.load(R.fontPixel,100));
      lbl_endscore.setTextFill(Color.WHITE);
   }

   public void onButtonClicked() throws IOException {
      SceneSwitcher.GAME_DESKTOP.load();
   }

   public void restartGame() {
      if(Main.getMainView().getViewID()==R.viewIdSplit){
         SceneSwitcher.GAME_DOUBLE.load();
      }else if(Main.getMainView().getViewID()==R.viewIdGameDesktop){
         SceneSwitcher.GAME_DESKTOP.load();
      }else if(Main.getMainView().getViewID()==R.viewIdGameLighthouse){
         SceneSwitcher.GAME_LH.load();
      }

   }

   public void exit(String nextViewID){
      SceneSwitcher.MENU.load();
   }




}