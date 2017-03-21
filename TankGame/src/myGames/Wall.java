/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myGames;

import java.util.Observable;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.awt.image.*;
import java.net.URL;
import java.io.*;
import java.text.AttributedString;
import java.util.*;



/**
 *
 * @author Russell
 */
public class Wall extends Unit {
    public Wall(int x, int y, double direction, int speed, Image[] img, GameEvents events, int maxdamage, int damageto,int eps,int type)
    {
      super(x,y,direction,speed, img, events, maxdamage, damageto, eps,type);
     
      events.addObserver(this);
    }
    @Override
    public void hitMe(Thing caller) {
        
  
    }

    @Override
    public void move() {

    }

    @Override
    public void dead() {
        setRDone(true);
 
    }

    public void itHit(Unit u){

    
    }

    @Override
    public void update(Observable o, Object arg) {
        
        GameEvents ev = (GameEvents) arg;
        if(ev.getType() == 1)
        {
            if(ev.getTarget() == this)
            {
                hitMe((Thing)ev.getCaller());
            }
        }
  
    }
    
}
