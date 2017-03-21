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
public class PowerUp extends NotUnit
{
    private int type;
    
    public PowerUp(int x, int y, double direction, int speed, Image[] img,
            GameEvents events, int source, ArrayList ev, int type)
    {
        super(x, y, direction, speed, img, events, source, ev);
        
        this.type = type;
    }
    
    @Override
    public void itHit(Unit u)
    {
        PlayerParent p = (PlayerParent) u;
        if(type > 0)
        {
            p.setPower(type);
        }
        else if(type == 0)
        {
            if(p.getDamage()-p.getMax()/2 >= 0)
            {
                p.changeDamage(p.getMax()/2);
            }
            else
            {
                p.setDamage(0);
            }
        }
        
        setDone(true);
    }
}
