package me.overfjord.programwindow.physicsToolkit;

import mikera.vectorz.Vector3;

public abstract class StepRule {

    protected Space space;
    abstract Vector3[] step();
}
