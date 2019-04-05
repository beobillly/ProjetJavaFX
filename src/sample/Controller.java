package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;

import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class Controller {

    @FXML
    public Label label;
    @FXML
    public Label label_Vie;
    @FXML
    public Label label_Munitions;
    @FXML
    public Label label_Ultimate;
    @FXML
    public Label label_Ennemis;
    @FXML
    public Label label_Ennemis_Mort;
    @FXML
    public Label label_Direction;
    @FXML
    Button button;
    @FXML
    ListView<String> listb;
    @FXML
    Slider slider;
    @FXML
    Pane pane;

    private AnimationTimer animationTimer;
    private Integer ennemis_Mort = 0;
    private CowboyParticle cowboy;
    private ArrayList<SingleParticle> particles;


    @FXML
    public void initialize() {

        int nb = 8; //nombre de particules
//nombre de particules, soit 8 soit donné en ligne de commande
        /*
        if (getParameters().getRaw().isEmpty()) {
            nb = 8;
        } else {
            nb = Integer.parseInt(getParameters().getRaw().get(0));
        }
*/
        particles = new ArrayList<SingleParticle>();

        Random random = new Random(System.nanoTime());


        //créer les particules avex les images
        URL url = getClass().getResource("ressources/cat.png");
        Image cat = new Image(url.toString());
        url = getClass().getResource("ressources/owl.png");
        Image owl = new Image(url.toString());
        url = getClass().getResource("ressources/Mite-48.png");
        Image mite = new Image(url.toString());

        AudioClip audioClip = new AudioClip(getClass()
                .getResource("ressources/1967.wav").toString());

//creer les particules
        for (int i = 0; i < nb; i++) {


            //utiliser soit l'image d'un hibou soit
            //l'image d'un chat
            Image image = ((i & 1) == 1) ? owl : cat;
            double px = random.nextDouble() * (pane.getPrefWidth() - image.getWidth());
            double py = random.nextDouble() * (pane.getPrefHeight() - image.getHeight());

            double vx = 8 * (random.nextDouble() - 0.5);
            double vy = 8 * (random.nextDouble() - 0.5);
            double genre = random.nextDouble() * 100;
            //particles[i] = new SingleParticle(image, px, py,
            //        vx, vy,audioClip, 0.0, 1.0);

            particles.add(new DemonParticle(image, px, py,
                    vx, vy, audioClip, 0.0, 1.0, 0.0, 0.0, genre));
            pane.getChildren().add(particles.get(i));
        }

        cowboy = new CowboyParticle(cat, pane.getPrefWidth() / 2, pane.getPrefHeight() / 2, 0, 0, audioClip, 0, 1, 0, 0, 10);

        pane.getChildren().add(cowboy);
        /* AnimationTimer :  implementation de la méthode
         * handle(long now).  On applique juste la méthode
         * move() à chaque particule                    */
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                for (SingleParticle p : particles) {
                    if (p != null)
                        p.move();
                }
                cowboy.move();
                pane.getParent().getParent().getParent().setOnKeyPressed(event -> {
                    switch (event.getCode()) {
                        case Z:
                            cowboy.set_VY(-2);
                            break;
                        case S:
                            cowboy.set_VY(2);
                            break;
                        case Q:
                            cowboy.set_VX(-2);
                            break;
                        case D:
                            cowboy.set_VX(2);
                            break;
                        //case SHIFT: running = true; break;
                    }

                    cowboy.faireRotation();
                });

                pane.getParent().getParent().getParent().setOnKeyReleased(event -> {
                    switch (event.getCode()) {
                        case Z:
                            cowboy.set_VY(0);
                            break;
                        case S:
                            cowboy.set_VY(0);
                            break;
                        case Q:
                            cowboy.set_VX(0);
                            break;
                        case D:
                            cowboy.set_VX(0);
                            break;
                        //case SHIFT: running = false; break;
                    }


                    cowboy.faireRotation();
                });

                /*pour chaque couple de particules
                verifier si les deux particules chevauchent.
                Si c'est le cas changer l'image affichée
                dans les deux particules.
                */

                for (int i = 0; i < particles.size() - 1; i++) {
                    for (int j = i + 1; j < particles.size(); j++) {

                        if (particles.get(i).getBoundsInParent()
                                .intersects(particles.get(j).getBoundsInParent())) {
                            if (particles.get(i).get_Genre() < 50 && particles.get(j).get_Genre() < 50) {
                                particles.get(j).setImage(null);
                                particles.remove(j);
                                j--;
                                ennemis_Mort++;
                                cowboy.set_Ultimate(cowboy.get_Ultimate() + 5);
                            } else if ((particles.get(i).get_Genre() < 50 && particles.get(j).get_Genre() > 50) || (particles.get(i).get_Genre() > 50 && particles.get(j).get_Genre() < 50)) {
                                Image image = ((i & 1) == 1) ? owl : cat;
                                double px = random.nextDouble() * (pane.getPrefWidth() - image.getWidth());
                                double py = random.nextDouble() * (pane.getPrefHeight() - image.getHeight());

                                double vx = 8 * (random.nextDouble() - 0.5);
                                double vy = 8 * (random.nextDouble() - 0.5);
                                double genre = random.nextDouble() % 100;
                                particles.add(new DemonParticle(image, px, py,
                                        vx, vy, audioClip, 0.0, 1.0, 0.0, 0.0, genre));

                                pane.getChildren().add(particles.get(particles.size() - 1));
                                particles.get(i).set_VX(-particles.get(i).get_VX());
                                particles.get(i).set_VY(-particles.get(i).get_VY());
                                particles.get(i).faireRotation();

                                particles.get(j).set_VX(-particles.get(j).get_VX());
                                particles.get(j).set_VY(-particles.get(j).get_VY());
                                particles.get(j).faireRotation();

                            }
                        }


                    }
                }

                label_Vie.setText("Vies restantes : " + cowboy.get_Vie());
                label_Munitions.setText("Munitions restantes : " + cowboy.get_Munitions());
                label_Ultimate.setText("Ultimate chargé : " + cowboy.get_Ultimate());
                label_Ennemis.setText("Ennemis restants : " + particles.size());
//                label_Ennemis_Mort.setText("Ennemis morts : " + ennemis_Mort);
                label_Direction.setText("Angle : " + cowboy.get_Degre());
            }
        };

        //bind le coeff de vitesse rate de chaque particule
        //à la valeur de slider
        for (SingleParticle p : particles) {
            p.rateProperty().bind(slider.valueProperty()
                    .multiply(1 / 0.3));
        }

        animationTimer.start();


    }


    public void quitter(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void itsHighNoooooon(ActionEvent actionEvent) {
        if (cowboy.get_Ultimate() >= 100) {
            animationTimer.stop();
            double cowboy_px = cowboy.getX();
            double cowboy_py = cowboy.getY();
            double cowboy_degree = cowboy.get_Degre();
            /*0 cest le haut*/
            /*90 cest a gauche */
            /*180 cest en bas*/
            /*270 cest a droite*/

            for (int i = 0; i < particles.size(); i++) {
                if (cowboy_degree == 0) {
                    if (particles.get(i).getY() < cowboy_py) {
                        particles.get(i).setImage(null);
                        particles.remove(particles.get(i));
                        i--;
                        ennemis_Mort++;
                    }
                } else if (cowboy_degree == 180) {
                    if (particles.get(i).getY() > cowboy_py) {
                        particles.get(i).setImage(null);
                        particles.remove(particles.get(i));
                        ennemis_Mort++;
                    }
                } else if (cowboy_degree == 90) {
                    if (particles.get(i).getX() < cowboy_px) {
                        particles.get(i).setImage(null);
                        particles.remove(particles.get(i));
                        ennemis_Mort++;
                    }
                } else if (cowboy_degree == 270) {
                    if (particles.get(i).getX() > cowboy_px) {
                        particles.get(i).setImage(null);
                        particles.remove(particles.get(i));
                        ennemis_Mort++;
                    }
                } else {

                }
            }
            cowboy.set_Ultimate(0);
            animationTimer.start();
        }
    }

    public void pause(ActionEvent actionEvent) {
        animationTimer.stop();
    }

    public void help(ActionEvent actionEvent) {
    }

    public void jouer(ActionEvent actionEvent) {
        animationTimer.start();
    }
}

