package me.overfjord.programwindow;

import me.overfjord.programwindow.graphicsPanel.GraphicsPanel;
import me.overfjord.programwindow.physicsToolkit.PhysicsStepper;
import me.overfjord.programwindow.physicsToolkit.Space;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * This class is the program window and handles the inputs
 */
public class MainProgramWindow extends JFrame {

    private final GraphicsPanel gp;

    /**På skoldatorn stötte jag på problemet att punkterna kunde försvinna innan jag hittade dem,
     * med detta kan användaren välja att starta simuleringen med 'R'
     */
    private final Runnable startSim;

    /**Used to do actions like move the cursor to the center of the screen
     */
    private final Robot sudo;
    {
        try {
            sudo = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Constructs an instance with instances of {@code GraphicsPanel} and {@code GraphicsPanel}
     */
    public MainProgramWindow() {
        Image windowIcon = Toolkit.getDefaultToolkit().getImage("src/main/resources/windowIcon.jpg");
        setIconImage(windowIcon);
        setTitle("Dark Matter Simulator");

        //Sets the window height the same as screen height and sets the width to height x golden_ratio
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = (int) (height * 1.61803398875);
        setSize(width,height);

        //Creating physicsThread
        PhysicsStepper ps = new PhysicsStepper(PhysicsStepper.GRAVITY);
        Thread physicsThread = new Thread(ps,"PhysicsStepperThread");
        Space space = new Space(ps);
        ps.setSpace(space);
        this.startSim = physicsThread::start;

        //Add panel to display simulation
        this.gp = new GraphicsPanel(space,getSize(),60);
        getContentPane().add(gp);
        Thread graphicsThread = new Thread(gp, "GraphicsThread");

        setLocationRelativeTo(null);
        getContentPane().setBackground(SystemColor.BLACK);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        addMouseMotionListener(new MouseInputHandler());
        addKeyListener(new KeyboardInputHandler());
        addWindowStateListener(new WindowStateHandler());
        //FileReader    (lägg till Let It Happen Musik)

        graphicsThread.start();     //TYDLIGEN VÄLDIGT VIKTIGT ATT THREADEN STARTAS I SLUTET AV KONSTRUKTÖREN! UPPTÄCKT GENOM TRIAL AND ERROR
        //JAG JÄMFÖRDE MED JPong OCH ENDA SKILLNADEN VAR PLACERINGEN AV DETTA METODANROPET

    }

    private void switchFullscreen() {
        if (getExtendedState() == MAXIMIZED_BOTH) {
            setExtendedState(NORMAL);
            //setUndecorated(false);
        } else {
            setExtendedState(MAXIMIZED_BOTH);
            //setUndecorated(true);     Funkar ej för tillfället
        }
    }

    class KeyboardInputHandler extends KeyAdapter {

        public void keyPressed(KeyEvent e) {

            //fullscreen-key
            if (e.getKeyCode() == KeyEvent.VK_F11) {
                switchFullscreen();
                return;
            }

            //start the simulation if it isn't already running
            if (e.getKeyCode() == KeyEvent.VK_R && !gp.space.physics.simulating) {
                startSim.run();
                return;
            }

            //movement-keys
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

            //stopping movement when a movement-key has been unpressed
            switch (e.getKeyCode()) {
                case KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D -> gp.move(0);
            }
        }
    }


    //handles "looking around"
    class MouseInputHandler extends MouseMotionAdapter {

        public void mouseMoved(MouseEvent e) {
            gp.moveMouse(e.getX() - (getWidth() >> 1),e.getY() - (getHeight() >> 1));   //calls .moveMouse() with the distance from the origin as x & y
            sudo.mouseMove(getX() + (getWidth() >> 1),getY() + (getHeight() >> 1));     //repositions the mouse at the origin
        }
    }

    class WindowStateHandler extends WindowAdapter {
        @Override
        public void windowStateChanged(WindowEvent e) {
            gp.setPanelSize(e.getWindow().getSize());
        }
    }
}
