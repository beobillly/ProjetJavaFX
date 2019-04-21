package sample;

import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;

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

    DemonParticle(Image image, double x, double y,
                  double v_x, double v_y, AudioClip audioClip,
                  double ax, double ay, double movex, double movey, double genre) {
        super(image, x, y, v_x, v_y, audioClip, ax, ay, movex, movey, genre);
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
        double delta = 3 * get_Rate();
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
        if (getX() > 900 && get_VX() > 0) {
            set_VX(-get_VX());
            faireRotation();
            if (audio != null)
                audio.play();
            //audio.play();
        }

        /*verifier si on ne sort pas sur le bord en bas du parent*/
        if (getY() > 700 && get_VY() > 0) {
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
}

