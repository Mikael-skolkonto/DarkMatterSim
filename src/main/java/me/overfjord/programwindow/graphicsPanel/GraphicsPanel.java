package me.overfjord.programwindow.graphicsPanel;

import me.overfjord.programwindow.physicsToolkit.Space;
import mikera.vectorz.Vector3;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.awt.font.GlyphVector;

public class GraphicsPanel extends JPanel implements Runnable {

    //The space that is displayed through this GraphicsPanel
    private final Space space;

    //Used by other classes to pause the frame-refreshing
    public boolean running = true;

    //Minimum ms delay between each frame.
    private final int FPS_CAP;

    private Graphics2D g2d;

    public GraphicsPanel(Space space, int fpsCap) {
        this.space = space;
        this.FPS_CAP = 1000 / fpsCap;
        this.g2d = (Graphics2D) this.getGraphics();

        setOpaque(false);
    }

    public void paint(Graphics g) {
        g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setBackground(SystemColor.BLACK);
        g2d.clearRect(0,0,getWidth(),getHeight());

        g2d.setColor(SystemColor.BLUE);
        drawPoints();
    }


    private void drawPoints() {
        for (Vector3 v : space.pointMassCoordinates) {
            g2d.fillOval((int)v.x,(int)v.y,90,90);
        }
    }

    @Override
    public void run() {
        try {
            while (running) {
                this.paint(this.getGraphics());
                Thread.sleep(FPS_CAP);
            }
        } catch (InterruptedException interruptException) {
            interruptException.printStackTrace();
        }
    }
}
