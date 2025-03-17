package me.overfjord.programwindow.physicsToolkit;

import mikera.vectorz.Vector3;

import java.util.ArrayList;

public class PhysicsStepper implements Runnable {

    public static final byte GRAVITY = 1, ELECTROSTATIC = 2;

    public boolean simulating = true;

    private Space space;

    private final ArrayList<StepRule> stepRules = new ArrayList<>();

    public PhysicsStepper(byte... stepRules) {
        for (byte stepRule : stepRules) {
            switch (stepRule) {
                case 0 -> this.stepRules.add(new Gravity());
                /*case 1 -> this.stepRules.add(new Electrostatic(space));*/
                case 2 -> this.stepRules.add(new DarkEnergyExpansion());
                default -> throw new IllegalArgumentException("Unexpected value: " + stepRule);
            }
        }
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    @Override
    public void run() {
        long timestep_ns = 5_000_000;   //5 ms

        //The continuous loop
        while (simulating) {
            Vector3[] deltaVelocities = new Vector3[space.universeActors.size()];

            //Calculate for each stepRule
            for (StepRule sr : stepRules) {

                //add velocities for each particle
                Vector3[] addedVelocities = sr.step(space,timestep_ns);
                for (int i = 0; i < addedVelocities.length; i++) {
                    deltaVelocities[i] = addedVelocities[i];    //for now this is incompatible with having multiple stepRules
                }
            }

            //add velocities to particles
            for (int i = 0; i < deltaVelocities.length; i++) {
                space.velocities.get(i).add(deltaVelocities[i]);
            }

            //move particles
            for (int i = 0, points = space.pointMassCoordinates.size(); i < points; i++) {
                space.pointMassCoordinates.get(i).add(deltaVelocities[i]);
            }
        }
    }
}
