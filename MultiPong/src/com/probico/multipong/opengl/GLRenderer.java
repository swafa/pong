package com.probico.multipong.opengl;

import net.probico.pong.opengl.PongGLRenderer;
import android.content.Context;

import com.probico.multipong.R;

/**
 * Provides drawing instructions for a GLSurfaceView object.
 * 
 * @author samir
 */
public class GLRenderer extends PongGLRenderer {

	public GLRenderer(Context context) {
		super(context);
	}

	@Override
	public int getBackgroundTextureResourceId() {
		return R.drawable.backgroundpattern_space;
	}

	@Override
	public int getPaddleTextureResourceId() {
		return R.drawable.paddle_green;
	}

}