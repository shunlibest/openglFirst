/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
***/
package com.test.openglfirst.GLUtils.GLShape;


import com.test.openglfirst.GLUtils.base.BaseSurfaceViewRender;
import com.test.openglfirst.GLUtils.third.data.VertexArray;
import com.test.openglfirst.GLUtils.third.programs.TextureShaderProgram;
import com.test.openglfirst.GLUtils.third.util.MatrixHelper;
import com.test.openglfirst.GLUtils.third.util.TextureHelper;
import com.test.openglfirst.MyApplication;
import com.test.openglfirst.R;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;
import static com.test.openglfirst.GLUtils.third.Constants.BYTES_PER_FLOAT;


public class GLTable2 extends BaseSurfaceViewRender {
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT
        + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private static final float[] VERTEX_DATA = {
        // Order of coordinates: X, Y, S, T

        // Triangle Fan
           1f,    1f, 0.5f, 0.5f,
        -0.5f, -0.8f,   0f, 0.9f,
         0.5f, -0.8f,   1f, 0.9f,
         0.5f,  0.8f,   1f, 0.1f,
        -0.5f,  0.8f,   0f, 0.1f,
        -0f, -0f,   0f, 0.9f };

    private final VertexArray vertexArray;


    public GLTable2() {
        vertexArray = new VertexArray(VERTEX_DATA);
    }
    
    public void bindData(TextureShaderProgram textureProgram) {
        vertexArray.setVertexAttribPointer(
            0, 
            textureProgram.getPositionAttributeLocation(), 
            POSITION_COMPONENT_COUNT,
            STRIDE);
        
        vertexArray.setVertexAttribPointer(
            POSITION_COMPONENT_COUNT, 
            textureProgram.getTextureCoordinatesAttributeLocation(),
            TEXTURE_COORDINATES_COMPONENT_COUNT, 
            STRIDE);
    }

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

    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];

    @Override
    protected void onSurfaceChanged(int width, int height) {
// Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height);

        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width
                / (float) height, 1f, 10f);

        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, 0f, 0f, -2.5f);
        rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);

        final float[] temp = new float[16];
        multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
    }

    int textureId;
    @Override
    protected void initValues() {

        textureId = TextureHelper.loadTexture(MyApplication.getContext(), R.drawable.fengj);

        uTextureUnitLocation = getUniform("u_TextureUnit");
    }

    private int uTextureUnitLocation;

    @Override
    protected void onDrawFrame() {

        int uMatrixLocation = getUniform("u_Matrix");

        // Pass the matrix into the shader program.
        glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);

        // Set the active texture unit to texture unit 0.
        glActiveTexture(GL_TEXTURE0);

        // Bind the texture to this unit.
        glBindTexture(GL_TEXTURE_2D, textureId);

        // Tell the texture uniform sampler to use this texture in the shader by
        // telling it to read from texture unit 0.
        glUniform1i(uTextureUnitLocation, 0);


        int aPositionLocation = getAttribute("a_Position");
        int aTextureCoordinatesLocation = getAttribute("a_TextureCoordinates");


        vertexArray.setVertexAttribPointer(
                0,
                aPositionLocation,
                POSITION_COMPONENT_COUNT,
                STRIDE);

        vertexArray.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT,
                aTextureCoordinatesLocation,
                TEXTURE_COORDINATES_COMPONENT_COUNT,
                STRIDE);


        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
    }
}
