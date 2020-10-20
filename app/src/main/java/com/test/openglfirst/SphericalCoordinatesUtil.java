package com.test.openglfirst;

public class SphericalCoordinatesUtil {

	public float x,y,z;
	public float r =10,fai=45,ceta=45;


	public float lookAtX=0;
	public float lookAtY=0;


	public void setXYZ(float x,float y,float z){
		this.x=x;
		this.y=y;
		this.z=z;
	}
	public void setRFC(float r,float fai,float ceta){
		this.r=r;
		this.fai=60;
		this.ceta=ceta;
	}


	public void toXYZ(){
//		this.fai=0;


//		if (ceta>=359){
//			ceta =359;
//		}else if (ceta<=1){
//			ceta =1;
//		}

		x= (float) (r*Math.sin(Math.toRadians(fai))*Math.cos(Math.toRadians(ceta))) ;
		z = (float) (r*Math.sin(Math.toRadians(fai))*Math.sin(Math.toRadians(ceta))) ;
		y= (float) (r*Math.cos(Math.toRadians(fai))) ;
	}

	public void toZero(){
		fai=0;
		ceta=-0;
	}

//	public void toXYZ(float x,float y,float z){
//		this.x=x;
//		this.y=y;
//		this.z=z;
//	}
}
