package sample;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;

import java.net.URL;
import java.util.Random;

public class Controller {
    @FXML
    Slider slider;
    @FXML
    Pane pane;

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
        SingleParticle[] particles = new SingleParticle[nb];

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
        for (int i = 0; i < particles.length; i++) {


            //utiliser soit l'image d'un hibou soit
            //l'image d'un chat
            Image image = ((i & 1) == 1) ? owl : cat;
            double px = random.nextDouble() * (pane.getPrefWidth() - image.getWidth());
            double py = random.nextDouble() * (pane.getPrefHeight() - image.getHeight());

            double vx = 8 * (random.nextDouble() - 0.5);
            double vy = 8 * (random.nextDouble() - 0.5);

            //particles[i] = new SingleParticle(image, px, py,
            //        vx, vy,audioClip, 0.0, 1.0);

            particles[i] = new DemonParticle(image, px, py,
                    vx, vy, audioClip, 0.0, 1.0, 0.0, 0.0);
            pane.getChildren().add(particles[i]);
        }

        CowboyParticle cowboy = new CowboyParticle(cat, pane.getPrefWidth() / 2, pane.getPrefHeight() / 2, 0, 0, audioClip, 0, 1, 0, 0);
        pane.getChildren().add(cowboy);


        /* AnimationTimer :  implementation de la méthode
         * handle(long now).  On applique juste la méthode
         * move() à chaque particule                    */
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                for (SingleParticle p : particles) {
                    p.move();
                }
                cowboy.move();

                pane.getParent().setOnKeyPressed(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {
                        switch (event.getCode()) {
                            case UP:
                                cowboy.set_VY(-2);
                                break;
                            case DOWN:
                                cowboy.set_VY(2);
                                break;
                            case LEFT:
                                cowboy.set_VX(-2);
                                break;
                            case RIGHT:
                                cowboy.set_VX(2);
                                break;
                            //case SHIFT: running = true; break;
                        }

                        cowboy.faireRotation();
                    }
                });

                pane.getParent().setOnKeyReleased(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {
                        switch (event.getCode()) {
                            case UP:
                                cowboy.set_VY(0);
                                break;
                            case DOWN:
                                cowboy.set_VY(0);
                                break;
                            case LEFT:
                                cowboy.set_VX(0);
                                break;
                            case RIGHT:
                                cowboy.set_VX(0);
                                break;
                            //case SHIFT: running = false; break;
                        }


                        cowboy.faireRotation();
                    }
                });

                 /*pour chaque couple de particules
                verifier si les deux particules chevauchent.
                Si c'est le cas changer l'image affichée
                dans les deux particules.
                */

                for (int i = 0; i < particles.length - 1; i++) {
                    for (int j = i + 1; j < particles.length; j++) {

                        if (particles[i].getBoundsInParent()
                                .intersects(particles[j].getBoundsInParent())) {

                            particles[i].setImage(mite);
                            particles[i].set_AX(0.0);
                            particles[i].set_AY(-1.0);
                            particles[i].faireRotation();

                            particles[j].setImage(mite);
                            particles[j].set_AX(0.0);
                            particles[j].set_AY(-1.0);
                            particles[j].faireRotation();

                        }
                    }
                }
            }
        };

        //bind le coeff de vitesse rate de chaque particule
        //à la valeur de slider
        for (SingleParticle p : particles) {
            p.rateProperty().bind(slider.valueProperty()
                    .multiply(1 / 0.3));
        }

        timer.start();


    }


}
