package com.test.openglfirst;

import com.threed.jpct.Mesh;

import org.smurn.jply.Element;
import org.smurn.jply.ElementReader;
import org.smurn.jply.PlyReader;
import org.smurn.jply.PlyReaderFile;

import java.io.File;
import java.io.IOException;




public class PlyImporter {

	/* data fields to store points, colors, faces information read from PLY file */
	private float[] points = null;
	private float[] colors = null;
	private int[] faces = null;
	private Mesh mesh = null;


	public Mesh loadPLY(File file) throws IOException {
		PlyReader ply = new PlyReaderFile(file);

		int vertexCount = ply.getElementCount("vertex");
		int triangleCount = ply.getElementCount("face");

		ElementReader reader;


		/* Iterate to read elements */
		while ((reader = ply.nextElementReader()) != null) {
			String elementType = reader.getElementType().getName();

			if (elementType.equals("vertex")) {
				if (points != null) {
					continue;
				}
				points = new float[3 * vertexCount];
				colors = new float[3 * vertexCount];

				Element element;
				int x = 0;
				while ((element = reader.readElement()) != null) {
					/* manipulated array indexes to store  */
					points[3 * x + 0] = (float) element.getDouble("x");
					points[3 * x + 1] = (float) element.getDouble("y");
					points[3 * x + 2] = (float) element.getDouble("z");
					colors[3 * x + 0] = (float) element.getDouble("red") / 255f;
					colors[3 * x + 1] = (float) element.getDouble("green") / 255f;
					colors[3 * x + 2] = (float) element.getDouble("blue") / 255f;
					x++;
				}
//				Log.debug(LogType.IO, "PLY importer: " + x + " vertices counted");

			} else if (elementType.equals("face")) {

				if (faces != null) {
					continue;
				}
				faces = new int[triangleCount * 3];

				Element element;
				int x = 0;
				while ((element = reader.readElement()) != null) {
					int[] vertex_index = null;

					try {
						vertex_index = element.getIntList("vertex_indices");
					}

					catch (Exception e) {}

					if(vertex_index == null) {
						try {
							vertex_index = element.getIntList("vertex_index");
						}

						catch (Exception e) {}
					}

					if(vertex_index == null) {
						throw new IOException("Failed to read vertices");
					}

					faces[3 * x + 0] = vertex_index[0];
					faces[3 * x + 1] = vertex_index[1];
					faces[3 * x + 2] = vertex_index[2];
					x++;
				}
//				Log.debug(LogType.IO, "PLY importer: " + x + " faces counted");
			}

			reader.close();
		}

		ply.close();

		/* Mesh is a class of JME 3.0 library */
//		mesh = new Mesh(10000);

//		Object3D object3D = Primitives.getCube(10);
//		Mesh mesh = object3D.getMesh();
//
//		mesh.
		/* created mesh object to include PLY object in JME 3 environment */
//		this.mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(points));
//		this.mesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(faces));
//		this.mesh.setBuffer(Type.Color, 3, BufferUtils.createFloatBuffer(colors));
//
//		this.mesh.updateBound();

		return this.mesh;
	}

	/* additional methods to return detailed description of object for further manipulation */
	public float[] getPoints(){
		return this.points;
	}

	public int[] getFaces(){
		return this.faces;
	}

	public float[] getColors(){
		return this.colors;
	}

	public Mesh getMesh(){
		return this.mesh;
	}
}