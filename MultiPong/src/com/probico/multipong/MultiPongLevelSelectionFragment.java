package com.probico.multipong;

import net.probico.pong.PongLevelSelectionFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment for level selection
 * 
 * @author samir
 * 
 */
public class MultiPongLevelSelectionFragment extends PongLevelSelectionFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);

	}

	@Override
	public int getLayoutResourceId() {
		return R.layout.fragment_level_selection;
	}

	@Override
	public int getEasyButtonResourceId() {
		return R.id.easy_button;
	}

	@Override
	public int getHardButtonResourceId() {
		return R.id.hard_button;
	}

	@Override
	public int getGreetingResourceId() {
		return R.id.hello;
	}

}
