package sample;

import javafx.scene.image.Image;

import java.io.FileWriter;
import java.io.IOException;

public class ObstacleParticle extends SingleParticle {

    public ObstacleParticle(Image image, double x, double y) {
        super(image, x, y, 0, 0, null, 0, 0, 0, 0, -2);

    }

    public void move() {
        //It doesn't move. That's what it does.
    }

    @Override
    public String toString() {
        return "obstacle";
    }

    protected void writeParticle(FileWriter writer) throws IOException {

        writer.write("PARTICLE");
        writer.write("#");

        writer.write("IMPEDIMENT");
        writer.write("#");

        writer.write(this.getImage().impl_getUrl());
        writer.write("#");

        writer.write(this.getX() + ";" + this.getY());
        writer.write("#");

    }
}
