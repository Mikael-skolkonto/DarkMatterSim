package me.overfjord.programwindow.physicsToolkit;

import mikera.vectorz.Vector3;

public class DarkEnergyExpansion extends StepRule {

    //TODO: Use hubbles expansion rate (NOTE: the expansion rate is the resulting velocity per distance from the effects of gravity and dark energy)
    private static final double EXPANSION_FACTOR = 1.000001;
    @Override
    public Vector3[] step(Space space, long dt) {
        for (int i = 0, points = space.pointMassCoordinates.size(); i < points; i++) {
            space.pointMassCoordinates.get(i).multiply(EXPANSION_FACTOR);
        }
        return new Vector3[0];
    }
}
