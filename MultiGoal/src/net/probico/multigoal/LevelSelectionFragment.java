/* Copyright (C) 2013 Google Inc.
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

package net.probico.multigoal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class LevelSelectionFragment extends Fragment implements OnClickListener {
	String mGreeting = "";

	public interface Listener {

		public void onEasyButtonClicked();

		public void onHardButtonClicked();

	}

	Listener mListener = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_level_selection, container, false);


		final int[] CLICKABLES = new int[] { R.id.easy_button, R.id.hard_button };
		for (int i : CLICKABLES) {
			v.findViewById(i).setOnClickListener(this);
		}

		return v;
	}

	public void setListener(Listener l) {
		mListener = l;
	}

	@Override
	public void onStart() {
		super.onStart();
		updateUi();
	}

	public void setGreeting(String greeting) {
		mGreeting = greeting;
		updateUi();
	}

	void updateUi() {
		if (getActivity() == null)
			return;
		TextView tv = (TextView) getActivity().findViewById(R.id.hello);
		if (tv != null)
			tv.setText(mGreeting);
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.easy_button) {
			mListener.onEasyButtonClicked();
		}

		else if (id == R.id.hard_button) {
			mListener.onHardButtonClicked();
		}

	}

}
