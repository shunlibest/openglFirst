package com.test.openglfirst.GLUtils.cameraUtils;

import android.util.Rational;
import android.util.Size;

import androidx.camera.core.CameraX;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.lifecycle.LifecycleOwner;

public class CameraXHelper {


	public static void init(LifecycleOwner lifecycleOwner, Preview.OnPreviewOutputUpdateListener listener){
		CameraX.bindToLifecycle(lifecycleOwner, getPreview(listener));
	}


	private static Preview getPreview(Preview.OnPreviewOutputUpdateListener listener){
		PreviewConfig previewConfig = new PreviewConfig.Builder()
				.setTargetAspectRatio(new Rational(1, 1))
				.setTargetResolution(new Size(640, 640))
				.build();

		Preview preview = new Preview(previewConfig);
		preview.setOnPreviewOutputUpdateListener(listener);

//		preview.op
		return preview;

	}
}
