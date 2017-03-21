/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myGames;

import java.awt.Image;
import java.util.*;

/**
 *
 * @author Lowell
 */
abstract public class Unit extends Thing implements Observer
{

    //damage is the object current damage
    //in general, when the current damage >= to the max damage, the object is dead
    private int damage;
    private int maxdamage;
    
    //damageto is the amount of damage done to an object colliding with this one
    //in general
    private int damageto;
    
    //eps is used to make the effective size of the Unit larger or smaller
    //to adjust the feel of the collision
    private int eps;
    
    //type of object
    private int type;
    

    public Unit(int x, int y, double direction, int speed, Image[] img,
            GameEvents events, int maxdamage, int damageto, int eps, int type)
    {
        super(x, y, direction, speed, img, events);
        this.damage = 0;
        this.maxdamage = maxdamage;
        this.damageto = damageto;
        this.eps = eps;
        this.type = type;
        
        //adds itself to the Observer list
        events.addObserver(this);
    }
    
    //calls when a Thing hits this Unit
    abstract public void hitMe(Thing caller);

    //this is what this Thing does to a Unit that hit it
    @Override
    public void itHit(Unit u)
    {
        u.changeDamage(getDamageTo());
    }

    //checks for collision by checking the intersection of the images + eps
    public boolean collision(int x, int y, int w, int h)
    {
        if(getDone())
        {
            return false;
        }
        
        if (y + h/2 > getY() + eps - getImage().getHeight(null)/2
                && y - h/2 < getY() - eps + getImage().getHeight(null)/2
                && x + w/2 > getX() + eps - getImage().getWidth(null)/2
                && x - w/2 < getX() - eps + getImage().getWidth(null)/2) 
        {
            return true;
        } else
        {
            return false;
        }
    }
    
    //get and set methods follow
    public void setEps(int eps)
    {
        this.eps = eps;
    }
    
    public int getDamage()
    {
        return damage;
    }
    
    public void changeDamage(int change)
    {
        damage += change;
    }
    
    public void setDamage(int damage)
    {
        this.damage = damage;
    }
    
    public int getMax()
    {
        return maxdamage;
    }
    
    public int getDamageTo()
    {
        return damageto;
    }
    
    public int getType()
    {
       return type;
    }
}
