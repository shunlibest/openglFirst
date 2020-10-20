package com.test.openglfirst.GLUtils;



import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;

import com.test.openglfirst.MyApplication;

/**
 * 加载顶点Shader与片元Shader的工具类
 */
public class ShaderUtil {

	/**
	 * 加载顶点着色器TXT字符串
	 * @param sourceFilePath shader的脚本字符串路径
	 * @return 对该shader的引用
	 */
	public static int loadVertexShader(String sourceFilePath){
		assert sourceFilePath.endsWith(".vert");
		return loadShader(GLES20.GL_VERTEX_SHADER,loadFromAssetsFile(sourceFilePath));
	}

	/**
	 * 加载片源着色器TXT字符串
	 * @param sourceFilePath shader的脚本字符串路径
	 * @return 对shader的引用
	 */
	public static int loadFragmentShader(String sourceFilePath){
		assert sourceFilePath.endsWith(".frag");
		return loadShader(GLES20.GL_FRAGMENT_SHADER,loadFromAssetsFile(sourceFilePath));
	}


	private static int loadShader(int shaderType, String source) {
		// 创建一个新shader
		int shader = GLES20.glCreateShader(shaderType);
		// 若创建成功则加载shader
		if (shader != 0) {
			// 加载shader的源代码
			GLES20.glShaderSource(shader, source);
			// 编译shader
			GLES20.glCompileShader(shader);
			// 存放编译成功shader数量的数组
			int[] compiled = new int[1];
			// 获取Shader的编译情况
			GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
			if (compiled[0] == 0) {// 若编译失败则显示错误日志并删除此shader
				Log.e("ES20_ERROR", "Could not compile shader " + shaderType + ":");
				Log.e("ES20_ERROR", GLES20.glGetShaderInfoLog(shader));
				GLES20.glDeleteShader(shader);
				shader = 0;
			}
		}
		return shader;
	}

	// 创建shader程序的方法
	public static int createProgram(String vertexSource, String fragmentSource) {
		// 加载顶点着色器
		int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
		if (vertexShader == 0) {
			return 0;
		}

		// 加载片元着色器
		int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
		if (pixelShader == 0) {
			return 0;
		}

		// 创建程序
		int program = GLES20.glCreateProgram();
		// 若程序创建成功则向程序中加入顶点着色器与片元着色器
		if (program != 0) {
			// 向程序中加入顶点着色器
			GLES20.glAttachShader(program, vertexShader);
			checkGlError("glAttachShader");
			// 向程序中加入片元着色器
			GLES20.glAttachShader(program, pixelShader);
			checkGlError("glAttachShader");
			// 链接程序
			GLES20.glLinkProgram(program);
			// 存放链接成功program数量的数组
			int[] linkStatus = new int[1];
			// 获取program的链接情况
			GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
			// 若链接失败则报错并删除程序
			if (linkStatus[0] != GLES20.GL_TRUE) {
				Log.e("ES20_ERROR", "Could not link program: ");
				Log.e("ES20_ERROR", GLES20.glGetProgramInfoLog(program));
				GLES20.glDeleteProgram(program);
				program = 0;
			}
		}
		return program;
	}

	// 检查每一步操作是否有错误的方法
	public static void checkGlError(String op) {
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			Log.e("ES20_ERROR", op + ": glError " + error);
			throw new RuntimeException(op + ": glError " + error);
		}
	}

	// 从sh脚本中加载shader内容的方法
	public static String loadFromAssetsFile(String fname) {
		String result = null;
		try {
			InputStream in = MyApplication.getContext().getAssets().open(fname);
			int ch = 0;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while ((ch = in.read()) != -1) {
				baos.write(ch);
			}
			byte[] buff = baos.toByteArray();
			baos.close();
			in.close();
			result = new String(buff, "UTF-8");
			result = result.replaceAll("\\r\\n", "\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}