/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
***/
package com.test.openglfirst.GLUtils.GLShape;


import android.opengl.Matrix;
import android.util.Log;

import com.google.ar.core.Pose;
import com.test.openglfirst.GLUtils.GLUtil;
import com.test.openglfirst.GLUtils.base.BaseSurfaceViewRender;
import com.test.openglfirst.GLUtils.base.SaveHelper;
import com.test.openglfirst.GLUtils.third.util.MatrixHelper;
import com.test.openglfirst.MainActivity;
import com.test.openglfirst.PlyTool;

import java.io.IOException;

import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.scaleM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;
import static com.test.openglfirst.GLUtils.third.Constants.BYTES_PER_FLOAT;


public class GLPointCloud extends BaseSurfaceViewRender {
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT
        + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private static final float[] VERTEX_DATA1 = {
            // Order of coordinates: X, Y, S, T

            // Triangle Fan
            0, 0, -50,
            1, 0, 0,
            2, 0, 0,
            2, 1, 0,
            2, 2, 0,
            1, 2, 0,
    };


    private static final float[] VERTEX_DATA2 = {
            // Order of coordinates: X, Y, S, T
            // Triangle Fan
            1, 0, 0,
            0, 1, 0,
            0, 0, 1,
            1, 1, 1,
            1, 1, 1,
            1, 1, 1,};



    @Override
    protected String loadVertexShader() {
        return "pointcloud.vert";
    }

    @Override
    protected String loadFragmentShader() {
        return "pointcloud.frag";
    }

    @Override
    protected void onSurfaceCreated() {

    }

    private final float[] finalMatrix = new float[16];


    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] modelMatrix = new float[16];

    PlyTool plyTool = new PlyTool();
    float[] vertices;
    float[] colors;
    public float pointSize=20f;


    @Override
    protected void onSurfaceChanged(int width, int height) {
// Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height);

        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width
                / (float) height, 0.1f, 1000f);

        for (int i = 0; i < projectionMatrix.length; i++) {
            Log.e("projectionMatrix","i:"+i+"  matrix:"+projectionMatrix[i]);

        }
           setIdentityM(finalMatrix, 0);

//        setIdentityM(viewMatrix, 0);
//        translateM(viewMatrix, 0, 1f, 1f, -10f);
//        multiplyMM(projectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

//        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);

        updateCamera(0,0,20);

        try {
            plyTool.read();

            vertices = plyTool.getVertices();
            colors = plyTool.getColors();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    int textureId;
    @Override
    protected void initValues() {
//        textureId = TextureHelper.loadTexture(MyApplication.getContext(), R.drawable.air_hockey_surface);
//        uTextureUnitLocation = getUniform("u_TextureUnit");
    }

//    private int uTextureUnitLocation;

    @Override
    protected void onDrawFrame() {

        updateCamera();
        int uMatrix = getUniform("uMatrix");

        GLUtil.setUniformMat4(uMatrix,finalMatrix);

//        // Set the active texture unit to texture unit 0.
//        glActiveTexture(GL_TEXTURE0);
//        glBindTexture(GL_TEXTURE_2D, textureId);
//        glUniform1i(uTextureUnitLocation, 0);
//

        int aPosition = getAttribute("aPosition");
//        int aTextureCoordinatesLocation = getAttribute("a_TextureCoordinates");


        GLUtil.setAttribPointer(aPosition,vertices);

        int uPonitSize = getUniform("uPonitSize");

        GLUtil.setUniform1f(uPonitSize,pointSize);



        int aColor = getAttribute("aColor");

        GLUtil.setAttribColor(aColor,colors);


        glDrawArrays(GL_POINTS, 0, vertices.length/3);
    }

    public void updateCamera(float x, float y, float z){
        float lookAtX = MainActivity.spherical.lookAtX;
        float lookAtY = MainActivity.spherical.lookAtY;


        Pose pose = SaveHelper.pose11;

//        Matrix.scaleM(viewMatrix,0,viewMatrix,0,10f,10f,10f);

        float[]  cameraMatrix= SaveHelper.cameraMatrix;

        Matrix.setLookAtM(viewMatrix, 0, x +lookAtX,y,z-lookAtY,
                0+lookAtX, 0, 0-lookAtY,
                0, 1, 0);

        multiply(finalMatrix, projectionMatrix,viewMatrix);

        if (cameraMatrix!=null) {
            System.arraycopy(cameraMatrix, 0, viewMatrix, 0, cameraMatrix.length);
            multiply(finalMatrix, finalMatrix, viewMatrix);

            Matrix.setLookAtM(viewMatrix, 0, pose.tx()/5,pose.ty()/5,pose.tz()/5,
                    0, 0, 0,
                    0, 1, 0);

            multiply(finalMatrix, projectionMatrix,viewMatrix);
        }
//        }else {
//
//        }



        setIdentityM(modelMatrix, 0);
        scaleM(modelMatrix, 0, 0.05f, 0.05f, 0.05f);

        translateM(modelMatrix, 0, 0f, 0f, 0f);

//        setRotateEulerM(modelMatrix,0,(float) Math.toRadians(0),(float) Math.toRadians(90),
//                (float) Math.toRadians(0));

        rotateM(modelMatrix,0,180f,0,0,1);
        multiply(finalMatrix, finalMatrix,modelMatrix);

    }

    public void updateCamera() {
//        pointSize=20;

            for (int i = 0; i < projectionMatrix.length; i++) {
            Log.e("projectionMatrix--","i:"+i+"  matrix:"+projectionMatrix[i]);

        }

        MainActivity.spherical.toXYZ();
        updateCamera(MainActivity.spherical.x,MainActivity.spherical.y,MainActivity.spherical.z);
    }
}
