package net.probico.pong.shape;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import net.probico.pong.common.TextureHelper;
import net.probico.pong.opengl.PongGLRenderer;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

/**
 * Rectangle shape. Used for pong paddles.
 * 
 * @author samir
 * 
 */
public class Rectangle {

	private float rectangleCoords[];

	private float xTranslateValue = 0;

	private final FloatBuffer mCubeTextureCoordinates;

	/** Size of the texture coordinate data in elements. */
	private final int mTextureCoordinateDataSize = 2;

	/** This will be used to pass in model texture coordinate information. */
	private int mTextureCoordinateHandle;

	/** This is a handle to our texture data. */
	private int mTextureDataHandle;

	/** How many bytes per float. */
	private final int mBytesPerFloat = 4;

	private int textureId = -1;

	public int getTextureId() {
		return textureId;
	}

	public void setTextureId(int textureId) {
		this.textureId = textureId;
	}

	private final FloatBuffer vertexBuffer;
	private int mProgram;
	private int mPositionHandle;
	private int mColorHandle;
	private int mMVPMatrixHandle;

	// number of coordinates per vertex in this array
	static final int COORDS_PER_VERTEX = 3;

	private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per
															// vertex

	private float color[] = { 1f, 1f, 1f, 1.0f };

	/** This will be used to pass in the texture. */
	private int mTextureUniformHandle;

	public float[] getColor() {
		return color;
	}

	public void setColor(float[] color) {
		this.color = color;
	}

	final static float radius = 0.05f;

	/**
	 * Sets up the drawing object data for use in an OpenGL ES context.
	 */
	public Rectangle(PongGLRenderer gameRenderer, float vertices[],
			int textureId, boolean invertTexture) {

		this.textureId = textureId;

		// initialize vertex byte buffer for shape coordinates
		ByteBuffer bb = ByteBuffer.allocateDirect(
		// (# of coordinate values * 4 bytes per float)
				vertices.length * 4);
		bb.order(ByteOrder.nativeOrder());
		vertexBuffer = bb.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);

		final String vertexShaderCode = gameRenderer.getVertexShader();
		final String fragmentShaderC = gameRenderer.getFragmentShader();

		// prepare shaders and OpenGL program
		int vertexShader = PongGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
				vertexShaderCode);
		int fragmentShader = PongGLRenderer.loadShader(
				GLES20.GL_FRAGMENT_SHADER, fragmentShaderC);

		mProgram = GLES20.glCreateProgram(); // create empty OpenGL Program
		GLES20.glAttachShader(mProgram, vertexShader); // add the vertex shader
														// to program
		GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment
															// shader to program

		String[] attributes = { "vPosition", "a_Color", "a_Normal",
				"a_TexCoordinate" };
		for (int i = 0; i < attributes.length; i++) {
			GLES20.glBindAttribLocation(mProgram, i, attributes[i]);
		}

		GLES20.glLinkProgram(mProgram); // create OpenGL program executables

		// Get the link status.
		final int[] linkStatus = new int[1];
		GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, linkStatus, 0);

		// If the link failed, delete the program.
		if (linkStatus[0] == 0) {
			Log.e("Shader",
					"Error compiling program: "
							+ GLES20.glGetProgramInfoLog(mProgram));
			GLES20.glDeleteProgram(mProgram);
			mProgram = 0;
		}

		if (mProgram == 0) {
			throw new RuntimeException("Error creating program.");
		}

		// Load the texture
		mTextureDataHandle = TextureHelper.loadTexture(
				gameRenderer.getActivity(), textureId);

		float[] cubeTextureCoordinateData;

		if (invertTexture) {
			cubeTextureCoordinateData = new float[] { 1, 1, 1, 0, 0, 0, 0, 1 };
		} else {
			cubeTextureCoordinateData = new float[] { 0, 0, 0, 1, 1, 1, 1, 0 };
		}

		mCubeTextureCoordinates = ByteBuffer
				.allocateDirect(
						cubeTextureCoordinateData.length * mBytesPerFloat)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		mCubeTextureCoordinates.put(cubeTextureCoordinateData).position(0);
	}

	/**
	 * Encapsulates the OpenGL ES instructions for drawing this shape.
	 * 
	 * @param mvpMatrix
	 *            - The Model View Project matrix in which to draw this shape.
	 */
	public void draw(float[] mvpMatrix) {
		// Add program to OpenGL environment
		GLES20.glUseProgram(mProgram);

		// get handle to vertex shader's vPosition member
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
		mTextureUniformHandle = GLES20.glGetUniformLocation(mProgram,
				"u_Texture");
		mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram,
				"a_TexCoordinate");

		// Set the active texture unit to texture unit 0.
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

		// Bind the texture to this unit.
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);

		// Tell the texture uniform sampler to use this texture in the shader by
		// binding to texture unit 0.
		GLES20.glUniform1i(mTextureUniformHandle, 0);

		// Prepare the triangle coordinate data
		GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
				GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

		// Enable a handle to the triangle vertices
		GLES20.glEnableVertexAttribArray(mPositionHandle);

		// Pass in the texture coordinate information
		mCubeTextureCoordinates.position(0);
		GLES20.glVertexAttribPointer(mTextureCoordinateHandle,
				mTextureCoordinateDataSize, GLES20.GL_FLOAT, false, 0,
				mCubeTextureCoordinates);

		GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);

		// get handle to fragment shader's vColor member
		mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

		// Set color for drawing the triangle
		GLES20.glUniform4fv(mColorHandle, 1, color, 0);

		// get handle to shape's transformation matrix
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
		PongGLRenderer.checkGlError("glGetUniformLocation");

		// translate paddle by xTranslateValue
		final float[] mModelMatrix = new float[16];
		Matrix.setIdentityM(mModelMatrix, 0); // initialize to identity

		if (xTranslateValue > 0.78)
			xTranslateValue = 0.78f;

		else if (xTranslateValue < -0.78) {
			xTranslateValue = -0.78f;
		}

		float[] resultMatrix = new float[16];
		float[] scratch = new float[16];
		Matrix.translateM(resultMatrix, 0, mModelMatrix, 0, xTranslateValue, 0,
				0);

		// Combine the rotation matrix with the projection and camera view
		// Note that the mMVPMatrix factor *must be first* in order
		// for the matrix multiplication product to be correct.
		Matrix.multiplyMM(scratch, 0, resultMatrix, 0, mvpMatrix, 0);

		// Apply the projection and view transformation
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, scratch, 0);
		PongGLRenderer.checkGlError("glUniformMatrix4fv");

		GLES20.glLineWidth(2.0f);

		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4);

		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mPositionHandle);

	}

	public float[] getRectangleCoords() {
		return rectangleCoords;
	}

	public void setRectangleCoords(float rectangleCoords[]) {
		this.rectangleCoords = rectangleCoords;
	}

	public float getxTranslateValue() {
		return xTranslateValue;
	}

	public void setxTranslateValue(float xTranslateValue) {
		this.xTranslateValue = xTranslateValue;
	}

}