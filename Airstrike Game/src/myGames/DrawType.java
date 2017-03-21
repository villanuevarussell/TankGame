/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myGames;

import java.awt.Graphics2D;

/**
 * This class is passed to GameSpace to give it a method to draw with
 * @author Lowell
 */
public interface DrawType
{
    public void drawThis(Thing thing, Graphics2D g2, GameSpace gs);
}
