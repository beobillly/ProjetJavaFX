package sample;

import javafx.scene.image.Image;

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
}
