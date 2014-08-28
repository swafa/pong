package com.probico.multipong;

import net.probico.pong.PongGameplayFragment;
import net.probico.pong.PongLevelSelectionFragment;
import net.probico.pong.PongMainActivity;
import net.probico.pong.PongMainMenuFragment;
import android.os.Bundle;

import com.probico.multipong.opengl.MultiPongGLSurfaceView;

/**
 * Our main activity for the game.
 * 
 * 
 * @author samir
 */
public class MainActivity extends PongMainActivity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5961189333992327470L;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
	}

	@Override
	protected PongGameplayFragment getPongGamePlayFragment() {
		return MultiPongGameplayFragment.newInstance(this);

	}

	@Override
	public MultiPongGLSurfaceView getPongGlSurfaceView() {
		return new MultiPongGLSurfaceView(this);
	}

	@Override
	public PongMainMenuFragment getPongMainMenuFragment() {
		return new MultiPongMainMenuFragment();
	}

	@Override
	public PongLevelSelectionFragment getPongLevelSelectionFragment() {
		return new MultiPongLevelSelectionFragment();
	}

}
