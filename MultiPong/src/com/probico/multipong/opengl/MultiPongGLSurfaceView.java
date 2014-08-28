package com.probico.multipong.opengl;

import net.probico.pong.opengl.PongGLRenderer;
import net.probico.pong.opengl.PongGLSurfaceView;
import android.content.Context;

/**
 * A view container where OpenGL ES graphics can be drawn on screen. This view
 * can also be used to capture touch events, such as a user interacting with
 * drawn objects.
 * 
 * @author samir
 */
public class MultiPongGLSurfaceView extends PongGLSurfaceView {

	public MultiPongGLSurfaceView(Context context) {
		super(context);
	}

	@Override
	public PongGLRenderer getPongGLRenderer(Context context) {
		return new MultiPongGLRenderer(context);
	}

}
