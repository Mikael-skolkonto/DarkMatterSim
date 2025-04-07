package me.overfjord.programwindow.physicsToolkit;

import mikera.vectorz.Vector3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

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

        DataCollector dataCollector = new DataCollector();

        long startTime = System.nanoTime();
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
                // TODO: 2025-04-06 FIND OUT WHY DELTAVELOCITIES[i] MIGHT BE NULL WHEN ONLY STEP RULE "DARK_ENERGY" IS ACTIVE
                space.velocities.get(i).add(deltaVelocities[i]);
            }

            //move particles
            for (int i = 0, points = space.pointMassCoordinates.size(); i < points; i++) {
                space.pointMassCoordinates.get(i).add(deltaVelocities[i]);
            }
            dataCollector.collectData();
            System.out.println(System.nanoTime() - startTime);
            startTime = System.nanoTime();
        }
    }

    class DataCollector {

        /**
         * Since collecting data every time step would take up too much data,
         * this constant determines how many calls of collectData() that will be ignored.
         */
        public static final int INTERVAL = 500;
        private int callCounter = 0;

        //proportionality constant for mass: 10^-22 kg/unit
        //proportionality constant for time: 10^-9 sec/unit
        //proportionality constant for distance: 10^18 m/unit
        private static final double DENSITY_PROPORTIONALITY_FACTOR = Math.pow(10,-76);
        private static final double EXPANSION_VELOCITY_PROPORTIONALITY_FACTOR = Math.pow(10,27);

        private final double mass = (double)space.universeActors.stream().filter(Predicate.isEqual(PointMass.MATTER)).count() *
                PointMass.MATTER.mass +
                (double)space.universeActors.stream().filter(Predicate.isEqual(PointMass.MATTER)).count() *
                        PointMass.MATTER.mass;
        private ArrayList<Double> density = new ArrayList<>(64);

        private ArrayList<Double> expansionVelocity = new ArrayList<>(64);

        public DataCollector() {

        }

        public void collectData() {
            if (callCounter % INTERVAL != 0) {
                callCounter++;
                return;
            }

            density.add(collectDensity());
            expansionVelocity.add(collectExpansionRate());

            callCounter++;
        }

        private double collectExpansionRate() {
            java.util.Iterator<Vector3> vector3Iterator = space.velocities.stream().iterator();
            return space.pointMassCoordinates.stream().sequential()
                    .mapToDouble(e -> vector3Iterator.next().dotProduct(e.toNormal()))
                    .average().getAsDouble();
            //With the interpretation that "expansion" is the average speed outward
        }

        private double collectDensity() {
            double distance = space.pointMassCoordinates.parallelStream().mapToDouble(e -> Math.sqrt(e.x*e.x+e.y*e.y+e.z*e.z)).max().getAsDouble();
            return DENSITY_PROPORTIONALITY_FACTOR*3*mass / (4*Math.PI*distance*distance*distance);
        }

        // TODO: 2025-04-07 Implement method to return the results to the user
        public String summarize(long dt) {
            final long timeStep = INTERVAL * dt;
            return timeStep + " ns timesteps \n" + "Density: " + Arrays.toString(density.toArray(Double[]::new)) + "\nExpansion: "
                    + Arrays.toString(expansionVelocity.toArray(Double[]::new));
        }
    }
}
