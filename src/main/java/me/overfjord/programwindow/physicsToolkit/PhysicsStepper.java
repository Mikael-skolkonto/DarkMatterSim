package me.overfjord.programwindow.physicsToolkit;

import java.util.ArrayList;

public class PhysicsStepper implements Runnable {

    public static final byte GRAVITY = 1, ELECTROSTATIC = 2;

    public boolean simulating = true;

    private Space space;

    private ArrayList<StepRule> stepRules = new ArrayList<>();

    public PhysicsStepper(byte... stepRules) {
        for (byte stepRule : stepRules) {
            switch (stepRule) {
                case 1 -> this.stepRules.add(new Gravity(space));
                /*case 2 -> this.stepRules.add(new Electrostatic(space));*/
                default -> throw new IllegalArgumentException("Unexpected value: " + stepRule);
            }
        }
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    @Override
    public void run() {
        while (simulating) {

        }
    }
}
