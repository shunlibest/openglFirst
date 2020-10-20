/*
 * Copyright 2020 Google Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.test.openglfirst.GLUtils;

import android.content.res.AssetManager;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * A SampleRender context.
 */
public class BaseRender {
	private final AssetManager assetManager;

	/**
	 * Constructs a SampleRender object and instantiates GLSurfaceView parameters.
	 *
	 * @param glSurfaceView Android GLSurfaceView
	 * @param renderer      Renderer implementation to receive callbacks
	 * @param assetManager  AssetManager for loading Android resources
	 */
	public BaseRender(GLSurfaceView glSurfaceView, final Renderer renderer, AssetManager assetManager) {
		this.assetManager = assetManager;
		glSurfaceView.setPreserveEGLContextOnPause(true);
		glSurfaceView.setEGLContextClientVersion(2);
		glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		glSurfaceView.setRenderer(
				new GLSurfaceView.Renderer() {
					@Override
					public void onSurfaceCreated(GL10 gl, EGLConfig config) {
						GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
						GLES30.glEnable(GLES30.GL_BLEND);
						renderer.onSurfaceCreated(BaseRender.this);
					}

					@Override
					public void onSurfaceChanged(GL10 gl, int w, int h) {
						GLES30.glViewport(0, 0, w, h);
						renderer.onSurfaceChanged(BaseRender.this, w, h);
					}

					@Override
					public void onDrawFrame(GL10 gl) {
						GLES30.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
						renderer.onDrawFrame(BaseRender.this);
					}
				});
		glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		glSurfaceView.setWillNotDraw(false);
	}


	public void draw() {

	}

	/**
	 * Interface to be implemented for rendering callbacks.
	 */
	public static interface Renderer {

		public void onSurfaceCreated(BaseRender render);

		public void onSurfaceChanged(BaseRender render, int width, int height);

		public void onDrawFrame(BaseRender render);
	}

	/* package-private */
	AssetManager getAssets() {
		return assetManager;
	}
}
