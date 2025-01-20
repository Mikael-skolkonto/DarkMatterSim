package me.overfjord.programwindow.physicsToolkit;

import mikera.vectorz.Vector3;

public class Gravity extends StepRule {

    public Gravity(Space space) {
        super.space = space;
    }

    @Override
    public Vector3[] step() {
        Vector3[] AppliedForces = new Vector3[space.universeActors.size()];
        //TODO: Implement gravity. Formulas: dv = dt * G(M/r^2) and F = G(M*m/r^2)
        return new Vector3[space.universeActors.size()];
    }
}
