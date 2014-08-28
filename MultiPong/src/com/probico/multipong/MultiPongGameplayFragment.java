/*
 * Copyright (C) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.probico.multipong;

import net.probico.pong.PongGameplayFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment for the gameplay portion of the game. It shows the keypad where the
 * user can request their score.
 * 
 * @author Bruno Oliveira (Google)
 * 
 */
public class MultiPongGameplayFragment extends PongGameplayFragment {

	private static final String MAIN_ACTIVITY_KEY = "main_activity_key";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	protected int getLayoutResourceId() {
		return R.layout.fragment_gameplay;
	}
	
	public static MultiPongGameplayFragment newInstance(MainActivity mainActivity) {
		MultiPongGameplayFragment fragment = new MultiPongGameplayFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(MAIN_ACTIVITY_KEY, mainActivity);
		fragment.setArguments(bundle);

		return fragment;

	}

	@Override
	public int getGameLayoutResourceId() {
		return R.id.screen_gameplay;
	}

}
