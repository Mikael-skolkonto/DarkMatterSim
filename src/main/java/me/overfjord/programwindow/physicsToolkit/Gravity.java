package me.overfjord.programwindow.physicsToolkit;

import mikera.vectorz.Vector3;

public class Gravity extends StepRule {

    public static final double GRAVITATIONAL_CONSTANT = 0.000000000066743;

    @Override
    public Vector3[] step(Space space, long dt) {

        Vector3[] appliedVelocities = new Vector3[space.universeActors.size()];
        //initialize variables
        for (int i = 0; i < appliedVelocities.length; i++) {
            appliedVelocities[i] = new Vector3();
        }
        double dt_times_G = dt * GRAVITATIONAL_CONSTANT;

        //TODO: Find what factors drop out when substituting units. Important formulas: dv = dt * G(M/r^2) and F = G(M*m/r^2)
        //I measured this approach to be slightly faster (normalising a vector once and applying the the opposite force at the same time)
        //than normalising every vector twice (with 100 point-masses), about 2 micros faster
        for (int i = 0; i < appliedVelocities.length; i++) {

            //j is initialized as value of i + 1
            for (int j = i+1; j < appliedVelocities.length; j++) {
                Vector3 directionVec = new Vector3(space.pointMassCoordinates.get(j));  //get the position of mass 2
                directionVec.sub(space.pointMassCoordinates.get(i));                    //subtract the position of mass 1
                double inverseDistanceSquared = 1 / directionVec.normalise();         //normalise() also returns original magnitude
                inverseDistanceSquared *= inverseDistanceSquared;                   //get inverse squared
                directionVec.multiply(inverseDistanceSquared);                      //scale directionVector
                appliedVelocities[j].addMultiple(directionVec,-space.universeActors.get(i).mass);    //add velocity to mass 2
                directionVec.multiply(space.universeActors.get(j).mass);    //directionVector becomes velocityVector
                appliedVelocities[i].add(directionVec);
            }
        }
        //hittills har alla vektorer storheten [v/(t*G)] eller enheten [kg/m^2]
        for (Vector3 appliedVelocity : appliedVelocities) {
            appliedVelocity.multiply(dt_times_G);
        }

        return appliedVelocities;
    }
}
