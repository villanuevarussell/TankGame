/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package planegame;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.awt.image.*;
import java.net.URL;
import java.text.AttributedString;
import java.util.*;
import javax.sound.midi.*;
import javax.swing.*;
import myGames.*;

/**
 *  The goal of the game is to destroy enemy boss.
 * For one player the controls are: arrow keys control movement, enter is fire
 * For the second player: wasd for movement and space to fire
 * You get 2 extra lives shared between the players
 * Player 1 spawns on the right and Player 2 on the left
 * Player 1 has the bottom life bar in the lower left corner and Player 2 has
 * the top life bar.
 * I got an error trying to run this in Chrome but it runs in Internet Explorer
 * and as an independent program.
 * @author Lowell Milliken
 */
public class PlaneGame extends Game
{
    private GameSpace screen;
    private BottomPanel bottom;
    private Random random = new Random();
    private ArrayList<ArrayList> everything;
    private ArrayList<Thing> things;
    private ArrayList<PlayerParent> players;
    private ArrayList<Enemy> enemies;
    private int score;
    final private int startPoint = -30;
    final private int backgroundspeed = 4;
    private Image[] islands[], enemy[], playerimg[], boss, bigbullet, powerup,
            ebullet, smallexpl, largeexpl, mybullet;
    private GameController gcontroller;
    private PlaneEvents events;
    private ArrayList<ScoreType> sTable;
    private ScoreTable scoreTable; 
    private boolean gameover;
    private boolean destroy = false;
    private boolean twoplayers = false;
    private boolean isBoss = false;
    private int lives;
    private Image lifeImg;
    private URL[] explsoundurl;

    //creates and adds all the game panel to the applet
    //also sets up images, sounds, and creates and initializes state for most
    //variables and objects.
    @Override
    public void init()
    {      
        super.init();
        
        screen = new GameSpace(getSprite("Resources/water.png"), new DrawAbs());
        screen.setBackSpeed(backgroundspeed);
        screen.setBackDirection(0.);
        
        bottom = new BottomPanel(getSprite("Resources/bottom.png"));

        add(screen, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        setBackground(Color.white);

        everything = new ArrayList<ArrayList>();
        things = new ArrayList<Thing>();
        everything.add(things);
        players = new ArrayList<PlayerParent>();
        everything.add(players);
        enemies = new ArrayList<Enemy>();
        everything.add(enemies);

        events = new PlaneEvents();

        KeyControl keys = new KeyControl(events);
        addKeyListener(keys);
        
        gcontroller = new GameController();
        score = 0;
        
        sTable = new ArrayList<ScoreType>(10);
        for(int i = 0; i < 10; i++)
        {
            sTable.add(new ScoreType("<No One>", 0));
        }
        
        gameover = false;
        
        Object[] options = {"One Player", "Two Players"};
        int n = JOptionPane.showOptionDialog(this, "One or two players?", "Welcome",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                options, options[0]);
        
        if(n == 1)
        {
            twoplayers = true;
        }
    }
    
    //getting all image files
    @Override
    public void initImages()
    {
        try
        {
            islands = new Image[3][1];
            for (int i = 0; i < 3; i++)
            {
                islands[i][0] = getSprite("Resources/island" + (i + 1) + ".png");
            }
            playerimg = new Image[2][3];
            for (int i = 0; i < 3; i++)
            {
                playerimg[0][i] = getSprite("Resources/myplane_" + (i + 1) + ".png");
            }
            for(int i = 0; i < 3; i++)
            {
                playerimg[1][i] = getSprite("Resources/plane2_" + (i + 1) + ".png");
            }
            boss = new Image[3];
            for(int i = 0; i < 3; i++)
            {
                boss[i] = getSprite("Resources/myboss" + (i+1) + ".png");
            }
            bigbullet = new Image[1];
            bigbullet[0] = getSprite("Resources/bigBullet.png");
            powerup = new Image[1];
            powerup[0] = getSprite("Resources/powerup.png");
            enemy = new Image[4][3];
            for (int i = 0; i < 4; i++)
            {
                enemy[i] = new Image[3];
                for (int j = 0; j < 3; j++)
                {
                    enemy[i][j] = getSprite("Resources/enemy" + (i + 1)
                            + "_" + (j + 1) + ".png");
                }
            }
            ebullet = new Image[2];
            for (int i = 0; i < 2; i++)
            {
                ebullet[i] = getSprite("Resources/enemybullet" + (i + 1) + ".png");
            }
            smallexpl = new Image[6];
            for (int i = 0; i < 6; i++)
            {
                smallexpl[i] = getSprite("Resources/explosion1_" + (i + 1) + ".png");
            }
            largeexpl = new Image[7];
            for (int i = 0; i < 7; i++)
            {
                largeexpl[i] = getSprite("Resources/explosion2_" + (i + 1) + ".png");
            }
            mybullet = new Image[1];
            mybullet[0] = getSprite("Resources/bullet.png");
            
            lifeImg = getSprite("Resources/life.png");
        } catch (Exception e)
        {
            System.out.println("Error in getting images: " + e.getMessage());
        }
    }
    
    //getting all sound files
    @Override
    public void initSound()
    {
        try
        {
        Sequence music;
        Sequencer seq;
        URL musicu = PlaneGame.class.getResource("Resources/background.mid");
        explsoundurl = new URL[2];
        explsoundurl[0] = PlaneGame.class.getResource("Resources/snd_explosion1.wav");
        explsoundurl[1] = PlaneGame.class.getResource("Resources/snd_explosion2.wav");
        
           music =  MidiSystem.getSequence(musicu);
           seq = MidiSystem.getSequencer();
           seq.open();
           seq.setSequence(music);
           seq.start();
           seq.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
        }
        catch(Exception e)
        {
            System.out.println("Error in midi: " + e.getMessage());
        }
    }

    //this stores scores paired with names
    public class ScoreType
    {

        private String name;
        private int score;

        public ScoreType(String name, int score)
        {
            this.name = name;
            this.score = score;
        }

        public int getScore()
        {
            return score;
        }

        public String getName()
        {
            return name;
        }
        
        public void setName(String name)
        {
            this.name = name;
        }
    }

    //this creates Things when needed to make the gameplay pattern
    public class GameController
    {

        private int timer;

        public GameController()
        {
            timer = 0;
        }

        public void timeline()
        {
            switch (timer)
            {
                case 0:
                    things.add(new Island(backgroundspeed, islands[0], events));
                    
                    if(twoplayers)
                    {
                        players.add(new PlayerPlane(2*screen.getWidth() / 3, screen.getHeight()
                            - playerimg[0][0].getHeight(screen), 0., 6, playerimg[0],
                            events, 30, 20, 30, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT,
                            KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_ENTER,
                            KeyEvent.VK_DELETE, 10, 5, largeexpl.length));
                        players.add(new PlayerPlane(screen.getWidth() / 3, screen.getHeight()
                            - playerimg[1][0].getHeight(screen), 0., 6, playerimg[1],
                            events, 30, 10, 30, KeyEvent.VK_A, KeyEvent.VK_D,
                            KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_SPACE,
                            KeyEvent.VK_SHIFT, 10, 5, largeexpl.length));
                    }
                    else
                    {
                        players.add(new PlayerPlane(screen.getWidth() / 2, screen.getHeight()
                            - playerimg[0][0].getHeight(screen), 0., 6, playerimg[0],
                            events, 30, 20, 30, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT,
                            KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_ENTER,
                            KeyEvent.VK_DELETE, 10, 5, largeexpl.length));
                    }
                    //screen.setDrawType(new DrawRel(players.get(0)));
                    lives = 2;
                    break;
                case 50:
                    things.add(new Island(backgroundspeed, islands[1], events));
                    spawnEnemy(randomX(), startPoint, 3, 0.);
                    break;
                case 100:
                    things.add(new Island(backgroundspeed, islands[2], events));
                    spawnEnemy(randomX(), startPoint, 3, 0.);
                    break;
                case 150:
                    spawnEnemy(screen.getWidth()/5, startPoint, 3, 0);
                    spawnEnemy(2*screen.getWidth()/5, startPoint, 3, 0);
                    spawnEnemy(3*screen.getWidth()/5, startPoint, 3, 0);
                    spawnEnemy(4*screen.getWidth()/5, startPoint, 3, 0);
                    break;
                case 180:
                    spawnEnemy(screen.getWidth()/5, startPoint, 3, 0);
                    spawnEnemy(2*screen.getWidth()/5, startPoint, 3, 0);
                    spawnEnemy(3*screen.getWidth()/5, startPoint, 3, 0);
                    spawnEnemy(4*screen.getWidth()/5, startPoint, 3, 0);
                    break;
                case 200:
                    spawnPower(screen.getWidth()/2, startPoint, backgroundspeed, 1);
                    break;
                case 250:
                    spawnShooter(randomX(), startPoint, 3, 0.);
                    break;
                case 300:
                    spawnShooter(randomX(), startPoint, 3, 0.);
                    break;
                case 350:
                    spawnShooter(screen.getWidth()/5, startPoint, 3, 0);
                    spawnShooter(2*screen.getWidth()/5, startPoint, 3, 0);
                    spawnShooter(3*screen.getWidth()/5, startPoint, 3, 0);
                    spawnShooter(4*screen.getWidth()/5, startPoint, 3, 0);
                    break;
                case 380:
                    spawnShooter(screen.getWidth()/5, startPoint, 3, 0);
                    spawnShooter(2*screen.getWidth()/5, startPoint, 3, 0);
                    spawnShooter(3*screen.getWidth()/5, startPoint, 3, 0);
                    spawnShooter(4*screen.getWidth()/5, startPoint, 3, 0);
                    break;
                case 400:
                    spawnPower(randomX(), startPoint, backgroundspeed, 1);
                    break;
                case 500:
                    spawnAimer(randomX(), startPoint, 3, 0.);
                    spawnAimer(randomX(), startPoint, 3, 0.);
                    break;
                case 600:
                    spawnAimer(-50, screen.getHeight()/3, 3, Math.PI/2.);
                    spawnAimer(screen.getWidth() + 50, 2*screen.getHeight()/3, 3, -Math.PI/2.);
                    break;
                case 620:
                    spawnAimer(-50, screen.getHeight()/3, 3, Math.PI/2.);
                    spawnAimer(screen.getWidth() + 50, 2*screen.getHeight()/3, 3, -Math.PI/2.);
                    break;
                case 640:
                    spawnAimer(-50, screen.getHeight()/3, 3, Math.PI/2.);
                    spawnAimer(screen.getWidth() + 50, 2*screen.getHeight()/3, 3, -Math.PI/2.);
                    break;
                case 660:
                    spawnAimer(-50, screen.getHeight()/3, 3, Math.PI/2.);
                    spawnAimer(screen.getWidth() + 50, 2*screen.getHeight()/3, 3, -Math.PI/2.);
                    break;
                case 680:
                    spawnAimer(-50, screen.getHeight()/3, 3, Math.PI/2.);
                    spawnAimer(screen.getWidth() + 50, 2*screen.getHeight()/3, 3, -Math.PI/2.);
                    break;
                case 700:
                    spawnAimer(-50, screen.getHeight()/3, 3, Math.PI/2.);
                    spawnAimer(screen.getWidth() + 50, 2*screen.getHeight()/3, 3, -Math.PI/2.);
                    break;
                case 720:
                    spawnAimer(-50, screen.getHeight()/3, 3, Math.PI/2.);
                    spawnAimer(screen.getWidth() + 50, 2*screen.getHeight()/3, 3, -Math.PI/2.);
                    break;
                case 800:
                    spawnEnemyBot(screen.getWidth()/5, 3);
                    spawnEnemyBot(2*screen.getWidth()/5, 3);
                    spawnEnemyBot(3*screen.getWidth()/5, 3);
                    spawnEnemyBot(4*screen.getWidth()/5, 3);
                    break;
                case 880:
                    spawnEnemy(randomX(), startPoint, 3, 0.);
                    break;
                case 890:
                    spawnEnemy(randomX(), startPoint, 4, 0.);
                    break;
                case 900:
                    spawnEnemy(randomX(), startPoint, 3, 0.);
                    break;
                case 910:
                    spawnEnemy(randomX(), startPoint, 4, 0.);
                    break;
                case 920:
                    spawnEnemy(randomX(), startPoint, 3, 0.);
                    break;
                case 930:
                    spawnEnemy(randomX(), startPoint, 4, 0.);
                    break;
                case 940:
                    spawnEnemy(randomX(), startPoint, 3, 0.);
                    break;
                case 950:
                    spawnEnemy(randomX(), startPoint, 3, 0.);
                    break;
                case 960:
                    spawnEnemy(randomX(), startPoint, 4, 0.);
                    break;
                case 970:
                    spawnEnemy(randomX(), startPoint, 3, 0.);
                    break;
                case 980:
                    spawnEnemy(randomX(), startPoint, 4, 0.);
                    spawnShooter(-50, randomY(), 3, Math.PI/2.);
                    spawnShooter(screen.getWidth() + 50, randomY(), 3, -Math.PI/2.);
                    break;
                case 990:
                    spawnEnemy(randomX(), startPoint, 3, 0.);
                    break;
                case 1000:
                    spawnEnemy(randomX(), startPoint, 3, 0.);
                    break;
                case 1010:
                    spawnEnemy(randomX(), startPoint, 4, 0.);
                    break;
                case 1020:
                    spawnEnemy(randomX(), startPoint, 3, 0.);
                    break;
                case 1030:
                    spawnEnemy(randomX(), startPoint, 4, 0.);
                    break;
                case 1040:
                    spawnEnemy(randomX(), startPoint, 3, 0.);
                    break;
                case 1050:
                    spawnEnemy(randomX(), startPoint, 4, 0.);
                    spawnShooter(-50, randomY(), 3, Math.PI/2.);
                    spawnShooter(screen.getWidth() + 50, randomY(), 3, -Math.PI/2.);
                    break;
                case 1060:
                    spawnEnemy(randomX(), startPoint, 3, 0.);
                    break;
                case 1070:
                    spawnEnemy(randomX(), startPoint, 4, 0.);
                    break;
                case 1080:
                    spawnEnemy(randomX(), startPoint, 3, 0.);
                    break;
                case 1090:
                    spawnEnemy(0, startPoint, 5, Math.PI/6);
                    spawnEnemy(screen.getWidth()-50, startPoint, 5, -Math.PI/6);
                    break;
                case 1100:
                    spawnEnemy(randomX(), startPoint, 3, 0.);
                    spawnEnemy(50, startPoint, 5, Math.PI/6);
                    spawnEnemy(screen.getWidth()-50, startPoint, 5, -Math.PI/6);
                    spawnEnemy(50, startPoint, 5, Math.PI/6);
                    spawnEnemy(screen.getWidth()-50, startPoint, 5, -Math.PI/6);
                    break;
                case 1110:
                    spawnEnemy(screen.getWidth(), startPoint, 5, -Math.PI/6);
                    break;
                case 1120:
                    spawnEnemy(randomX(), startPoint, 3, 0.);
                    break;
                case 1130:
                    spawnEnemy(50, startPoint, 5, Math.PI/6);
                    spawnEnemy(screen.getWidth()-50, startPoint, 5, -Math.PI/6);
                    break;
                case 1140:
                    spawnEnemy(randomX(), startPoint, 3, 0.);
                    spawnEnemy(screen.getWidth()-randomX()/3, startPoint, 5, -Math.PI/6);
                    spawnEnemy(randomX()/3, startPoint, 5, Math.PI/6);
                    spawnEnemy(screen.getWidth()-randomX()/3, startPoint, 5, -Math.PI/6);
                    spawnEnemy(randomX()/3, startPoint, 5, Math.PI/6);
                    break;
                case 1150:
                    spawnEnemy(screen.getWidth()-50, startPoint, 5, -Math.PI/6);
                    spawnEnemy(screen.getWidth()-randomX()/3, startPoint, 5, -Math.PI/6);
                    spawnEnemy(randomX()/3, startPoint, 5, Math.PI/6);
                    break;
                case 1160:
                    spawnEnemy(randomX(), startPoint, 3, 0.);
                    spawnEnemy(screen.getWidth()-randomX()/3, startPoint, 5, -Math.PI/6);
                    spawnEnemy(randomX()/3, startPoint, 5, Math.PI/6);
                    break;
                case 1170:
                    spawnEnemy(randomX(), startPoint, 3, 0.);
                    spawnEnemy(screen.getWidth()-randomX()/3, startPoint, 5, -Math.PI/6);
                    spawnEnemy(randomX()/3, startPoint, 5, Math.PI/6);
                    break;
                case 1350:
                    spawnPower(randomX(), startPoint, backgroundspeed, 2);
                    break;
                case 1400:
                    spawnEnemyBot(screen.getWidth()/5, 3);
                    spawnEnemyBot(2*screen.getWidth()/5, 3);
                    spawnEnemyBot(3*screen.getWidth()/5, 3);
                    spawnEnemyBot(4*screen.getWidth()/5, 3);
                    break;
                case 1450:
                    spawnEnemyBot(screen.getWidth()/5 + 50, 3);
                    spawnEnemyBot(2*screen.getWidth()/5 + 50, 3);
                    spawnEnemyBot(3*screen.getWidth()/5 + 50, 3);
                    spawnEnemyBot(4*screen.getWidth()/5 + 50, 3);
                    break;
                case 1500:
                    spawnEnemyBot(screen.getWidth()/5, 3);
                    spawnEnemyBot(2*screen.getWidth()/5, 3);
                    spawnEnemyBot(3*screen.getWidth()/5, 3);
                    spawnEnemyBot(4*screen.getWidth()/5, 3);
                    break;
                case 1550:
                    spawnEnemyBot(screen.getWidth()/5 + 50, 3);
                    spawnEnemyBot(2*screen.getWidth()/5 + 50, 3);
                    spawnEnemyBot(3*screen.getWidth()/5 + 50, 3);
                    spawnEnemyBot(4*screen.getWidth()/5 + 50, 3);
                    break;
                case 1600:
                    spawnEnemyBot(screen.getWidth()/5, 3);
                    spawnEnemyBot(2*screen.getWidth()/5, 3);
                    spawnEnemyBot(3*screen.getWidth()/5, 3);
                    spawnEnemyBot(4*screen.getWidth()/5, 3);
                    spawnShooter(-50, screen.getHeight()-70, 3, Math.PI/2.);
                    spawnShooter(screen.getWidth() + 50, 70, 3, -Math.PI/2.);
                    break;
                case 1650:
                    spawnEnemyBot(screen.getWidth()/5 + 50, 3);
                    spawnEnemyBot(2*screen.getWidth()/5 + 50, 3);
                    spawnEnemyBot(3*screen.getWidth()/5 + 50, 3);
                    spawnEnemyBot(4*screen.getWidth()/5 + 50, 3);
                    spawnShooter(-50, screen.getHeight()-70, 3, Math.PI/2.);
                    spawnShooter(screen.getWidth() + 50, 70, 3, -Math.PI/2.);
                    break;
                case 1700:
                    spawnEnemyBot(screen.getWidth()/5, 3);
                    spawnEnemyBot(2*screen.getWidth()/5, 3);
                    spawnEnemyBot(3*screen.getWidth()/5, 3);
                    spawnEnemyBot(4*screen.getWidth()/5, 3);
                    spawnShooter(-50, screen.getHeight()-70, 3, Math.PI/2.);
                    spawnShooter(screen.getWidth() + 50, 70, 3, -Math.PI/2.);
                    break;
                case 1750:
                    spawnEnemyBot(screen.getWidth()/5 + 50, 3);
                    spawnEnemyBot(2*screen.getWidth()/5 + 50, 3);
                    spawnEnemyBot(3*screen.getWidth()/5 + 50, 3);
                    spawnEnemyBot(4*screen.getWidth()/5 + 50, 3);
                    spawnShooter(-50, screen.getHeight()-70, 3, Math.PI/2.);
                    spawnShooter(screen.getWidth() + 50, 70, 3, -Math.PI/2.);
                    break;
                case 1800:
                    spawnEnemyBot(screen.getWidth()/5, 3);
                    spawnEnemyBot(2*screen.getWidth()/5, 3);
                    spawnEnemyBot(3*screen.getWidth()/5, 3);
                    spawnEnemyBot(4*screen.getWidth()/5, 3);
                    spawnShooter(-50, screen.getHeight()-70, 3, Math.PI/2.);
                    spawnShooter(screen.getWidth() + 50, 70, 3, -Math.PI/2.);
                    break;
                case 1850:
                    spawnEnemyBot(screen.getWidth()/5 + 50, 3);
                    spawnEnemyBot(2*screen.getWidth()/5 + 50, 3);
                    spawnEnemyBot(3*screen.getWidth()/5 + 50, 3);
                    spawnEnemyBot(4*screen.getWidth()/5 + 50, 3);
                    spawnShooter(-50, screen.getHeight()-70, 3, Math.PI/2.);
                    spawnShooter(screen.getWidth() + 50, 70, 3, -Math.PI/2.);
                    break;
                case 1950:
                    spawnShooter(screen.getWidth()/5, startPoint, 3, 0);                    
                    spawnShooter(4*screen.getWidth()/5, startPoint, 3, 0);
                    break;
                case 1970:
                    spawnShooter(2*screen.getWidth()/5, startPoint, 3, 0);
                    spawnShooter(3*screen.getWidth()/5, startPoint, 3, 0);
                    break;
                case 2050:
                    spawnAimer(screen.getWidth()/5, startPoint, 3, 0);                    
                    spawnAimer(4*screen.getWidth()/5, startPoint, 3, 0);
                    break;
                case 2070:
                    spawnAimer(2*screen.getWidth()/5, startPoint, 3, 0);
                    spawnAimer(3*screen.getWidth()/5, startPoint, 3, 0);
                    break;
                case 2100:
                    spawnAimer(2*screen.getWidth()/5, startPoint, 3, 0);
                    spawnAimer(3*screen.getWidth()/5, startPoint, 3, 0);
                    break;
                case 2120:                   
                    spawnAimer(screen.getWidth()/5, startPoint, 3, 0);                    
                    spawnAimer(4*screen.getWidth()/5, startPoint, 3, 0);
                    break;
                case 2150:
                    spawnAimer(screen.getWidth()/5, startPoint, 3, 0);                    
                    spawnAimer(4*screen.getWidth()/5, startPoint, 3, 0);
                    break;
                case 2170:
                    spawnAimer(2*screen.getWidth()/5, startPoint, 3, 0);
                    spawnAimer(3*screen.getWidth()/5, startPoint, 3, 0);
                    break;
                case 2200:
                    spawnAimer(2*screen.getWidth()/5, startPoint, 3, 0);
                    spawnAimer(3*screen.getWidth()/5, startPoint, 3, 0);
                    break;
                case 2220:                   
                    spawnAimer(screen.getWidth()/5, startPoint, 3, 0);                    
                    spawnAimer(4*screen.getWidth()/5, startPoint, 3, 0);
                    break;
                case 2270:
                    spawnEnemy(screen.getWidth()-randomX()/3, startPoint, 5, -Math.PI/6);
                    spawnEnemy(randomX()/3, startPoint, 5, Math.PI/6);
                    spawnEnemy(screen.getWidth()-randomX()/3, startPoint, 5, -Math.PI/6);
                    spawnEnemy(randomX()/3, startPoint, 5, Math.PI/6);
                    spawnEnemy(screen.getWidth()-randomX()/3, startPoint, 5, -Math.PI/6);
                    spawnEnemy(randomX()/3, startPoint, 5, Math.PI/6);
                    spawnEnemy(screen.getWidth()-randomX()/3, startPoint, 5, -Math.PI/6);
                    spawnEnemy(randomX()/3, startPoint, 5, Math.PI/6);
                    break;
                case 2300:
                    spawnEnemy(screen.getWidth()-randomX()/3, startPoint, 5, -Math.PI/6);
                    spawnEnemy(randomX()/3, startPoint, 5, Math.PI/6);
                    spawnEnemy(screen.getWidth()-randomX()/3, startPoint, 5, -Math.PI/6);
                    spawnEnemy(randomX()/3, startPoint, 5, Math.PI/6);
                    spawnEnemy(screen.getWidth()-randomX()/3, startPoint, 5, -Math.PI/6);
                    spawnEnemy(randomX()/3, startPoint, 5, Math.PI/6);
                    spawnEnemy(screen.getWidth()-randomX()/3, startPoint, 5, -Math.PI/6);
                    spawnEnemy(randomX()/3, startPoint, 5, Math.PI/6);
                    spawnEnemy(screen.getWidth()-randomX()/3, startPoint, 5, -Math.PI/6);
                    spawnEnemy(randomX()/3, startPoint, 5, Math.PI/6);
                    break;
                case 2330:
                    spawnEnemy(screen.getWidth()-randomX()/3, startPoint, 5, -Math.PI/6);
                    spawnEnemy(randomX()/3, startPoint, 5, Math.PI/6);
                    spawnEnemy(screen.getWidth()-randomX()/3, startPoint, 5, -Math.PI/6);
                    spawnEnemy(randomX()/3, startPoint, 5, Math.PI/6);
                    spawnEnemy(screen.getWidth()-randomX()/3, startPoint, 5, -Math.PI/6);
                    spawnEnemy(randomX()/3, startPoint, 5, Math.PI/6);
                    spawnEnemy(screen.getWidth()-randomX()/3, startPoint, 5, -Math.PI/6);
                    spawnEnemy(randomX()/3, startPoint, 5, Math.PI/6);
                    spawnEnemy(screen.getWidth()-randomX()/3, startPoint, 5, -Math.PI/6);
                    spawnEnemy(randomX()/3, startPoint, 5, Math.PI/6);
                    break;
                case 2360:
                    spawnEnemy(screen.getWidth()-randomX()/3, startPoint, 5, -Math.PI/6);
                    spawnEnemy(randomX()/3, startPoint, 5, Math.PI/6);
                    spawnEnemy(screen.getWidth()-randomX()/3, startPoint, 5, -Math.PI/6);
                    spawnEnemy(randomX()/3, startPoint, 5, Math.PI/6);
                    spawnEnemy(screen.getWidth()-randomX()/3, startPoint, 5, -Math.PI/6);
                    spawnEnemy(randomX()/3, startPoint, 5, Math.PI/6);
                    spawnEnemy(screen.getWidth()-randomX()/3, startPoint, 5, -Math.PI/6);
                    spawnEnemy(randomX()/3, startPoint, 5, Math.PI/6);
                    spawnEnemy(screen.getWidth()-randomX()/3, startPoint, 5, -Math.PI/6);
                    spawnEnemy(randomX()/3, startPoint, 5, Math.PI/6);
                    break;
                case 2390:
                    spawnEnemy(screen.getWidth()-randomX()/3, startPoint, 5, -Math.PI/6);
                    spawnEnemy(randomX()/3, startPoint, 5, Math.PI/6);
                    spawnEnemy(screen.getWidth()-randomX()/3, startPoint, 5, -Math.PI/6);
                    spawnEnemy(randomX()/3, startPoint, 5, Math.PI/6);
                    spawnEnemy(screen.getWidth()-randomX()/3, startPoint, 5, -Math.PI/6);
                    spawnEnemy(randomX()/3, startPoint, 5, Math.PI/6);
                    spawnEnemy(screen.getWidth()-randomX()/3, startPoint, 5, -Math.PI/6);
                    spawnEnemy(randomX()/3, startPoint, 5, Math.PI/6);
                    spawnEnemy(screen.getWidth()-randomX()/3, startPoint, 5, -Math.PI/6);
                    spawnEnemy(randomX()/3, startPoint, 5, Math.PI/6);
                    break;
                case 2420:
                    spawnEnemy(screen.getWidth()-randomX()/3, startPoint, 5, -Math.PI/6);
                    spawnEnemy(randomX()/3, startPoint, 5, Math.PI/6);
                    spawnEnemy(screen.getWidth()-randomX()/3, startPoint, 5, -Math.PI/6);
                    spawnEnemy(randomX()/3, startPoint, 5, Math.PI/6);
                    spawnEnemy(screen.getWidth()-randomX()/3, startPoint, 5, -Math.PI/6);
                    spawnEnemy(randomX()/3, startPoint, 5, Math.PI/6);
                    spawnEnemy(screen.getWidth()-randomX()/3, startPoint, 5, -Math.PI/6);
                    spawnEnemy(randomX()/3, startPoint, 5, Math.PI/6);
                    spawnEnemy(screen.getWidth()-randomX()/3, startPoint, 5, -Math.PI/6);
                    spawnEnemy(randomX()/3, startPoint, 5, Math.PI/6);
                    break;
                case 2500:
                    spawnShooter(screen.getWidth()-randomX()/3, startPoint, 4, -Math.PI/6);
                    spawnShooter(randomX()/3, startPoint, 4, Math.PI/6);
                    spawnShooter(screen.getWidth()-randomX()/3, startPoint, 4, -Math.PI/6);
                    spawnShooter(randomX()/3, startPoint, 4, Math.PI/6);
                    spawnShooter(screen.getWidth()-randomX()/3, startPoint, 4, -Math.PI/6);
                    spawnShooter(randomX()/3, startPoint, 4, Math.PI/6);
                    spawnShooter(screen.getWidth()-randomX()/3, startPoint, 4, -Math.PI/6);
                    spawnShooter(randomX()/3, startPoint, 4, Math.PI/6);
                    spawnShooter(screen.getWidth()-randomX()/3, startPoint, 5, -Math.PI/6);
                    spawnShooter(randomX()/3, startPoint, 4, Math.PI/6);
                    break;
                case 2550:
                    spawnShooter(screen.getWidth()-randomX()/3, startPoint, 4, -Math.PI/6);
                    spawnShooter(randomX()/3, startPoint, 4, Math.PI/6);
                    spawnShooter(screen.getWidth()-randomX()/3, startPoint, 4, -Math.PI/6);
                    spawnShooter(randomX()/3, startPoint, 4, Math.PI/6);
                    spawnShooter(screen.getWidth()-randomX()/3, startPoint, 4, -Math.PI/6);
                    spawnShooter(randomX()/3, startPoint, 4, Math.PI/6);
                    spawnShooter(screen.getWidth()-randomX()/3, startPoint, 4, -Math.PI/6);
                    spawnShooter(randomX()/3, startPoint, 4, Math.PI/6);
                    spawnShooter(screen.getWidth()-randomX()/3, startPoint, 5, -Math.PI/6);
                    spawnShooter(randomX()/3, startPoint, 4, Math.PI/6);
                    break;
                case 2600:
                    spawnShooter(screen.getWidth()-randomX()/3, startPoint, 4, -Math.PI/6);
                    spawnShooter(randomX()/3, startPoint, 4, Math.PI/6);
                    spawnShooter(screen.getWidth()-randomX()/3, startPoint, 4, -Math.PI/6);
                    spawnShooter(randomX()/3, startPoint, 4, Math.PI/6);
                    spawnShooter(screen.getWidth()-randomX()/3, startPoint, 4, -Math.PI/6);
                    spawnShooter(randomX()/3, startPoint, 4, Math.PI/6);
                    spawnShooter(screen.getWidth()-randomX()/3, startPoint, 4, -Math.PI/6);
                    spawnShooter(randomX()/3, startPoint, 4, Math.PI/6);
                    spawnShooter(screen.getWidth()-randomX()/3, startPoint, 5, -Math.PI/6);
                    spawnShooter(randomX()/3, startPoint, 4, Math.PI/6);
                    break;
                case 2650:
                    spawnShooter(screen.getWidth()-randomX()/3, startPoint, 4, -Math.PI/6);
                    spawnShooter(randomX()/3, startPoint, 4, Math.PI/6);
                    spawnShooter(screen.getWidth()-randomX()/3, startPoint, 4, -Math.PI/6);
                    spawnShooter(randomX()/3, startPoint, 4, Math.PI/6);
                    spawnShooter(screen.getWidth()-randomX()/3, startPoint, 4, -Math.PI/6);
                    spawnShooter(randomX()/3, startPoint, 4, Math.PI/6);
                    spawnShooter(screen.getWidth()-randomX()/3, startPoint, 4, -Math.PI/6);
                    spawnShooter(randomX()/3, startPoint, 4, Math.PI/6);
                    spawnShooter(screen.getWidth()-randomX()/3, startPoint, 5, -Math.PI/6);
                    spawnShooter(randomX()/3, startPoint, 4, Math.PI/6);
                    break;
                case 2700:
                    spawnShooter(screen.getWidth()-randomX()/3, startPoint, 4, -Math.PI/6);
                    spawnShooter(randomX()/3, startPoint, 4, Math.PI/6);
                    spawnShooter(screen.getWidth()-randomX()/3, startPoint, 4, -Math.PI/6);
                    spawnShooter(randomX()/3, startPoint, 4, Math.PI/6);
                    spawnShooter(screen.getWidth()-randomX()/3, startPoint, 4, -Math.PI/6);
                    spawnShooter(randomX()/3, startPoint, 4, Math.PI/6);
                    spawnShooter(screen.getWidth()-randomX()/3, startPoint, 4, -Math.PI/6);
                    spawnShooter(randomX()/3, startPoint, 4, Math.PI/6);
                    spawnShooter(screen.getWidth()-randomX()/3, startPoint, 5, -Math.PI/6);
                    spawnShooter(randomX()/3, startPoint, 4, Math.PI/6);
                    break;
                case 2800:
                    spawnAimer(50, screen.getHeight() + 50, 3, Math.PI);
                    break;
                case 2820:
                    spawnAimer(50, screen.getHeight() + 50, 3, Math.PI);
                    break;
                case 2840:
                    spawnAimer(50, screen.getHeight() + 50, 3, Math.PI);
                    break;
                case 2860:
                    spawnAimer(50, screen.getHeight() + 50, 3, Math.PI);
                    break;
                case 2880:
                    spawnAimer(50, screen.getHeight() + 50, 3, Math.PI);
                    break;
                case 2900:
                    spawnAimer(50, screen.getHeight() + 50, 3, Math.PI);
                    break;
                case 2920:
                    spawnAimer(50, screen.getHeight() + 50, 3, Math.PI);
                    break;
                case 2940:
                    spawnAimer(50, screen.getHeight() + 50, 3, Math.PI);
                    break;
                case 2960:
                    spawnAimer(50, screen.getHeight() + 50, 3, Math.PI);
                    break;
                case 3000:
                    spawnAimer(screen.getWidth()-50, screen.getHeight() + 50, 3, Math.PI);
                    break;
                case 3020:
                    spawnAimer(screen.getWidth()-50, screen.getHeight() + 50, 3, Math.PI);
                    break;
                case 3040:
                    spawnAimer(screen.getWidth()-50, screen.getHeight() + 50, 3, Math.PI);
                    break;
                case 3060:
                    spawnAimer(screen.getWidth()-50, screen.getHeight() + 50, 3, Math.PI);
                    break;
                case 3080:
                    spawnAimer(screen.getWidth()-50, screen.getHeight() + 50, 3, Math.PI);
                    break;
                case 3100:
                    spawnAimer(screen.getWidth()-50, screen.getHeight() + 50, 3, Math.PI);
                    break;
                case 3120:
                    spawnAimer(screen.getWidth()-50, screen.getHeight() + 50, 3, Math.PI);
                    break;
                case 3140:
                    spawnAimer(screen.getWidth()-50, screen.getHeight() + 50, 3, Math.PI);
                    break;
                case 3160:
                    spawnAimer(screen.getWidth()-50, screen.getHeight() + 50, 3, Math.PI);
                    break;
                case 3400:
                    spawnPower(randomX(), startPoint, backgroundspeed, 2);
                    break;
                case 3500:
                    spawnBoss(screen.getWidth()/2, startPoint);
                    isBoss = true;
                    spawnPower(randomX(), startPoint, backgroundspeed, 2);
            }
            
            //controls some boss actions after it spawns
            if(timer % 100 == 0)
            {
                events.setBoss(10);
            }
            if(timer % 400 == 0)
            {
                events.setBoss(11);
            }
            
            timer++;
        }
        
        private void spawnPower(int x, int y, int speed, int type)
        {
            things.add(new PowerUp(x, y, 0., speed, powerup, events, 2,
                            everything, type));
        }
        
        private void spawnEnemy(int x, int y, int speed, double dir)
        {
            enemies.add(new Enemy(x, y, dir, speed, enemy[0], events, 10, 10, 0,
                    200, players));
        }
        
        private void spawnEnemyBot(int x, int speed)
        {
            enemies.add(new Enemy(x, screen.getHeight() + 50, 0., -speed, enemy[3],
                    events, 10, 10, 0, 300, players));
        }
        
        private void spawnShooter(int x, int y, int speed, double dir)
        {
            enemies.add(new Shooter(x, y, dir, speed, enemy[1], events, 10, 10, 0,
                    400, players, 5, 5, 10, 10));                    
        }
        
        private void spawnAimer(int x, int y, int speed, double dir)
        {
            enemies.add(new Aimer(x, y, dir, speed, enemy[2], events, 10, 10, 0,
                    600, players, 5, 5, 10, 5));
        }
        
        private void spawnBoss(int x, int y)
        {
            enemies.add(new Boss(x, y, 0., 2, boss, events, 3000, 200, 20, 10000,
                    players, 5, 5, 75, 150, 75, 10));
        }
        
        private int randomX()
        {
            return random.nextInt(screen.getWidth());
        }
        
        private int randomY()
        {
            return random.nextInt(screen.getHeight());
        }

        public void resetTimer()
        {
            timer = 0;
        }
    }

    //Islands that move with the background and reset to random position when
    //they move out of bounds
    public class Island extends Thing
    {

        public Island(int speed, Image[] img, GameEvents events)
        {
            super(random.nextInt(screen.getWidth()), startPoint,
                    0., speed, img, events);
        }

        @Override
        public void dead()
        {
            setX(random.nextInt(screen.getWidth()));
            setY(startPoint);
            setDone(false);
        }

        @Override
        public void move()
        {
            setRDone(false);
            changeY(getSpeed());
        }

        @Override
        public void itHit(Unit u)
        {
        }
    }

    //basic enemy that moves in a straight line and causes damage to the player
    //if they hit it
    public class Enemy extends Unit
    {

        private int value;
        ArrayList<PlayerParent> players;

        public Enemy(int x, int y, double direction, int speed, Image[] img,
                GameEvents events, int maxdamage, int damageto,
                int eps, int value, ArrayList pl)
        {
        
            super(x, y, direction, speed, img, events, maxdamage, damageto, eps);
            this.value = value;
            players = pl;
        }

        @Override
        public void hitMe(Thing t)
        {
            t.itHit(this);
        }

        @Override
        public void itHit(Unit u)
        {
            u.changeDamage(getDamageTo());
            u.setDone(true);
        }

        //moves, goes through all players to check for a hit, checks if it has
        //take enough damage to die
        @Override
        public void move()
        {
            PlayerParent temp;
            changeX((int) (getSpeed() * Math.sin(getDirection())));
            changeY((int) (getSpeed() * Math.cos(getDirection())));

            Iterator<PlayerParent> it = players.listIterator();
            while (it.hasNext())
            {
                temp = it.next();
                if (temp.collision(getX(), getY(), getWidth(), getHeight()))
                {
                    events.setCollision(this, temp);
                }
            }
            
            if(getDamage() > getMax())
            {
                score += value;
                setDone(true);
            }
        }

        //if there is a hit somewhere, checks if it hit them and calls
        //the method for handling that
        @Override
        public void update(Observable o, Object arg)
        {
            GameEvents ev = (GameEvents) arg;
            
            if(ev.getType() == 1)
            {
                if(ev.getTarget() == this)
                {
                    hitMe((Thing)ev.getCaller());
                }
            }
        }

        //explodes when dead
        @Override
        public void dead()
        {
            things.add(new Explosion(getX(),getY(), smallexpl, getEvents(), 1,
                    explsoundurl[0]));
            getEvents().deleteObserver(this);
            setRDone(true);
        }
        
        public int getValue()
        {
            return value;
        }
    }
    
    //enemy that shoots in the direction of motion
    public class Shooter extends Enemy
    {
        private int bulletDmg;
        private int bulletSpd;
        
        //this chance is out of 100
        private int chance;
        private int shotTime;
        private int shotTimer;
        
        public Shooter(int x, int y, double direction, int speed, Image[] img,
                GameEvents events, int maxdamage, int damageto,
                int eps, int value, ArrayList pl, int bltdg, int bltsp, int ch,
                int st)
        {
            super(x, y, direction, speed, img, events, maxdamage, damageto,
                    eps, value, pl);
            
            this.bulletDmg = bltdg;
            this.bulletSpd = bltsp;
            this.chance = ch;
            this.shotTime = st;
            this.shotTimer = 0;
        }
        
        //has a chance % to shoot whenever the shotTimer is 0 or less
        @Override
        public void action()
        {
            if(random.nextInt(100) <= chance && shotTimer <= 0)
            {
                shoots(bulletSpd, bulletDmg, getDirection());
                shotTimer = shotTime;
            }
            else
            {
                shotTimer--;
            }
        }
        
        //creates a bullet with the appropriate stats
        public void shoots(int bulletSpd, int bulletDmg, double dir)
        {
            things.add(new Bullet(getX(),getY(), dir, bulletSpd, ebullet,
                    events, 2, everything, bulletDmg));
        }
        
        public int getBulletDmg()
        {
            return bulletDmg;
        }
        
        public int getBulletSpd()
        {
            return bulletSpd;
        }
    }
    
    //a shooter that aims at a player
    public class Aimer extends Shooter
    {
        public Aimer(int x, int y, double direction, int speed, Image[] img,
                GameEvents events, int maxdamage, int damageto,
                int eps, int value, ArrayList pl, int bltdg, int bltsp, int ch,
                int st)
        {
            super(x, y, direction, speed, img, events, maxdamage, damageto,
                eps, value, pl, bltdg, bltsp, ch, st);
        }
        
        //aims at a random player and shoots towards them
        @Override
        public void shoots(int bulletSpd, int bulletDmg, double dir)
        {
            PlayerParent play;
            
            if (getY() < screen.getHeight())
            {
                int targ = random.nextInt(100) % players.size();

                play = players.get(targ);

                dir = ((double) getX() - play.getX())
                        / ((double) getY() - play.getY());

                dir = Math.atan(dir);

                if (getY() > play.getY())
                {
                    things.add(new Bullet(getX(), getY(), dir, -bulletSpd, ebullet,
                            events, 2, everything, bulletDmg));
                } else
                {
                    things.add(new Bullet(getX(), getY(), dir, bulletSpd, ebullet,
                            events, 2, everything, bulletDmg));
                }
            }
        }
    }
    
    //the Boss: shoots in random directions and in a circle
    //spawns aimers near itself. The game is over when it dies
    public class Boss extends Shooter
    {
        private int timer1;
        private int timer2;
        private int timerReset2;
        private int shotCount;
        private int deadTimer;
        private int deadTimerMax;
        private boolean circleShots = false;
        private int circleDir;
        
        public Boss(int x, int y, double direction, int speed, Image[] img,
                GameEvents events, int maxdamage, int damageto,
                int eps, int value, ArrayList pl, int bltdg, int bltsp,
                int timer1, int timer2, int timerReset2, int shotCount)
        {
            super(x, y, direction, speed, img, events, maxdamage, damageto,
                    eps, value, pl, bltdg, bltsp, 100, 0);
            
            this.timer1 = timer1;
            this.timer2 = timer2;
            this.timerReset2 = timerReset2;
            this.shotCount = shotCount;
            
            deadTimer = smallexpl.length + 5;
            deadTimerMax = deadTimer;
        }
        
        //this means that it will kill a player instantly on contact
        @Override
        public void itHit(Unit u)
        {
            u.setDamage(200);
        }
        
        @Override
        public void action()
        {
            double shotDir;
            timer1--;
            timer2--;
            
            //stops after moving forward a bit
            if(timer1 == 0)
            {
                setSpeed(0);
                timer1 = 0;
            }
            
            //shoots random bullets every so often
            if(timer2 == 0)
            {
                for(int i = 0; i < shotCount; i++)
                {
                    shotDir = Math.toRadians((double)random.nextInt(360));
                    shoots(getBulletDmg(), getBulletSpd(), shotDir);
                }
                
                timer2 = timerReset2;
            }
            
            //shoots bullets in a circle, timing for this is in GameController
            //and passed through GameEvents
            if(circleShots)
            {
                if(circleDir%2 == 0)
                {
                    shoots(getBulletDmg(), getBulletSpd(),(double) circleDir * Math.PI/180.);
                    shoots(getBulletDmg(), getBulletSpd(),(double) circleDir * Math.PI/180. + Math.PI);
                }
                circleDir--;
                
                if(circleDir == 0)
                {
                    circleShots = false;
                }
            }
        }
        
        //creates explosions, waits a bit, then makes the score table and
        //stops the updates
        @Override
        public void dead()
        {    
            if(deadTimer == 0)
            {
                scoreTable = new ScoreTable("High Scores");
                
                scoreTable.setVisible(true);
            
                gameover = true;
                setRDone(true);
            }
            else if (deadTimer == deadTimerMax)
            {
                things.add(new Explosion(getX(), getY(), largeexpl, events, 2,
                        explsoundurl[1]));
                things.add(new Explosion(getX() + random.nextInt(20), getY() + random.nextInt(20), smallexpl, events, 1,
                        explsoundurl[0]));
                things.add(new Explosion(getX() + random.nextInt(20), getY() + random.nextInt(20), smallexpl, events, 1,
                        explsoundurl[0]));
                things.add(new Explosion(getX() + random.nextInt(20), getY() + random.nextInt(20), smallexpl, events, 1,
                        explsoundurl[0]));
                things.add(new Explosion(getX() + random.nextInt(20), getY() + random.nextInt(20), smallexpl, events, 1,
                        explsoundurl[0]));
                things.add(new Explosion(getX() + random.nextInt(20), getY() + random.nextInt(20), smallexpl, events, 1,
                        explsoundurl[0]));
                things.add(new Explosion(getX() + random.nextInt(20), getY() + random.nextInt(20), smallexpl, events, 1,
                        explsoundurl[0]));
                getEvents().deleteObserver(this);
                isBoss = false;
                deadTimer--;
            }
            else
            {
                deadTimer--;
            }
        }
        
        //watches observable for events types 10 and 11 which come from the
        //timeline in the GameController
        @Override
        public void update(Observable o, Object arg)
        {
            GameEvents ev = (GameEvents) arg;
            
            if(ev.getType() == 1)
            {
                if(ev.getTarget() == this)
                {
                    hitMe((Thing)ev.getCaller());
                }
            }

            //spawns aimers
            if (ev.getType() == 10 && isBoss)
            {
                enemies.add(new Aimer(getX() + 40, getY(), 0., 3, enemy[2], events,
                        10, 10, 0, 600, players, 5, 5, 10, 5));
                enemies.add(new Aimer(getX() - 40, getY(), 0., 3, enemy[2], events,
                        10, 10, 0, 600, players, 5, 5, 10, 5));
            }
            
            //starts shooting in a circle
            if (ev.getType() == 11)
            {
                circleShots = true;
                circleDir = 360;
            }
        }           
    }

    //This is the player's plane
    public class PlayerPlane extends PlayerParent
    {
        private int startx, starty, spawnDelay;
        private int mercyTimer;
        
        public PlayerPlane(int x, int y, double direction, int speed, Image[] img,
                GameEvents events, int maxdamage, int damageto, int eps, 
                int left, int right, int up, int down, int fire, int spfire,
                int shotTime, int fastShotTime, int deadTime)
        {
            super(x, y, direction, speed, img, events, maxdamage, damageto, eps,
                    left, right, up, down, fire, spfire, shotTime, fastShotTime,
                    deadTime);
            
            startx = x;
            starty = y;
            spawnDelay = 30;
            mercyTimer = 20;
        }

        //moves based on the keys pressed, but only with the basic update
        @Override
        public void move()
        {
            if(getMvLeft())
            {
                if (getX() - getSpeed() > 0)
                {
                    changeX(-getSpeed());
                } else
                {
                    setX(0);
                }
            }
            
            if(getMvRight())
            {
                if (getX() + getSpeed() < screen.getWidth())
                {
                    changeX(getSpeed());
                }
            }
            
            if(getMvUp())
            {
                if (getY() - getSpeed() > 0)
                {
                    changeY(-getSpeed());
                } else
                {
                    setY(0);
                }
            }
            
            if(getMvDown())
            {
                if (getY() + getSpeed() < screen.getHeight())
                {
                    changeY(getSpeed());
                }
            }
            
            if (getDamage() >= getMax())
            {
                lives--;
                if(getPower() > 0)
                {
                    setPower(getPower() - 1);
                }
                
                setDone(true);
            }

            if(getShotDelay() > 0)
            {
                changeShotDelay(-1);
            }
            
            if(mercyTimer > 0)
            {
                mercyTimer--;
            }
        }
        
        //shoots with button pressed, but only with the basic update
        @Override
        public void action()
        {
            if(getIsFiring())
            {
                if(getShotDelay() == 0)
                {
                    if(getPower() == 0)
                    {
                        things.add(new Bullet(getX(),getY()-5, 0., -10, mybullet, events,
                                1, everything, 10));
                    }
                    else if(getPower() == 1)
                    {
                        things.add(new Bullet(getX(),getY()-5, Math.PI/6., -10, mybullet, events,
                                1, everything, 10));
                        things.add(new Bullet(getX(),getY()-5, 0., -10, mybullet, events,
                                1, everything, 10));
                        things.add(new Bullet(getX(),getY()-5, -Math.PI/6., -10, mybullet, events,
                                1, everything, 10));
                    }
                    else if(getPower() == 2)
                    {
                        things.add(new Bullet(getX(),getY()-5, Math.PI/6., -10, mybullet, events,
                                1, everything, 10));
                        things.add(new Bullet(getX(),getY()-5, 0., -10, mybullet, events,
                                1, everything, 10));
                        things.add(new Bullet(getX(),getY()-5, -Math.PI/6., -10, mybullet, events,
                                1, everything, 10));
                        things.add(new Bullet(getX()+10,getY()-5, 0., -10, mybullet, events,
                                1, everything, 10));
                        things.add(new Bullet(getX()-10,getY()-5, 0., -10, mybullet, events,
                                1, everything, 10));
                    }
                    
                    setShotDelay(getShotTime());
                }
            }
            else
            {
                if(getShotDelay() > getFastShotTime())
                {
                    setShotDelay(getFastShotTime());
                }
            }
        }

        //explodes, then set up the scoretable or respawns depending on lives
        @Override
        public void dead()
        {   
            if(getDeadTimer() == 0)
            {
                things.add(new Explosion(getX(), getY(), largeexpl, events, 2,
                        explsoundurl[1]));
            }
            
            setDeadTimer(getDeadTimer() + 1);
            
            if(getDeadTimer() == getDeadTime() + spawnDelay)
            {
                if(lives < 0)
                {
                    isBoss = false;
                    gameover = true;
                    scoreTable = new ScoreTable("High Scores");
                    scoreTable.setVisible(true);
                }
                else
                {
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
        public void draw(Graphics2D g2, ImageObserver obs)
        {
            if(mercyTimer%2 == 0)
            {
                super.draw(g2, obs);
            }
        }
        
        //cannot be hit for some time after being hit
        @Override
        public boolean collision(int x, int y, int w, int h)
        {
            if(mercyTimer == 0)
            {
                return super.collision(x, y, w, h);
            }
            
            return false;
        }

        //starting mercy timer
        @Override
        public void hitMe(Thing t)
        {
            if(!(t instanceof PowerUp))
            {
                mercyTimer = 20;
            }
            t.itHit(this);
        }
        
        
    }
    
    //adds an event type to GameEvents
    public class PlaneEvents extends GameEvents
    {

        public void setBoss(int type)
        {
            setType(type);
            setChanged();

            notifyObservers(this);
        }
    }

    //Updates all Things and then draws everything
    //when the game is resetting, this method will also 
    @Override
    public void drawAll(int w, int h, Graphics2D g2)
    {
        Boss theBoss = null;
        Thing temp;
   
        screen.drawBackground(g2);

        Iterator<ArrayList> it = everything.listIterator();
        
        while (it.hasNext())
        {
            Iterator<Thing> it2 = it.next().listIterator();
            while (it2.hasNext())
            {
                if (gameover)
                {
                    break;
                }
                temp = it2.next();
                temp.update(w, h);
                if(temp instanceof Boss)
                {
                    theBoss = (Boss) temp;
                }
                if (temp.getRDone())
                {
                    it2.remove();
                }
            }
            if (gameover)
            {
                break;
            }
        }

        screen.drawHere(everything, g2);

        bottom.drawBot(w, h, g2, theBoss);

        if(destroy)
        {
            it = everything.listIterator();
            while(it.hasNext())
            {
                Iterator<Thing> it2 = it.next().listIterator();
                while(it2.hasNext())
                {
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
    public class BottomPanel extends JPanel
    {

        private Image img;

        public BottomPanel(Image img)
        {
            super();
            this.img = img;
            Dimension d = new Dimension(img.getWidth(null), img.getHeight(null));
            this.setPreferredSize(d);
        }

        public void drawBot(int w, int h, Graphics2D g2, Boss theBoss)
        {
            String s = "" + score;
            AttributedString as = new AttributedString(s);
            Font bigLetters = new Font("Monospaced", Font.BOLD, 40);
            as.addAttribute(TextAttribute.FOREGROUND, Color.CYAN);
            as.addAttribute(TextAttribute.FONT, bigLetters);
            int x = img.getWidth(this);
            int y = img.getHeight(this);

            g2.drawImage(img, 0, h - y, x, y, this);
            
            g2.drawString(as.getIterator(), 3*w/8 , h - y/2);
            
            Iterator<PlayerParent> it = players.listIterator();
            PlayerParent temp;
            int i = 0;
            while(it.hasNext())
            {
                temp = it.next();
                g2.setColor(Color.BLACK);
                g2.drawRect(10, h - 50 - i*16, w/4 ,15);
                g2.setColor(Color.GREEN);
                g2.fillRect(11, h - 50 - i*16, (int)(w/4*(1-(double)temp.getDamage()/
                        (double)temp.getMax())), 14);
                i++;
            }
            
            for(i = 0; i < lives; i++)
            {
                g2.drawImage(lifeImg, i*lifeImg.getWidth(this), h-40,
                        lifeImg.getWidth(this), lifeImg.getHeight(this), this);
            }
            
            if(isBoss)
            {
                g2.setColor(Color.BLACK);
                g2.drawRect(10, 10, screen.getWidth()/2, 20);
                g2.setColor(Color.RED);
                g2.fillRect(11, 11, (int)(w/2*(1-(double)theBoss.getDamage()/
                        (double)theBoss.getMax())), 19);
            }
            
        }
    }
    
    //This is the high score table. It is created by Boss or PlayerPlane when
    //needed. It shows the high scores and asks for the users to enter their
    //name if needed. The high scores are not persistent after the game is
    //closed
    public class ScoreTable extends JFrame implements ActionListener
    {
        private JTextField enterName;
        private boolean high = false;
        private int thisIndex;
        
        public ScoreTable(String title)
        {
            super(title);
            this.setLocation(screen.getWidth()/2, screen.getHeight()/2);
            this.setLayout(new GridLayout(0,2));
            for(int i = 0; i < 10; i++)
            {
                if(score >= sTable.get(i).getScore() && !high)
                {
                    enterName = new JTextField(10);
                    enterName.setEditable(true);
                    enterName.setFocusable(true);
                    this.add(enterName);
                    
                    JTextField scoreT = new JTextField(6);
                    scoreT.setEditable(false);
                    scoreT.setText("" + score);
                    this.add(scoreT);
                    
                    sTable.add(i, new ScoreType("dummy", score));
                    
                    high = true;
                    thisIndex = i;
                }
                else
                {
                    JTextField name = new JTextField(20);
                    name.setEditable(false);
                    name.setText(sTable.get(i).getName());
                    this.add(name);
                    
                    JTextField scoreT = new JTextField(6);
                    scoreT.setEditable(false);
                    scoreT.setText("" + sTable.get(i).getScore());
                    this.add(scoreT);
                }
            }
            
            JButton one = new JButton("One Player");
            one.setActionCommand("one");
            this.add(one);
            JButton two = new JButton("Two Players");
            two.setActionCommand("two");
            this.add(two);
            one.addActionListener(this);
            two.addActionListener(this);
            this.addWindowListener(new WindowAdapter()
            {
                @Override
                public void windowClosing(WindowEvent e)
                {
                    actionPerformed(new ActionEvent(this,
                            ActionEvent.RESERVED_ID_MAX+1, "None"));
                }
            });
            this.pack();
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            if("one".equals(e.getActionCommand()))
            {
                twoplayers = false;
            }
            else if("two".equals(e.getActionCommand()))
            {
                twoplayers = true;
            }
            
            destroy = true;
            if(high)
            {
                sTable.get(thisIndex).setName(enterName.getText());
            }
            this.dispose();
        }
    }
    
   
    public static void main(String[] args)
    {
        final PlaneGame game = new PlaneGame();
        game.init();
        final JFrame f = new JFrame("Wingman Game");
        f.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                f.dispose();
                System.exit(0);
            }
        });
        f.getContentPane().add("Center", game);
        f.pack();
        f.setSize(new Dimension(640, 480));
        f.setVisible(true);
        f.setResizable(false);
        game.start();
    }
}