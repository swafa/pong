package net.probico.multigoal;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import net.probico.multigoal.R;
import net.probico.multigoal.MainActivity.GameMode;
import android.media.MediaPlayer;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

public class Line {

	final static float PI = 3.1415926535897932384626433832795f;
	private int mProgram, mPositionHandle, mColorHandle, mMVPMatrixHandle;
	private FloatBuffer mVertexBuffer;
	private float vertices[] = new float[2 * 3];
	float color[] = { 1f, 0f, 0f, 1.0f };


	public float[] getColor() {
		return color;
	}

	public void setColor(float[] color) {
		this.color = color;
	}

	private GLRenderer gameRenderer;


	private final String vertexShaderCode = "uniform mat4 uMVPMatrix;"
			+ "attribute vec4 vPosition;" + "void main() {"
			+ "  gl_Position = uMVPMatrix * vPosition;" + "}";

	private final String fragmentShaderCode = "precision mediump float;"
			+ "uniform vec4 vColor;" + "void main() {"
			+ "  gl_FragColor = vColor;" + "}";


	public Line(GLRenderer gameRenderer, float[] vertices) {

		this.gameRenderer = gameRenderer;
		this.vertices = vertices;

		ByteBuffer vertexByteBuffer = ByteBuffer
				.allocateDirect(vertices.length * 4);
		vertexByteBuffer.order(ByteOrder.nativeOrder());
		mVertexBuffer = vertexByteBuffer.asFloatBuffer();
		mVertexBuffer.put(vertices);
		mVertexBuffer.position(0);
        // prepare shaders and OpenGL program
        int vertexShader = GLRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = GLRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

		mProgram = GLES20.glCreateProgram(); // create empty OpenGL ES Program
		GLES20.glAttachShader(mProgram, vertexShader); // add the vertex shader
														// to program
		GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment
															// shader to program
		GLES20.glLinkProgram(mProgram);

	}

	public void draw(float[] mvpMatrix) {

		GLES20.glUseProgram(mProgram);

		// get handle to vertex shader's vPosition member
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

		// Enable a handle to the triangle vertices
		GLES20.glEnableVertexAttribArray(mPositionHandle);

		// Prepare the triangle coordinate data
		GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT,
				false, 12, mVertexBuffer);

		// get handle to fragment shader's vColor member
		mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

		// Set color for drawing the triangle
		GLES20.glUniform4fv(mColorHandle, 1, color, 0);

		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

		final float[] mModelMatrix = new float[16];
		Matrix.setIdentityM(mModelMatrix, 0); // initialize to identity

		float[] resultMatrix = new float[16];
		float[] scratch = new float[16];

		Matrix.translateM(resultMatrix, 0, mModelMatrix, 0, 0,
				0, 0);

		// scratch = new float[16];
		Matrix.multiplyMM(scratch, 0, resultMatrix, 0, mvpMatrix, 0);

		// Apply the projection and view transformation
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, scratch, 0);

		GLRenderer.checkGlError("glUniformMatrix4fv");

		GLES20.glLineWidth(6.0f);
		
		GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, 2);

		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mPositionHandle);

	}


}
