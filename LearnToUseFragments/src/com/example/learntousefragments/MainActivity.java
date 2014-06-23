package com.example.learntousefragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import com.example.learntousefragments.DescriptionFragment.OnDescriptionSessionListener;
import com.example.learntousefragments.ListSessionFragment.OnListSessionListener;

public class MainActivity extends ActionBarActivity implements OnListSessionListener, OnDescriptionSessionListener{
	
	private SessionDatabase myDb;
	private SessionDbAdapter adapter;
	private Cursor cursorToAll;
	
	private ListSessionFragment listFragment;
	private DescriptionFragment descriptionFragment = null;
	private Bundle sessionSelect;
	public static final String SESSION_INFO = "sessionInfo"; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		listFragment = (ListSessionFragment) (getSupportFragmentManager().findFragmentById(R.id.titlesFragment));
		descriptionFragment = (DescriptionFragment) getSupportFragmentManager().findFragmentById(R.id.descriptionFragment);
		
		myDb = new SessionDatabase(getApplicationContext());
		myDb.open();
		cursorToAll = myDb.fetchAll(SessionDatabase.COLUMN_NAME);
		if(cursorToAll.getCount() == 0){
			myDb.addSession("PROVA", new Float[]{(float)1,(float)2} );
			myDb.addSession("PROVA 1", new Float[]{(float)1,(float)2} );
			myDb.addSession("PROVA 2", new Float[]{(float)1,(float)2} );
			myDb.addSession("PROVA 3", new Float[]{(float)1,(float)2} );
		}
		cursorToAll = myDb.fetchAll(SessionDatabase.COLUMN_NAME);
		adapter = new SessionDbAdapter(getApplicationContext(), cursorToAll);
		listFragment.setListAdapter(adapter);		
		myDb.close();
		Log.d("Database", myDb.toString());
		if(descriptionFragment != null && descriptionFragment.isInLayout())
		{			
			if(savedInstanceState != null && savedInstanceState.getBundle(SESSION_INFO)!=null){
				Bundle SessionBefore = savedInstanceState.getBundle(SESSION_INFO);
				descriptionFragment.setSessionDetailIntoFragment(SessionBefore);
			}
		}	

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;			
		}
		if (id == R.id.action_deleteAll){
			Log.d("ACTION DELETE", "BYE BYE");
			myDb.open();
			myDb.deleteAll();
			adapter.changeCursor(myDb.fetchAll(SessionDatabase.COLUMN_NAME));
			myDb.close();
			Toast.makeText(this, "All session delete", Toast.LENGTH_SHORT)
			.show(); 
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemSelect(int index) {
		Cursor cursorToItem = (Cursor) adapter.getItem(index);
		// TODO gestire eccezione
		// copio i dati della sessione
		int sessionId = cursorToItem.getInt(cursorToItem.getColumnIndex(SessionDatabase.COLUMN_ID));
		String sessionName = cursorToItem.getString(cursorToItem.getColumnIndex(SessionDatabase.COLUMN_NAME));
		long sessionFirstDate = cursorToItem.getLong(cursorToItem.getColumnIndex(SessionDatabase.COLUMN_FIRSTDATA));
		long sessionLastDate = cursorToItem.getLong(cursorToItem.getColumnIndex(SessionDatabase.COLUMN_LASTDATA));
		byte[] sessionAccelerData = cursorToItem.getBlob(cursorToItem.getColumnIndex(SessionDatabase.COLUMN_ACCELERDATA));
		byte[] sessionParameters = cursorToItem.getBlob(cursorToItem.getColumnIndex(SessionDatabase.COLUMN_PARAMETERS));
		// metto tutto dentro un bundle
		Bundle sessionInfo = new Bundle();
		sessionInfo.putInt(SessionDatabase.COLUMN_ID, sessionId);
		sessionInfo.putString(SessionDatabase.COLUMN_NAME, sessionName);
		sessionInfo.putLong(SessionDatabase.COLUMN_FIRSTDATA, sessionFirstDate);
		sessionInfo.putLong(SessionDatabase.COLUMN_LASTDATA, sessionLastDate);
		sessionInfo.putByteArray(SessionDatabase.COLUMN_ACCELERDATA, sessionAccelerData);
		sessionInfo.putByteArray(SessionDatabase.COLUMN_PARAMETERS, sessionParameters);
		Log.d("MAIN_BUNDLE",sessionInfo.toString());
		sessionSelect = sessionInfo;
		Log.d("MAIN", Integer.toString(index));
		
		descriptionFragment = (DescriptionFragment) getSupportFragmentManager().findFragmentById(R.id.descriptionFragment);
		if(descriptionFragment != null && descriptionFragment.isInLayout())
		{
			descriptionFragment.setSessionDetailIntoFragment(sessionInfo);
		}
		else
		{
			Intent intent = new Intent(this, DescriptionActivity.class);
			intent.putExtra(SESSION_INFO, sessionInfo);
			startActivity(intent);
		}
		
	}

	@Override
	public void sessionAdd() {
		myDb.open();
		myDb.addSession("PROVA ADD", new Float[]{(float)1,(float)2} );
		adapter.changeCursor(myDb.fetchAll(SessionDatabase.COLUMN_NAME));
		myDb.close();
	}

	@Override
	public void onItemLongSelect(int index) {
		Log.d("LONG SELECT", "premuto il: "+Integer.toString(index));	
	}

	@Override
	public void sessionRename(int sessionId) {
		myDb.open();
		myDb.renameSession("name", sessionId);
		adapter.changeCursor(myDb.fetchAll(SessionDatabase.COLUMN_NAME));
		myDb.close();
		
		
	}

	@Override
	public void sessionDuplicate(int sessionId) {		
		myDb.open();		
		myDb.duplicateSession(sessionId);
		adapter.changeCursor(myDb.fetchAll(SessionDatabase.COLUMN_NAME));
		myDb.close();
		
	}

	@Override
	public void sessionDelete(int sessionId) {
		myDb.open();
		myDb.deleteSession(sessionId);
		adapter.changeCursor(myDb.fetchAll(SessionDatabase.COLUMN_NAME));
		myDb.close();
		
	}

	@Override
	protected void onSaveInstanceState (Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putBundle(SESSION_INFO, sessionSelect);
		
	}

	@Override
	public void setSessionParameters(Bundle parameters) {
		int id = parameters.getInt(SessionDatabase.COLUMN_ID);
		boolean x = parameters.getBoolean("X ASIX");
		boolean y = parameters.getBoolean("Y ASIX");
		boolean z = parameters.getBoolean("Z ASIX");
		Parameters p = new Parameters(x, y, z);
		Toast.makeText(getApplicationContext(), "id; "+Integer.toString(id)+Boolean.toString(x)+Boolean.toString(y)+Boolean.toString(z), Toast.LENGTH_SHORT).show();;
		
	}
}
