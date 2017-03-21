/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myGames;

import java.awt.Image;
import java.util.*;

/**
 * This class is for Things which cannot cause collisions
 * @author Lowell
 */
abstract public class NotUnit extends Thing
{
    private int source;
    //targets contains everything that can cause collisions
    private ArrayList<ArrayList> targets;
    
    public NotUnit(int x, int y, double direction, int speed, Image[] img,
            GameEvents events, int source, ArrayList ev)
    {
        super(x, y, direction, speed, img, events);
        this.source = source;
        this.targets = ev;
    }
    
    @Override
    public void move()
    {
        Unit temp;
        int i = 1;
        
        changeX((int)(getSpeed() * Math.sin(getDirection())));
        changeY((int)(getSpeed() * Math.cos(getDirection())));
        
        Iterator<ArrayList> it = targets.listIterator(1);
        while (it.hasNext())
        {
            Iterator<Unit> it2 = it.next().listIterator();
            if(i != source)
            {
                while (it2.hasNext())
                {
                    temp = it2.next();
                    if(temp.collision(getX(), getY(), getWidth(), getHeight()))
                    {
                        getEvents().setCollision(this, temp);
                    }
                }
            }
            i++;
        }   
    }
    
    @Override
    public void dead()
    {
        setRDone(true);
    }
            
    public int getSource()
    {
        return source;
    }
}
