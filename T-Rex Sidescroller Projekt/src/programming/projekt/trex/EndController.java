package programming.projekt.trex;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;

import java.io.IOException;

/**
 * Created by janek on 25.02.2017.
 */
public class EndController {

    int score;

    @FXML
    Label lbl_endscore;
    @FXML
    Label lbl_gameover;

    EndView endView;

    public EndController() {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                lbl_endscore.setText("Endscore: " + score);
                Font customFont = Helper.loadFont();
                lbl_gameover.setFont(customFont);
                lbl_endscore.setFont(customFont);
            }
        });

    }

    public void keyEventHandler(KeyEvent event) throws IOException {
        if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.SPACE) {
            new GameView();
        }
        if (event.getCode() == KeyCode.ESCAPE) {
            new MenuView();
        }

    }

    public void setView(EndView view) {
        this.endView = view;
    }

    public void setScore(int score) {
        this.score = score;
    }
}