package com.example.learntousefragments;

import com.example.learntousefragments.ListSessionFragment.OnListSessionListener;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class DescriptionFragment extends Fragment implements
		OnCheckedChangeListener {
	// --- musicSession details ---
	private ItemImageView thumbnail;
	private TextView sessionID;
	private TextView sessionName;
	private TextView sessionFirstDate;
	private TextView sessionLastDate;
	// --- musicSession parameters ---
	private CheckBox xAsix;
	private CheckBox yAsix;
	private CheckBox zAsix;
	private Bundle parameters = null;

	private Bundle fragmentInstanceState = null;
	private OnDescriptionSessionListener descriptionListener;

	// interfaccia pubblica per comunicare con l'activity
		public interface OnDescriptionSessionListener {
			public void setSessionParameters(Bundle parameters);
		}
		
		// Controlla che l'activity genitore implementi l'interfaccia pubblica,
		// altrimenti lancia un'eccezione.
		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			try {
				descriptionListener = (OnDescriptionSessionListener) activity;
			} catch (ClassCastException e) {
				throw new ClassCastException(activity.toString()
						+ " must implement OnDescriptionSessionListener");
			}
		}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_description, container,
				false);

		sessionID = (TextView) view.findViewById(R.id.tvDetailID);
		sessionName = (TextView) view.findViewById(R.id.tvDetailName);
		sessionFirstDate = (TextView) view.findViewById(R.id.tvDetailFirstDate);
		sessionLastDate = (TextView) view.findViewById(R.id.tvDetailLastDate);
		thumbnail = (ItemImageView) view.findViewById(R.id.itemDetailedIcon);
		
		xAsix = (CheckBox) view.findViewById(R.id.checkAxisX);
		yAsix = (CheckBox) view.findViewById(R.id.checkAxisY);
		zAsix = (CheckBox) view.findViewById(R.id.checkAsixZ);
		xAsix.setOnCheckedChangeListener(this);
		yAsix.setOnCheckedChangeListener(this);
		zAsix.setOnCheckedChangeListener(this);

		return view;
	}

	/*
	 * Just an helper method to write the value received from the intent/action
	 * into the TextView in the layout
	 */
	public void setSessionDetailIntoFragment(Bundle sessionDetail) {
		int id = sessionDetail.getInt(SessionDatabase.COLUMN_ID);
		long dateFirst = sessionDetail
				.getLong(SessionDatabase.COLUMN_FIRSTDATA);
		long dateLast = sessionDetail.getLong(SessionDatabase.COLUMN_LASTDATA);

		sessionID.setText(Long.toString(id));
		sessionName.setText(sessionDetail
				.getString(SessionDatabase.COLUMN_NAME));
		sessionFirstDate.setText(Long.toString(dateFirst));
		sessionLastDate.setText(Long.toString(dateLast));
		thumbnail.setImageData(id, dateLast, dateFirst);
		thumbnail.invalidate();

		fragmentInstanceState = sessionDetail;
		//ho aggiornato descrizione sessione, e creo un bundle per "ricordare" 
		//i cambiamenti ai parametri.
		parameters = new Bundle();
		parameters.putInt(SessionDatabase.COLUMN_ID, id);
	}

	// arg1 il nuovo stato true o false
	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		if (arg0 == xAsix) {
			parameters.putBoolean(Parameters.X_ASIX, arg1);		
			Log.d("X ASSE", Boolean.toString(arg1));
			//TODO classe parameters??
		}
		if (arg0 == yAsix) {
			parameters.putBoolean(Parameters.Y_ASIX, arg1);		
		}
		if (arg0 == zAsix) {
			parameters.putBoolean(Parameters.Z_ASIX, arg1);		
		}
		descriptionListener.setSessionParameters(parameters);
	}

}
