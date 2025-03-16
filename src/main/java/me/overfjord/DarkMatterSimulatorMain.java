package me.overfjord;

import me.overfjord.programwindow.MainProgramWindow;

public class DarkMatterSimulatorMain {

    public static void main(String[] args) {
        //Run the program window
        MainProgramWindow mainFrame = new MainProgramWindow();
        //Thread windowThread = new Thread(mainFrame, "WindowThread");
        //MainProgramWindow has an empty run() method, so no windowThread.start() needed
    }
}