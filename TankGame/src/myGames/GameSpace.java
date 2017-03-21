/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myGames;

import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JPanel;

/**
 * This is the space in which the game object live.
 * It draws the background and all objects.
 * This exact way in which the objects are drawn is determined by the DrawType
 * class which
 * @author Lowell
 */
public class GameSpace extends JPanel
{

    private int backspeed;
    private int movex = 0, movey = 0;
    private double backdirection;
    private Image tile;
    
    private DrawType drawer;

    public GameSpace(Image tile, DrawType drawer)
    {
        super();
        this.tile = tile;
        backspeed = 0;
        backdirection = 0.;
        this.drawer = drawer;
    }

    public void drawBackground(Graphics2D g2)
    {
        int h = this.getHeight();
        int w = this.getWidth();
        int TileWidth = tile.getWidth(this);
        int TileHeight = tile.getHeight(this);
        int NumberX = (int) (w / TileWidth);
        int NumberY = (int) (h / TileHeight);


        movex += (int) (backspeed * Math.sin(backdirection));
        movey += (int) (backspeed * Math.cos(backdirection));

        for (int i = -1; i <= NumberY + 1; i++)
        {
            for (int j = -1; j <= NumberX + 1; j++)
            {
                g2.drawImage(tile, j * TileWidth + (movex % TileWidth),
                        i * TileHeight + (movey % TileHeight), TileWidth,
                        TileHeight, this);
            }
        }
    }

    //goes through and draws everything
    public void drawHere(ArrayList<ArrayList> everything, Graphics2D g2)
    {
        Thing temp;
        Iterator<ArrayList> it = everything.listIterator();
        while (it.hasNext())
        {
            Iterator<Thing> it2 = it.next().listIterator();
            while (it2.hasNext())
            {
                temp = it2.next();
                drawer.drawThis(temp, g2, this);
            }
        }
    }

    public void setTile(Image tile)
    {
        this.tile = tile;
    }
    
    public Image getTile()
    {
        return tile;
    }

    public void setBackDirection(double direction)
    {
        backdirection = direction;
    }
    
    public double getBackDirection()
    {
        return backdirection;
    }

    public void setBackSpeed(int speed)
    {
        backspeed = speed;
    }
    
    public int getBackSpeed()
    {
        return backspeed;
    }
    
    public void setDrawType(DrawType drawer)
    {
        this.drawer = drawer;
    }
    
    public DrawType getDrawType()
    {
        return drawer;
    }
}
