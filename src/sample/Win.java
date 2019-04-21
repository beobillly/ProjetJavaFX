package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.stage.Stage;


public class Win extends Controller {

    @FXML
    public Label label_win;
    @FXML
    private javafx.scene.control.Button replayButton;
    @FXML
    private javafx.scene.control.Button quitButton;

    public void initialize() {

        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                label_win.setText(
                        "Vous avez gagné !\n"
                );
            }
        };
        animationTimer.start();
    }

    public void rejouer(ActionEvent actionEvent) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) replayButton.getScene().getWindow();
            Controller controller = loader.getController();
            controller.animationTimer.start();
            stage.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void quitterLeJeu(ActionEvent actionEvent) {

        Platform.exit();
    }

}
