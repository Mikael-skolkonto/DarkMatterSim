package MathTests;

import me.overfjord.programwindow.graphicsPanel.Camera;
import me.overfjord.programwindow.graphicsPanel.GraphicsPanel;
import me.overfjord.programwindow.physicsToolkit.Space;
import mikera.matrixx.Matrix33;
import mikera.vectorz.Vector3;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class CameraTest {

    @org.junit.jupiter.api.Test
    @DisplayName("Rotate a matrix")
    void rotate() {
        Camera c = new Camera(new GraphicsPanel(new Space(),1),1);
       // c.rotate(4.0,0.21);
        c.rotate(0.0,Math.PI*0.5);
        Matrix33 cRotation = c.getRotationMatrix();
        assertEquals(1.0,cRotation.determinant());
        System.out.println(cRotation);
        //point located on x-axis after transformation
        //Vector3 v = new Vector3(0.208459899846,0.639283668417,0.740176236752);
        //Vector3 result = cRotation.transform(v);
        /*assertAll(() -> assertEquals(0,result.x),
                () -> assertEquals(0,result.y),
                () -> assertEquals(1,result.z));*/

        //Assert all statement: assertAll(() -> assertEquals(2,2));
    }
}