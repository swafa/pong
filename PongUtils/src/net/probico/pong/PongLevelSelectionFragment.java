package net.probico.pong;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 
 * @author samir
 *
 */
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
