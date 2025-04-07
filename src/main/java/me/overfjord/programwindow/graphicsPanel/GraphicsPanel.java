package me.overfjord.programwindow.graphicsPanel;

import me.overfjord.programwindow.physicsToolkit.Space;
import mikera.vectorz.Vector3;

import javax.swing.JPanel;
import java.awt.*;

public class GraphicsPanel extends JPanel implements Runnable {

    /**The space that is displayed through this <code>GraphicsPanel</code>
     */
    public final Space space;

    /**Camera object will draw particles and keep track of orientation
     */
    private final Camera camera;

    /**Used by other classes to pause the frame-refreshing
     */
    public boolean running = false;

    /**Minimum ms delay between each frame.
     */
    private int FPS_CAP;

    private Graphics2D g2d;

    /**
     * Constructs an instance with an instance of <code>Camera</code>
     * @param space Reference to an instance of <code>Space</code> that is displayed through this <code>GraphicsPanel</code>
     * @param size The dimensions of this <code>JPanel</code>
     * @param fpsCap An upper limit on the refresh rate, in milliseconds
     */
    public GraphicsPanel(Space space, Dimension size, int fpsCap) {
        this.space = space;
        this.setPanelSize(size);
        this.camera = new Camera(this);
        setFPSCap(fpsCap);
        this.g2d = (Graphics2D) getGraphics();

        setOpaque(false);
    }

    /**
     * Sets the cap of redraws per second
     * @param fpsCap the maximum allowed frames per second
     */
    public void setFPSCap(int fpsCap) {
        if (this.running) {
            throw new RuntimeException(new IllegalThreadStateException());
        }
        this.FPS_CAP = 1000 / fpsCap;
    }

    /**
     * Sets the Field Of View of the {@code Camera} object
     * @param FOV a double value between {@code Math.PI} and {@code 0}
     */
    public void setFOV(double FOV) {
        if (this.running) {
            throw new RuntimeException(new IllegalThreadStateException());
        }
        if (FOV < 0.0 || FOV > Math.PI) {
            throw new IllegalArgumentException(FOV + " isn't in the span between 0 and PI");
        }

        camera.setFieldOfView(FOV);
    }

    /**
     * Paints the visual components, e.g. text and particles
     * @param g  the <code>Graphics</code> context in which to paint
     */
    public void paint(Graphics g) {
        g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
        g2d.setBackground(SystemColor.BLACK);
        g2d.clearRect(0,0,getWidth(),getHeight());

        camera.drawPoints(space, g2d);
    }

    /**Updates the camera with the new size of the frame, if the field isn't null
     * @param d The dimension specifying the new size
     */
    public void setPanelSize(Dimension d) {
        if (camera != null) {
            camera.setPanelSize(d.width,d.height);
        }
    }

    /**
     * Continually refreshes the frame of the <code>GraphicsPanel</code> when running = true
     */
    @Override
    public void run() {
        running = true;
        //Creates invisible cursor and sets the cursor to it
        setCursor(getToolkit().createCustomCursor(
                new java.awt.image.BufferedImage(1,1,java.awt.image.BufferedImage.TYPE_INT_ARGB),
                new Point(),
                null));

        try {
            while (running) {
                this.paint(this.getGraphics());
                Thread.sleep(FPS_CAP);
            }
        } catch (InterruptedException interruptException) {
            interruptException.printStackTrace();
        }
        setCursor(Cursor.getDefaultCursor());
    }

    /**Interfaces with the camera that projects space onto the screen.
     * Moving in a direction depending on the constant given.
     * <pre><li>0 -> stop</li>
     * <li>1 -> forward</li>
     * <li>2 -> left</li>
     * <li>3 -> backward</li>
     * <li>4 -> right</li>
     * </pre>
     * @param direction The direction to move the camera object,
     */
    public void move(int direction) {
        Vector3 directionVec;
        //direction = 0 -> stop
        //direction = 1 -> W key
        //direction = 2 -> A key
        //direction = 3 -> S key
        //direction = 4 -> D key
        switch (direction) {
            case 0 -> directionVec = null;
            case 1 -> directionVec = new Vector3(0.0, 0.0, 1.0);
            case 2 -> directionVec = new Vector3(-1.0, 0, 0);
            case 3 -> directionVec = new Vector3(0.0, 0.0, -1.0);
            case 4 -> directionVec = new Vector3(1.0, 0, 0);
            default -> directionVec = new Vector3();
        }
        if (directionVec == null) {
            this.camera.stopMoving();
            return;
        }

        directionVec.multiply(0.005);    //speed is 0.5%
        this.camera.getRotationMatrix().inverse().transformInPlace(directionVec);

        this.camera.move(directionVec);
    }

    /**Call method when mouse is moved, this will make the camera rotate
     * @param x The amount of pixels the mouse is moved to the right
     * @param y The amount of pixels the mouse is moved down
     */
    public void moveMouse(int x, int y) {
        //sensitivity constant is 2PI radians / 2560 pixels (1 turn across my entire screen)
        double theta = -x * 0.00245436926061702596754894014319;
        double phi = -y * 0.00245436926061702596754894014319;
        camera.rotate(theta,phi);
    }
}
