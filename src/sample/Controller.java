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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import static java.lang.Thread.sleep;
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
    private double comportement = 0;
    private double delta = 1.5;
    private int currentWallPaperChoice;
    private Image current_cowboy_image;
    private Image current_obstacle_image;
    private Image current_demon_f_image;
    private Image current_demon_m_image;
    private Image current_demon_f_dead_image;
    private Image current_demon_m_dead_image;
    private Image current_bullet_image;
    private AudioClip current_audio_clip;
    private AudioClip current_death_audio_clip;
    private AudioClip current_background_audio_clip;

    @FXML
    public void initialize() {

        initStages();

        setGame();

        difficute();

        current_background_audio_clip = new AudioClip(getClass()
                .getResource("ressources/Mexican Mariachi Music.mp3").toString());

        current_death_audio_clip = new AudioClip(getClass()
                .getResource("ressources/Bruh Sound Effect #2.mp3").toString());

        current_background_audio_clip.play();

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
                    .multiply(1 / 0.5));
        }

        for (SingleParticle p : cowboyList) {
            p.rateProperty().bind(slider.valueProperty()
                    .multiply(1));
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

        current_obstacle_image = obstacleImage;

        url = getClass().getResource("ressources/demon_f.png");
        Image demon_f = new Image(url.toString());
        url = getClass().getResource("ressources/demon_m.png");
        Image demon_m = new Image(url.toString());

        current_demon_f_image = demon_f;
        current_demon_m_image = demon_m;

        url = getClass().getResource("ressources/demon_f_dead.png");
        current_demon_f_dead_image = new Image(url.toString());
        url = getClass().getResource("ressources/demon_m_dead.png");
        current_demon_m_dead_image = new Image(url.toString());

//        current_demon_f_dead_image = demon_f_dead;
//        current_demon_m_dead_image = demon_m_dead;

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
                .getResource("ressources/Cartoon Boing Sound Effect.mp3").toString());

        current_audio_clip = audioClip;


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
                    vx, vy, audioClip, 0.0, 1.0, 0.0, 0.0, genre, comportement, delta);
            dP.setCowboyParticle(cowboyList);
            particles.add(dP);
            dP.rateProperty().bind(slider.valueProperty()
                    .multiply(1 / 0.3));
            pane.getChildren().add(dP);
        }
    }

    private void fillWallpapers() {

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
        currentWallPaperChoice = choix;
        pane.setBackground(Background.EMPTY);
        pane.setBackground(new Background(new BackgroundImage(fond_ecran.get(choix), null, null, null, null)));
    }

    void setGame() {
        particles = new ArrayList<SingleParticle>();
        fond_ecran = new ArrayList<Image>();
        cowboyList = new ArrayList<CowboyParticle>();

        URL url = getClass().getResource("ressources/soldat.png");
        Image soldat = new Image(url.toString());

        current_cowboy_image = soldat;

        String s = soldat.impl_getUrl();


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
                        current_bullet_image = image;
                        particles.add(new BulletParticle(image, cb.getX(), cb.getY(), bulletSpeed * Math.cos(Math.toRadians(cb.get_Degre() - 90)), bulletSpeed * Math.sin(Math.toRadians(cb.get_Degre() - 90)), null));
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

                    if (kC == I) {
                        cb.set_Degre(0);
                    } else if (kC == J) {
                        cb.set_Degre(270);
                    } else if (kC == L) {
                        cb.set_Degre(90);
                    } else if (kC == K) {
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
                .getResource("ressources/Cartoon Boing Sound Effect.mp3").toString());

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

                        audioClip.play();

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

                        audioClip.play();

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


                            if (!(particles.get(j).isDying())) {
                                particles.get(j).setImage(particles.get(j).get_Genre() < 50 ? current_demon_m_dead_image : current_demon_f_dead_image);
                                particles.get(j).touer();
                                current_death_audio_clip.play();
                            } else {
                                try {
                                    sleep(1000);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                ennemis_Mort++;
                                nbEnnemisCourant--;
                                cowboyList.get(0).set_Ultimate(cowboyList.get(0).get_Ultimate() + 5);
                                pane.getChildren().remove(particles.get(j));
                                particles.remove(j);
                                j--;
                            }

                        }

                        pane.getChildren().remove(particles.get(j));
                        particles.remove(j);
                        j--;

                    }
                    if (particles.get(j) instanceof BulletParticle) {


                        if (particles.get(i) instanceof DemonParticle) {


                            if (!(particles.get(i).isDying())) {
                                particles.get(i).setImage(particles.get(i).get_Genre() < 50 ? current_demon_m_dead_image : current_demon_f_dead_image);
                                particles.get(i).touer();
                                current_death_audio_clip.play();
                            } else {
                                try {
                                    sleep(1000);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                ennemis_Mort++;
                                nbEnnemisCourant--;
                                cowboyList.get(0).set_Ultimate(cowboyList.get(0).get_Ultimate() + 5);
                                pane.getChildren().remove(particles.get(i));
                                particles.remove(i);
                                i--;
                            }

                        } else {
                            pane.getChildren().remove(particles.get(i));
                            particles.remove(i);
                            i--;
                        }
                    } else if (particles.get(i).get_Genre() < 50 && particles.get(j).get_Genre() < 50) {

                        if (!(particles.get(j).isDying())) {
                            particles.get(j).setImage(current_demon_m_dead_image);
                            particles.get(j).touer();
                        } else {
                            try {
                                sleep(1000);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            ennemis_Mort++;
                            nbEnnemisCourant--;
                            cowboyList.get(0).set_Ultimate(cowboyList.get(0).get_Ultimate() + 5);
                            pane.getChildren().remove(particles.get(j));
                            particles.remove(j);
                            j--;
                        }

                    } else if (((particles.get(i).get_Genre() < 50 && particles.get(j).get_Genre() >= 50) || (particles.get(i).get_Genre() >= 50 && particles.get(j).get_Genre() < 50)) && (particles.size() - nbObstacles) < nbEnnemisMax) {

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
                        if (genre < 50) {
                            image = demon_m;
                        } else image = demon_f;
                        DemonParticle dP = new DemonParticle(image, px, py,
                                vx, vy, audioClip, 0.0, 1.0, 0.0, 0.0, genre, comportement, delta);
                        particles.add(dP);
                        dP.setCowboyParticle(cowboyList);
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
        fenetre_lose.setTitle("Perdu!");
        assert root != null;
        fenetre_lose.setScene(new Scene(root, 700, 400));
    }


    public void quitter(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void sauvegarder() {


        try {
            FileWriter fw = new FileWriter("sauvegarde.txt");

            IO.ecrireMode(fw, mode);

            IO.ecrireSeparateurPrincipal(fw);

            for (CowboyParticle cb : cowboyList) {
                IO.ecrireParticule(fw, cb);
            }

            IO.ecrireSeparateurPrincipal(fw);

            for (SingleParticle p : particles) {
                IO.ecrireParticule(fw, p);
            }

            IO.ecrireSeparateurPrincipal(fw);

            IO.ecrire(fw, Integer.toString(currentWallPaperChoice));

            fw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void charger() throws IOException {

        String content = "";

        try {
            FileReader fr = new FileReader("sauvegarde.txt");
            content = IO.lireToutFichier(fr);
            fr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] groupes = new String[4];
        groupes = content.split("%");

        for (String groupe : groupes) {
            System.out.println(groupe);
            System.out.println();
            System.out.println();
        }

        String modeACharger = groupes[0];

        String[] cowboys = groupes[1].split("PARTICLE");

        String[] particles = groupes[2].split("PARTICLE");

        String wallpaper = groupes[3];

        modeACharger = modeACharger.trim();

        //on remet le bon mode

        this.mode = Integer.parseInt(modeACharger);

        wallpaper = wallpaper.trim();

        //On remet le bon wallpaper
        pane.setBackground(new Background(new BackgroundImage(fond_ecran.get(Integer.parseInt(wallpaper)), null, null, null, null)));

        //on clear les arraylists

        resetCowboys();
        resetParticles();

        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("------------------------------");

        for (int ind = 1; ind < cowboys.length; ind++) {

            System.out.println(cowboys[ind]);
            String[] cbParts = cowboys[ind].split("#");

            String[] XandY = cbParts[3].split(";");
            assert XandY.length == 2;
            Double xPos = Double.parseDouble(XandY[0]);
            Double yPos = Double.parseDouble(XandY[1]);

            String[] VXandVY = cbParts[4].split(";");
            assert VXandVY.length == 2;
            Double vX = Double.parseDouble(VXandVY[0]);
            Double vY = Double.parseDouble(VXandVY[1]);

            Double ammo = Double.parseDouble(cbParts[5]);

            Double ult = Double.parseDouble(cbParts[6]);

            Double degree = Double.parseDouble(cbParts[7]);

            CowboyParticle cow_boy = new CowboyParticle(current_cowboy_image, xPos, yPos, vX, vY, null, 10, ammo);
            cow_boy.set_Ultimate(ult);
            cow_boy.set_Degre(degree);

            cowboyList.add(cow_boy);
            pane.getChildren().add(cow_boy);


        }

        for (int ind = 1; ind < particles.length; ind++) {

            System.out.println(particles[ind]);

            String[] pParts = particles[ind].split("#");

            if (pParts[1].equals("IMPEDIMENT")) {
                String[] XandY = pParts[3].split(";");
                assert XandY.length == 2;
                Double xPos = Double.parseDouble(XandY[0]);
                Double yPos = Double.parseDouble(XandY[1]);
                ObstacleParticle oP = new ObstacleParticle(current_obstacle_image, xPos, yPos);
                this.particles.add(oP);
                pane.getChildren().add(oP);
            }

            if (pParts[1].equals("DEAMON")) {
                String[] XandY = pParts[3].split(";");
                assert XandY.length == 2;
                Double xPos = Double.parseDouble(XandY[0]);
                Double yPos = Double.parseDouble(XandY[1]);
                String[] VXandVY = pParts[4].split(";");
                assert VXandVY.length == 2;
                Double vX = Double.parseDouble(VXandVY[0]);
                Double vY = Double.parseDouble(VXandVY[1]);

                Double genre = Double.parseDouble(pParts[5]);


                DemonParticle dP = new DemonParticle((genre < 50) ? current_demon_m_image : current_demon_f_image, xPos, yPos, vX, vY, current_audio_clip, 0, 0, 0, 0, genre, comportement, delta);
                this.particles.add(dP);
                dP.setCowboyParticle(cowboyList);
                pane.getChildren().add(dP);
            }

            if (pParts[1].equals("BULLET")) {
                System.out.println("Je rentre là");
                String[] XandY = pParts[3].split(";");
                assert XandY.length == 2;
                Double xPos = Double.parseDouble(XandY[0]);
                Double yPos = Double.parseDouble(XandY[1]);

                System.out.println(xPos);
                System.out.println(yPos);

                String[] VXandVY = pParts[4].split(";");
                assert VXandVY.length == 2;
                Double vX = Double.parseDouble(VXandVY[0]);
                Double vY = Double.parseDouble(VXandVY[1]);

                System.out.println(vX);
                System.out.println(vY);


                URL url = getClass().getResource("ressources/Advanced_Sniper_Bullet-ConvertImage.png");
                Image image = new Image(url.toString());

                BulletParticle bP = new BulletParticle(image, xPos, yPos, vX, vY, null);
                this.particles.add(bP);
                pane.getChildren().add(bP);
            }
//            String[] XandY = pParts[3].split(";");
//            assert XandY.length == 2;
//            Double xPos = Double.parseDouble(XandY[0]);
//            Double yPos = Double.parseDouble(XandY[1]);
//
//            String[] VXandVY = pParts[4].split(";");
//            assert VXandVY.length == 2;
//            Double vX = Double.parseDouble(VXandVY[0]);
//            Double vY = Double.parseDouble(VXandVY[1]);
//
//            ammo = Double.parseDouble(pParts[5]);
//
//            ult = Double.parseDouble(pParts[6]);
//
//            degree = Double.parseDouble(pParts[7]);


//
//
//            CowboyParticle cow_boy = new CowboyParticle(current_cowboy_image, xPos, yPos, vX, vY, null, 10, ammo);
//            cow_boy.set_Ultimate(ult);
//            cow_boy.set_Degre(degree);
//
//            cowboyList.add(cow_boy);
//            pane.getChildren().add(cow_boy);


        }


        for (SingleParticle p : this.particles) {
            p.rateProperty().bind(slider.valueProperty()
                    .multiply(1 / 0.5));
        }

        for (SingleParticle p : this.cowboyList) {
            p.rateProperty().bind(slider.valueProperty()
                    .multiply(1));
        }


        try {
            changerControle();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    void changerControle() throws IOException {
        animationTimer.stop();
        BufferedReader in;
        in = new BufferedReader(new FileReader("Controle.txt"));
        String ligne = "";
        String[] touches;
        String up = "";
        String down = "";
        String left = "";
        String right = "";
        String ult = "";
        String fire = "";
        int i = 0;
        CowboyParticle cb;

        while ((ligne = in.readLine()) != null && i < cowboyList.size()) {
            touches = ligne.split(";");
            cb = cowboyList.get(i);
            up = touches[0];
            left = touches[1];
            down = touches[2];
            right = touches[3];
            ult = touches[4];
            fire = touches[5];
            cb.setUp(KeyCode.valueOf(up));
            cb.setLeft(KeyCode.valueOf(left));
            cb.setDown(KeyCode.valueOf(down));
            cb.setRight(KeyCode.valueOf(right));
            cb.setUlt(KeyCode.valueOf(ult));
            cb.setFireKey(KeyCode.valueOf(fire));
            i++;
        }
        animationTimer.start();

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

        for (SingleParticle p : particles) {
            p.rateProperty().bind(slider.valueProperty()
                    .multiply(1 / 0.5));
        }

        for (SingleParticle p : cowboyList) {
            p.rateProperty().bind(slider.valueProperty()
                    .multiply(1));
        }

    }


    public void relancer(ActionEvent actionEvent) {
        rejouer();
    }

    public void controle(ActionEvent actionEvent) throws IOException {
        changerControle();
    }

    public void background(ActionEvent actionEvent) {
        chooseWallpaper();
    }

    public void difficute() {
        for (SingleParticle p : particles) {
            if (p != null) {
                if (p instanceof DemonParticle) {
                    ((DemonParticle) p).set_Comportement(comportement);
                    ((DemonParticle) p).set_Delta(delta);
                }
            }
        }
    }

    public void createPlayer(ActionEvent actionEvent) {

        URL url = getClass().getResource("ressources/soldat.png");
        Image soldat = new Image(url.toString());

        CowboyParticle cb = new CowboyParticle(soldat, pane.getPrefWidth() / 2 - 10 + cowboyList.size() * 20, pane.getPrefHeight() / 2, 0, 0, null, 10, nbBallesDebut, I, K, J, L, N);
        cowboyList.add(cb);
        pane.getChildren().add(cb);

        cb.rateProperty().bind(slider.valueProperty()
                .multiply(1));

    }

    public void removePlayer(ActionEvent actionEvent) {
        if (cowboyList.size() == 1) {
            defaite();
        } else {
            CowboyParticle cb = cowboyList.get(cowboyList.size() - 1);
            cowboyList.remove(cb);
            pane.getChildren().remove(cb);
        }
    }

    public void difficute_facile(ActionEvent actionEvent) {
        animationTimer.stop();
        comportement = 0;
        delta = 1;
        difficute();
        animationTimer.start();
    }

    public void difficute_normal(ActionEvent actionEvent) {
        animationTimer.stop();
        comportement = 0;
        delta = 1.5;
        difficute();
        animationTimer.start();
    }

    public void difficute_difficile(ActionEvent actionEvent) {
        animationTimer.stop();
        comportement = 1;
        delta = 1.5;
        difficute();
        animationTimer.start();
    }

    public void difficute_impossible(ActionEvent actionEvent) {
        animationTimer.stop();
        comportement = 1;
        delta = 2;
        difficute();
        animationTimer.start();
    }


}

