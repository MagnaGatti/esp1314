package com.example.learntousefragments;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Questa classe realizza il database delle sessioni musicali.
 * Ogni sessione musicale è composta da:
 * - un identifivativo univoco(ID);
 * - un nome;
 * - un set di parametri che serviranno alla riproduzione;
 * - un set di valori campionati dall'accelerometro;
 * - la data di aquisizione dei campioni dall'accelerometro;
 * - la data di ultima modifica alla sessione.
 * 
 * Il database prevede le funzioni di add, delete, rename, duplicate
 * e fetch per leggere l'intero database; prima di ogni operazione bisogna aprire
 * in scrittura il database, e al termine chiuderlo per rendere effettive le modifiche ad esso.
 * 
 * @author Matteo
 *
 */
public class SessionDatabase {
	
	private SQLiteDatabase myDb;
	private SessionDatabaseHelper myDbHelper;
	
	// ----- CAMPI DELLA TABELLA MUSICSESSION -----
	public static final String TABLE_NAME = "musicSession";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_FIRSTDATA = "firstDate";
	public static final String COLUMN_LASTDATA = "lastData";
	public static final String COLUMN_PARAMETERS = "parameters";
	public static final String COLUMN_ACCELERDATA = "accelerData";
	public static final String COLUMN_THUMBNAILDATA  = "thumbnail";

	/**
	 * Costruttore per istanziare il database.
	 * @param context il context dell'applicazione
	 */
	public SessionDatabase(Context context) {
		myDbHelper = new SessionDatabaseHelper(context);
	}
	
	/**
	 * Apre il database, rendendolo scrivibile e leggibile.
	 * Aprire il database prima di ogni operazione, e chiuderlo successivamente.
	 */
	public void open(){
		myDb = myDbHelper.getWritableDatabase();
	}
	
	/**
	 * Chiude il database.
	 */
	public void close(){
		myDb.close();
	}
	
	/**
	 * Aggiunge una sessione musicale al database.
	 * @param name nome della sessione
	 * @param accelerData l'array contenente i valori dell'accelerometro
	 */
	public void addSession(String name, Float[] accelerData){
		// TODO accettare un sample[] invece di un float[]
		// converto i float in byte
		byte[] accelerByteData = floatToByte(accelerData);
		Long longLastDate = new Date().getTime();
		Date myDate = new Date(1992, 7, 14, 22, 28);
		Long longFirstDate = myDate.getTime();
		
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME, name);
		values.put(COLUMN_ACCELERDATA, accelerByteData);
		values.put(COLUMN_LASTDATA, longLastDate);
		values.put(COLUMN_FIRSTDATA, longFirstDate);
		myDb.insert(TABLE_NAME, null, values);
	}
	
	/**
	 * Metodo ausiliario privato per convertire l'array di float in un array di byte.
	 * @param floatData l'array di float da convertire
	 * @return un array di byte
	 */
	private byte[] floatToByte(Float[] floatData){
		byte[] byteData = new byte[floatData.length];
		for(int i=0; i < byteData.length; i++){
			Log.d("FLOAT", Float.toString(floatData[i]));
			byteData[i] = floatData[i].byteValue();
			Log.d("BYTE", Byte.toString(byteData[i]));	
		}
		Log.d("BYTEDATA", byteData.toString());
		return byteData;
	}
	
	/**
	 * Rinomina una sessione cercandola per ID.
	 * @param name il nuovo nome per la sessione 
	 * @param id l'ID della sessione da rinominare
	 */
	public void renameSession(String name, int id){
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME, name);
		myDb.update(TABLE_NAME, values, SessionDatabase.COLUMN_ID+"=?", new String[]{Integer.toString(id)}); 		
	}
	
	/**
	 * Duplica una sessione del database. La data dell'ultima modifica viene aggiornata.
	 * @param id l'ID della sessione da duplicare
	 */
	public void duplicateSession(int id){
		Cursor session = myDb.query(TABLE_NAME, null, COLUMN_ID+"=?", new String[]{Integer.toString(id)}, null, null, null);
		// preparo i valori da copiare nella nuova sessione
		ContentValues values = new ContentValues();
		if(session.moveToFirst()){
			Log.d("FIRST?", "TRUE");
			values.put(COLUMN_NAME, session.getString(session.getColumnIndex(SessionDatabase.COLUMN_NAME)));
			values.put(COLUMN_PARAMETERS, session.getBlob(session.getColumnIndex(SessionDatabase.COLUMN_PARAMETERS)));
			values.put(COLUMN_ACCELERDATA, session.getBlob(session.getColumnIndex(SessionDatabase.COLUMN_ACCELERDATA)));
			values.put(COLUMN_FIRSTDATA, session.getLong(session.getColumnIndex(SessionDatabase.COLUMN_FIRSTDATA)));
			values.put(COLUMN_LASTDATA, new Date().getTime());
			// inserisco i valori in una nuova sessione
			myDb.insert(TABLE_NAME, null, values);
		}
	}
	
	/**
	 * Cancella una singola sessione dal database.
	 * @param id l'ID della sessione da cancellare
	 */
	public void deleteSession(int id){
		myDb.delete(TABLE_NAME, COLUMN_ID+"=?", new String[]{Integer.toString(id)});
	}
	
	/**
	 * Cancella l'intero database.
	 */
	public void deleteAll(){
		myDb.delete(TABLE_NAME, null, null);
	}
	
	/**
	 * Ritorna un cursore con la lista di tutte le sessioni, ordinate per nome o per ultima modifica.
	 * @param orderColumn colonna rispetto cui ordinare il risultato (nome o ultima modifica)
	 * @return un cursore a tutte le sessioni
	 */
	public Cursor fetchAll(String orderColumn){
		if(!(orderColumn.equalsIgnoreCase(COLUMN_NAME) || orderColumn.equalsIgnoreCase(COLUMN_LASTDATA)))
			orderColumn = null;
		return myDb.query(TABLE_NAME, null, null, null, null, null, orderColumn);
	}
	
	

	/**
	 * Classe che estende SQLiteOpenHelper e aiuta nella gestione del database.
	 * E' privata ed interna in quanto usata solo dalla classe SessionDatabase.
	 * @author Matteo
	 *
	 */
	private class SessionDatabaseHelper extends SQLiteOpenHelper {
		
		// ---- Nome e versione del mio database ----
		private static final String DB_NAME ="Elenco Sessioni musicali";
		private static final int DB_VERSION = 1;

		public SessionDatabaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase arg0) {
			
			// Stringa contenente la sintassi SQL per creare la tabella
			String sql = "CREATE TABLE "+TABLE_NAME; 
	        sql += "("+ COLUMN_ID +" INTEGER PRIMARY KEY,";
	        sql += COLUMN_NAME +" TEXT NOT NULL,";
	        sql += COLUMN_FIRSTDATA +" LONG,";
	        sql += COLUMN_LASTDATA +" LONG,";
	        sql += COLUMN_PARAMETERS +" BLOB,";
	        sql += COLUMN_ACCELERDATA +" BLOB);";	 
	        //Eseguiamo la query
	        arg0.execSQL(sql);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// FIXME migliorare un pò il codice
			String sql = null;
			// gestisco il caso per ciascuna versione ormai vecchia
			switch (oldVersion){
			case 1: 
				break;
			default: 
				Log.d("DB", "oldVersion: "+Integer.toString(oldVersion));
				break;					
			}			
			if(sql != null)
				db.execSQL(sql);

		}

	}
}
