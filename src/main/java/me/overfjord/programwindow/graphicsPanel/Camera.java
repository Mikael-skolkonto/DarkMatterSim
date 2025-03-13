package me.overfjord.programwindow.graphicsPanel;

import me.overfjord.programwindow.physicsToolkit.PointMass;
import me.overfjord.programwindow.physicsToolkit.Space;
import mikera.matrixx.Matrix33;
import mikera.matrixx.Matrixx;
import mikera.vectorz.Vector3;

import java.awt.*;
import java.util.ArrayList;

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

    /**Field of view of the camera in radians.
     */
    private double FOV;
    private double panelWidth;
    private double panelHeight;
    private double dotsPerUnit;

    public Camera(int panelWidth, int panelHeight) {
        this(panelWidth, panelHeight, Math.PI * 0.5);
    }

    public Camera(int panelWidth, int panelHeight, double fieldOfView) {
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
        this.FOV = fieldOfView;
        this.dotsPerUnit = 0.5 * panelWidth / Math.tan(0.5 * FOV);     //Vidden på virtuella skärmen är 2*tan(FOV/2)

    }

    public Matrix33 getRotationMatrix() {
        return rotation;
    }

    /**Should be called whenever the {@code GraphicsPanel} changes width or height.
     * @param width the new width of the GraphicsPanel
     * @param height the new height of the GraphicsPanel
     */
    public void setPanelSize(int width, int height) {
        this.panelWidth = width;
        this.panelHeight = height;
    }

    /**Sets the horizontal field of view to the given angle.
     * @param angle angle between {@literal 0} and {@code Math.PI}
     */
    public void setFieldOfView(double angle) {

    }

    public void drawPoints(Space space, Graphics2D g2d) {
        //int[n][3] = project(int[m][]);
        //drawToScreen(int[n][3] n points with x,y,diameter, PointMass[n] DARK-/MATTER);
        for (PointMass p:
             PointMass.values()) {
            p.ordinal()
        }
        //project(space)
        /*for (int i = 0, ceiling = space.universeActors.size(); i < ceiling; i++) {
            g2d.setColor(space.universeActors.get(i).color);
            //draws a circle circumscribed by the square "(x1,y1),(x1+width,y1+height))
            g2d.fillOval((int)space.pointMassCoordinates.get(i).x,(int)space.pointMassCoordinates.get(i).y,10,10);

        }*/
    }

    /**Project points in space onto screen.
     * @param space Points and properties
     * @return List of vectors with elements: x, y, diameter and ordinal of {@code PointMass} type.
     */
    private int[][] project(Space space) {
        Vector3[] rotatedPos = new Vector3[space.pointMassCoordinates.size()];
        for (int i = 0; i < rotatedPos.length; i++) {
            rotatedPos[i] = (Vector3) rotation.transform(space.pointMassCoordinates.get(i).addMultipleCopy(this.pos,-1.0));
            //should create a new transformed vector
            //transforms the position-vector of the point by subtracting the camera-position and performing a matrix multiplication
            
        }

        //TODO Add the rest of the logic from the desmos graph
        //TODO Test the format of the generated integer matrix
        //Arbitrarily long list of draw commands [x,y,diameter,color]
        ArrayList<int[]> projected = new ArrayList<>();

        for (int i = 0; i < rotatedPos.length; i++) {
            if (rotatedPos[i].z > 0.0) {

            }
        }

        return projected.toArray(new int[][]{});
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
