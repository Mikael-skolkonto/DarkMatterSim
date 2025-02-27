package me.overfjord.programwindow.graphicsPanel;

import me.overfjord.programwindow.physicsToolkit.PointMass;
import me.overfjord.programwindow.physicsToolkit.Space;
import mikera.matrixx.Matrix33;
import mikera.matrixx.Matrixx;
import mikera.vectorz.Vector3;

import java.awt.*;

//TODO: Make the angles and position user-changeable
//TODO: add "static-state" for the camera to follow a particle
//TODO: Give the user tools to add particles and measure the expansion

//The camera should calculate the perspective of the user optimally.
public class Camera {

    /**the angle between the normal vector and the yz-plane
     */
    private double alpha = 0.0;
    /**the angle between the normal vector and the xz-plane
     */
    private double beta = 0.0;
    /**the rotation matrix
     */
    private Matrix33 rotation = Matrix33.createIdentityMatrix();
    /**the position of the camera
     */
    private Vector3 pos = new Vector3();

    public Camera() {

    }

    public Matrix33 getRotationMatrix() {
        return rotation;
    }

    public void drawPoints(Space space, Graphics2D g2d) {
        //int[n][3] = project(int[m][]);
        //drawToScreen(int[n][3] n points with x,y,diameter, PointMass[n] DARK-/MATTER);

        //project(space)
        /*for (int i = 0, ceiling = space.universeActors.size(); i < ceiling; i++) {
            g2d.setColor(space.universeActors.get(i).color);
            //draws a circle circumscribed by the square "(x1,y1),(x1+width,y1+height))
            g2d.fillOval((int)space.pointMassCoordinates.get(i).x,(int)space.pointMassCoordinates.get(i).y,10,10);

        }*/
    }

    /**Project points in space onto screen.
     * @param space Points and properties
     * @return List of vectors with elements: x, y and diameter
     */
    private int[][] project(Space space) {

    }

    /**Rotate the camera and update the matrix
     * @param theta the added pan-angle
     * @param phi the added nod-angle
     */
    public void rotate(double theta, double phi) {
        this.alpha += theta;
        //Keeping the angle between plus-minus PI
        if (Math.abs(alpha) > Math.PI) {
            //Adding or subtracting a full turn
            alpha -= Math.signum(alpha)*2.0*Math.PI;
        }

        this.beta += phi;
        //Keeping the angle between plus-minus 90 degrees
        if (Math.abs(beta) > Math.PI*0.5) {
            beta = Math.signum(beta)*Math.PI;
        }

        this.rotation = Matrixx.createYAxisRotationMatrix(alpha)
                        .innerProduct(Matrixx.createXAxisRotationMatrix(beta));
    }
}
