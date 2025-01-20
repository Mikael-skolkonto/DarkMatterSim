package me.overfjord.programwindow;

import me.overfjord.programwindow.graphicsPanel.GraphicsPanel;
import me.overfjord.programwindow.physicsToolkit.Space;

import javax.swing.*;
import java.awt.*;

public class MainProgramWindow extends JFrame implements Runnable {

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
        int width = (int) (height * 1.61803398875d);
        setSize(width,height);

        setLocationRelativeTo(null);
        getContentPane().setBackground(SystemColor.BLACK);

        //Add panel to display simulation
        GraphicsPanel gp = new GraphicsPanel(new Space(),60);
        getContentPane().add(gp);
        Thread graphicsThread = new Thread(gp, "GraphicsThread");
        graphicsThread.start();

        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
