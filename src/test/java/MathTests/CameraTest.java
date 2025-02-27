package MathTests;

import me.overfjord.programwindow.graphicsPanel.Camera;
import mikera.matrixx.Matrix33;
import mikera.vectorz.Vector3;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class CameraTest {

    @org.junit.jupiter.api.Test
    @DisplayName("Rotate a matrix")
    void rotate() {
        Camera c = new Camera();
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
        /*assertAll(() -> assertEquals(0.9780309147241483,cRotation.m00),
                () -> assertEquals(0.0,cRotation.m10),
                () -> assertEquals(-0.20845989984609956,cRotation.m20),
                () -> assertEquals(-0.15776297237516898,cRotation.m01),
                () -> assertEquals(-0.6536436208636117,cRotation.m11),
                () -> assertEquals(-0.7401762367515312,cRotation.m21),
                () -> assertEquals(-0.13625848374027036,cRotation.m02),
                () -> assertEquals(0.7568024953079284,cRotation.m12),
                () -> assertEquals(-0.6392836684168426,cRotation.m22));*/
    }
}