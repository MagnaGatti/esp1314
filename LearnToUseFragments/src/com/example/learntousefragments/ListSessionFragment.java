package com.example.learntousefragments;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ListView;

public class ListSessionFragment extends ListFragment {

	private OnListSessionListener itemSelectListener;
	
	private Context mContext;
    private Button addSession;
    private ListView listSessions;
    // ---- ID per le azioni del menù ----
    private final int actionRename = 0;
    private final int actionDuplicate = 1;
    private final int actionDelete = 2;

	// interfaccia pubblica per comunicare con l'activity
	public interface OnListSessionListener {
		public void onItemSelect(int index);
		public void onItemLongSelect(int index);
		public void sessionAdd();
		public void sessionRename(int sessionId);
		public void sessionDuplicate(int sessionId);
		public void sessionDelete(int sessionId);
		
		
	}

	// Controlla che l'activity genitore implementi l'interfaccia pubblica,
	// altrimenti lancia un'eccezione.
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			itemSelectListener = (OnListSessionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnListSessionListener");
		}
	}

	// lancia il metodo dell'interfaccia quando utente seleziona un elemento
	// della lista.
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		itemSelectListener.onItemSelect(position);
	}
	
 
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		mContext = getActivity();
		if(mContext==null) Log.d("CONTEXT", "NULL");
		// collego l'oggetto Button alla UI e associo un listener
		addSession = (Button)rootView.findViewById(R.id.buttonStartRecording);
		addSession.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//il listener passa il controllo all'activity container
				itemSelectListener.sessionAdd();				
			}
		});		
		Log.d("LIST_SESSION", "ONCREATEVIEW");
		
        return rootView;		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//recupero la ListView del Fragment e vi associo un listener
		listSessions = getListView();
		registerForContextMenu(listSessions);		
//		listSessions.setOnItemLongClickListener(new OnItemLongClickListener() {
//
//			@Override
//			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
//					int arg2, long arg3) {
//				itemSelectListener.onItemLongSelect(arg2);
//				return true;
//			}
//		});
		Log.d("LIST_SESSION", "ONACTIVITYCREATED");
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Context Menu");
		menu.add(Menu.NONE, actionRename, Menu.NONE, "Rename Session");
		menu.add(Menu.NONE, actionDuplicate, Menu.NONE, "Duplicate Session");		
		menu.add(Menu.NONE, actionDelete, Menu.NONE, "Delete Session");		
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item){
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		//recupero la posizione dell'elemento della lista cliccato
		int itemPosition = info.position;
		Cursor cursorToAll; 
		try{
			cursorToAll= (Cursor)getListAdapter().getItem(itemPosition);
		}
		catch(ClassCastException e){
			throw new ClassCastException("not a cursor");			
		}
		int itemSessionId = cursorToAll.getInt(cursorToAll.getColumnIndex(SessionDatabase.COLUMN_ID));
		
		Log.d("CONTEXT_MENU", "cliccato il: "+Integer.toString(itemPosition));
		
		switch(item.getItemId()){
		case actionRename:
			itemSelectListener.sessionRename(itemSessionId);
			Log.d("MENU_ITEM", "hai cliccato RENAME");
			return true;
		case actionDuplicate:
			itemSelectListener.sessionDuplicate(itemSessionId);
			Log.d("MENU_ITEM", "hai cliccato DUPLICATE");
			return true;
		case actionDelete:
			itemSelectListener.sessionDelete(itemSessionId);
			Log.d("MENU_ITEM", "hai cliccato DELETE");
			return true;
		default:
			return false;		
		}		
	}
}
