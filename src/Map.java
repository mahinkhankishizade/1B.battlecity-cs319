import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Map {


    private final int TILES = 20;
    private final int MAP_DIMENSION = 32;
    private final double SHIFT = 0.33;
    private Scene mapScene;
    private Stage mapStage;
    private int playerCount;
    private int level;
    private int remainingBots;
    private int botCount;
    private GameObject[][] gameObjects;
    private int[][] obstaclesMap;
    private Pane mapPane;
    private Player players[];
    private ArrayList<Bullet> bullets;
    private ArrayList<Tank> tanks;
    private ArrayList<Bot> bots;
    private ArrayList<Bonus> bonuses;
    private ArrayList<GameObject> objectHolder;
    private ArrayList<Destructible> destructibles;
    private int lifeBonusCount;
    private int speedBonusCount;

    /* GameObject File Decode
    * 0 = Brick, 1 = Wall, 2 = Bush, 3 = Water
    * 4 = Player, 5 = Bot
    * */
    public Map(int playerCount, int level, int[][] obstaclesMap){
        createObjectArrays();
        this.obstaclesMap = obstaclesMap;
        this.playerCount = playerCount;
        this.level = level;
        initMapObjects();
        intToObject();
        addObjects();
        initPlayers();
        lifeBonusCount = 0;
        speedBonusCount = 0;
    }

    //Init all objects
    private void initMapObjects(){
        mapPane = new Pane();
        gameObjects = new GameObject[TILES][TILES];
        players = new Player[playerCount];
        mapPane.setPrefWidth(640);
        mapPane.setPrefHeight(680);
        botCount = 10 + 2 * level; // WOW lol
        remainingBots = botCount;
    }

    //Create map holder arrays
    private void createObjectArrays(){
        bullets = new ArrayList<>();
        bots = new ArrayList<>();
        bonuses = new ArrayList();
        objectHolder = new ArrayList<GameObject>();
        tanks = new ArrayList<Tank>();
        destructibles = new ArrayList<Destructible>();
    }

    //Decide how to spawn a bot
    public void spawnBot(){
            Bot bot = new Bot( 100, 30);
            mapPane.getChildren().addAll(bot.getView());
            bots.add(bot);
            tanks.add(bot);
            objectHolder.add(bot);
            remainingBots--;
    }

    public Stage getMapStage() {
        return mapStage;
    }

    public void newBonus( int type) {
        if( type == 0 && lifeBonusCount < 2) { // there should be a time between the creation of bonuses and the bonuses should not be released on the obstacles
            Bonus lifeBonus = new LifeBonus((int)(Math.random()*30) + 1, (int)(Math.random()*30) + 1);
            lifeBonus.setReleased(true);
            mapPane.getChildren().addAll(lifeBonus.getView());
            lifeBonusCount++;
            bonuses.add(lifeBonus);
            objectHolder.add(lifeBonus);
        }
        else if( type == 1 && speedBonusCount < 2) {
            Bonus speedBonus = new SpeedBonus((int)(Math.random()*30) + 1, (int)(Math.random()*30) + 1);
            speedBonus.setReleased(true);
            mapPane.getChildren().addAll(speedBonus.getView());
            speedBonusCount++;
            bonuses.add(speedBonus);
        }
    }


    private void initPlayers(){
        for(int i = 0; i < playerCount; i++){
            players[i] = new Player(2, 2);
        }
        for( Player player : players){
            tanks.add(player);
            mapPane.getChildren().addAll(player.getView());
        }
    }

    //Update Methods
    //Update of Tanks
    public void updateTanks(){
        updatePlayer();
        updateBots();
    }

    private void updatePlayer(){
        for ( Player player : players){
            if ( player.getHealth() >= 0)
                player.draw();
            else{
                mapPane.getChildren().remove(player.getView());
            }
        }
    }

    private void updateBots() {
        for ( Bot bot : bots){
            if ( bot.getHealth() >= 0)
                bot.draw();
            else{
                mapPane.getChildren().remove(bot.getView());
            }
        }
    }

    //Update of Bullets
    public void updateBullets(){
        for( Bullet bullet : bullets) {
            if (bullet.isCrushed()) {
                mapPane.getChildren().remove(bullet.getView());
            } else {
                bullet.move();
            }
        }
    }

    //Update Methods
    public void updateDestructibles() {
        for( Destructible destructible: destructibles) {
            if (destructible.isDestructed())
                mapPane.getChildren().remove(destructible.getView());
            else
                destructible.draw();
        }
    }

    public void updateBonuses() {
        for( Bonus bonus : bonuses) {
            if( bonus.isTaken()) {
                mapPane.getChildren().remove(bonus.getView());
                objectHolder.remove(bonus);
            }
            else
                bonus.draw();
        }
        bonuses.removeIf(Bonus::isTaken);
    }

    public Player getPlayer( int index){
        try {
            return players[index];
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }
        return null;
    }

    public Pane getMapPane() {
        return mapPane;
    }

   /* public void showMap(){
        mapScene = new Scene( mapPane);
        mapStage = new Stage();
        mapStage.setScene(mapScene);
        mapStage.show();
    }*/

    private void intToObject(){
        for(int i = 0; i < TILES; i++){
            for(int j = 0; j < TILES; j++) {
                int cordinate_x = i * MAP_DIMENSION;
                int cordinate_y = j * MAP_DIMENSION;
                Tile tile = new Tile(cordinate_x ,cordinate_y);
                mapPane.getChildren().addAll( tile.getView());
                tile.draw();
                if(obstaclesMap[i][j] == 0){
                    continue;
                }
                else {
                    if (obstaclesMap[i][j] == 1) {
                        Brick brick = new Brick(cordinate_x,cordinate_y, 0);
                        objectHolder.add( brick);
                        destructibles.add( brick);
                        brick.draw();
                    } else if (obstaclesMap[i][j] == 2) {
                        Bush bush = new Bush( cordinate_x, cordinate_y);
                        objectHolder.add( bush);
                        bush.draw();
                    } else if (obstaclesMap[i][j] == 3) {
                        IronWall ironWall = new IronWall( cordinate_x, cordinate_y);
                        objectHolder.add( ironWall);
                        ironWall.draw();
                    } else if (obstaclesMap[i][j] == 4) {
                        Water water = new Water(cordinate_x,cordinate_y);
                        objectHolder.add( water);
                        water.draw();
                    }
                    else if (obstaclesMap[i][j] == 5) {
                        Brick brick = new Brick(cordinate_x,cordinate_y, 1);
                        objectHolder.add( brick);
                        destructibles.add( brick);
                        brick.draw();
                    }
                    else if (obstaclesMap[i][j] == 6) {
                        Brick brick = new Brick(cordinate_x,cordinate_y, 2);
                        objectHolder.add( brick);
                        destructibles.add( brick);
                        brick.draw();
                    }
                }
            }
        }
    }

    public void addObjects() {
        for (GameObject gameObject : objectHolder) {
            gameObject.draw();
            mapPane.getChildren().add(gameObject.getView());
        }
    }

    public void fire(Tank tank){
        Bullet fired = tank.fire();
        mapPane.getChildren().addAll(fired.getView());
        bullets.add(fired);
        System.out.println( bullets.toString());
    }

    public void addObjects(GameObject[][] gameObjects){
        this.gameObjects = gameObjects;
    }
    public void updateObjects(){
        for(int i = 0; i < bullets.size(); i++){
            bullets.get(i).move();
        }
    }

    public void finishMap(){

    }

    public boolean tryNextMove( Tank tank, int dir){
        ImageView tankView = tank.getView();
        for( GameObject gameObject : objectHolder){
            tankView.setVisible(true);
            if( tankView.getBoundsInParent().intersects( gameObject.getView().getBoundsInParent())){
                if( gameObject.isPassableByTanks()){
                    if(gameObject.isHideable()){
                        tankView.setVisible( false);
                        return true;
                    }
                }else{
                    switch ( dir){
                        case 0:
                            tank.setxLoc( gameObject.getxLoc() - tankView.getFitWidth()-SHIFT);
                            break;
                        case 1:
                            tank.setxLoc(gameObject.getxLoc() + gameObject.getView().getFitWidth()+SHIFT);
                            break;
                        case 2:
                            tank.setyLoc( gameObject.getyLoc() - gameObject.getView().getFitHeight()+SHIFT);
                            break;
                        case 3:
                            tank.setyLoc( gameObject.getyLoc() + gameObject.getView().getFitHeight()  +SHIFT);
                            break;
                    }
                    return false;
                }
            }
        }
        return true;
    }

    public boolean bonusTaken( Bonus bonus, Tank tank, int dir) {
        ImageView tankView = tank.getView();
        ImageView bonusView = bonus.getView();

        for( GameObject gameObject : objectHolder) {
            if( tankView.getBoundsInParent().intersects( bonusView.getBoundsInParent())) {
                bonusView.setVisible(false);
                bonus.setTaken(true);
            }
        }
        return true;
    }

    // getters and setters

    public int getRemainingBots() {
        return remainingBots;
    }


    public int getAliveBots(){ return bots.size(); }


    public ArrayList<GameObject> getGameObjects() {
        return objectHolder;
    }


    public ArrayList<Tank> getTanks() {
        return tanks;
    }


    public GameObject[][] getGameObjectsArray(){
        return gameObjects;
    }

    public ArrayList<Bot> getBots() {
        return bots;
    }

    public ArrayList<Bullet> getBullets() {
        return bullets;
    }

    private int getLifeBonusCount() {
        return lifeBonusCount;
    }

    private void setLifeBonusCount(int newCount) {
        lifeBonusCount = newCount;
    }

    private int getSpeedBonusCount() {
        return speedBonusCount;
    }

    private void setSpeedBonusCount(int newCount) {
        speedBonusCount = newCount;
    }


}