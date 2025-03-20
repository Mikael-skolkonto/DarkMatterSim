package me.overfjord.programwindow.physicsToolkit;

import mikera.vectorz.Vector3;

public class Gravity extends StepRule {

    private static final double GRAVITATIONAL_CONSTANT = 0.000000000066743;

    @Override
    public Vector3[] step(Space space, long dt) {
        Vector3[] appliedVelocities = new Vector3[space.universeActors.size()];

        //TODO: Find what factors drop out when substituting units. Important formulas: dv = dt * G(M/r^2) and F = G(M*m/r^2)
        for (int i = 0; i < appliedVelocities.length; i++) {
            //initialize variable
            appliedVelocities[i] = new Vector3();
            for (int j = 0; j < appliedVelocities.length; j++) {
                //one particle can't affect itself, indices i and j therefore have to be different
                if (i != j) {
                    Vector3 directionVec = new Vector3(space.pointMassCoordinates.get(j));
                    directionVec.sub(space.pointMassCoordinates.get(i));
                    double reciprocalDistanceSquare = 1 / directionVec.normalise();
                    reciprocalDistanceSquare *= reciprocalDistanceSquare;

                    directionVec.multiply(space.universeActors.get(j).mass * reciprocalDistanceSquare);    //becomes velocityVector
                    appliedVelocities[i].add(directionVec);
                }
            }
            appliedVelocities[i].scale(dt * GRAVITATIONAL_CONSTANT);
        }

        return appliedVelocities;
    }
}
