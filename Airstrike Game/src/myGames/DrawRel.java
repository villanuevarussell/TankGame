/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myGames;

import java.awt.Graphics2D;

/**
 * This draws Things relative to a central Thing and only if it is within the
 * current GameSpace area
 * @author Lowell
 */
public class DrawRel implements DrawType
{
    private Thing center;
    
    public DrawRel(Thing center)
    {
        this.center = center;
    }
    
    @Override
    public void drawThis(Thing thing, Graphics2D g2, GameSpace gs)
    {
        int left = center.getX() - gs.getWidth()/2;
        int right = center.getX() + gs.getWidth()/2;
        
        int top = center.getY() - gs.getHeight()/2;
        int bot = center.getY() + gs.getHeight()/2;
        
        if(thing.getX() > left -50 && thing.getX() < right +50
                && thing.getY() > top -50 && thing.getY() < bot +50)
        {
            thing.changeX(-left);
            thing.changeY(-top);
            
            thing.draw(g2, gs);
            
            thing.changeX(left);
            thing.changeY(top);
        }
    }
    
}
