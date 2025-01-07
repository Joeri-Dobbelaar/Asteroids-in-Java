
package asteroids;

import javafx.scene.shape.Polygon;

public class Asteroid extends Character {
    
    public Asteroid(int x, int y){
        super(new PolygonFactory().createPolygon(), x, y);
        
    }
    
}
