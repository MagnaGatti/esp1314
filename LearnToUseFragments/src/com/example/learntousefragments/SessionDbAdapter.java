package com.example.learntousefragments;

import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SessionDbAdapter extends CursorAdapter {

	public SessionDbAdapter(Context context, Cursor c) {
		super(context, c);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Associa dati del singolo oggetto da visualizzare ai campi della riga.
	 */
	@Override
	public void bindView(View arg0, Context arg1, Cursor arg2) {
		// here we are setting our data
        // that means, take the data from the cursor and put it in views
		int ID = arg2.getInt(arg2.getColumnIndex(SessionDatabase.COLUMN_ID));
		long firstDate = arg2.getLong(arg2.getColumnIndex(SessionDatabase.COLUMN_FIRSTDATA));
 
        TextView textViewSessionName = (TextView) arg0.findViewById(R.id.tvNameItem);
        textViewSessionName.setTextColor(Color.BLACK);
        textViewSessionName.setText(arg2.getString(arg2.getColumnIndex(SessionDatabase.COLUMN_NAME)));
 
        TextView textViewLastDate = (TextView) arg0.findViewById(R.id.tvlastDateItem);
        Long longLastDate = arg2.getLong(arg2.getColumnIndex(SessionDatabase.COLUMN_LASTDATA));
        Date lastDate = new Date(longLastDate);
        textViewLastDate.setTextColor(Color.DKGRAY);
        textViewLastDate.setText(lastDate.toString());
        
        ItemImageView thumbnail = (ItemImageView) arg0.findViewById(R.id.thumbnailItem);
        Log.d("LONG "+arg2.getString(arg2.getColumnIndex(SessionDatabase.COLUMN_NAME)), Long.toString(longLastDate));
        thumbnail.setImageData(ID, longLastDate, firstDate);
	}

	
	/**
	 * 
	 * Crea una nuova riga nella listview.
	 */
	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		// when the view will be created for first time,
        // we need to tell the adapters, how each item will look
        LayoutInflater inflater = LayoutInflater.from(arg2.getContext());
        View rowView = inflater.inflate(R.layout.item_complete, arg2, false);
 
        return rowView;
	}

}