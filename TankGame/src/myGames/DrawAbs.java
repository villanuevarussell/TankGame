/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myGames;

import java.awt.Graphics2D;

/**
 * This draws things as if the GameSpace is the same as the coordinates system
 * @author Lowell
 */
public class DrawAbs implements DrawType
{
    @Override
    public void drawThis(Thing thing, Graphics2D g2, GameSpace gs)
    {
        thing.draw(g2, gs);
    }
}
