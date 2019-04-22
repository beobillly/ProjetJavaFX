package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;


public class Lose extends Controller {

    @FXML
    public Label label_lose;
    @FXML
    private javafx.scene.control.Button replayButton;
    @FXML
    private javafx.scene.control.Button quitButton;

    public void initialize() {
        label_lose.setText(
                "Vous avez perdu !\n"
        );
    }

    public void rejouer(ActionEvent actionEvent) {

        try {
            Stage stage = (Stage) replayButton.getScene().getWindow();
            stage.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void quitterLeJeu(ActionEvent actionEvent) {

        Platform.exit();
    }

}
