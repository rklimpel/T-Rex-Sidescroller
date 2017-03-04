package main.java.cau.project.services;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import main.java.cau.project.R;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Rico on 04.03.2017.
 */
public class LighthouseService {

   LighthouseNetwork lighthouseNetwork = new LighthouseNetwork();
   Color[][] lastPixels;

   public LighthouseService() {

   }

   public Boolean connect(){
      try {
         lighthouseNetwork.connect();
      } catch (IOException e) {
         return false;
      }
      return true;
   }


   public Boolean sendPixelsToLighthouse(Color[][] pixels){

      lastPixels = pixels;

      try {
         lighthouseNetwork.send(convertToByteArray(pixels));
      } catch (IOException e) {
         e.printStackTrace();
         return false;
      }
      return true;

   }

   int x = 0;
   int y = 0;

   public void lighthouseEnd(){

      try {
         lighthouseNetwork.connect();
      } catch (IOException e) {
         e.printStackTrace();
      }

      Color[][] color = lastPixels;

      Timer timer = new Timer();
      TimerTask task = new TimerTask() {
         public void run() {

            color[y][x] = Color.DARKRED;

            x+=1;

            if(x == R.lighthouseWidth){
               y+=1;
               x=0;
            }

            if(y == R.lighthouseHeight && x == R.lighthouseWidth){
               timer.purge();
            }

            try {
               lighthouseNetwork.send(convertToByteArray(color));
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      };
      timer.scheduleAtFixedRate(task, 0, 5);

      x = 0;
      y = 0;

   }


   public byte[] convertToByteArray(Color[][] pixelColors) {

      byte[] returnBytes = new byte[1176];

      int color = 0;
      int positionY = 0;
      int positionX = 0;

      for (int i = 0; i < returnBytes.length; i++) {

         if (color == 0) {
            returnBytes[i] = (byte) ((int) (pixelColors[positionY][positionX].getRed() * 127));
         } else if (color == 1) {
            returnBytes[i] = (byte) ((int) (pixelColors[positionY][positionX].getGreen() * 127));
         } else if (color == 2) {
            returnBytes[i] = (byte) ((int) (pixelColors[positionY][positionX].getBlue() * 127));
         }

         color += 1;

         if (color == 3) {

            color = 0;

            positionX += 1;
            if (positionX >= R.lighthouseWidth) {
               positionX = 0;
               positionY += 1;
            }
         }

         returnBytes[i] *= 2;
      }

      return returnBytes;
   }


   public static byte[] convertLighthouseImage() {

      Image image = new Image("file:src/main/res/assets/mexiko/player_mexiko.png",
              R.lighthouseWidth, R.lighthouseHeight, false, true);

      Color[][] pixelColors = new Color[R.lighthouseHeight][R.lighthouseWidth];

      for (int i = 0; i < pixelColors.length; i++) {
         for (int j = 0; j < pixelColors[0].length; j++) {
            pixelColors[i][j] = image.getPixelReader().getColor(j, i);
         }
      }

      byte[] returnBytes = new byte[1176];

      int color = 0;
      int positionY = 0;
      int positionX = 0;

      for (int i = 0; i < returnBytes.length; i++) {

         if (color == 0) {
            returnBytes[i] = (byte) ((int) (pixelColors[positionY][positionX].getRed() * 127));
         } else if (color == 1) {
            returnBytes[i] = (byte) ((int) (pixelColors[positionY][positionX].getGreen() * 127));
         } else if (color == 2) {
            returnBytes[i] = (byte) ((int) (pixelColors[positionY][positionX].getBlue() * 127));
         }

         System.out.println((int) returnBytes[i]);

         color += 1;

         if (color == 3) {

            color = 0;

            positionX += 1;
            if (positionX >= R.lighthouseWidth) {
               positionX = 0;
               positionY += 1;
            }
         }

         returnBytes[i] *= 2;
      }

      return returnBytes;
   }

}