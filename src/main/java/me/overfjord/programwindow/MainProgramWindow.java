package me.overfjord.programwindow;

import me.overfjord.programwindow.graphicsPanel.GraphicsPanel;
import me.overfjord.programwindow.physicsToolkit.PhysicsStepper;
import me.overfjord.programwindow.physicsToolkit.Space;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.SeparatorUI;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

/**
 * This class is the program window and handles the inputs
 */
public class MainProgramWindow extends JFrame {

    private final GraphicsPanel gp;

    /**Starts the simulation GUI
     */
    private final Runnable startVisuals;

    /**På skoldatorn stötte jag på problemet att punkterna kunde försvinna innan jag hittade dem,
     * med detta kan användaren välja att starta simuleringen med 'R'
     */
    private final Runnable startSim;

    //TODO: extract class to handle music and sound
    /**
     * Plays the background music
     */
    private Clip clip;

    /**
     * Feeds the Clip the input file
     */
    private AudioInputStream audioIn;

    /**
     * A single instance, so not multiple instances of the same file fills up the memory
     */
    private final File musicFile = new File("src/main/resources/soundtrack.wav");

    /**Used to do actions like move the cursor to the center of the screen.
     * Sudo is the common name for commands that mimic user actions.
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
        PhysicsStepper ps = new PhysicsStepper(PhysicsStepper.GRAVITY,PhysicsStepper.DARK_ENERGY);
        Thread physicsThread = new Thread(ps,"PhysicsStepperThread");
        Space space = new Space(ps);
        ps.setSpace(space);
        this.startSim = physicsThread::start;

        //Add panel to display simulation
        this.gp = new GraphicsPanel(space,getSize(),60);
        Thread graphicsThread = new Thread(gp, "GraphicsThread");

        this.startVisuals = () -> {
            getContentPane().remove(0);

            getContentPane().add(gp);
            addMouseMotionListener(new MouseInputHandler());
            addKeyListener(new KeyboardInputHandler());
            addWindowStateListener(new WindowStateHandler());
            graphicsThread.start();
        };

        setLocationRelativeTo(null);
        getContentPane().setBackground(SystemColor.BLACK);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        getContentPane().add(new SettingsPanel(gp, this.startVisuals));
    }

    private void switchFullscreen() {
        //setUndecorated(false);    Detta ska sätta fönstret som "borderless fullscreen"
        //setUndecorated(true);     men det funkar ej för tillfället, därför förenklades metodens if-sats
        setExtendedState(getExtendedState() == MAXIMIZED_BOTH ? NORMAL : MAXIMIZED_BOTH);
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

            //stop the simulation and return the results
            if (e.getKeyCode() == KeyEvent.VK_ENTER && gp.space.physics.simulating) {
                gp.running = false;
                getContentPane().remove(0);
                getContentPane().add(new ResultsPanel(gp.space));
            }

            //TODO fix the memory leak from continually starting and stopping the music
            //Background music
            if (e.getKeyCode() == KeyEvent.VK_M) {
                if (clip != null) {
                    try {
                        audioIn.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    clip.stop();
                    //A little dirty to set it to null to mean the music is turned off
                    clip = null;
                    return;
                }
                try {
                    audioIn = AudioSystem.getAudioInputStream(musicFile.toURI().toURL());

                    clip = AudioSystem.getClip();
                    clip.open(audioIn);
                    clip.loop(Clip.LOOP_CONTINUOUSLY);
                } catch (LineUnavailableException | UnsupportedAudioFileException | IOException ex) {
                    ex.printStackTrace();
                }
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

    /**Handles setting the screen size of the Camera instance and
     */
    class WindowStateHandler extends WindowAdapter {
        @Override
        public void windowStateChanged(WindowEvent e) {
            gp.setPanelSize(e.getWindow().getSize());
        }
    }
}

class SettingsPanel extends JPanel {

    final GraphicsPanel gp;
    final Runnable startVisuals;
    final JCheckBox stepRuleGravityCheckbox;
    final JCheckBox stepRuleDarkEnergyCheckbox;
    final JSpinner FPSCapChooser;
    final JSpinner FOVChooser;

    public SettingsPanel(GraphicsPanel gp, Runnable startVisuals) {
        this.gp = gp;
        this.startVisuals = startVisuals;

        JLabel fpsLabel = new JLabel("FPS cap:");
        fpsLabel.setBackground(SystemColor.WHITE);
        fpsLabel.setOpaque(true);
        add(fpsLabel);
        FPSCapChooser = new JSpinner();
        FPSCapChooser.setValue(60);
        add(FPSCapChooser);

        JLabel fovLabel = new JLabel("Field of View (degrees):");
        fovLabel.setBackground(SystemColor.WHITE);
        fovLabel.setOpaque(true);
        add(fovLabel);
        FOVChooser = new JSpinner();
        FOVChooser.setValue(90);
        add(FOVChooser);

        stepRuleGravityCheckbox = new JCheckBox("Gravity",true);
        stepRuleDarkEnergyCheckbox = new JCheckBox("Dark Energy",true);
        add(stepRuleGravityCheckbox).setVisible(true);
        add(stepRuleDarkEnergyCheckbox);

        JSeparator menuSeparator = new JSeparator(JSeparator.HORIZONTAL);
        menuSeparator.setSize(35,menuSeparator.getHeight());
        add(menuSeparator);

        JButton startButton = new JButton("Start simulation");
        //startButton.setLocation(400,350);
        startButton.setSize(200,30);
        add(startButton);
        startButton.addActionListener(new StartButtonActionListener());

        this.setOpaque(false);
    }

    private class StartButtonActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            //set to the selected FPS cap (default is 60)
            int fps = (int)FPSCapChooser.getValue();
            if (!(fps == 60) && fps > 0) {
                gp.setFPSCap((int)FPSCapChooser.getValue());
            }

            //set to the selected FOV (default is 90 degrees)
            int fov = (int)FOVChooser.getValue();
            if (!(fov == 90) && fov > 0 && fov < 180) {
                gp.setFOV(Math.toRadians(fov));
            }

            //set to the selected step-rules (default is both being selected)
            if (!(stepRuleGravityCheckbox.isSelected() && stepRuleDarkEnergyCheckbox.isSelected())) {
                if (stepRuleGravityCheckbox.isSelected()) {
                    gp.space.physics.setStepRules(PhysicsStepper.GRAVITY);
                } else if (stepRuleDarkEnergyCheckbox.isSelected()) {
                    gp.space.physics.setStepRules(PhysicsStepper.DARK_ENERGY);
                }
            }

            //run the simulation-window
            startVisuals.run();
        }
    }
}

class ResultsPanel extends JPanel {

    /**
     * Takes the results recorded by the physics-stepper and displays them
     * @param space The
     */
    public ResultsPanel(Space space) {
        JLabel resultsLabel = new JLabel("FPS cap:");
        resultsLabel.setBackground(SystemColor.WHITE);
        resultsLabel.setOpaque(true);
        add(resultsLabel);

        this.setOpaque(false);
    }

}