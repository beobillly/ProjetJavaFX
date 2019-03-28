package sample;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.media.AudioClip;

public class CowboyParticle extends ImageView {


    /*audioClip à jouer si on cogne sur les parois de parent*/
    AudioClip audio;

    DoubleProperty vx = new SimpleDoubleProperty();
    DoubleProperty vy = new SimpleDoubleProperty();
    DoubleProperty mass = new SimpleDoubleProperty(1.0);


    DoubleProperty movex = new SimpleDoubleProperty();
    DoubleProperty movey = new SimpleDoubleProperty();


    /*  ax,ay - l'axe de personnage.
        Si le personnage a la face tourné en bas alors
         (ax,ay)=(0.0,1.0)
        Si le personnage a la face tournée vers le haut alors
         (ax,ay)=(0.0,-1.0)
        Si le personnage a la face tournée à gauche alors
          (ax,ay)=(1.0,0.0)
        Si le personnage a la face tournée à droite alors
          (ax,ay)=(-1.0,0.0)
    */
    DoubleProperty ax = new SimpleDoubleProperty();
    DoubleProperty ay = new SimpleDoubleProperty();

    //coefficient de vitesse,
    //la vitesse de la particule est multipliée par la valeur de rate
    DoubleProperty rate = new SimpleDoubleProperty(0.2);

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

    public CowboyParticle(Image image, double x, double y,
                          double v_x, double v_y, AudioClip audioClip,
                          Double ax, double ay) {
        super(image);
        vx.set(v_x);
        vy.set(v_y);
        setX(x);
        setY(y);
        this.audio = audioClip;
        setax(ax);
        setay(ay);
        faireRotation();
    }

    /* faireRotation() fait tourner l'objet dee façon à ce que la face
    soit tournée vers la direction du vecteur de la vitesse.
    La méthode n'est pas terminée, et marche pour l'instant uniquement
    avec les Icones dont la face est par défaut
    tournée vers le bas ou vers le haut.
    */
    void faireRotation() {
        double tn = (ax.doubleValue() * vy.doubleValue() - ay.doubleValue() * vx.doubleValue())
                / (ax.doubleValue() * vx.doubleValue() + ay.doubleValue() * vy.doubleValue());
        double degrees = Math.toDegrees(Math.atan(tn));

        if (vy.getValue() > 0) {
            degrees = 180 + degrees;
        }

        setRotate(degrees);
    }

    DoubleProperty movexProperty() {
        return movex;
    }

    DoubleProperty moveyProperty() {
        return movey;
    }

    DoubleProperty vxProperty() {
        return vx;
    }

    DoubleProperty vyProperty() {
        return vy;
    }

    DoubleProperty axProperty() {
        return ax;
    }

    DoubleProperty ayProperty() {
        return ay;
    }

    DoubleProperty massProperty() {
        return mass;
    }

    DoubleProperty rateProperty() {
        return rate;
    }

    void setay(double v) {
        ay.set(v);
    }

    void setax(double v) {
        ax.set(v);
    }

    void setmovex(double v) {
        movex.set(v);
    }

    void setmovey(double v) {
        movey.set(v);
    }

    double getVx() {
        return vx.getValue();
    }

    void setVx(double v) {
        vx.set(v);
    }

    double getMovex() {
        return movex.getValue();
    }

    double getMovey() {
        return movey.getValue();
    }

    double getVy() {
        return vy.getValue();
    }

    void setVy(double v) {
        vy.set(v);
    }

    double getax() {
        return ax.getValue();
    }

    double getay() {
        return vy.getValue();
    }

    double getMass() {
        return mass.getValue();
    }

    void setMass(double m) {
        mass.set(m);
    }

    double getRate() {
        return rate.getValue();
    }

    void setRate(double r) {
        rate.setValue(r);
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


        setX(getX() * getMovex());
        setY(getY() * getMovey());
        /*verifier si on ne sort pas sur une position
          avec x negatif*/
        if (getX() < 0 && getVx() < 0) {
            setVx(-getVx());
            faireRotation();
            if (audio != null)
                audio.play();
        }
        /* verifier si on ne sort pas sur une position
        avec y negatif*/
        if (getY() < 0 && getVy() < 0) {
            setVy(-getVy());
            faireRotation();
            if (audio != null)
                audio.play();
            //audio.play();
        }
        /*verifier si on ne sort pas sur le bord droit du parent*/
        if (getX() + getBoundsInLocal().getWidth()
                > ((Region) getParent()).getWidth()
                && getVx() > 0) {
            setX(((Region) getParent()).getWidth() - getBoundsInLocal().getWidth());
            setVx(-getVx());
            faireRotation();
            if (audio != null)
                audio.play();
            //audio.play();
        }
        /*verifier si on ne sort pas sur le bord en bas du parent*/
        if (getY() + getBoundsInLocal().getHeight()
                > ((Region) getParent()).getHeight()
                && getVy() > 0) {
            setY(((Region) getParent()).getWidth() - getBoundsInLocal().getHeight());
            setVy(-getVy());
            faireRotation();
            if (audio != null)
                audio.play();
            //audio.play();
        }
    }

}

