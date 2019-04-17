package sample;

import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.media.AudioClip;

public class BulletParticle extends SingleParticle {

    private int bounceCount;

    public BulletParticle(Image image, double x, double y,
                          double v_x, double v_y, AudioClip audioClip,
                          double ax, double ay, double movex, double movey, double genre) {

        super(image, x, y, v_x, v_y, audioClip, ax, ay, movex, movey, genre);
        bounceCount = 0;
    }

    int getBounceCount() {
        return this.bounceCount;
    }

    void move() {
        double delta = 8 * get_Rate();
        setX(getX() + get_VX() * delta);
        setY(getY() + get_VY() * delta);
        /*verifier si on ne sort pas sur une position
          avec x negatif*/
        if (getX() < 0 && get_VX() < 0) {
            bounceCount++;
            set_VX(-get_VX());
            faireRotation();
            if (audio != null)
                audio.play();
        }
        /* verifier si on ne sort pas sur une position
        avec y negatif*/
        if (getY() < 0 && get_VY() < 0) {
            bounceCount++;
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
            bounceCount++;
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
            bounceCount++;
            setY(((Region) getParent()).getWidth() - getBoundsInLocal().getHeight());
            set_VY(-get_VY());
            faireRotation();
            if (audio != null)
                audio.play();
            //audio.play();
        }
    }

    public String toString() {
        return "balle";
    }
}
