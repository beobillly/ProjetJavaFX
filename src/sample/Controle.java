package sample;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class Controle extends Controller {

    @FXML
    public Label label_center;
    @FXML
    public Label label_top;
    @FXML
    public Label label_right;
    @FXML
    public Label label_left;
    @FXML
    public Label label_bottom;
    @FXML
    public TextField text_top;
    @FXML
    public Pane pane_top;
    @FXML
    private javafx.scene.control.Button replayButton;
    @FXML
    private javafx.scene.control.Button quitButton;
    AnimationTimer animationTimer;
    @FXML
    private HBox hbox_top;

    public void initialize() {

        label_bottom.setText("bottom");
        label_center.setText("center");
        label_top.setText("top");
        label_left.setText("left");
        label_right.setText("right");
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                pane_top.getParent().getParent().getParent().setOnKeyPressed(event -> {
                    KeyCode kc = event.getCode();
                    System.out.println("blebleblebleble");
                });
            }
        };
    }

    public void quitterLeJeu(ActionEvent actionEvent) {

        try {
            Stage stage = (Stage) replayButton.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void mouse_top(MouseEvent mouseEvent) {

        label_top.setText("Veuillez presser une touche");
        pane_top.setOnKeyPressed(event -> {
            KeyCode kc = event.getCode();
            label_top.setText("blebleblebleble");
        });
    }

    public void key_top(KeyEvent keyEvent) {
        label_top.setText("blebleblebleble");

    }
}
