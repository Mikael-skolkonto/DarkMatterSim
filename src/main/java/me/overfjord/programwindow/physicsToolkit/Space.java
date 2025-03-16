package me.overfjord.programwindow.physicsToolkit;

import mikera.vectorz.Vector3;
import java.util.ArrayList;

public class Space {

    public ArrayList<PointMass> universeActors = new ArrayList<>();
    public ArrayList<Vector3> pointMassCoordinates = new ArrayList<>();

    public ArrayList<Vector3> velocities = new ArrayList<>();

    public Space() {
        for (int i = 0; i < 500; i++) {
            if (i % 2 == 0) {
                this.add(PointMass.DARK_MATTER, new Vector3(5*Math.random(),5*Math.random(),5*Math.random()),new Vector3());
            } else {
                this.add(PointMass.MATTER, new Vector3(5*Math.random(),5*Math.random(),5*Math.random()),new Vector3());
            }
        }
    }

    public Space add(PointMass p) {
        return this.add(p,new Vector3());

    }

    public Space add(PointMass p, Vector3 xyz) {
        return this.add(p,xyz,new Vector3());
    }

    public Space add(PointMass p, Vector3 xyz, Vector3 v) {
        universeActors.add(p);
        pointMassCoordinates.add(xyz);
        velocities.add(v);
        return this;
    }

}
