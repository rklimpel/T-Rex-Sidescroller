package main.java.cau.project.screens.game.view.lh;


import javafx.application.Platform;
import javafx.scene.paint.Color;
import main.java.cau.project.Main;
import main.java.cau.project.R;
import main.java.cau.project.screens.game.model.GameModel;
import main.java.cau.project.screens.game.view.GameView;
import main.java.cau.project.services.Helper;
import main.java.cau.project.services.LighthouseService;
import main.java.cau.project.services.listeners.KeyboardListener;
import main.java.cau.project.services.LighthouseNetwork;
import main.java.cau.project.services.listeners.MouseListener;

import java.io.IOException;

public class LhView extends GameView{

    private Color[][] pixelsReduced;

    private double scaleFactorWidth;
    private double scaleFactorHeight;

    private LighthouseService lighthouseService;

    public LhView() {

        view = this;
        view.setViewID(R.viewIdGameLighthouse);

        Main.setMainView(this);

        super.setController();

        pixelsReduced = new Color[R.lighthouseHeight][R.lighthouseWidth];

        scaleFactorWidth = controller.getModelPaneWidth()/R.lighthouseWidth;
        scaleFactorHeight = controller.getModelPaneHeight()/R.lighthouseHeight;

        System.out.println("scaleFactorWidth: "+scaleFactorWidth);
        System.out.println("scaleFactorHeihgt: "+scaleFactorHeight);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                new KeyboardListener(view);
                new MouseListener(view);
            }
        });

        lighthouseService = Main.getLighthouseService();
    }

    @Override
    public void Update() {

        super.calcAndShowFPS();

        if(pixelsReduced!=null
                && controller.gameModel.player != null
                && controller.gameModel.getObstacles() != null){

            lighthouseService.sendPixelsToLighthouse(loadGamemodelData(controller.gameModel, pixelsReduced));

        }

    }

    public Color[][] loadGamemodelData(GameModel gameModel, Color[][] pixelsReduced){

        //Make All Pixels Black
        for (int i = 0; i < pixelsReduced.length; i++) {

            for (int j = 0; j < pixelsReduced[0].length; j++) {

                pixelsReduced[i][j] = Color.BLACK;

            }
        }

        //Paint Ground pixels
        for (int i = scaleY(gameModel.getGround().getY());
             i < scaleY(gameModel.getGround().getY())+1; i++) {

            for (int j = scaleX(gameModel.getGround().getX());
                 j < scaleX(gameModel.getGround().getX()+gameModel.getGround().getWidth()); j++) {

                try {
                    pixelsReduced[i][j] = Color.GREEN;
                }catch (ArrayIndexOutOfBoundsException e){
                    //Ground out of Lighthouse Bounds...
                }
            }
        }

        //Paint obstacle Pixels
        for (int i = 0; i < gameModel.getObstacles().size(); i++) {

            for (int j = scaleY(gameModel.getObstacles().get(i).getY());
                 j < scaleY(gameModel.getObstacles().get(i).getY()
                         +gameModel.getObstacles().get(i).getHeight()); j++) {

                for (int k = scaleX(gameModel.getObstacles().get(i).getX());
                     k < scaleX(gameModel.getObstacles().get(i).getX()
                             +gameModel.getObstacles().get(i).getWidth()); k++) {

                    if(!(j>pixelsReduced.length||k>=pixelsReduced[0].length||j<0||k<0)){
                        pixelsReduced[j][k]=Color.BLUE;
                    }
                }
            }
        }

        //Paint PlayerPixels Red
        for (int i = scaleY(gameModel.player.getY());
             i < scaleY(gameModel.player.getY()+gameModel.player.getHeight()); i++) {

            for (int j = scaleX(gameModel.player.getX());
                 j < scaleX(gameModel.player.getX()+gameModel.player.getWidth()); j++) {

                //System.out.println("PlayerPixels: " + i + " / " + j);

                pixelsReduced[i][j] = Color.RED;
            }
        }

        return pixelsReduced;
    }



    private int scaleX(int value){
        int scaledValue = (int)Math.round(value/scaleFactorWidth);
        if(scaledValue>=R.lighthouseWidth){
            scaledValue=R.lighthouseWidth;
        }
        return scaledValue;
    }

    private int scaleY(int value){
        return (int)Math.round(value/scaleFactorHeight);
    }
}
