package sample;

import javafx.beans.property.DoubleProperty;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.media.AudioClip;

import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;

class DemonParticle extends SingleParticle {


    /* le constructeur prend en parametre :
       - un Image (une petite icone) qui represent un personage (animal etc),
       - (x,y) la position initiale de l'objet
       - (v_x,v_y) la vitesse initiale de l'objet dans les axes x et y
       - un AudioClip qui est joué quand l'objet rebondit le parois
       - (ax,ay) - on suppose que l'image possede 4 côté et une
         côté represente la face du presonnage.
         Quand le personnage se déplace nous voulons qu'il soit tourné
         vers la direction du mouvement.
    */
    final DoubleProperty comportement = new SerializableSimpleDoubleProperty();
    final DoubleProperty delta = new SerializableSimpleDoubleProperty();

    ArrayList<CowboyParticle> cowboyParticle;

    DemonParticle(Image image, double x, double y,
                  double v_x, double v_y, AudioClip audioClip,
                  double ax, double ay, double movex, double movey, double genre, double comportement, double delta) {
        super(image, x, y, v_x, v_y, audioClip, ax, ay, movex, movey, genre);
        set_Comportement(comportement);
        set_Delta(delta);
    }

    DoubleProperty comportementProperty() {
        return comportement;
    }

    DoubleProperty deltaProperty() {
        return comportement;
    }

    ArrayList<CowboyParticle> getCowboyParticle() {
        return cowboyParticle;
    }

    void setCowboyParticle(ArrayList<CowboyParticle> cb) {
        this.cowboyParticle = cb;
    }

    double get_Delta() {
        return delta.getValue();
    }

    void set_Delta(double v) {
        delta.set(v);
    }

    double get_Comportement() {
        return comportement.getValue();
    }

    void set_Comportement(double v) {
        comportement.set(v);
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
        double delta = get_Delta() * get_Rate();
        CowboyParticle cb = cowboyParticle.get(0);

        switch ((int) get_Comportement()) {
            //comportement de base
            case 1:
                if (getX() < cb.getX()) {
                    if (get_VX() < 0) {
                        set_VX(-get_VX());
                        faireRotation();
                    }
                    setX(getX() + get_VX() * delta);
                }
                if (getX() > cb.getX()) {
                    if (get_VX() > 0) {
                        set_VX(-get_VX());
                        faireRotation();
                    }
                    setX(getX() + get_VX() * delta);
                }
                if (getY() < cb.getY()) {
                    if (get_VY() < 0) {
                        set_VY(-get_VY());
                        faireRotation();
                    } else setY(getY() + get_VY() * delta);
                }
                if (getY() > cb.getY()) {
                    if (get_VY() > 0) {
                        set_VY(-get_VY());
                        faireRotation();
                    }
                    setY(getY() + get_VY() * delta);
                }

            default:
                setX(getX() + get_VX() * delta);
                setY(getY() + get_VY() * delta);
                break;
        }


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

    public String toString() {
        return "demon";
    }

    protected void writeParticle(FileWriter writer) throws IOException {

        writer.write("PARTICLE");
        writer.write("#");


        writer.write("DEAMON");
        writer.write("#");

        writer.write(this.getImage().impl_getUrl());
        writer.write("#");

        writer.write(this.getX() + ";" + this.getY());
        writer.write("#");

        writer.write(this.get_VX() + ";" + this.get_VY());
        writer.write("#");

        writer.write(Double.toString(this.get_Genre()));
        writer.write("#");
    }

}

