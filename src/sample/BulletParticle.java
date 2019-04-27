package sample;

import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.media.AudioClip;

import java.io.FileWriter;
import java.io.IOException;

public class BulletParticle extends SingleParticle {

    private int bounceCount;


    public BulletParticle(Image image, double x, double y,
                          double v_x, double v_y, AudioClip audioClip,
                          double genre, CowboyParticle cowboy) {

        super(image, x, y, v_x, v_y, audioClip, genre);
        bounceCount = 0;

    }

    public BulletParticle(Image image, double x, double y,
                          double v_x, double v_y, AudioClip audioClip) {

        super(image, x, y, v_x, v_y, audioClip, -1);
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

    protected void writeParticle(FileWriter writer) throws IOException {

        writer.write("PARTICLE");
        writer.write("#");

        writer.write("BULLET");
        writer.write("#");

        writer.write(this.getImage().impl_getUrl());
        writer.write("#");

        writer.write(this.getX() + ";" + this.getY());
        writer.write("#");

        writer.write(this.get_VX() + ";" + this.get_VY());
        writer.write("#");

    }
}
