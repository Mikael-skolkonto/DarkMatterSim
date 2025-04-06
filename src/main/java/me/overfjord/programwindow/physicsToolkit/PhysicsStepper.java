package me.overfjord.programwindow.physicsToolkit;

import mikera.vectorz.Vector3;

import java.util.ArrayList;

public class PhysicsStepper implements Runnable {

    public static final byte GRAVITY = 0, ELECTROSTATIC = 1, DARK_ENERGY = 2;

    public boolean simulating = false;

    private Space space;

    private final ArrayList<StepRule> stepRules = new ArrayList<>();

    public PhysicsStepper(byte... stepRules) {
        this.setStepRules(stepRules);
    }

    public void setStepRules(byte... stepRules) {
        if (this.simulating) {
            throw new RuntimeException(new IllegalThreadStateException());
        }
        if (this.stepRules.size() != 0) {
            this.stepRules.clear();
        }

        for (byte stepRule : stepRules) {
            switch (stepRule) {
                case GRAVITY -> this.stepRules.add(new Gravity());
                /*case ELECTROSTATIC -> this.stepRules.add(new Electrostatic(space));*/
                case DARK_ENERGY -> this.stepRules.add(new DarkEnergyExpansion());
                default -> throw new IllegalArgumentException("Unexpected value: " + stepRule);
            }
        }
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    @Override
    public void run() {
        this.simulating = true;
        long timestep_ns = 5_000_000;   //5 ms

        //The continuous loop
        while (simulating) {
            Vector3[] deltaVelocities = new Vector3[space.velocities.size()];

            //Calculate for each stepRule
            for (StepRule sr : stepRules) {

                //add velocities for each particle
                Vector3[] addedVelocities = sr.step(space,timestep_ns);
                for (int i = 0; i < addedVelocities.length; i++) {
                    //Add dv or assign initial value
                    if (deltaVelocities[i] == null) {
                        deltaVelocities[i] = addedVelocities[i];
                    } else {
                        deltaVelocities[i].add(addedVelocities[i]);
                    }
                }
            }

            //add velocities to particles
            for (int i = 0; i < deltaVelocities.length; i++) {
                //assert(deltaVelocities[i].magnitude()<10000) : "too high velocity";
                // TODO: 2025-04-06 FIND OUT WHY DELTAVELOCITIES[i] MIGHT BE NULL
                space.velocities.get(i).add(deltaVelocities[i]);
            }

            //move particles
            for (int i = 0, points = space.pointMassCoordinates.size(); i < points; i++) {
                space.pointMassCoordinates.get(i).add(deltaVelocities[i]);
            }
        }
    }
}
