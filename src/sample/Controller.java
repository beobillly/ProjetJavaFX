package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import static javafx.scene.input.KeyCode.*;

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

    protected AnimationTimer animationTimer;
    private Integer ennemis_Mort = 0;
    private final int nb = 3; //nombre de particules ennemies en début de partie
    private ArrayList<SingleParticle> particles;
    private ArrayList<Image> fond_ecran;
    private final int nbObstacles = 5;//nombre d'obstacles
    private final int nbBallesDebut = 30;
    private int nbEnnemisCourant = nb; //nombre d'ennemis sur l'arene;
    private double bulletSpeed = 10; //Vitesse des balles
    private int nbEnnemisMax = 35; //nb d'ennemis maximal sur l'arene
    protected Stage fenetre_lose = new Stage();
    int mode = 1;//mode de déplacement
    protected Stage fenetre_win = new Stage();
    //    private CowboyParticle cowboy;
    private ArrayList<CowboyParticle> cowboyList;
    protected Stage fenetre_controle = new Stage();
    protected Stage fenetre_help = new Stage();
    private int nbBalles = nbBallesDebut;

    @FXML
    public void initialize() {

        initStages();

        setGame();

        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {

                for (SingleParticle p : particles) {
                    if (p != null)
                        p.move();
                }

                for (CowboyParticle cb : cowboyList) {
                    if (cb != null)
                        cb.move();
                }


                manageCowboyControls();

                manageCollisions();

                updateLabels();

            }
        };

        //bind le coeff de vitesse rate de chaque particule
        //à la valeur de slider
        for (SingleParticle p : particles) {
            p.rateProperty().bind(slider.valueProperty()
                    .multiply(1 / 0.3));
        }

        animationTimer.stop();


    }


    public void resetVariables() {
        nbEnnemisCourant = nb; //nombre de particules ennemies
    }

    public void resetParticles() {
        synchronized (this) {
            for (int i = 0; i < particles.size(); i++) {

                pane.getChildren().remove(particles.get(i));
                particles.remove(i);
                i--;
            }
        }
    }

    public void resetCowboys() {
        synchronized (this) {
            for (int i = 0; i < cowboyList.size(); i++) {

                pane.getChildren().remove(cowboyList.get(i));
                cowboyList.remove(i);
                i--;
            }
        }
    }

    private void fillParticles() {
        URL url = getClass().getResource("ressources/squareObstacle.png");
        Image obstacleImage = new Image(url.toString());

        url = getClass().getResource("ressources/demon_f.png");
        Image demon_f = new Image(url.toString());
        url = getClass().getResource("ressources/demon_m.png");
        Image demon_m = new Image(url.toString());

        Random random = new Random(System.nanoTime());


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
        AudioClip audioClip = new AudioClip(getClass()
                .getResource("ressources/1967.wav").toString());


//creer les particules
        for (int i = 0; i < nb; i++) {


            boolean b = ((i & 1) == 1);
            Image image = b ? demon_f : demon_m;
            double genre = b ? 90 : 25;
            double px = random.nextDouble() * (pane.getPrefWidth() - image.getWidth());
            double py = random.nextDouble() * (pane.getPrefHeight() - image.getHeight());

            boolean pass;

            //On vérifie qu'on invoque pas le monstre dans un obstacle ou dans le cowboy.
            do {

                pass = true;
                px = random.nextDouble() * (pane.getPrefWidth() - image.getWidth());
                py = random.nextDouble() * (pane.getPrefHeight() - image.getHeight());

                for (int n = 0; n < nbObstacles; n++) {
                    if (Math.abs(px - particles.get(n).getX()) < particles.get(n).getFitWidth() + image.getWidth() - 10 && Math.abs(py - particles.get(n).getY()) < particles.get(n).getFitHeight() + image.getHeight() - 10) {
                        pass = false;
                        break;
                    }

                }
                for (CowboyParticle cb : cowboyList)
                    if ((Math.abs(px - cb.getX()) < cb.getFitWidth() + image.getWidth() - 10) && (Math.abs(py - cb.getY()) < cb.getFitWidth() + image.getHeight() - 10)) {
                    pass = false;
                }


            } while (!pass);

            double vx = 8 * (random.nextDouble() - 0.5);
            double vy = 8 * (random.nextDouble() - 0.5);
            //particles[i] = new SingleParticle(image, px, py,
            //        vx, vy,audioClip, 0.0, 1.0);

            genre = random.nextDouble() * 100;

            if (genre < 50) {
                image = demon_m;
            } else {
                image = demon_f;
            }

            DemonParticle dP = new DemonParticle(image, px, py,
                    vx, vy, audioClip, 0.0, 1.0, 0.0, 0.0, genre);
            particles.add(dP);
            dP.rateProperty().bind(slider.valueProperty()
                    .multiply(1 / 0.3));
            pane.getChildren().add(dP);
        }
    }

    private void fillWallpapers() {

        Random random = new Random(System.nanoTime());

        URL url = getClass().getResource("ressources/ciel.jpg");
        Image ciel = new Image(url.toString());
        fond_ecran.add(ciel);
        url = getClass().getResource("ressources/desert.jpg");
        Image desert = new Image(url.toString());
        fond_ecran.add(desert);
        url = getClass().getResource("ressources/lave.jpg");
        Image lave = new Image(url.toString());
        fond_ecran.add(lave);
        url = getClass().getResource("ressources/terre.jpg");
        Image terre = new Image(url.toString());
        fond_ecran.add(terre);
    }

    private void chooseWallpaper() {

        Random random = new Random(System.nanoTime());
        int choix = random.nextInt(4);
        pane.setBackground(new Background(new BackgroundImage(fond_ecran.get(choix), null, null, null, null)));
    }

    void setGame() {
        particles = new ArrayList<SingleParticle>();
        fond_ecran = new ArrayList<Image>();
        cowboyList = new ArrayList<CowboyParticle>();

        URL url = getClass().getResource("ressources/soldat.png");
        Image soldat = new Image(url.toString());

        CowboyParticle cb = new CowboyParticle(soldat, pane.getPrefWidth() / 2 - 60 + cowboyList.size() * 20, pane.getPrefHeight() / 2, 0, 0, null, 10, nbBallesDebut, W, S, A, D, X);
        pane.getChildren().add(cb);
        cowboyList.add(cb);

        fillParticles();
        fillWallpapers();
        chooseWallpaper();
    }

    public void manageCowboyControls() {
        pane.getParent().getParent().getParent().setOnKeyPressed(event -> {

            for (CowboyParticle cb : cowboyList) {

                KeyCode kC = event.getCode();
                if (kC == cb.getUpKey()) {
                    cb.set_VY(-2);
                } else if (kC == cb.getDownKey()) {
                    cb.set_VY(2);
                } else if (kC == cb.getLeftKey()) {
                    cb.set_VX(-2);
                } else if (kC == cb.getRightKey()) {
                    cb.set_VX(2);
                } else if (kC == cb.getFireKey()) {
                    if (cb.get_Munitions() > 0) {
                        URL url = getClass().getResource("ressources/Advanced_Sniper_Bullet-ConvertImage.png");
                        Image image = new Image(url.toString());
                        particles.add(new BulletParticle(image, cb.getX(), cb.getY(), bulletSpeed * Math.cos(Math.toRadians(cb.get_Degre() - 90)), bulletSpeed * Math.sin(Math.toRadians(cb.get_Degre() - 90)), null, 0.0, 1.0, 0.0, 0.0, -1, cb));
                        pane.getChildren().add(particles.get(particles.size() - 1));
                        cb.set_Munitions(cb.get_Munitions() - 1);
                    }
                } else if (kC == cb.getUltKey()) {
                    itsHighNoooooon(null);
                }

                if (mode == 1)
                    cb.faireRotation();
                if (mode == 2)
                    cb.faireRotation2();

                if (mode == 2) {

                    if (kC == O) {
                        cb.set_Degre(0);
                    } else if (kC == K) {
                        cb.set_Degre(270);
                    } else if (kC == SEMICOLON) {
                        cb.set_Degre(90);
                    } else if (kC == L) {
                        cb.set_Degre(180);
                    }

                }

                if (mode == 1)
                    cb.faireRotation();
                if (mode == 2)
                    cb.faireRotation2();

            }

        });

        pane.getParent().getParent().getParent().setOnKeyReleased(event -> {
            for (CowboyParticle cb : cowboyList) {
                KeyCode kC = event.getCode();
                if (kC == cb.getUpKey()) {
                    cb.set_VY(0);
                } else if (kC == cb.getDownKey()) {
                    cb.set_VY(0);
                } else if (kC == cb.getLeftKey()) {
                    cb.set_VX(0);
                } else if (kC == cb.getRightKey()) {
                    cb.set_VX(0);
                }

                if (mode == 1)
                    cb.faireRotation();
                if (mode == 2)
                    cb.faireRotation2();
            }
        });

    }

    public void manageCollisions() {

        URL url = getClass().getResource("ressources/demon_f.png");
        Image demon_f = new Image(url.toString());
        url = getClass().getResource("ressources/demon_m.png");
        Image demon_m = new Image(url.toString());
        url = getClass().getResource("ressources/soldat.png");
        Image soldat = new Image(url.toString());

        Random random = new Random(System.nanoTime());

        AudioClip audioClip = new AudioClip(getClass()
                .getResource("ressources/1967.wav").toString());

                /*pour chaque couple de particules
                verifier si les deux particules chevauchent.
                Si c'est le cas, gérer les collisions
                */
        for (int i = 0; i < particles.size() - 1; i++) {

            for (CowboyParticle cb : cowboyList) {
                if (particles.get(i) instanceof DemonParticle && particles.get(i).getBoundsInParent()
                        .intersects(cb.getBoundsInParent())) {

                    if (cowboyList.size() == 1) {
                        defaite();
                    } else {
                        cowboyList.remove(cb);
                        pane.getChildren().remove(cb);
                    }

                }
            }
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

                        if (particles.get(j) instanceof DemonParticle) {

                            ennemis_Mort++;
                            nbEnnemisCourant--;
                            ((BulletParticle) particles.get(i)).owner.set_Ultimate(((BulletParticle) particles.get(i)).owner.get_Ultimate() + 5);
                        }

                        pane.getChildren().remove(particles.get(j));
                        particles.remove(j);
                        j--;


                    }
                    if (particles.get(j) instanceof BulletParticle) {

                        if (particles.get(i) instanceof DemonParticle) {

                            ennemis_Mort++;
                            nbEnnemisCourant--;
                            ((BulletParticle) particles.get(j)).owner.set_Ultimate(((BulletParticle) particles.get(j)).owner.get_Ultimate() + 5);
                        }

                        pane.getChildren().remove(particles.get(i));
                        particles.remove(i);
                        i--;


                    } else if (particles.get(i).get_Genre() < 50 && particles.get(j).get_Genre() < 50) {

                        pane.getChildren().remove(particles.get(j));
                        particles.remove(j);
                        j--;
                        ennemis_Mort++;
                        nbEnnemisCourant--;
                        for (CowboyParticle cb : cowboyList) {
                            cb.set_Ultimate(cb.get_Ultimate() + 5);
                        }

                    } else if (((particles.get(i).get_Genre() < 50 && particles.get(j).get_Genre() > 50) || (particles.get(i).get_Genre() > 50 && particles.get(j).get_Genre() < 50)) && (particles.size() - nbObstacles) < nbEnnemisMax) {

                        Image image = ((i & 1) == 1) ? demon_m : demon_f;
                        double px;
                        double py;

                        double vx = 8 * (random.nextDouble() - 0.5);
                        double vy = 8 * (random.nextDouble() - 0.5);


                        double genre;

                        boolean pass;

                        do {

                            pass = true;
                            px = random.nextDouble() * (pane.getPrefWidth() - image.getWidth());
                            py = random.nextDouble() * (pane.getPrefHeight() - image.getHeight());

                            for (int n = 0; n < nbObstacles; n++) {
                                if (Math.abs(px - particles.get(n).getX()) < particles.get(n).getFitWidth() + image.getWidth() && Math.abs(py - particles.get(n).getY()) < particles.get(n).getFitHeight() + image.getHeight()) {
                                    pass = false;
                                    break;
                                }

                            }

                            for (CowboyParticle cb : cowboyList) {
                                if ((Math.abs(px - cb.getX()) < cb.getFitWidth() + image.getWidth() - 10) && (Math.abs(py - cb.getY()) < cb.getFitWidth() + image.getHeight() - 10)) {
                                    pass = false;
                                }
                            }

                        } while (!pass);

                        genre = random.nextDouble() * 100;
                        DemonParticle dP = new DemonParticle(image, px, py,
                                vx, vy, audioClip, 0.0, 1.0, 0.0, 0.0, genre);
                        particles.add(dP);

                        dP.rateProperty().bind(slider.valueProperty()
                                .multiply(1 / 0.3));

                        pane.getChildren().add(particles.get(particles.size() - 1));
                        particles.get(i).set_VX(-particles.get(i).get_VX());
                        particles.get(i).set_VY(-particles.get(i).get_VY());
                        particles.get(i).faireRotation();
                        nbEnnemisCourant++;

                        particles.get(j).set_VX(-particles.get(j).get_VX());
                        particles.get(j).set_VY(-particles.get(j).get_VY());
                        particles.get(j).faireRotation();

                    }
                }


            }
        }
    }

    public void updateLabels() {
        label_Munitions.setText("Munitions restantes : " + cowboyList.get(0).get_Munitions());
        label_Ultimate.setText("Ultimate chargé : " + cowboyList.get(0).get_Ultimate());
        label_Ennemis.setText("Ennemis restants : " + (nbEnnemisCourant));
//                label_Ennemis_Mort.setText("Ennemis morts : " + ennemis_Mort);
        label_Direction.setText("Angle : " + cowboyList.get(0).get_Degre());
        if (nbEnnemisCourant == 0) {
            victoire();
        }
    }


    public void initStages() {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass()
                    .getResource("help.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        fenetre_help.setTitle("HELP");
        assert root != null;
        fenetre_help.setScene(new Scene(root, 700, 400));


        try {
            root = FXMLLoader.load(getClass()
                    .getResource("win.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        fenetre_win.setTitle("Gagné!");
        assert root != null;
        fenetre_win.setScene(new Scene(root, 700, 400));


        try {
            root = FXMLLoader.load(getClass()
                    .getResource("lose.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        fenetre_lose.setTitle("Gagné!");
        assert root != null;
        fenetre_lose.setScene(new Scene(root, 700, 400));
    }


    public void quitter(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void sauvegarder() {

    }

    public void charger() {

    }

    public void itsHighNoooooon(ActionEvent actionEvent) {

        if (cowboyList.get(0).get_Ultimate() >= 100) {
            animationTimer.stop();
            double cowboy_px = cowboyList.get(0).getX();
            double cowboy_py = cowboyList.get(0).getY();
            double cowboy_degree = cowboyList.get(0).get_Degre();
            /*0 cest le haut*/
            /*90 cest a gauche */
            /*180 cest en bas*/
            /*270 cest a droite*/

            for (int i = 0; i < particles.size(); i++) {

                if (particles.get(i) instanceof DemonParticle) {
                    if (cowboy_degree == 0) {
                        if (particles.get(i).getY() < cowboy_py) {
                            pane.getChildren().remove(particles.get(i));
                            particles.remove(particles.get(i));
                            i--;
                            ennemis_Mort++;
                            nbEnnemisCourant--;
                        }
                    } else if (cowboy_degree == 180) {
                        if (particles.get(i).getY() > cowboy_py) {
                            pane.getChildren().remove(particles.get(i));
                            particles.remove(particles.get(i));
                            ennemis_Mort++;
                            nbEnnemisCourant--;
                        }
                    } else if (cowboy_degree == 270) {
                        if (particles.get(i).getX() < cowboy_px) {
                            pane.getChildren().remove(particles.get(i));
                            particles.remove(particles.get(i));
                            ennemis_Mort++;
                            nbEnnemisCourant--;
                        }
                    } else if (cowboy_degree == 90) {
                        if (particles.get(i).getX() > cowboy_px) {
                            pane.getChildren().remove(particles.get(i));
                            particles.remove(particles.get(i));
                            ennemis_Mort++;
                            nbEnnemisCourant--;
                        }
                    } else {

                    }
                }
            }
            cowboyList.get(0).set_Ultimate(0);
            animationTimer.start();
        }
    }

    public void pause(ActionEvent actionEvent) {
        animationTimer.stop();

    }

    public void jouer(ActionEvent actionEvent) {
        animationTimer.start();
    }

    public void help(ActionEvent actionEvent) throws IOException {
        animationTimer.stop();
        fenetre_help.show();
    }

    public void victoire() {
        animationTimer.stop();
        fenetre_win.show();
        rejouer();
    }

    public void defaite() {
        animationTimer.stop();
        fenetre_lose.show();
        rejouer();
    }

    public void rejouer() {

        animationTimer.stop();
        resetParticles();
        resetVariables();
        resetCowboys();

        URL url = getClass().getResource("ressources/soldat.png");
        Image soldat = new Image(url.toString());

        CowboyParticle cb = new CowboyParticle(soldat, pane.getPrefWidth() / 2, pane.getPrefHeight() / 2, 0, 0, null, 0, 1, 0, 0, 10, nbBallesDebut);
        pane.getChildren().add(cb);
        cowboyList.add(cb);

        fillParticles();
        chooseWallpaper();

    }


    public void relancer(ActionEvent actionEvent) {
        rejouer();
    }

    public void controle(ActionEvent actionEvent) {
        animationTimer.stop();
        fenetre_controle.show();
    }

    public void background(ActionEvent actionEvent) {
    }

    public void difficute(ActionEvent actionEvent) {
    }

    public void createPlayer(ActionEvent actionEvent) {

        URL url = getClass().getResource("ressources/soldat.png");
        Image soldat = new Image(url.toString());

        CowboyParticle cb = new CowboyParticle(soldat, pane.getPrefWidth() / 2 - 10 + cowboyList.size() * 5, pane.getPrefHeight() / 2, 0, 0, null, 10, nbBallesDebut, I, K, J, L, N);
        cowboyList.add(cb);
        pane.getChildren().add(cb);
    }

    public void removePlayer(ActionEvent actionEvent) {
    }
}

