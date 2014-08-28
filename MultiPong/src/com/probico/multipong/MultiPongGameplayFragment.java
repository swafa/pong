package com.probico.multipong;

import net.probico.pong.PongGameplayFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment for the gameplay portion of the game.
 * 
 * @author samir
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

	public static MultiPongGameplayFragment newInstance(
			MainActivity mainActivity) {
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
