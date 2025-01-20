package me.overfjord;

import me.overfjord.programwindow.MainProgramWindow;
import me.overfjord.programwindow.graphicsPanel.GraphicsPanel;
import me.overfjord.programwindow.physicsToolkit.Space;

public class DarkMatterSimulatorMain {

    public static void main(String[] args) {
        //Run the program window
        MainProgramWindow mainFrame = new MainProgramWindow();
        Thread windowThread = new Thread(mainFrame, "WindowThread");
    }
}