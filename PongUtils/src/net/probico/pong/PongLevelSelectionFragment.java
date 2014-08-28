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

package net.probico.pong;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public abstract class PongLevelSelectionFragment extends Fragment implements
		OnClickListener {
	String mGreeting = "";

	int easyBtnResourceId;
	int hardBtnResourceId;
	int greetingResourceId;

	public interface Listener {

		public void onEasyButtonClicked();

		public void onHardButtonClicked();

	}

	Listener mListener = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(getLayoutResourceId(), container, false);

		initResourceIds();

		final int[] CLICKABLES = new int[] { easyBtnResourceId,
				hardBtnResourceId };
		for (int i : CLICKABLES) {
			v.findViewById(i).setOnClickListener(this);
		}

		return v;
	}

	private void initResourceIds() {
		easyBtnResourceId = getEasyButtonResourceId();
		hardBtnResourceId = getHardButtonResourceId();
		greetingResourceId = getGreetingResourceId();
	}

	public abstract int getEasyButtonResourceId();

	public abstract int getHardButtonResourceId();

	public abstract int getGreetingResourceId();

	public abstract int getLayoutResourceId();

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
		TextView tv = (TextView) getActivity().findViewById(greetingResourceId);
		if (tv != null)
			tv.setText(mGreeting);
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		if (id == easyBtnResourceId) {
			mListener.onEasyButtonClicked();
		}

		else if (id == hardBtnResourceId) {
			mListener.onHardButtonClicked();
		}

	}

}
