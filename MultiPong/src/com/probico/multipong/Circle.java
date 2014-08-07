package com.probico.multipong;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.media.MediaPlayer;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.probico.multipong.MainActivity.GameMode;
import com.probico.multipong.MainActivity.Level;

public class Circle {

	final static float PI = 3.1415926535897932384626433832795f;
	private int mProgram, mPositionHandle, mColorHandle, mMVPMatrixHandle;
	private FloatBuffer mVertexBuffer;
	private float vertices[] = new float[360 * 3];
	float color[] = { 1f, 0f, 0f, 1.0f };

	float ySpeed = 0.01f;
	float xSpeed = 0.01f;

	public float getyTranslateValue() {
		return yTranslateValue;
	}

	public void setyTranslateValue(float yTranslateValue) {
		this.yTranslateValue = yTranslateValue;
	}

	public float getxTranslateValue() {
		return xTranslateValue;
	}

	public void setxTranslateValue(float xTranslateValue) {
		this.xTranslateValue = xTranslateValue;
	}

	private float yTranslateValue = 0;
	private float xTranslateValue = 0;

	private GLRenderer gameRenderer;

	private boolean ballMovingDirectionUp = false;

	public boolean isBallMovingDirectionUp() {
		return ballMovingDirectionUp;
	}

	public void setBallMovingDirectionUp(boolean ballMovingDirectionUp) {
		this.ballMovingDirectionUp = ballMovingDirectionUp;
	}

	public boolean isBallMovingDirectionRight() {
		return ballMovingDirectionRight;
	}

	public void setBallMovingDirectionRight(boolean ballMovingDirectionRight) {
		this.ballMovingDirectionRight = ballMovingDirectionRight;
	}

	private boolean ballMovingDirectionRight = false;

	private final String vertexShaderCode = "uniform mat4 uMVPMatrix;"
			+ "attribute vec4 vPosition;" + "void main() {"
			+ "  gl_Position = uMVPMatrix * vPosition;" + "}";

	private final String fragmentShaderCode = "precision mediump float;"
			+ "uniform vec4 vColor;" + "void main() {"
			+ "  gl_FragColor = vColor;" + "}";

	final static float radius = 0.05f;

	public Circle(GLRenderer gameRenderer) {
		// vertices[0] = 0;
		// vertices[1] = 0;
		// vertices[2] = 0;

		this.gameRenderer = gameRenderer;
		for (int i = 0; i < 360; i++) {
			vertices[(i * 3) + 0] = (float) (radius * Math.cos((float) i));
			vertices[(i * 3) + 1] = (float) (radius * Math.sin((float) i));
			vertices[(i * 3) + 2] = 0;
		}

		Log.v("Thread", "" + vertices[0] + "," + vertices[1] + ","
				+ vertices[2]);
		ByteBuffer vertexByteBuffer = ByteBuffer
				.allocateDirect(vertices.length * 4);
		vertexByteBuffer.order(ByteOrder.nativeOrder());
		mVertexBuffer = vertexByteBuffer.asFloatBuffer();
		mVertexBuffer.put(vertices);
		mVertexBuffer.position(0);
		// prepare shaders and OpenGL program
		int vertexShader = GLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
				vertexShaderCode);
		int fragmentShader = GLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
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

		if (yTranslateValue <= -0.88
				&& gameRenderer.getBottomPaddle().getxTranslateValue() <= xTranslateValue + 0.22
				&& gameRenderer.getBottomPaddle().getxTranslateValue() >= xTranslateValue - 0.22) {

//			MediaPlayer mp = MediaPlayer.create(
//					this.gameRenderer.getActivity(), R.raw.pong);
//			mp.start();
			
        	gameRenderer.getActivity().getSoundPool().play(gameRenderer.getActivity().getSoundIds()[0], 1, 1, 1, 0, 1.0f);
			
			ballMovingDirectionUp = true;

			if ((gameRenderer.getActivity().getGameMode()
					.equals(GameMode.TWO_PLAYERS_ONLINE) && ySpeed <= 0.014)
					|| (gameRenderer.getActivity().getGameMode()
							.equals(GameMode.TWO_PLAYERS) || gameRenderer
							.getActivity().getGameMode()
							.equals(GameMode.SINGLE_PLAYER)) && ySpeed <= 0.03) {
				ySpeed += 0.002f;
			}

			xSpeed = 0.01f;
			if (xTranslateValue > gameRenderer.getBottomPaddle()
					.getxTranslateValue() + 0.1) {
				ballMovingDirectionRight = true;
				// if (ySpeed >= 0.026)
				// ySpeed -= 0.002f;
				xSpeed = ySpeed * 2;
			} else if (xTranslateValue < gameRenderer.getBottomPaddle()
					.getxTranslateValue() - 0.1) {
				ballMovingDirectionRight = false;
				// if (ySpeed >= 0.026)
				// ySpeed -= 0.002f;
				xSpeed = ySpeed * 2;
			}

			if (gameRenderer.getActivity().getGameMode()
					.equals(MainActivity.GameMode.TWO_PLAYERS_ONLINE)
					&& !gameRenderer.getActivity()
							.isCurrentParticipantInvitee()) {
				gameRenderer.getActivity().sendBallInformation(xTranslateValue,
						yTranslateValue, ballMovingDirectionRight,
						ballMovingDirectionUp, xSpeed, ySpeed);
			}
		}

		else if (yTranslateValue >= 0.88
				&& gameRenderer.getTopPaddle().getxTranslateValue() <= xTranslateValue + 0.22
				&& gameRenderer.getTopPaddle().getxTranslateValue() >= xTranslateValue - 0.22) {
//			 
//			MediaPlayer mp =
//			 MediaPlayer.create(this.gameRenderer.getActivity(),
//			 R.raw.pong);
//			 mp.start();
			
			gameRenderer.getActivity().getSoundPool().play(gameRenderer.getActivity().getSoundIds()[0], 1, 1, 1, 0, 1.0f);
			ballMovingDirectionUp = false;

			if ((gameRenderer.getActivity().getGameMode()
					.equals(GameMode.TWO_PLAYERS_ONLINE) && ySpeed <= 0.014)
					|| (gameRenderer.getActivity().getGameMode()
							.equals(GameMode.TWO_PLAYERS) || gameRenderer
							.getActivity().getGameMode()
							.equals(GameMode.SINGLE_PLAYER)) && ySpeed <= 0.03) {
				ySpeed += 0.002f;
			}

			xSpeed = 0.01f;
			// If ball meets the edge of the paddle, reverse the horizontal
			// direction and accelerate
			if (xTranslateValue > gameRenderer.getTopPaddle()
					.getxTranslateValue() + 0.1) {
				
				ballMovingDirectionRight = true;
				// if (ySpeed >= 0.026)
				// ySpeed -= 0.002f;
				xSpeed = ySpeed * 2;
			} else if (xTranslateValue < gameRenderer.getTopPaddle()
					.getxTranslateValue() - 0.1) {
				
				
				ballMovingDirectionRight = false;
				// if (ySpeed >= 0.026)
				// ySpeed -= 0.002f;
				xSpeed = ySpeed * 2;

			}

			if (gameRenderer.getActivity().getGameMode()
					.equals(MainActivity.GameMode.TWO_PLAYERS_ONLINE)
					&& gameRenderer.getActivity().isCurrentParticipantInvitee()) {
				gameRenderer.getActivity().sendBallInformation(xTranslateValue,
						yTranslateValue, ballMovingDirectionRight,
						ballMovingDirectionUp, xSpeed, ySpeed);
			}
		}

		if (xTranslateValue > 0.95) {
//			MediaPlayer mp =
//					 MediaPlayer.create(this.gameRenderer.getActivity(),
//					 R.raw.pong);
//					 mp.start();
//					 
			
			gameRenderer.getActivity().getSoundPool().play(gameRenderer.getActivity().getSoundIds()[0], 1, 1, 1, 0, 1.0f);
			ballMovingDirectionRight = false;
		}

		else if (xTranslateValue < -0.95) {
			
//			MediaPlayer mp =
//					 MediaPlayer.create(this.gameRenderer.getActivity(),
//					 R.raw.pong);
//					 mp.start();

			gameRenderer.getActivity().getSoundPool().play(gameRenderer.getActivity().getSoundIds()[0], 1, 1, 1, 0, 1.0f);
			ballMovingDirectionRight = true;
		}

		
		if(!(gameRenderer.getActivity().gameOver() || (xSpeed == 0 && ySpeed == 0))){
			
		if (ballMovingDirectionUp)
			yTranslateValue += ySpeed;
		else
			yTranslateValue -= ySpeed;

		if (ballMovingDirectionRight) {
			xTranslateValue += xSpeed;

		} else {
			xTranslateValue -= xSpeed;
		}
		}

		if (this.gameRenderer.getActivity().getGameMode()
				.equals(GameMode.SINGLE_PLAYER)) {

			float speedRatio = ySpeed != 0 ? xSpeed / ySpeed : 0;

			float robotFactor = 0f;
			
			if ((gameRenderer.getActivity().getLevel().equals(Level.EASY) && ySpeed > 0.014) ||
					(gameRenderer.getActivity().getLevel().equals(Level.HARD) && ySpeed > 0.03)) {
				robotFactor = 0.20f;
			} 
			
			if (ballMovingDirectionRight) {
				this.gameRenderer.getTopPaddle().setxTranslateValue(
						xTranslateValue - robotFactor * speedRatio);
			} else {
				this.gameRenderer.getTopPaddle().setxTranslateValue(
						xTranslateValue + robotFactor * speedRatio);
			}
		}

		if ((yTranslateValue < -0.95 && (gameRenderer.getActivity().getGameMode().equals(GameMode.SINGLE_PLAYER) || gameRenderer.getActivity().getGameMode().equals(GameMode.TWO_PLAYERS) || (gameRenderer.getActivity().getGameMode().equals(GameMode.TWO_PLAYERS_ONLINE) && !gameRenderer.getActivity().isCurrentParticipantInvitee())) ) 
	     || (yTranslateValue >  0.95 && (gameRenderer.getActivity().getGameMode().equals(GameMode.SINGLE_PLAYER) || gameRenderer.getActivity().getGameMode().equals(GameMode.TWO_PLAYERS) || (gameRenderer.getActivity().getGameMode().equals(GameMode.TWO_PLAYERS_ONLINE) && gameRenderer.getActivity().isCurrentParticipantInvitee())) )) {
			
//			MediaPlayer mp = MediaPlayer.create(
//					this.gameRenderer.getActivity(), R.raw.blop);
//			mp.start();
//			
			
			gameRenderer.getActivity().getSoundPool().play(gameRenderer.getActivity().getSoundIds()[1], 1, 1, 1, 0, 1.0f);
			ySpeed = 0.01f;
			xSpeed = 0.01f;
			if (yTranslateValue > 0.95) {
				this.gameRenderer.getActivity().incrementPlayer1Score();

			} else if (yTranslateValue < -0.95) {
				this.gameRenderer.getActivity().incrementPlayer2Score();
			}
			// gameRenderer.showMessage();
			xTranslateValue = 0;
			yTranslateValue = 0;

			ballMovingDirectionUp = !ballMovingDirectionUp;
			if (gameRenderer.getActivity().getGameMode()
					.equals(MainActivity.GameMode.TWO_PLAYERS_ONLINE)) {
				// && ((gameRenderer.getActivity().isCurrentParticipantInvitee()
				// && yTranslateValue > 0.9) ||
				// (!gameRenderer.getActivity().isCurrentParticipantInvitee() &&
				// yTranslateValue < 0.9))){
				// Corrective action

				gameRenderer.getActivity().sendBallInformation(xTranslateValue,
						yTranslateValue, ballMovingDirectionRight,
						ballMovingDirectionUp, xSpeed, ySpeed);
				gameRenderer.getActivity().sendUpdateScoreMessage();

			}
		}

		// if(yTranslateValue > 0.9 &&
		// ((gameRenderer.getActivity().getGameMode().equals(MainActivity.GameMode.SINGLE_PLAYER)
		// ||
		// gameRenderer.getActivity().getGameMode().equals(MainActivity.GameMode.TWO_PLAYERS)
		// ||
		// (gameRenderer.getActivity().getGameMode().equals(MainActivity.GameMode.TWO_PLAYERS_ONLINE)
		// && gameRenderer.getActivity().isCurrentParticipantInvitee())))){
		//
		// this.gameRenderer.getActivity().incrementPlayer1Score();
		// xTranslateValue = 0;
		// yTranslateValue = 0;
		//
		// if(gameRenderer.getActivity().getGameMode().equals(MainActivity.GameMode.TWO_PLAYERS_ONLINE)){
		// gameRenderer.getActivity().sendBallCoordinates(xTranslateValue,
		// yTranslateValue);
		// gameRenderer.getActivity().sendUpdateScoreMessage();
		// }
		// }
		//
		// if(yTranslateValue < -0.9 &&
		// ((gameRenderer.getActivity().getGameMode().equals(MainActivity.GameMode.SINGLE_PLAYER)
		// ||
		// gameRenderer.getActivity().getGameMode().equals(MainActivity.GameMode.TWO_PLAYERS)
		// ||
		// (gameRenderer.getActivity().getGameMode().equals(MainActivity.GameMode.TWO_PLAYERS_ONLINE)
		// && !gameRenderer.getActivity().isCurrentParticipantInvitee())))){
		//
		// this.gameRenderer.getActivity().incrementPlayer2Score();
		// xTranslateValue = 0;
		// yTranslateValue = 0;
		//
		// if(gameRenderer.getActivity().getGameMode().equals(MainActivity.GameMode.TWO_PLAYERS_ONLINE)){
		// gameRenderer.getActivity().sendBallCoordinates(xTranslateValue,
		// yTranslateValue);
		// gameRenderer.getActivity().sendUpdateScoreMessage();
		// }
		// }

		// if(Math.round(yTranslateValue * 100.00) / 100.00 == 0 &&
		// gameRenderer.getActivity().getGameMode().equals(GameMode.TWO_PLAYERS_ONLINE)){
		// gameRenderer.getActivity().sendBallInformation(xTranslateValue,
		// yTranslateValue, ballMovingDirectionRight,
		// ballMovingDirectionUp, xSpeed, ySpeed);
		// }

		// translate ball by yTranslateValue
		final float[] mModelMatrix = new float[16];
		Matrix.setIdentityM(mModelMatrix, 0); // initialize to identity

		float[] resultMatrix = new float[16];
		float[] scratch = new float[16];

		Matrix.translateM(resultMatrix, 0, mModelMatrix, 0, xTranslateValue,
				yTranslateValue, 0);

		// scratch = new float[16];
		Matrix.multiplyMM(scratch, 0, resultMatrix, 0, mvpMatrix, 0);

		// Apply the projection and view transformation
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, scratch, 0);

		GLRenderer.checkGlError("glUniformMatrix4fv");

		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 360);

		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mPositionHandle);

		// if(!gameRenderer.getActivity().getGameFreezed()){
		// this.gameRenderer.getActivity().getGlSurfaceView().requestRender();
		// }

	}

	public float getySpeed() {
		return ySpeed;
	}

	public void setySpeed(float ySpeed) {
		this.ySpeed = ySpeed;
	}

	public float getxSpeed() {
		return xSpeed;
	}

	public void setxSpeed(float xSpeed) {
		this.xSpeed = xSpeed;
	}

}
