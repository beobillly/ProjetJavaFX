package sample;

import javafx.beans.property.DoubleProperty;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Region;
import javafx.scene.media.AudioClip;

import java.io.FileWriter;
import java.io.IOException;

import static javafx.scene.input.KeyCode.*;

class CowboyParticle extends SingleParticle {

    final DoubleProperty munitions = new SerializableSimpleDoubleProperty();
    final DoubleProperty vie = new SerializableSimpleDoubleProperty();
    final DoubleProperty ultimate = new SerializableSimpleDoubleProperty();
    private KeyCode upKey = W;
    private KeyCode downKey = S;
    private KeyCode leftKey = A;
    private KeyCode rightKey = D;
    private KeyCode fireKey = C;
    private KeyCode ultKey = Q;



    CowboyParticle(Image image, double x, double y,
                   double v_x, double v_y, AudioClip audioClip,
                   double ax, double ay, double movex, double movey, double genre) {
        super(image, x, y, v_x, v_y, audioClip, ax, ay, movex, movey, genre);
        set_Munitions(50);
        set_Vie(1);
        set_Ultimate(0);
    }

    CowboyParticle(Image image, double x, double y,
                   double v_x, double v_y, AudioClip audioClip, double genre, double ammo) {
        super(image, x, y, v_x, v_y, audioClip, genre);
        set_Munitions(ammo);
        set_Ultimate(0);
    }

    CowboyParticle(Image image, double x, double y,
                   double v_x, double v_y, double genre, double ammo) {
        super(image, x, y, v_x, v_y, null, genre);
        set_Munitions(ammo);
        set_Ultimate(0);
    }

    CowboyParticle(Image image, double x, double y,
                   double v_x, double v_y, AudioClip audioClip, double genre, double ammo, KeyCode up, KeyCode down, KeyCode left, KeyCode right, KeyCode fire) {
        super(image, x, y, v_x, v_y, audioClip, genre);
        set_Munitions(ammo);
        set_Ultimate(0);
        this.upKey = up;
        this.downKey = down;
        this.rightKey = right;
        this.leftKey = left;
        this.fireKey = fire;

    }

    CowboyParticle(Image image, double x, double y,
                   double v_x, double v_y, AudioClip audioClip,
                   double ax, double ay, double movex, double movey, double genre, double munitions) {
        this(image, x, y, v_x, v_y, audioClip, ax, ay, movex, movey, genre);
        set_Munitions(munitions);
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

    void setUp(KeyCode kc) {
        this.upKey = kc;
    }

    void setDown(KeyCode kc) {
        this.downKey = kc;
    }

    void setLeft(KeyCode kc) {
        this.leftKey = kc;
    }

    void setRight(KeyCode kc) {
        this.rightKey = kc;
    }

    void setUlt(KeyCode kc) {
        this.ultKey = kc;
    }

    KeyCode getUpKey() {
        return upKey;
    }

    KeyCode getDownKey() {
        return downKey;
    }

    KeyCode getLeftKey() {
        return leftKey;
    }

    KeyCode getRightKey() {
        return rightKey;
    }

    KeyCode getFireKey() {
        return fireKey;
    }

    void setFireKey(KeyCode kc) {
        this.fireKey = kc;
    }

    KeyCode getUltKey() {
        return ultKey;
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

    @Override
    public String toString() {
        return "cowboy";
    }

    protected void writeParticle(FileWriter writer) throws IOException {

        writer.write("PARTICLE");
        writer.write("#");

        writer.write("COWBOY");
        writer.write("#");

        writer.write(this.getImage().impl_getUrl());
        writer.write("#");

        writer.write(this.getX() + ";" + this.getY());
        writer.write("#");

        writer.write(this.get_VX() + ";" + this.get_VY());
        writer.write("#");

        writer.write(Double.toString(this.get_Munitions()));
        writer.write("#");

        writer.write(Double.toString(this.get_Ultimate()));
        writer.write("#");

        writer.write(Double.toString(this.get_Degre()));
        writer.write("#");
    }

}

