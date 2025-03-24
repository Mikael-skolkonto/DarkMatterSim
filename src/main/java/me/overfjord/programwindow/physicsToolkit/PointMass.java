package me.overfjord.programwindow.physicsToolkit;

import java.awt.Color;
import java.awt.Toolkit;

public enum PointMass {

    //TODO: check what constants to use here (source) https://www.livescience.com/dark-matter-mass.html
    //A dark matter particle has a mass somewhere between 1.7826879198*10^-29 and 1.7826879198*10^-39
    //DARK_MATTER(1.782687919810*Math.pow(10,-29), new Color(39, 74, 77)),
    //MATTER(1.6726219*Math.pow(10,-27), new Color(213, 64, 64));
    DARK_MATTER(1.782687919810*Math.pow(10,-6), new Color(39, 74, 77)),
    MATTER(1.6726219*Math.pow(10,-5), new Color(213, 64, 64));

    public static final double particleDiameter = 0.015625 * Toolkit.getDefaultToolkit().getScreenSize().width; //40.0 for screen width 2560p
    public final double mass;
    public final Color color;
    PointMass(double m, Color color) {
        this.mass = m;
        this.color = color;
    }
}
