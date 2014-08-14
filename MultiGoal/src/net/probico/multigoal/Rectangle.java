/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.probico.multigoal;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.probico.multipong.common.TextureHelper;

public class Rectangle {

	private float rectangleCoords[];

	private float xTranslateValue = 0;

	private GLRenderer gameRenderer;

	private final FloatBuffer mCubeTextureCoordinates;

	/** Size of the texture coordinate data in elements. */
	private final int mTextureCoordinateDataSize = 2;

	/** This will be used to pass in model texture coordinate information. */
	private int mTextureCoordinateHandle;

	private float xSpeed = 0.01f;

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

	private float vertices[] = new float[4 * 3];

	// private final String vertexShaderCode =
	// // This matrix member variable provides a hook to manipulate
	// // the coordinates of the objects that use this vertex shader
	// "uniform mat4 uMVPMatrix;" +
	// "attribute vec4 vPosition;" +
	// "void main() {" +
	// // The matrix must be included as a modifier of gl_Position.
	// // Note that the uMVPMatrix factor *must be first* in order
	// // for the matrix multiplication product to be correct.
	// "  gl_Position = uMVPMatrix * vPosition;" +
	// "}";

	private final String fragmentShaderCode = "precision mediump float;"
			+ "uniform vec4 vColor;" + "void main() {"
			+ "  gl_FragColor = vColor;" + "}";

	private final FloatBuffer vertexBuffer;
	// private final ShortBuffer drawListBuffer;
	private int mProgram;
	private int mPositionHandle;
	private int mColorHandle;
	private int mMVPMatrixHandle;

	// number of coordinates per vertex in this array
	static final int COORDS_PER_VERTEX = 3;
	// static float squareCoords[] = {
	// -0.5f, 0.5f, 0.0f, // top left
	// -0.5f, -0.5f, 0.0f, // bottom left
	// 0.5f, -0.5f, 0.0f, // bottom right
	// 0.5f, 0.5f, 0.0f }; // top right

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
	public Rectangle(GLRenderer gameRenderer, float vertices[], int textureId, boolean invertTexture) {

		this.gameRenderer = gameRenderer;
		this.textureId = textureId;
		// for (int i = 0; i < 360; i++) {
		// vertices[(i * 3) + 0] = (float) (radius * Math.cos((float) i));
		// vertices[(i * 3) + 1] = (float) (radius * Math.sin((float) i));
		// vertices[(i * 3) + 2] = 0;
		// }
		this.vertices = vertices;

		// initialize vertex byte buffer for shape coordinates
		ByteBuffer bb = ByteBuffer.allocateDirect(
		// (# of coordinate values * 4 bytes per float)
				vertices.length * 4);
		bb.order(ByteOrder.nativeOrder());
		vertexBuffer = bb.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);

		// initialize byte buffer for the draw list
		// ByteBuffer dlb = ByteBuffer.allocateDirect(
		// // (# of coordinate values * 2 bytes per short)
		// drawOrder.length * 2);
		// dlb.order(ByteOrder.nativeOrder());
		// drawListBuffer = dlb.asShortBuffer();
		// drawListBuffer.put(drawOrder);
		// drawListBuffer.position(0);

		final String vertexShaderCode = gameRenderer.getVertexShader();
		final String fragmentShaderC = gameRenderer.getFragmentShader();

		// prepare shaders and OpenGL program
		int vertexShader = GLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
				vertexShaderCode);
		int fragmentShader = GLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
				fragmentShaderC);

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

		// S, T (or X, Y)
		// Texture coordinate data.
		// Because images have a Y axis pointing downward (values increase as
		// you move down the image) while
		// OpenGL has a Y axis pointing upward, we adjust for that here by
		// flipping the Y axis.
		// What's more is that the texture coordinates are the same for every
		// face.

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
		GLRenderer.checkGlError("glGetUniformLocation");

		// translate paddle by xTranslateValue
		final float[] mModelMatrix = new float[16];
		Matrix.setIdentityM(mModelMatrix, 0); // initialize to identity

		// xTranslateValue += xSpeed;

		// if(xTranslateValue > GLRenderer.SCREEN_WIDTH - radius){
		// xSpeed *= -1;
		// }
		//
		// else if(xTranslateValue < -(GLRenderer.SCREEN_WIDTH - radius)){
		// xSpeed *= -1;
		// }

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
		GLRenderer.checkGlError("glUniformMatrix4fv");

		GLES20.glLineWidth(2.0f);

		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4);
		// Draw the square
		// GLES20.glDrawElements(
		// GLES20.GL_TRIANGLES, drawOrder.length,
		// GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

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