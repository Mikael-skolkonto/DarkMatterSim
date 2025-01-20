package me.overfjord.programwindow.physicsToolkit;

public enum PointMass {
    //A dark matter particle has a mass somewhere between 1.7826879198*10^-29 and 1.7826879198*10^-39
    DARK_MATTER(-1.782687919810*Math.pow(10,-29)),
    MATTER(1.6726219*Math.pow(10,-27));

    double mass;
    PointMass(double m) {
        this.mass = m;
    }
}
