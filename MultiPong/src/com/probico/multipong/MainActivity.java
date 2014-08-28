/*
 * Copyright (C) 2013 Google Inc.
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
 * IMPORTANT: Before attempting to run this sample, please change the package
 * name to your own package name (not com.android.*) and replace the IDs on
 * res/values/ids.xml by your own IDs (you must create a game in the developer
 * console to get those IDs).
 * 
 * This is a very simple game where the user selects "easy mode" or "hard mode"
 * and then the "gameplay" consists of inputting the desired score (0 to 9999).
 * In easy mode, you get the score you request; in hard mode, you get half.
 * 
 * @author Bruno Oliveira
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
