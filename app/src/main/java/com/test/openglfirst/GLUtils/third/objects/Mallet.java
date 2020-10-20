/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
***/
package com.test.openglfirst.GLUtils.third.objects;


import android.opengl.GLES20;

import com.test.openglfirst.GLUtils.third.data.VertexArray;
import com.test.openglfirst.GLUtils.third.programs.ColorShaderProgram;

import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glDrawArrays;
import static com.test.openglfirst.GLUtils.third.Constants.BYTES_PER_FLOAT;

public class Mallet {
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int STRIDE = 
        (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) 
        * BYTES_PER_FLOAT;
    private static final float[] VERTEX_DATA = {
        // Order of coordinates: X, Y, R, G, B
        0f, -0.4f, 0f, 0f, 1f, 
        0f,  0.4f, 1f, 0f, 0f };
    private final VertexArray vertexArray;

    public Mallet() {
        vertexArray = new VertexArray(triangleCoords);
    }
    
    public void bindData(ColorShaderProgram colorProgram) {
        vertexArray.setVertexAttribPointer(
            0,
            colorProgram.getPositionAttributeLocation(),
            POSITION_COMPONENT_COUNT, 
            STRIDE);
        vertexArray.setVertexAttribPointer(
            POSITION_COMPONENT_COUNT,
            colorProgram.getColorAttributeLocation(), 
            COLOR_COMPONENT_COUNT,
            STRIDE);
    }

    public void draw() {        
        glDrawArrays(GLES20.GL_TRIANGLES, 0,triangleCoords.length/3);
    }

    float[] triangleCoords = {
            1,  1, 0, // top
            -1f, -0.5f, 0.0f, // bottom left
            0.5f, -0.5f, 0.0f  // bottom right
    };
}
