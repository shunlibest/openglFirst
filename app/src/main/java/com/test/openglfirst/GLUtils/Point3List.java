package com.test.openglfirst.GLUtils;

import android.util.Log;

import java.util.ArrayList;

public class Point3List {
	ArrayList<Point3> point3ArrayList = new ArrayList<Point3>();

	static final int COORDS_PER_VERTEX = 3;


	public void addPoint(Point3 point3){
		point3ArrayList.add(point3);
	}

	public void addPoint(float x, float y ,float z){
		point3ArrayList.add(new Point3(x, y, z));
	}

	public float[] toArray(){
		float[] floats=new float[point3ArrayList.size()*3];
		int start=0;
		for (Point3 point3: point3ArrayList) {
			floats[start++] = point3.x;
			floats[start++] = point3.y;
			floats[start++] = point3.z;

//			Log.e("point"+start/3,"x:"+point3.x+"y:"+point3.y+"z:"+point3.z);
		}
		return floats;
	}

	public int getPointNum(){
		return point3ArrayList.size();
	}

}
