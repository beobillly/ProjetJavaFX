package sample;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.media.AudioClip;

class CowboyParticle extends SingleParticle {

    final DoubleProperty munitions = new SimpleDoubleProperty();
    final DoubleProperty vie = new SimpleDoubleProperty();
    final DoubleProperty ultimate = new SimpleDoubleProperty();

    CowboyParticle(Image image, double x, double y,
                   double v_x, double v_y, AudioClip audioClip,
                   double ax, double ay, double movex, double movey, double genre) {
        super(image, x, y, v_x, v_y, audioClip, ax, ay, movex, movey, genre);
        set_Munitions(50);
        set_Vie(1);
        set_Ultimate(0);
    }

    CowboyParticle(Image image, double x, double y,
                   double v_x, double v_y, AudioClip audioClip,
                   double ax, double ay, double movex, double movey, double genre, double munitions, double vie, double ultimate) {
        this(image, x, y, v_x, v_y, audioClip, ax, ay, movex, movey, genre);
        set_Munitions(munitions);
        set_Vie(vie);
        set_Ultimate(ultimate);
    }

    DoubleProperty munitionsProperty() {
        return munitions;
    }

    DoubleProperty vieProperty() {
        return vie;
    }

    DoubleProperty ultimateProperty() {
        return ultimate;
    }

    double get_Munitions() {
        return munitions.getValue();
    }

    void set_Munitions(double v) {
        munitions.set(v);
    }

    double get_Vie() {
        return vie.getValue();
    }

    void set_Vie(double v) {
        vie.set(v);
    }

    double get_Ultimate() {
        return ultimate.getValue();
    }

    void set_Ultimate(double v) {
        ultimate.set(v);
        if (ultimate.getValue() > 100)
            ultimate.set(100);
    }


    /* la methode move() fait bouger l'objet
    sur la scene. Cette méthode sera appelée dans la methode handle()
    de AnimationTimer.
    pour l'animation.
    On change la position de l'objet selon selon la formule :
    (x',y')=(x,y)+rate*(v_x,v_y)
    où
        -  (x,y) : l'ancienne position,
        -  (v_x,v_y) : le vecteur de vitesse
        -  rate : un coefficient dont le changement ralentit
           ou accelere le mouvement.
    Si la nouvelle position (x',y') sort de conteneur Parent alors
    rebondir sur le parois du parent en modifiant le signe
    du vecteur de vitesse.

    Jouer audioClip chaque fois quand l'objet cogne
    sur les parois du parent :  audio.play() */
    void move() {


        double delta = 2.7 * get_Rate();
        setX(getX() + get_VX() * delta);
        setY(getY() + get_VY() * delta);
        /*verifier si on ne sort pas sur une position
          avec x negatif*/
        if (getX() < 0 && get_VX() < 0) {
            set_VX(-get_VX());
            faireRotation();
            if (audio != null)
                audio.play();
        }
        /* verifier si on ne sort pas sur une position
        avec y negatif*/
        if (getY() < 0 && get_VY() < 0) {
            set_VY(-get_VY());
            faireRotation();
            if (audio != null)
                audio.play();
            //audio.play();
        }
        /*verifier si on ne sort pas sur le bord droit du parent*/
        if (getX() + getBoundsInLocal().getWidth()
                > ((Region) getParent()).getWidth()
                && get_VX() > 0) {
            setX(((Region) getParent()).getWidth() - getBoundsInLocal().getWidth());
            set_VX(-get_VX());
            faireRotation();
            if (audio != null)
                audio.play();
            //audio.play();
        }
        /*verifier si on ne sort pas sur le bord en bas du parent*/
        if (getY() + getBoundsInLocal().getHeight()
                > ((Region) getParent()).getHeight()
                && get_VY() > 0) {
            setY(((Region) getParent()).getWidth() - getBoundsInLocal().getHeight());
            set_VY(-get_VY());
            faireRotation();
            if (audio != null)
                audio.play();
            //audio.play();
        }
    }

}

