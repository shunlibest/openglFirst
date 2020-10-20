package com.test.openglfirst.GLUtils.GLShape;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.test.openglfirst.GLUtils.GLUtil;
import com.test.openglfirst.GLUtils.base.BaseSurfaceViewRender;
import com.test.openglfirst.GLUtils.third.util.MatrixHelper;
import com.test.openglfirst.MainActivity;

import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

public class GLGrid extends BaseSurfaceViewRender {


//	public float[] color = new float[4];
//	private FloatBuffer vertexBuffer;
//	private int mProgram;
//	private int mMatrixUniform;
//	private int m_ColorUniform;
//	private int mAttributePosition;
//	private float[] modelMatrix = new float[16];

	private  float[] lines;
 	public GLGrid() {
	    lines=createPositions();
	}

	private float[] createPositions() {
		// 绘制
		float PlaneWidth = 50;
		float PlaneHeight = 50;

		float[] f = new float[600];
		int count = 25;
		for (int i = 0; i < f.length / 12; i++) {
			f[i * 12] = count; // x;
			f[i * 12 + 1] = count - i; // y;
			f[i * 12 + 2] = 0;

			f[i * 12 + 3] = -count; // x;
			f[i * 12 + 4] = count - i; // y;
			f[i * 12 + 5] = 0;

			f[i * 12 + 6] = -count + i; // x;
			f[i * 12 + 7] = count; // y;
			f[i * 12 + 8] = 0;

			f[i * 12 + 9] = -count + i; // x;
			f[i * 12 + 10] = -count;
			f[i * 12 + 11] = 0;
		}

		for (int i = 0; i < f.length; i++) {
			f[i]/=2;
		}

		return f;
	}


	@Override
	protected String loadVertexShader() {
		return "common.vert";
	}

	@Override
	protected String loadFragmentShader() {
		return "common.frag";
	}

	private final float[] finalMatrix = new float[16];


	private final float[] projectionMatrix = new float[16];
	private final float[] viewMatrix = new float[16];
	private final float[] modelMatrix = new float[16];


	@Override
	protected void onSurfaceCreated() {

	}

	@Override
	protected void onSurfaceChanged(int width, int height) {
		glViewport(0, 0, width, height);

		MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width
				/ (float) height,  0.1f, 100f);


		setIdentityM(finalMatrix, 0);

	}

	@Override
	protected void initValues() {

	}

	@Override
	protected void onDrawFrame() {
		updateCamera();

        int uMatrix = getUniform("uMatrix");
		GLUtil.setUniformMat4(uMatrix,finalMatrix);

		int aPosition = getAttribute("aPosition");
		GLUtil.setAttribPointer(aPosition,lines);


//		int u_color = getUniform("u_color");
//
//		GLUtil.setUniform4f(u_color,32.0f / 255.0f,99.0f / 255.0f,155.0f / 255.0f);
//

		GLES20.glLineWidth(2f);
		GLES20.glDrawArrays(GLES20.GL_LINES, 0, lines.length/3);
//    GLES20.glDisableVertexAttribArray(mAttributePosition);
	}


	public void updateCamera(float x, float y, float z) {

//		Log.e("hanhsunli", "x:" + x + "y:" + y + "z:" + z);

		float lookAtX = MainActivity.spherical.lookAtX;
		float lookAtY = MainActivity.spherical.lookAtY;

		Matrix.setLookAtM(viewMatrix, 0, x +lookAtX,y,z-lookAtY,
				0+lookAtX, 0, 0-lookAtY,
				0, 1, 0);


		multiply(finalMatrix, projectionMatrix, viewMatrix);

		setIdentityM(modelMatrix, 0);
		translateM(modelMatrix, 0, 0f, 0f, 0f);
		rotateM(modelMatrix,0,90f,1,0,0);

		multiply(finalMatrix, finalMatrix, modelMatrix);

	}

	public void updateCamera() {

		MainActivity.spherical.toXYZ();
		updateCamera(MainActivity.spherical.x, MainActivity.spherical.y, MainActivity.spherical.z);
	}
}
