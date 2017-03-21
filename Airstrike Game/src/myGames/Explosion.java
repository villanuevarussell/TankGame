/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myGames;

import java.awt.Image;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import javax.sound.sampled.*;

/**
 *
 * @author Lowell
 */
public class Explosion extends Thing
    {
        private int explode;
        private int type;
        
        public Explosion(int x, int y, Image[] img, GameEvents events, int type, URL snd)
        {
            super(x, y, 0., 0, img, events);
            explode = img.length;
            this.type = type;
            try
            {
                AudioInputStream explSound;
                Clip clip;
                explSound = AudioSystem.getAudioInputStream(snd);
                clip = AudioSystem.getClip();
                clip.open(explSound);
                clip.start();
            }
            catch(Exception e)
            {
                System.out.println("Error in explosion sound: " + e.getMessage());
            }
            
        }
        
        @Override
        public void move()
        {
            if(explode > 0)
            {
                explode--;
            }
            else
            {
                setDone(true);
            }
        }
        
        @Override
        public void dead()
        {
            setRDone(true);
        }
        
        @Override
        public void itHit(Unit u)
        {
        }
        
    }
