/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myGames;

import java.awt.event.KeyEvent;
import java.util.Observable;

/**
 * This class is watched by most game Things.
 * Events are passed through this object.
 * @author Lowell
 */
public class GameEvents extends Observable
{

    private int type;
    private Object caller, target;

    public void setCollision(Thing caller, Thing target)
    {
        type = 1;
        this.caller = caller;
        this.target = target;

        setChanged();

        this.notifyObservers(this);
    }

    public void setKeys(KeyEvent key)
    {
        type = 2;
        this.target = key;
        setChanged();

        notifyObservers(this);
    }
       
    public int getType()
    {
        return type;
    }
    
    public void setType(int type)
    {
        this.type = type;
    }

    public Object getCaller()
    {
        return caller;
    }

    public Object getTarget()
    {
        return target;
    }
}