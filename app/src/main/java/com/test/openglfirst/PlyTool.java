package com.test.openglfirst;

import android.util.Log;

import org.smurn.jply.Element;
import org.smurn.jply.ElementReader;
import org.smurn.jply.ElementType;
import org.smurn.jply.PlyReader;
import org.smurn.jply.PlyReaderFile;

import java.io.IOException;

public class PlyTool {
	private PlyReader ply;
	private float[] vertices;
	private float[] colors;

	public void read() throws IOException {

		ply = new PlyReaderFile("/sdcard/1/outputfinal.ply");

		// The elements are stored in order of their types. For each
		// type we get a reader that reads the elements of this type.

		Log.e("hanshunli",""+ply.getElementTypes());
		ElementReader reader = ply.nextElementReader();
		while (reader != null) {

			ElementType type = reader.getElementType();

			// In PLY files vertices always have a type named "vertex".
			if (type.getName().equals("vertex")) {
				Log.e("----------","--------------------");
//				vertices = getVertices(reader);
//				colors = getColors(reader);
				getVertices(reader);

			}
			if (type.getName().equals("colors")) {
//				colors = getColors(reader);
			}

			// Close the reader for the current type before getting the next one.
			reader.close();

			reader = ply.nextElementReader();
		}
		ply.close();
	}



	private float[] getVertices(ElementReader reader) throws IOException {

		// The number of elements is known in advance. This is great for
		// allocating buffers and a like.
		// You can even get the element counts for each type before getting
		// the corresponding reader via the PlyReader.getElementCount(..)
		// method.
		System.out.println("There are " + reader.getCount() + " vertices:");

		vertices =new float[reader.getCount()*3];
		colors = new float[reader.getCount()*3];

		Element element = reader.readElement();
		int i=0,j=0;
		while (element != null) {

			// Use the the 'get' methods to access the properties.
			// jPly automatically converts the various data types supported
			// by PLY for you.
//			Log.e("x=",""+element.getDouble("x"));
//			Log.e("y=",""+element.getDouble("y"));
//			Log.e("z=",""+element.getDouble("z"));
			vertices[i++]=(float)element.getDouble("x");
			vertices[i++]=(float)element.getDouble("y");
			vertices[i++]=(float)element.getDouble("z");

			colors[j++]=(float) element.getInt("red")/255;
			colors[j++]=(float) element.getInt("green")/255;
			colors[j++]=(float) element.getInt("blue")/255;

//			Log.e("x=",""+element.getDouble("x"));
//			Log.e("y=",""+element.getDouble("y"));
//			Log.e("z=",""+element.getDouble("z"));
//			Log.e("red=",""+element.getInt("red"));
			element = reader.readElement();
		}

		return null;
	}


	public float[] getVertices() {
		return vertices;
	}

	public float[] getColors() {
		return colors;
	}
}
