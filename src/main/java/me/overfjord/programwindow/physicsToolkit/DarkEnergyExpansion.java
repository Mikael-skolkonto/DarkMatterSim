package me.overfjord.programwindow.physicsToolkit;

import mikera.vectorz.Vector3;

public class DarkEnergyExpansion extends StepRule {

    private static final double EXPANSION_FACTOR = 1.000001;
    @Override
    public Vector3[] step(Space space, long dt) {
        for (int i = 0, points = space.pointMassCoordinates.size(); i < points; i++) {
            space.pointMassCoordinates.get(i).multiply(EXPANSION_FACTOR);
        }
        return new Vector3[0];
    }
}
