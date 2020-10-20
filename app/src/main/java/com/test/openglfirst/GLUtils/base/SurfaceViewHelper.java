package com.test.openglfirst.GLUtils.base;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.google.ar.core.Camera;
import com.google.ar.core.Frame;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.test.openglfirst.GLUtils.GLConfig;
import com.test.openglfirst.GLUtils.RenderMode;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class SurfaceViewHelper {
	ArrayList<BaseSurfaceViewRender> renders = new ArrayList<>();

	public void init(GLSurfaceView glSurfaceView){
		GLConfig.setVersion(glSurfaceView, 2);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLError.maybeThrowGLException( "Failed to free vertex shader", "glDeleteShader");

		glSurfaceView.setRenderer(new GLSurfaceView.Renderer() {
			@Override
			public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

				GLError.maybeThrowGLException( "Failed to free vertex shader", "glDeleteShader");


				Log.e("hanshunli","msg===================="+renders.size());
				for (BaseSurfaceViewRender render : renders) {


					render.onSurfaceCreated(gl10,eglConfig);
					Log.e("hanshunli","11111111111");
					GLError.maybeThrowGLException( "Failed to free vertex shader", "glDeleteShader");

				}

				GLError.maybeThrowGLException( "Failed to free vertex shader", "glDeleteShader");
			}

			@Override
			public void onSurfaceChanged(GL10 gl10, int i, int i1) {
				Log.e("render","onSurfaceChanged, width:"+i+"height:"+i1);

				GLError.maybeThrowGLException( "Failed to free vertex shader", "glDeleteShader");

				for (BaseSurfaceViewRender render : renders) {
					render.onSurfaceChanged(gl10,i,i1);
				}

				GLES20.glEnable(GLES20.GL_DEPTH_TEST);
			}

			@Override
			public void onDrawFrame(GL10 gl10) {
//				GLConfig.clearCanvas();

				GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
//				GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);
////
				GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);
				GLES20.glDepthRangef(0f,1000.0f);


				GLES20.glDepthFunc(GLES20.GL_ALWAYS);
//
//				GLES20.glEnable(GLES20.GL_POLYGON_OFFSET_FILL);
				initAR();

				for (BaseSurfaceViewRender render : renders) {
//					GLError.maybeThrowGLException( "Failed to free vertex shader", "glDeleteShader");
//					Log.e("hanshunli","222222");

					GLError.maybeThrowGLException( "Failed to free vertex shader", "glDeleteShader");


					render.onDrawFrame(gl10);

					GLError.maybeThrowGLException( "Failed to free vertex shader", "glDeleteShader");

				}
			}
		});
//		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLConfig.seMode(glSurfaceView, RenderMode.ALWAYS);
//		GLES20.glEnable(GLES20.GL_DEPTH_TEST);

	}

	public void addRender(BaseSurfaceViewRender render){
		renders.add(render);
	}
	public void removeRender(BaseSurfaceViewRender render){
		renders.remove(render);
	}


	private boolean hasSetTextureNames = false;

	Session session;
	int textureId;
	public void  setSession(Session session,int textureId ){
		this.session = session;
		this.textureId = textureId;

		Log.e("hansuhnli","textureIdmain11--"+textureId);

	}

	float[] cameraMatrix= new float[16];

	private void initAR( ){

		if (!hasSetTextureNames) {
//			int textureId = glCamera.getTextureId();
			Log.e("hansuhnli","textureIdmain--"+textureId);

			session.setCameraTextureNames(new int[]{textureId});
			hasSetTextureNames = true;
		}


		try {

			assert session !=null;

			Frame frame = session.update();

//			Camera camera = frame.getCamera();

			// Get projection matrix.
//			camera.getProjectionMatrix(projectionMatrix, 0, 0.1f, 100.0f);
//
//			// Get camera matrix and draw.
//			camera.getViewMatrix(viewMatrix, 0);
//
//
//			Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
//			Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0);

			Pose androidSensorPose = frame.getAndroidSensorPose();
			Camera camera11 = frame.getCamera();

			Pose pose11 = camera11.getPose();

			pose11.toMatrix(cameraMatrix,0);
			SaveHelper.updateCameraMatrix(cameraMatrix);

			SaveHelper.updateCameraPose(pose11);

//			pose11.toMatrix();

//			MainActivity


			Quaternion quaternion = new Quaternion(pose11.qw(), pose11.qx(), pose11.qy(), pose11.qz());

			EulerAngles eulerAngles = quaternion.toEulerAngles();


			float qx11 = pose11.tx();
			float qy11 = pose11.ty();
			float qz11 = pose11.tz();
			float qw11 = pose11.qw();
			SaveHelper.updateXYZW(qx11,qy11,qz11,eulerAngles.roll);




//			Log.e("hanshunli","qx11"+qx11);
//			Log.e("hanshunli","qy11"+qy11);
//			Log.e("hanshunli","qz11"+qz11);
//			Log.e("hanshunli","qw11"+eulerAngles.);



		} catch (CameraNotAvailableException e) {
			e.printStackTrace();
		}

	}




}
