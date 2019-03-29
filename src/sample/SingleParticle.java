package sample;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioClip;

abstract class SingleParticle extends ImageView {


    /*audioClip à jouer si on cogne sur les parois de parent*/
    final AudioClip audio;

    final DoubleProperty vx = new SimpleDoubleProperty();
    final DoubleProperty vy = new SimpleDoubleProperty();
    final DoubleProperty mass = new SimpleDoubleProperty(1.0);

    final DoubleProperty degre = new SimpleDoubleProperty(0.0);
    final DoubleProperty movex = new SimpleDoubleProperty();
    final DoubleProperty movey = new SimpleDoubleProperty();
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
    final DoubleProperty ax = new SimpleDoubleProperty();
    final DoubleProperty ay = new SimpleDoubleProperty();

    final DoubleProperty genre = new SimpleDoubleProperty();

    //coefficient de vitesse,
    //la vitesse de la particule est multipliée par la valeur de rate
    final DoubleProperty rate = new SimpleDoubleProperty(0.1);

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

    SingleParticle(Image image, double x, double y,
                   double v_x, double v_y, AudioClip audioClip,
                   double ax, double ay, double movex, double movey, double genre) {
        super(image);
        vx.set(v_x);
        vy.set(v_y);
        setX(x);
        setY(y);
        this.audio = audioClip;
        set_AX(ax);
        set_AY(ay);
        set_MoveX(movex);
        set_MoveY(movey);
        set_Genre(genre);
        faireRotation();
    }

    /* faireRotation() fait tourner l'objet dee façon à ce que la face
    soit tournée vers la direction du vecteur de la vitesse.
    La méthode n'est pas terminée, et marche pour l'instant uniquement
    avec les Icones dont la face est par défaut
    tournée vers le bas ou vers le haut.
    */
    void faireRotation() {
        double degrees = get_Degre();

        if (vx.getValue() != 0 || vy.getValue() != 0) {
            degrees = Math.toDegrees(Math.atan(vy.getValue() / vx.getValue())) + 90;
            set_Degre(degrees);
        }
        if (vx.getValue() < 0) {
            degrees += 180;
        }

        setRotate(degrees);
    }

    DoubleProperty vxProperty() {
        return vx;
    }

    DoubleProperty degreProperty() {
        return degre;
    }

    DoubleProperty vyProperty() {
        return vy;
    }

    DoubleProperty genreProperty() {
        return genre;
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

    double get_MoveX() {
        return movex.getValue();
    }

    void set_MoveX(double v) {
        movex.set(v);
    }

    double get_Degre() {
        return degre.getValue();
    }

    void set_Degre(double v) {
        degre.set(v);
    }

    double get_Genre() {
        return genre.getValue();
    }

    void set_Genre(double v) {
        genre.set(v);
    }

    double get_MoveY() {
        return movey.getValue();
    }

    void set_MoveY(double v) {
        movey.set(v);
    }

    double get_VX() {
        return vx.getValue();
    }

    void set_VX(double v) {
        vx.set(v);
    }

    double get_VY() {
        return vy.getValue();
    }

    void set_VY(double v) {
        vy.set(v);
    }

    double get_AX() {
        return ax.getValue();
    }

    void set_AX(double v) {
        ax.set(v);
    }

    double get_AY() {
        return ay.getValue();
    }

    void set_AY(double v) {
        ay.set(v);
    }

    double get_Mass() {
        return mass.getValue();
    }

    void set_Mass(double m) {
        mass.set(m);
    }

    double get_Rate() {
        return rate.getValue();
    }

    void set_Rate(double r) {
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
    abstract void move();

}

