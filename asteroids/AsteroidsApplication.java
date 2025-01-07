package asteroids;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.animation.AnimationTimer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicInteger;


public class AsteroidsApplication extends Application {

    public static int WIDTH = 800;
    public static int HEIGHT = 600;
    
    private static int numOfAstroidsAtStart = 5;
    private static int maxNumOfProjectiles = 10;
    private static boolean pointsOverTime = true;
    private static double newAstroidRandomValue = 0.010;    //0.005 = 0.5%
                            
    @Override
    public void start(Stage window) throws Exception {
        
        //Background
        Pane pane = new Pane();
        pane.setPrefSize(WIDTH, HEIGHT);
        
        //SCORE
        Text score = new Text(10, 20, "Points: 0");
        pane.getChildren().add(score);
        AtomicInteger points = new AtomicInteger();
        
        
        //SHIP                      
        Ship ship = new Ship(WIDTH / 2, HEIGHT / 2);
        pane.getChildren().add(ship.getCharacter());
        List<Projectile> projectiles = new ArrayList<>();
        
        //ASTEROIDS
        Random random = new Random();
        List<Asteroid> asteroids = new ArrayList<>();
        
        for(int i = 0; i < numOfAstroidsAtStart; ++i){
            //Random random = new Random();
            asteroids.add(new Asteroid(random.nextInt(WIDTH / 3), random.nextInt(HEIGHT) ) );
        }
        
        asteroids.forEach( asteroid -> pane.getChildren().add(asteroid.getCharacter()) );
        asteroids.forEach( asteroid -> {
            asteroid.turnRight();
            asteroid.turnRight();
            asteroid.accelerate();
            asteroid.accelerate(); 
            asteroid.accelerate();            
        });
        
        
        //SCENE
        Scene scene = new Scene(pane);

        //TURNING SHIP LEFT RIGHT
        //Recording key presses
        Map<KeyCode, Boolean> pressedKeys = new HashMap<>();
        
        scene.setOnKeyPressed( event -> {
            if ( event.getCode() != KeyCode.SPACE){
                pressedKeys.put(event.getCode(), Boolean.TRUE);
            }else if(event.getCode() == KeyCode.SPACE && projectiles.size() < maxNumOfProjectiles){
                Projectile projectile = new Projectile( (int) ship.getCharacter().getTranslateX(), (int) ship.getCharacter().getTranslateY() );
                projectile.getCharacter().setRotate(ship.getCharacter().getRotate());
                projectiles.add(projectile);

                projectile.accelerate();
                projectile.setMovement(projectile.getMovement().normalize().multiply(3));

                pane.getChildren().add(projectile.getCharacter());
            }
        });
        scene.setOnKeyReleased( event -> {
            if ( event.getCode() != KeyCode.SPACE){
                pressedKeys.put(event.getCode(), Boolean.FALSE);
            }
        });
        
        
        
    
        
        //HANDLING CHARACTER BEHAVIOR
        new AnimationTimer() {
            @Override
            public void handle(long now){
                
                //SHIP MOVEMENT
                if(pressedKeys.getOrDefault(KeyCode.LEFT, false)){
                    ship.turnLeft();
                }
                if(pressedKeys.getOrDefault(KeyCode.RIGHT, false)){
                    ship.turnRight();
                }
                
                if(pressedKeys.getOrDefault(KeyCode.UP, false)){
                    ship.accelerate();
                }
                
                //SHOOT PROJECTILES this part moved up
                /*
                if(pressedKeys.getOrDefault(KeyCode.SPACE, false) && projectiles.size() < maxNumOfProjectiles ){
                    Projectile projectile = new Projectile( (int) ship.getCharacter().getTranslateX(), (int) ship.getCharacter().getTranslateY() );
                    projectile.getCharacter().setRotate(ship.getCharacter().getRotate());
                    projectiles.add(projectile);
                    
                    projectile.accelerate();
                    projectile.setMovement(projectile.getMovement().normalize().multiply(3));
                    
                    
                    pane.getChildren().add(projectile.getCharacter());  
                } */
                
                //Move everything
                ship.move();
                asteroids.forEach( asteroid -> asteroid.move());
                projectiles.forEach( projectile -> projectile.move() );
                
                //COLLISIONS

                //when a projectile hits an asteroid, both get un-alived
                projectiles.forEach( projectile -> {
                    asteroids.forEach( asteroid -> {
                        if(projectile.collide(asteroid)){
                            projectile.setAlive(false);
                            asteroid.setAlive(false);
                            //add score
                            score.setText("Points: " + points.addAndGet(1000));
                        }                    
                    });
                });
                //remove dead projectile characters from pane
                projectiles.stream()
                        .filter( projectile -> !projectile.isAlive())
                        .forEach( projectile -> pane.getChildren().remove(projectile.getCharacter()));
                //remove dead projectiles from List
                projectiles.removeAll(projectiles.stream()
                        .filter(projectile -> !projectile.isAlive())
                        .collect(Collectors.toList()));
                //now same for Asteroids, should refactor ( like maybe a method)
                asteroids.stream()
                        .filter( asteroid -> !asteroid.isAlive())
                        .forEach( asteroid -> pane.getChildren().remove(asteroid.getCharacter()));
                asteroids.removeAll(asteroids.stream()
                        .filter(asteroid -> !asteroid.isAlive())
                        .collect(Collectors.toList()));
                
                
                //asteroids hitting ships ends the game
                asteroids.forEach(asteroid -> {
                    if(ship.collide(asteroid)){
                        stop();
                    }
                });
                
                
                //randomly adds asteroids   (Animation Timer ticks roughly 60x per second)
                //in 0.5% of ticks a new asteroid is added
                if(Math.random() < newAstroidRandomValue) {
                    Asteroid asteroid = new Asteroid(random.nextInt(WIDTH / 3), random.nextInt(HEIGHT));
                    if(!asteroid.collide(ship)){
                        asteroids.add(asteroid);
                        pane.getChildren().add(asteroid.getCharacter());
                    }
                    
                }
                
                //handle running score (points for staying alive)
                if(pointsOverTime){
                    score.setText("Points: " + points.incrementAndGet());
                }
            }
        }.start();
        
        
        
        
        
        
        
        //WINDOW
        window.setTitle("Asteroids!");
        window.setScene(scene);
        window.show();
        
    }
    
    
    public static void main(String[] args) {
        launch(args);
    }

    public static int partsCompleted() {
        
    // State how many parts you have completed using the return value of this method
        return 4;
    }

}
