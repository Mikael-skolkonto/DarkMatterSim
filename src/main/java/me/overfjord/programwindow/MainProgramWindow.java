package me.overfjord.programwindow;

import me.overfjord.programwindow.graphicsPanel.GraphicsPanel;
import me.overfjord.programwindow.physicsToolkit.Space;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * This class is the program window and handles the inputs
 */
public class MainProgramWindow extends JFrame implements Runnable {

    private GraphicsPanel gp;

    @Override
    public void run() {

    }

    public MainProgramWindow() {
        Image windowIcon = Toolkit.getDefaultToolkit().getImage("src/main/resources/windowIcon.jpg");
        setIconImage(windowIcon);
        setTitle("Dark Matter Simulator");

        //Sets the window height the same as screen height and sets the width to height x golden_ratio
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = (int) (height * 1.61803398875);
        setSize(width,height);

        setLocationRelativeTo(null);
        getContentPane().setBackground(SystemColor.BLACK);

        //Add panel to display simulation
        this.gp = new GraphicsPanel(new Space(),getSize(),60);
        getContentPane().add(gp);
        Thread graphicsThread = new Thread(gp, "GraphicsThread");

        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        addMouseMotionListener(new MouseInputHandler());
        addKeyListener(new KeyboardInputHandler());

        graphicsThread.start();     //TYDLIGEN VÄLDIGT VIKTIGT ATT THREADEN STARTAS I SLUTET AV KONSTRUKTÖREN! UPPTÄCKT GENOM TRIAL AND ERROR
    }

    class KeyboardInputHandler extends KeyAdapter {

        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_W -> gp.move((byte)1);   //forward
                case KeyEvent.VK_A -> gp.move((byte)2);   //left
                case KeyEvent.VK_S -> gp.move((byte)3);   //backward
                case KeyEvent.VK_D -> gp.move((byte)4);   //right
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

            //exiting program
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                System.exit(0);
            }

            //movement
            switch (e.getKeyCode()) {
                case KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D -> gp.move((byte)0);
            }
        }
    }

    //handles "looking around"
    class MouseInputHandler extends MouseMotionAdapter {

        private int lastMouseLocationX = gp.getLocationOnScreen().x + (gp.getWidth() >> 1);     //center of GraphicsPanel
        private int lastMouseLocationY = gp.getLocationOnScreen().y + (gp.getHeight() >> 1);    //center of GraphicsPanel

        public void mouseMoved(MouseEvent e) {
            gp.moveMouse(e.getX() - lastMouseLocationX,e.getY() - lastMouseLocationY);
            lastMouseLocationX = e.getX();
            lastMouseLocationY = e.getY();
        }
    }
}
