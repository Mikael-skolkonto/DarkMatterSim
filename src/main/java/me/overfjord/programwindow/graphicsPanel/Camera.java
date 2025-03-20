package me.overfjord.programwindow.graphicsPanel;

import me.overfjord.programwindow.physicsToolkit.PointMass;
import me.overfjord.programwindow.physicsToolkit.Space;
import mikera.matrixx.Matrixx;
import mikera.matrixx.Matrix33;
import mikera.vectorz.Vector3;

import java.awt.Graphics2D;
import java.util.ArrayList;

//TODO: add "static-state" for the camera to follow a particle
//TODO: Give the user tools to add particles and measure the expansion

//The camera should calculate the perspective of the user optimally(?).
@SuppressWarnings("SpellCheckingInspection")
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

    private Vector3 movingVec = new Vector3();

    private boolean isMoving = false;

    /**Field of view of the camera in radians.
     */
    private double FOV;
    private double panelWidth;
    private double panelHeight;
    private double dotsPerUnit;

    public Camera(GraphicsPanel gp) {
        this(gp,Math.PI * 0.5);
    }

    public Camera(GraphicsPanel gp, double fieldOfView) {
        this.panelWidth = gp.getWidth();
        this.panelHeight = gp.getHeight();
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
        this.dotsPerUnit = 0.5 * panelWidth / Math.tan(0.5 * FOV);
    }

    /**Sets the horizontal field of view to the given angle.
     * @param angle angle between {@literal 0} and {@code Math.PI}
     * @throws IllegalArgumentException if {@code angle <= 0 || angle >= Math.PI} returns true
     */
    public void setFieldOfView(double angle) {
        if (angle <= 0.0 || angle >= Math.PI) {
            throw new IllegalArgumentException("The angle %f is out of the accepted range of FOVs!".formatted(angle));
        }
        this.FOV = angle;
        this.dotsPerUnit = 0.5 * panelWidth / Math.tan(0.5 * FOV);  //virtual camera width is 2 * tan(FOV/2)
    }

    /**Adds the velocity v to the camera
     * @param v the movement vector
     */
    public void move(Vector3 v) {
        this.movingVec.add(v);
    }

    /**Sets the velocity to 0.0
     */
    public void stopMoving() {
        this.movingVec.fill(0.0);
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
            beta = Math.signum(beta)*Math.PI*0.5;
        }

        //Changes the rotation matrix to be the new angles
        this.rotation = Matrixx.createXAxisRotationMatrix(beta)
                .innerProduct(Matrixx.createYAxisRotationMatrix(alpha));
    }

    public void drawPoints(Space space, Graphics2D g2d) {
        pos.add(movingVec);     //Lite konstigt kanske att kameran rörs en konstant per bild, men orkar inte lägga till deltaTime

        int[][] drawCommands = project(space);

        for (int[] drawCommand : drawCommands) {

            //Kanske de magiska konstanterna "0" och "1" ska bytas ut mot "PointMass.X.ordinal()" men det funkar ej inuti en switch
            //switchen är där för att det lätt ska gå att lägga till fler sorters "PointMass"
            switch (drawCommand[3]) {
                case 0 -> g2d.setColor(PointMass.DARK_MATTER.color);
                case 1 -> g2d.setColor(PointMass.MATTER.color);
            }
            g2d.fillOval(drawCommand[0], drawCommand[1], drawCommand[2], drawCommand[2]);  //x,y,diameter,diameter
        }
    }

    /**Project points in space onto screen.
     * @param space Points and properties
     * @return List of vectors with elements: x, y, diameter and ordinal of {@code PointMass} type.
     */
    private int[][] project(Space space) {
        Vector3[] rotatedPos = new Vector3[space.pointMassCoordinates.size()];
        for (int i = 0; i < rotatedPos.length; i++) {
            rotatedPos[i] = rotation.transform(space.pointMassCoordinates.get(i).addCopy(this.pos.negateCopy()));
            //Synd att man inte kan skriva ".subCopy(this.pos)" för då får man AVector och inte Vector3
            //should create a new transformed vector
            //transforms the position-vector of the point by subtracting the camera-position and performing a matrix multiplication
        }

        //TODO överväg om "panelWidth" och "panelHeight" ska sparas som int istället
        //Arbitrarily long list of draw commands [x,y,diameter,color]
        ArrayList<int[]> projected = new ArrayList<>();

        for (int i = 0; i < rotatedPos.length; i++) {
            if (rotatedPos[i].z < 0.0 || rotatedPos[i].z > PointMass.particleDiameter) {
                continue;
            }
            //Here the point has to be in front of the camera and the diameter has to be > 1 pixel

            double reciprocalZ = 1 / rotatedPos[i].z;

            int x = (int)(reciprocalZ * rotatedPos[i].x * dotsPerUnit) + ((int)panelWidth >> 1);
            if (x < 0 || x > (int)panelWidth) {
                continue;
            }
            int y = -(int)(reciprocalZ * rotatedPos[i].y * dotsPerUnit) + ((int)panelHeight >> 1);
            if (y < 0 || y > (int)panelHeight) {
                continue;
            }
            //Here the point has to be within the panel and will therefore be drawn

            int diameter = (int)(reciprocalZ * PointMass.particleDiameter);

            int[] drawCommand = new int[] {x - (diameter >> 1),y - (diameter >> 1),diameter,space.universeActors.get(i).ordinal()};
            projected.add(drawCommand);
            //adds new draw command to arraylist
        }

        return projected.toArray(new int[][]{});
    }
}
