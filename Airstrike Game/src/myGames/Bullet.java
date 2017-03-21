/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myGames;

import java.awt.Image;
import java.util.ArrayList;

/**
 *
 * @author Lowell
 */
public class Bullet extends NotUnit
{
    private int damageto;
    
    public Bullet(int x, int y, double direction, int speed, Image[] img,
            GameEvents events, int source, ArrayList ev, int damageto)
    {
        super(x, y, direction, speed, img, events, source, ev);
        this.damageto = damageto;
    }

    @Override
    public void itHit(Unit u)
    {
        u.changeDamage(damageto);
        setDone(true);
    }
    
    public int getDamageTo()
    {
        return damageto;
    }           
}
