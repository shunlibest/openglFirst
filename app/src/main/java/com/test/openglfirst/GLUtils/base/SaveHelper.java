package com.test.openglfirst.GLUtils.base;

import com.google.ar.core.Pose;

public class SaveHelper {

	public static float qx11 =0 ;
	public static float qy11 =0;
	public static float qz11 =0;
	public static float qw11 =0;

	public static float[] cameraMatrix;

	public static Pose pose11 =new Pose(new float[]{0,0,0},new float[]{0,0,0,0});



	public static void updateXYZW(float qx,float qy,float qz,float qw){
		qx11=qx;
		qy11=qy;
		qz11=qz;
		qw11=qw;
	}

	public static void updateCameraMatrix(float[] cameraMatrix) {
		for (int i = 0; i < cameraMatrix.length; i++) {
			cameraMatrix[i]/=1;
		}
		SaveHelper.cameraMatrix=cameraMatrix;
	}

	public static void updateCameraPose(Pose pose11) {
		SaveHelper.pose11 =pose11;
	}
}
