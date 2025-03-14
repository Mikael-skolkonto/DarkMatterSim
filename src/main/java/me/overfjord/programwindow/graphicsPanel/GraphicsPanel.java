package me.overfjord.programwindow.graphicsPanel;

import me.overfjord.programwindow.physicsToolkit.Space;
import mikera.vectorz.Vector3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class GraphicsPanel extends JPanel implements Runnable {

    //The space that is displayed through this GraphicsPanel
    private final Space space;

    //Camera object will draw particles and keep track of orientation
    private final Camera camera;

    //Used by other classes to pause the frame-refreshing
    public boolean running = true;

    //Minimum ms delay between each frame.
    private final int FPS_CAP;

    private Graphics2D g2d;

    public GraphicsPanel(Space space, Dimension size, int fpsCap) {
        this.space = space;
        this.setSize(size);
        this.camera = new Camera(this);
        this.FPS_CAP = 1000 / fpsCap;
        this.g2d = (Graphics2D) this.getGraphics();

        setOpaque(false);
    }

    public void paint(Graphics g) {
        g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setBackground(SystemColor.BLACK);
        g2d.clearRect(0,0,getWidth(),getHeight());

        camera.drawPoints(space, g2d);
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

    public void move(int direction) {
        Vector3 directionVec;
        switch (direction) {
            case 0 -> directionVec = new Vector3(0.0, 0.0, 1.0);
            case 1 -> directionVec = new Vector3(-1.0, 0, 0);
            case 2 -> directionVec = new Vector3(0.0, 0.0, -1.0);
            case 3 -> directionVec = new Vector3(1.0, 0, 0);
            default -> directionVec = new Vector3(0.0,0.0,0.0);
        }
        this.camera.getRotationMatrix().transformInPlace(directionVec);
        this.camera.move();
    }

    public void moveMouse(MouseEvent e) {

    }
}
