/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
***/
package com.test.openglfirst.GLUtils.GLShape;


import android.opengl.GLES20;
import android.opengl.Matrix;

import com.test.openglfirst.GLUtils.GLUtil;
import com.test.openglfirst.GLUtils.base.BaseSurfaceViewRender;
import com.test.openglfirst.GLUtils.base.SaveHelper;
import com.test.openglfirst.GLUtils.third.util.MatrixHelper;
import com.test.openglfirst.GLUtils.third.util.TextureHelper;
import com.test.openglfirst.MainActivity;
import com.test.openglfirst.MyApplication;
import com.test.openglfirst.R;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;
import static com.test.openglfirst.GLUtils.third.Constants.BYTES_PER_FLOAT;


public class GLPerson extends BaseSurfaceViewRender {
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT
        + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private static final float[] VERTEX_DATA1 = {
        // Order of coordinates: X, Y, S, T

        // Triangle Fan
           0f,    0f,   0,
        -0.1f, -0.1f,   0,
            0.1f, -0.1f,   0,
            0.1f,  0.1f,   0,
        -0.1f,  0.1f,   0,
        -0.1f, -0.1f,   0,};

    private static final float[] VERTEX_DATA2 = {
            // Order of coordinates: X, Y, S, T
            // Triangle Fan
           0.5f, 0.5f,0,
            0f, 1f,0,
           1f, 1f,0,
              1f, 0,0,
            0f, 0,0,
             0f, 1,0 };


    public float currentPersonX=0;
    public float currentPersonY=0;
    public float currentPersonAngle=0;



    @Override
    protected String loadVertexShader() {
        return "shaders/table.vert";
    }

    @Override
    protected String loadFragmentShader() {
        return "shaders/table.frag";
    }

    @Override
    protected void onSurfaceCreated() {

    }

    private final float[] finalMatrix = new float[16];


    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] modelMatrix = new float[16];

    @Override
    protected void onSurfaceChanged(int width, int height) {
// Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height);

        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width
                / (float) height,  0.1f, 100f);

//        setIdentityM(viewMatrix, 0);
//        translateM(viewMatrix, 0, 1f, 1f, -10f);
//        multiplyMM(projectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

//        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
        updateCamera(0,0,4);
    }

    int textureId;
    @Override
    protected void initValues() {
        textureId = TextureHelper.loadTexture(MyApplication.getContext(), R.drawable.ic_person_up);
        uTextureUnitLocation = getUniform("u_TextureUnit");
    }

    private int uTextureUnitLocation;

    @Override
    protected void onDrawFrame() {

        updateCamera();
        GLES20.glEnable(GLES20.GL_BLEND); //开混合模式贴图
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);// 指定混合模式算法



        int uMatrixLocation = getUniform("u_Matrix");

        GLUtil.setUniformMat4(uMatrixLocation,finalMatrix);

        // Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureId);
        glUniform1i(uTextureUnitLocation, 0);


        int aPositionLocation = getAttribute("a_Position");
        int aTextureCoordinatesLocation = getAttribute("a_TextureCoordinates");


        GLUtil.setAttribPointer(aPositionLocation,VERTEX_DATA1);
        GLUtil.setAttribPointer(aTextureCoordinatesLocation,VERTEX_DATA2);


        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);

        GLES20.glDisable(GLES20.GL_BLEND); //开混合模式贴图

    }

    public void updateCamera(float x, float y, float z){
        Matrix.setLookAtM(viewMatrix, 0, x,y,z,
                0, 0, 0,
                0, 1, 0);
//        Matrix.scaleM(viewMatrix,0,viewMatrix,0,10f,10f,10f);

        multiply(finalMatrix, projectionMatrix,viewMatrix);

        setIdentityM(modelMatrix, 0);


        currentPersonX = -SaveHelper.qx11*1;
        currentPersonY = -SaveHelper.qz11*1;


        translateM(modelMatrix, 0, currentPersonX, 0.3f,currentPersonY);
        rotateM(modelMatrix,0,90f,1,0,0);


//        rotateM(modelMatrix, 0, currentPersonAngle , 0,0,1);

        multiply(finalMatrix, finalMatrix,modelMatrix);

    }

    public void updateCamera(){
        MainActivity.spherical.toXYZ();
        updateCamera(MainActivity.spherical.x,MainActivity.spherical.y,MainActivity.spherical.z);
    }


}
