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
package com.test.openglfirst.GLUtils.base;

import android.opengl.GLES20;
import android.opengl.GLException;
import android.opengl.GLU;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* package-private */
public class GLError {
	public static void maybeThrowGLException(String reason, String api) {
		List<Integer> errorCodes = getGlErrors();
		if (errorCodes != null) {
			throw new GLException(errorCodes.get(0), formatErrorMessage(reason, api, errorCodes));
		}
	}

	public static void maybeLogGLError(int priority, String tag, String reason, String api) {
		List<Integer> errorCodes = getGlErrors();
		if (errorCodes != null) {
			Log.println(priority, tag, formatErrorMessage(reason, api, errorCodes));
		}
	}

	private static String formatErrorMessage(String reason, String api, List<Integer> errorCodes) {
		StringBuilder builder = new StringBuilder(String.format("%s: %s: ", reason, api));
		Iterator<Integer> iterator = errorCodes.iterator();
		while (iterator.hasNext()) {
			int errorCode = iterator.next();
			builder.append(String.format("%s (%d)", GLU.gluErrorString(errorCode), errorCode));
			if (iterator.hasNext()) {
				builder.append(", ");
			}
		}
		return builder.toString();
	}

	private static List<Integer> getGlErrors() {
		int errorCode = GLES20.glGetError();
		// Shortcut for no errors
		if (errorCode == GLES20.GL_NO_ERROR) {
			return null;
		}
		List<Integer> errorCodes = new ArrayList<>();
		errorCodes.add(errorCode);
		while (true) {
			errorCode = GLES20.glGetError();
			if (errorCode == GLES20.GL_NO_ERROR) {
				break;
			}
			errorCodes.add(errorCode);
		}
		return errorCodes;
	}

	private GLError() {
	}
}
