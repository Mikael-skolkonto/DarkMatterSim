package me.overfjord.programwindow.physicsToolkit;

import mikera.vectorz.Vector3;

//TODO refactor this to be a static class
public abstract class StepRule {

    protected Space space;
    abstract Vector3[] step(Space space, long dt);
}
