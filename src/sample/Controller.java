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

import java.io.*;
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

        int nb = 15; //nombre de particules ennemies
        final int nbObstacles = 5;//nombre d'obstacles
        double bulletSpeed = 10; //Vitesse des balles
        int nbEnnemisMax = 50; //nb d'ennemis maximal sur l'arene
        int nbBallesVivantes = 0; //nombre de balles sur l'arene
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


        //créer les particules obstacles avec les images
        URL url = getClass().getResource("ressources/squareObstacle.png");
        Image obstacleImage = new Image(url.toString());


        //creer les particules obstacles
        for (int i = 0; i < nbObstacles; i++) {

            double px = random.nextDouble() * (pane.getPrefWidth() - obstacleImage.getWidth());
            double py = random.nextDouble() * (pane.getPrefHeight() - obstacleImage.getHeight());

            double vx = 8 * (random.nextDouble() - 0.5);
            double vy = 8 * (random.nextDouble() - 0.5);

            ObstacleParticle obsPart = new ObstacleParticle(obstacleImage, px, py);
            particles.add(obsPart);
            pane.getChildren().add(obsPart);
        }

        //créer les particules ennemies avec les images
        url = getClass().getResource("ressources/cat.png");
        Image cat = new Image(url.toString());
        url = getClass().getResource("ressources/owl.png");
        Image owl = new Image(url.toString());
        url = getClass().getResource("ressources/Mite-48.png");
        Image mite = new Image(url.toString());

        AudioClip audioClip = new AudioClip(getClass()
                .getResource("ressources/1967.wav").toString());

        Image image = owl;

        //creer les particules
        for (int i = 0; i < nb; i++) {


            double px;
            double py;

            double vx;
            double vy;

            double genre;

            boolean pass;

            //On vérifie qu'on invoque pas le monstre dans un obstacle.
            do {

                pass = true;
                px = random.nextDouble() * (pane.getPrefWidth() - image.getWidth());
                py = random.nextDouble() * (pane.getPrefHeight() - image.getHeight());

                for (int n = 0; n < nbObstacles; n++) {
                    if (Math.abs(px - particles.get(n).getX()) < particles.get(n).getFitWidth() + image.getWidth()) {
                        pass = false;
                        break;
                    }
                    if (Math.abs(py - particles.get(n).getY()) < particles.get(n).getFitHeight() + image.getHeight()) {
                        pass = false;
                        break;
                    }
                }

            } while (!pass);


            vx = 8 * (random.nextDouble() - 0.5);
            vy = 8 * (random.nextDouble() - 0.5);
            genre = random.nextDouble() * 100;

            if (genre < 50) {
                image = cat;
            } else {
                image = owl;
            }

            DemonParticle dP = new DemonParticle(image, px, py,
                    vx, vy, audioClip, 0.0, 1.0, 0.0, 0.0, genre);
            particles.add(dP);
            pane.getChildren().add(dP);
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
                        case W:
                            cowboy.set_VY(-2);
                            break;
                        case S:
                            cowboy.set_VY(2);
                            break;
                        case A:
                            cowboy.set_VX(-2);
                            break;
                        case D:
                            cowboy.set_VX(2);
                            break;
                        case F:
                            URL url = getClass().getResource("ressources/Advanced_Sniper_Bullet-ConvertImage.png");
                            Image image = new Image(url.toString());
                            particles.add(new BulletParticle(image, cowboy.getX(), cowboy.getY(), bulletSpeed * Math.cos(Math.toRadians(cowboy.get_Degre() - 90)), bulletSpeed * Math.sin(Math.toRadians(cowboy.get_Degre() - 90)), null, 0.0, 1.0, 0.0, 0.0, -1));
                            pane.getChildren().add(particles.get(particles.size() - 1));
                    }

                    cowboy.faireRotation();
                });

                pane.getParent().getParent().getParent().setOnKeyReleased(event -> {
                    switch (event.getCode()) {
                        case W:
                            cowboy.set_VY(0);
                            break;
                        case S:
                            cowboy.set_VY(0);
                            break;
                        case A:
                            cowboy.set_VX(0);
                            break;
                        case D:
                            cowboy.set_VX(0);
                            break;
                        case F:

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

                            if (particles.get(i) instanceof ObstacleParticle) {
                                double diffX = Math.abs(particles.get(j).getX() - particles.get(i).getX());
                                double diffY = Math.abs(particles.get(j).getY() - particles.get(i).getY());

                                if (diffY >= diffX) {
                                    particles.get(j).set_VY(-particles.get(j).get_VY());
                                }

                                if (diffX >= diffY) {
                                    particles.get(j).set_VX(-particles.get(j).get_VX());
                                }

                                particles.get(j).faireRotation();
                                continue;
                            }

                            if (particles.get(j) instanceof ObstacleParticle) {

                                double diffX = Math.abs(particles.get(j).getX() - particles.get(i).getX());
                                double diffY = Math.abs(particles.get(j).getY() - particles.get(i).getY());

                                if (diffY >= diffX) {
                                    particles.get(i).set_VY(-particles.get(i).get_VY());
                                }

                                if (diffX >= diffY) {
                                    particles.get(i).set_VX(-particles.get(i).get_VX());
                                }

                                particles.get(i).faireRotation();
                                continue;
                            }

                            if (particles.get(i) instanceof BulletParticle) {
                                particles.get(j).setImage(null);
                                particles.remove(j);
                                j--;
                                ennemis_Mort++;
                                cowboy.set_Ultimate(cowboy.get_Ultimate() + 5);
                            }
                            if (particles.get(j) instanceof BulletParticle) {
                                particles.get(i).setImage(null);
                                particles.remove(i);
                                i--;
                                ennemis_Mort++;
                                cowboy.set_Ultimate(cowboy.get_Ultimate() + 5);

                            } else if (particles.get(i).get_Genre() < 50 && particles.get(j).get_Genre() < 50) {

                                particles.get(j).setImage(null);
                                particles.remove(j);
                                j--;
                                ennemis_Mort++;
                                cowboy.set_Ultimate(cowboy.get_Ultimate() + 5);

                            } else if (((particles.get(i).get_Genre() < 50 && particles.get(j).get_Genre() > 50) || (particles.get(i).get_Genre() > 50 && particles.get(j).get_Genre() < 50)) && (particles.size() - nbObstacles - nbBallesVivantes) < nbEnnemisMax) {

                                Image image = ((i & 1) == 1) ? owl : cat;
                                double px;
                                double py;

                                double vx;
                                double vy;

                                double genre;

                                boolean pass;

                                do {

                                    pass = true;
                                    px = random.nextDouble() * (pane.getPrefWidth() - image.getWidth());
                                    py = random.nextDouble() * (pane.getPrefHeight() - image.getHeight());

                                    for (int n = 0; n < nbObstacles; n++) {
                                        if (Math.abs(px - particles.get(n).getX()) < particles.get(n).getFitWidth() + image.getWidth()) {
                                            pass = false;
                                            break;
                                        }
                                        if (Math.abs(py - particles.get(n).getY()) < particles.get(n).getFitHeight() + image.getHeight()) {
                                            pass = false;
                                            break;
                                        }
                                    }

                                } while (!pass);


                                vx = 8 * (random.nextDouble() - 0.5);
                                vy = 8 * (random.nextDouble() - 0.5);
                                genre = random.nextDouble() * 100;

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
                label_Ennemis.setText("Ennemis restants : " + (particles.size() - nbObstacles - nbBallesVivantes));
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

    public void sauvegarder() {
        ObjectOutputStream oos = null;
        try {

            final FileOutputStream fichier = new FileOutputStream("donnees.ser");
            oos = new ObjectOutputStream(fichier);
            System.out.println("l'angle du cowboy est " + cowboy.get_Degre());
            oos.writeObject(cowboy);
            oos.flush();

        } catch (final java.io.IOException e) {
            e.printStackTrace();

        } finally {

            try {
                if (oos != null) {
                    oos.flush();
                    oos.close();
                }

            } catch (final IOException ex) {
                ex.printStackTrace();
            }

        }
    }

    public void charger() {
        ObjectInputStream ois = null;

        try {
            final FileInputStream fichier = new FileInputStream("donnees.ser");
            ois = new ObjectInputStream(fichier);
            cowboy = (CowboyParticle) ois.readObject();
            System.out.println("l'angle du cowboy est " + cowboy.get_Degre());

            if (cowboy == null) {
                System.out.println("Cowboy est nul");
                Platform.exit();
            }
        } catch (final java.io.IOException e) {
            e.printStackTrace();
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        }
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

