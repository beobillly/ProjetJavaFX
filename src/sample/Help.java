package sample;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;


public class Help extends Controller {

    @FXML
    public Label label_help;

    public void initialize() {

        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                label_help.setText(
                        "Le jeu du pistolero consiste a controler un personnage et a tirer sur des demons\n" +
                                "Deplacez vous avec Z-Q-S-D (resp HAUT GAUCHE BAS DROITE)\n" +
                                "Tirer avec F sur les ennemis\n" +
                                "Les touches peuvent etre changées dans le menu en cliquant sur : Changer les controles\n" +
                                "Vous pouvez modifier la vitesse du jeu en scrollant la bar en bas de la fenetre\n" +
                                "Ne laissez pas les demons vous manger\n" +
                                "Le jeu se termine quand vous avez tué tous les demons ou lorsque vous vous etes fait manger\n" +
                                "Différentes options sont disponibles dans le menu n'hésitez pas à les utiliser !\n"
                );
            }
        };
        animationTimer.start();
    }

    public void fermerHelp(ActionEvent actionEvent) {
        fenetre_help.close();
    }
}
