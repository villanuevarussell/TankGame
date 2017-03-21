/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tankgame;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.awt.Image.*;
import java.awt.image.*;
import java.net.URL;
import java.io.*;
import java.text.AttributedString;
import java.util.*;
import javax.imageio.*;
import javax.sound.midi.*;
import javax.swing.*;
import myGames.*;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * The goal of the game is to destroy enemy boss. For one player the controls
 * are: arrow keys control movement, enter is fire For the second player: wasd
 * for movement and space to fire You get 2 extra lives shared between the
 * players Player 1 spawns on the right and Player 2 on the left Player 1 has
 * the bottom life bar in the lower left corner and Player 2 has the top life
 * bar. I got an error trying to run this in Chrome but it runs in Internet
 * Explorer and as an independent program.
 *
 * @author Lowell Milliken
 */
public class TankGame extends Game {

    private GameSpace screen,screen2;
    private BottomPanel bottom;
    private Random random = new Random();
    private ArrayList<ArrayList> everything;
    private ArrayList<Thing> things;
    private ArrayList<PlayerParent> players;
    private ArrayList<Wall> walls;
    private int score;
    final private int startPoint = -30;
    final private int backgroundspeed = 0;
    private Image[] ebullet, smallexpl, largeexpl, mybullet[], playerimg[],wallImg[],rocket[],bouncing[];
    private BufferedImage[] tankImg, powerup, rockettemp;
    
    private Image leftscreen, rightscreen,minimap;
    private PlayerTank tank1,tank2;
    

    private GameController gcontroller;
    private TankEvents events;
    private ArrayList<ScoreType> sTable;
    private boolean gameover;
    private boolean destroy = false;
    private boolean twoplayers = false;
    private int lives;
    public Image lifeImg;
    private URL[] explsoundurl;
    


    //creates and adds all the game panel to the applet
    //also sets up images, sounds, and creates and initializes state for most
    //variables and objects.
    @Override
    public void init() {
        super.init();
        
        screen = new GameSpace(getSprite("Resources/Background.jpg"), new DrawAbs());
        screen.setBackSpeed(backgroundspeed);
        screen.setBackDirection(0);
        
        screen2 = new GameSpace(getSprite("Resources/Background.jpg"), new DrawAbs());
        screen2.setBackSpeed(backgroundspeed);
        screen2.setBackDirection(0);


        events = new TankEvents();
        KeyControl keys = new KeyControl(events);
        addKeyListener(keys);
        
        tank1 = new PlayerTank(16*60, 11*60, 0., 6, playerimg[0],mybullet[0],
                     events, 30, 20, 30, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT,
                    KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_ENTER,
                    KeyEvent.VK_DELETE, 10, 5, largeexpl.length,0);
        tank2 = new PlayerTank( 4*60, 3*60, 0., 6, playerimg[1],mybullet[0],
                    events, 30, 10, 30, KeyEvent.VK_A, KeyEvent.VK_D,
                    KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_SPACE,
                    KeyEvent.VK_SHIFT, 10, 5, largeexpl.length,0);
 
        
        add(screen, BorderLayout.CENTER);
        setBackground(Color.white);
        
        
        
        everything = new ArrayList<ArrayList>();
        things = new ArrayList<Thing>();
        everything.add(things);
        players = new ArrayList<PlayerParent>();
        everything.add(players);
        walls = new ArrayList<Wall>();
        everything.add(walls);
        
        
        
        gcontroller = new GameController();
                        

        


        



        


        bottom = new BottomPanel(getSprite("Resources/bottom_1.png"));




        score = 0;


        gameover = false;

        Object[] options = {"Exit", "Play"};
        
        ImageIcon icon = new ImageIcon("title.png");

        int n = JOptionPane.showOptionDialog(this, "TANK GAME", "Welcome",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, icon,
                options, options[0]);



        if (n == 1) {
            twoplayers = true;
        }
    }

    //getting all image files
    @Override
    public void initImages() {
        try {
            //initializes Image and BufferedImage for the tank
            tankImg = new BufferedImage[60];
            playerimg = new Image[2][1];
            
            //sets the tank_strip into image that will be split up
            BufferedImage buffed = convertToBuffered(getSprite("Resources/tank_strip.png"));
            //splits the image using SubImage
            for (int i = 0; i < 60; i++) {
            tankImg[i] = buffed.getSubimage(i * 64, 0, 64, 64);
            }
            //initializes the player image to the buffered tank image
            playerimg[0][0] = tankImg[0];
            playerimg[1][0] = tankImg[0];
            
            //splits the Powerup strip
            powerup = new BufferedImage[3];
            buffed = convertToBuffered(getSprite("Resources/PowerupStrip.png"));
            for (int i = 0; i < 3; i++) {
             powerup[i] = buffed.getSubimage(i * 32, 0, 32, 32);
            }
            bouncing = new Image[1][1];
            bouncing[0][0] = powerup[1];
            
            //splits the rocket strip into gif
            rockettemp = new BufferedImage[60];
            rocket = new Image[1][1];
            buffed = convertToBuffered(getSprite("Resources/Rocket_Strip.png"));

            for (int i = 0; i < 60; i++) {
                rockettemp[i] = buffed.getSubimage(i * 24, 0, 24, 24);
            }
            
            largeexpl = new Image[7];
            for (int i = 0; i < 7; i++) 
            {
             largeexpl[i] = getSprite("Resources/explosion2_" + (i + 1) + ".png");
            }
            
            rocket[0][0] = rockettemp[15];
            
            
            //initializes bullet image
            mybullet = new Image[1][1];
            mybullet[0][0] = getSprite("Resources/bullet.png");
            //initializes life image on the bottom of screen
            lifeImg = getSprite("Resources/life.png");
            
            //initializes wall sprite
            wallImg= new Image[2][1];
            wallImg[0][0] = getSprite("Resources/NonBreakableWall.png");
            wallImg[1][0] = getSprite("Resources/BreakableWall.png");
            
            
            
            //catches if error getting image
             } catch (Exception e) {
            System.out.println("Error in getting images: " + e.getMessage());
            

            
            
        }
    }

    //getting all sound files
    @Override
    public void initSound() {
        try {
            Sequence music;
            Sequencer seq;
            URL musicu = TankGame.class.getResource("Resources/background.mid");
            explsoundurl = new URL[2];
            explsoundurl[0] = TankGame.class.getResource("Resources/snd_explosion1.wav");
            explsoundurl[1] = TankGame.class.getResource("Resources/snd_explosion2.wav");

            music = MidiSystem.getSequence(musicu);
            seq = MidiSystem.getSequencer();
            seq.open();
            seq.setSequence(music);
            seq.start();
            seq.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            System.out.println("Error in midi: " + e.getMessage());
        }
    }

    //this stores scores paired with names
    public class ScoreType {

        private String name;
        private int score;

        public ScoreType(String name, int score) {
            this.name = name;
            this.score = score;
        }

        public int getScore() {
            return score;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    //this creates Things when needed to make the gameplay pattern
    public class GameController {

        private int timer;

        public GameController() {
            timer = 0;
        }

        public void timeline() {
            switch (timer) {
                case 0:

                    if (twoplayers) {
                    players.add(tank1);
                    players.add(tank2);
                    //screen.setDrawType(new DrawRel(players.get(0))); 

 
                        
                    } else {
                        
                         System.exit(0);
                        
                    }


                    
                   for(int y = 0; y< 40 ; y++)
                   {
                        for (int x = 0; x < 40; x++)
                       {
                            if(x==0|| x==39||y == 0||y == 34)
                            {

                              makeNonBreakableWall(x*32,y*22);
                    
                            }
                            
                            if((x >5 && x<10) && (y==6 || y==10 ))
                            {
                                makeNonBreakableWall(x*32,y*22);
                            }
                            if((x==6) && (y>6 && y <10 ))
                            {
                                makeNonBreakableWall(x*32,y*22+10);
                            }
                            
                            if((x >27 && x<32) && (y==28 || y==32))
                            {
                                makeNonBreakableWall(x*32,y*22);
                            }
                            if((x==28) && (y>28 && y <32 ))
                            {
                                makeNonBreakableWall(x*32,y*22+10);
                            }
                            
                            if(y>=17 && y <=20)
                            {
                              if(x<= 5 || x>=34)
                              {
                               makeNonBreakableWall(x*32,y*22);
                              }
                              
                              if(x>=12 && x <=17)
                              {
                               makeNonBreakableWall(x*32,y*22);
                              }
                              if(x>=22&& x<= 27)
                              {
                               makeNonBreakableWall(x*32,y*22);
                              }
                            }
                            if(x>18 && x < 21 && y > 0 && y < 12)
                            {
                                makeBreakableWall(x*32,y*22+8);
                                
                            }
                            if(x>18 && x< 21 && y > 27&& y <36)
                            {
                               makeBreakableWall(x*32,y*22);
                            }

                       }
                        
                       
                    }

      break;         
      case 1:

      randompowerup(randomX(),randomY(),random.nextInt(1)+1);
      
        break;
      } 
      timer++;
      if(timer == 400)
      {
       timer =1;
      }
     }

        //image to get a random X coordinate
        private int randomX() {
            return random.nextInt(screen.getWidth());
        }
        //image to get a random Y coordinate
        private int randomY() {
            return random.nextInt(screen.getHeight());
        }
        //resets timer
        public void resetTimer() {
            timer = 0;
        }
    }
    //class used for a breakable wall
    public class BreakWall extends Wall
    {
        public BreakWall(int x, int y, int direction, int speed, Image[] img, 
                GameEvents events, int maxdamage, int damageto, int eps,int type)
        {
                super(x,y,direction, speed, img, events, maxdamage, damageto, eps,type);
        }
        //if the wall is hit then it explodes the wall and removes it
        public void hitMe(Thing u)
        {
            if(u instanceof PowerUp)
            {
              u.itHit(this);
            }else
            {
            things.add(new Explosion(getX(),getY(), largeexpl, getEvents(), 1,
            explsoundurl[0]));
            u.itHit(this);
            walls.remove(this);
            }

        }
 
       
                
    }
    
    //this handles unbreakable walls
    public class NoBreakWall extends Wall
    {
        public NoBreakWall(int x, int y, int direction, int speed, Image[] img, 
                GameEvents events, int maxdamage, int damageto, int eps,int type)
        {
                super(x,y,direction, speed, img, events, maxdamage, damageto, eps,type);
        }
        
        //handles the wall being hit but does not destroy it
        public void hitMe(Thing u)
        {
            if(u instanceof PowerUp)
            {
                
            }else
            {
            u.itHit(this);
            things.add(new Explosion(getX(),getY(), largeexpl, getEvents(), 1,
            explsoundurl[0]));

            }
        }
                
    }
    
    //method to call a non breakable wall
    public void makeNonBreakableWall(int x,int y)
    {
      walls.add(new NoBreakWall(x,y,0,0,wallImg[0],events,10,0,0,0));
    }
    //method to call a breakable wall
    public void makeBreakableWall(int x,int y)
    {
      walls.add(new BreakWall(x,y,0,0,wallImg[1],events,10,0,0,0));
    }
    
    //class used for bounce power up
    public class RandomPickup extends PowerUp{
        public RandomPickup(int x, int y, double direction, int speed, Image[] img, GameEvents events, int source, ArrayList ev, int type){
            super(x,y,direction,speed,img,events,source,ev,type);
        }
        
        public void itHit(Unit u)
        {
          if(u instanceof PlayerParent)
          {
           if( this.type > 0)
           {
               PlayerParent p = (PlayerParent) u;
               p.setPower(type);
           }
           
           setDone(true);
          }
          else if(u instanceof Wall)
          {
           this.setX(random.nextInt(1280));
           this.setY(random.nextInt(896));
          }
        }
        

    }
    //method used to spawn a random power up
    public void randompowerup(int x, int y, int type)
    {            
      things.add(new RandomPickup(x,y,0,0,powerup,events,2,everything,type));           
    }
    
    //This is the player's tank
    public class PlayerTank extends PlayerParent {

        private int startx, starty, spawnDelay;
        private int mercyTimer;
        private int angle = 270;
        private int rotation = 0;
        private int lives = 3;
        private int width = 64;
        private int height = 64;
        private int source;




        

        public PlayerTank(int x, int y, double direction, int speed, Image[] tankimg, Image[] bullet,
                GameEvents events, int maxdamage, int damageto, int eps,
                int left, int right, int up, int down, int fire, int spfire,
                int shotTime, int fastShotTime, int deadTime,int type) {
            super(x, y, direction, speed, tankimg,bullet, events, maxdamage, damageto, eps,
                    left, right, up, down, fire, spfire, shotTime, fastShotTime,
                    deadTime,type);

            startx = x;
            starty = y;
            spawnDelay = 30;
            mercyTimer = 20;
            


        }

        //moves based on the keys pressed, but only with the basic update
        @Override
        public void move() {

            Wall tempwall;
            Iterator<Wall> itwalls = walls.listIterator();
            
            if (getMvLeft()) {
                //controls the direction and rotation of the tank  
                if (getX() - getSpeed() > 0) {
                    angle += 6;
                    rotation += 1;
                    if (rotation > 59) {
                        rotation = 0;
                    }

                    if (angle > 360) {
                        angle -= 360;
                    }
                    //controls which player image is being selected
                    if(this.getLeft() == KeyEvent.VK_LEFT)
                    {
                    playerimg[0][0] = tankImg[rotation];
                    }
                    else
                    {
                     playerimg[1][0] = tankImg[rotation];
                    }
                    

                } else {
                    setX(0);
                }
            }
            //controls if the button pressed is the right button
            if (getMvRight()) {
                //controls direction of tank
                if (getX() + getSpeed() < screen.getWidth()) {
                    angle -= 6;
                    rotation -= 1;
                    if (rotation < 0) {
                        rotation = 59;
                    }
                    if (angle < 0) {
                        angle += 360;
                    }
                    //adjusts the player image according to what key is pressed
                    if(this.getRight() == KeyEvent.VK_RIGHT)
                    {
                    playerimg[0][0] = tankImg[rotation];
                    }
                    else
                    {
                     playerimg[1][0] = tankImg[rotation];
                    }
   
                }
            }
            //moves up 
            if (getMvUp()) {
                if (getY() - getSpeed() > 0) {

                    changeX((int) (-getSpeed() * Math.sin((angle * Math.PI) / 180)));
                    changeY((int) (-getSpeed() * Math.cos((angle * Math.PI) / 180)));
                    

                    while(itwalls.hasNext())
                    {
                    tempwall = itwalls.next();
                    if(tempwall.collision(this.getX(),this.getY(),this.getWidth(),this.getHeight()))
                    {
                     if(angle <= 22 || angle >= 338)
                     {
                     setY(getY()+5);
                     }
                     if(angle > 22 && angle < 67)
                     {
                     setX(getX()+5);
                     setY(getY()+5);                     
                     }  
                     if(angle >= 67 && angle <=112)
                     {
                     setX(getX()+5);
                     }
                     if (angle > 112 && angle < 157)
                     {
                     setX(getX()+5);
                     setY(getY()-5);
                     }
                     if(angle >= 157 && angle <= 202)
                     {
                     setY(getY()-5);
                     }
                     if(angle > 202 && angle < 247)
                     {
                     setY(getY()-5);
                     setX(getX()-5);                    
                     }

                     if(angle <= 247 && angle >= 292)
                     {
                     setX(getX()-5);                     
                     }
                     if(angle > 292 && angle < 338)
                     {
                     setY(getY()+5);
                     setX(getX()-5);                    
                     }   
                    }
            
                 
            }
                } else {
                    setX(0);
                    setY(0);
                }                         
            }
            //moves down
            if (getMvDown()) {
                if (getY() + getSpeed() < screen.getHeight()) {
                    changeX((int) (getSpeed() * Math.sin((angle * Math.PI) / 180)));
                    changeY((int) (getSpeed() * Math.cos((angle * Math.PI) / 180)));  
                    
                  while(itwalls.hasNext())
                    {
                    tempwall = itwalls.next();
                    if(tempwall.collision(this.getX(),this.getY(),this.getWidth(),this.getHeight()))
                    {
                     if(angle <= 22 || angle >= 338)
                     {
                     setY(getY()-5);
                     }
                     if(angle > 22 && angle < 67)
                     {
                     setX(getX()-5);
                     setY(getY()-5);                     
                     }  
                     if(angle >= 67 && angle <=112)
                     {
                     setX(getX()-5);
                     }
                     if (angle > 112 && angle < 157)
                     {
                     setX(getX()-5);
                     setY(getY()+5);
                     }
                     if(angle >= 157 && angle <= 202)
                     {
                     setY(getY()+5);
                     }
                     if(angle > 202 && angle < 247)
                     {
                     setY(getY()+5);
                     setX(getX()+5);                    
                     }

                     if(angle <= 247 && angle >= 292)
                     {
                     setX(getX()+5);                     
                     }
                     if(angle > 292 && angle < 338)
                     {
                     setY(getY()-5);
                     setX(getX()+5);                    
                     }   
                    }
            }          
                }
            }

            if (getDamage() >= getMax()) {
                lives--;
                if (getPower() > 0) {
                    setPower(getPower() - 1);
                }

                setDone(true);
            }

            if (getShotDelay() > 0) {
                changeShotDelay(-1);
            }

            if (mercyTimer > 0) {
                mercyTimer--;
            }
        }

        //shoots with button pressed, but only with the basic update
        @Override
        public void action() {
            if (getIsFiring()) {
                if (getShotDelay() == 0) {
                    if (getPower() == 0) {
                        
                        setBullet(mybullet[0]);
                        shoots(-20,5, (((angle) * Math.PI) / 180));                  
                        
                        
                    } else if (getPower() == 1) 
                    {                      
                        setBullet(rocket[0]);
                        shoots(-20,30,(((angle) * Math.PI) / 180));
                        setPower(0);
                        

                    } else if (getPower() == 2) 
                    {
                        setBullet(bouncing[0]);
                        shoots(-20,5, (((angle) * Math.PI) / 180));                          
                        
                      
                    }

                    setShotDelay(getShotTime());
                }
            } else {
                if (getShotDelay() > getFastShotTime()) {
                    setShotDelay(getFastShotTime());
                }
           
            }
        }

        //creates a bullet with the appropriate stats
        public void shoots(int bulletSpd, int bulletDmg, double dir)
        {
          things.add(new Bullet(getX(), getY(),dir, 
          bulletSpd,getBullet(), events,2, everything, bulletDmg));
          mercyTimer=2;
          
        }
        
  
        //explodes, then set up the scoretable or respawns depending on lives
        @Override
        public void dead() {
            if (getDeadTimer() == 0) {
                things.add(new Explosion(getX(), getY(), largeexpl, events, 2,
                        explsoundurl[1]));
                this.lives--;
            }

            setDeadTimer(getDeadTimer() + 1);

            if (getDeadTimer() == getDeadTime() + spawnDelay) {
                if (this.lives < 0) {
                    gameover = true;
                    System.exit(0);                   
                    
                    
                } else {
                    setX(startx);
                    setY(starty);
                    setDone(false);
                    setDeadTimer(0);
                    setDamage(0);
                    mercyTimer = 30;
                }
            }
        }
        

        //this makes the plane flash during mercy invincibility
        @Override
        public void draw(Graphics2D g2, ImageObserver obs) {
            if (mercyTimer % 2 == 0) {
                super.draw(g2, obs);
            }
            
        }

        //cannot be hit for some time after being hit
        @Override
        public boolean collision(int x, int y, int w, int h) {
            
            if (mercyTimer == 0) {
                return super.collision(x, y, w, h);
            }
 
            return false;
                    
        }
        

        //starting mercy timer
        @Override
        public void hitMe(Thing t) 
        {
            
            
            

            
            t.itHit(this); 
            }   

    }
    
    

    //adds an event type to GameEvents
    public class TankEvents extends GameEvents {

    }

    //Updates all Things and then draws everything
    //when the game is resetting, this method will also 
    @Override
    public void drawAll(int w, int h, Graphics2D g2) {
        
        int x1,y1,x2,y2;
        x1 = tank1.getX() -120;
        y1 = tank1.getY() -300;
        x2 = tank2.getX() -120;
        y2 = tank2.getY() -300;
        
        
       if(x1 < 0){
            x1 = 0;
        }
        if(x2 < 0){
            x2 = 0;
        }
        if(y1 < 0){
            y1 = 0;
        }
        if(y2 < 0){
            y2 = 0;
        }
        if(x1 + 750 > 1240) {
            x1 = 500;
        }
        if(x2 + 750 > 1240){
            x2 = 500;
        }
        if(y1 + 600 > 750){
            y1 = 140;
        }
        if(y2 + 600 > 750){
            y2 = 140;
        }
        
        
        
        
        
        Thing temp;
        screen.drawBackground(g2);
        screen.drawHere(everything, g2);
        
        
        Iterator<ArrayList> it = everything.listIterator();

        while (it.hasNext()) {
            Iterator<Thing> it2 = it.next().listIterator();
            while (it2.hasNext()) {
                if (gameover) {
                    break;
                }
                temp = it2.next();
                temp.update(w, h);
                if (temp.getRDone()) {
                    it2.remove();
                }
            }
            if (gameover) {
                break;
            }
        }
        
        leftscreen = getbimg().getSubimage(x2,y2, 750, 600);
        rightscreen = getbimg().getSubimage(x1,y1,750, 600);
        
        leftscreen = leftscreen.getScaledInstance(640, 750, Image.SCALE_FAST);
        rightscreen = rightscreen.getScaledInstance(640,750, Image.SCALE_FAST);
        minimap = getbimg().getScaledInstance(300, 200, Image.SCALE_FAST);
        
        BufferedImage display = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics temp1 = display.getGraphics();
        
        
        temp1.drawImage(leftscreen,0,0,null);
        temp1.drawImage(rightscreen, 800, 0, null);
        temp1.drawImage(minimap, 500, 0, null);
        
        
        g2.drawImage(leftscreen, 0, 0,this);
        g2.drawImage(rightscreen,640, 0, this);
        g2.drawImage(minimap, 490,0, this);


        //bottom.drawBot(w, h, g2,players);


        if (destroy) {
            it = everything.listIterator();
            while (it.hasNext()) {
                Iterator<Thing> it2 = it.next().listIterator();
                while (it2.hasNext()) {
                    it2.next();
                    it2.remove();
                }

            }
            
            
            

            gcontroller = new GameController();
            destroy = false;
            gameover = false;
            score = 0;
            events.deleteObservers();
            this.requestFocus();
        }

        gcontroller.timeline();
    }

    //draw and contains the bottom panel
    //this includes the life bars, score, and lives remaining
    //this also draws the boss life bar when it exists
    public class BottomPanel extends JPanel {

        private Image img;

        public BottomPanel(Image img) {
            super();
            this.img = img;
            Dimension d = new Dimension(img.getWidth(null), img.getHeight(null));
            this.setPreferredSize(d);
        }

        public void drawBot(int w, int h, Graphics2D g2,ArrayList<PlayerParent> players) {
            String s = "" + score;


            AttributedString as = new AttributedString(s);

            Font bigLetters = new Font("Monospaced", Font.BOLD, 40);
            as.addAttribute(TextAttribute.FOREGROUND, Color.CYAN);
            as.addAttribute(TextAttribute.FONT, bigLetters);
            

            int x = img.getWidth(this);
            int y = img.getHeight(this);

            g2.drawImage(img, 0, h - y, x, y, this);


            g2.drawString(as.getIterator(), 3 * w / 8, h - y / 2);

            Iterator<PlayerParent> it = players.listIterator();
            PlayerParent temp;
            int i = 0;
            while (it.hasNext()) {
                temp = it.next();
                g2.setColor(Color.BLACK);
                g2.drawRect(10, h - 50 - i * 16, w / 4, 15);
                g2.setColor(Color.GREEN);
                g2.fillRect(11, h - 50 - i * 16, (int) (w / 4 * (1 - (double) temp.getDamage()
                        / (double) temp.getMax())), 14);
                i++;
            }

        }
    }


    private BufferedImage convertToBuffered(Image img) {
        int w = img.getWidth(this);
        int h = img.getHeight(this);
        BufferedImage bimg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bimg.createGraphics();
        g2.drawImage(img, 0, 0, this);
        g2.dispose();
        return bimg;
    }

    public static void main(String[] args) {
        final TankGame game = new TankGame();
        game.init();
        final JFrame f = new JFrame("Tank Game");
        
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                f.dispose();
                
                System.exit(0);
            }
        });
        f.getContentPane().add("Center", game);
        
        f.pack();
        f.setSize(new Dimension(1280,896));
        f.setVisible(true);
        f.setResizable(false);
        game.start();
    }
    
    }
     
