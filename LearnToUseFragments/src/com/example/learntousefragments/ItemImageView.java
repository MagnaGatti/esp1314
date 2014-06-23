package com.example.learntousefragments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class ItemImageView extends View {

	private Paint pBorder;
	private Paint pInside;
	private String imageData;

	private float deltaX;
	private float deltaY;

	public ItemImageView(Context context) {
		super(context);
		setStartCondition();
	}

	public ItemImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setStartCondition();
	}

	public ItemImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setStartCondition();
	}

	/**
	 * Inizializza i vari campi di esemplare.
	 */
	private void setStartCondition() {
		pBorder = new Paint();
		pBorder.setAntiAlias(true);
		pBorder.setColor(Color.DKGRAY);
		//solo bordo: pBorder.setStyle(Style.STROKE);
		pInside = new Paint();
		pInside.setAntiAlias(true);
		pInside.setColor(Color.BLACK);
		imageData = null;
	}

	/**
	 * "Setta" le informazioni in base a cui generare la thumbnail; prende le informazioni
	 * utili della sessione e ne crea una stringa di zero e di uni.
	 * IDEA: TODO usare ID, firstDate e accelerData in quanto univoci della sessione. 
	 * @param id ID della sessione
	 * @param dataLast data di ultima modifica della sessione
	 * @param dataFirst data di registrazione della sessione
	 */
	public void setImageData(int id, Long dataLast, Long dataFirst) {
		
		Log.d("BIT LONG ID", Long.toString(id));
		imageData = Long.toBinaryString(id);
		Log.d("BIT STRING ID", imageData);
		
		Log.d("BIT LONG DATE", Long.toString(dataLast));	
		Log.d("BIT LONG REVERSE", Long.toString(Long.reverse(dataLast)));
		// faccio reverse perchè parte finale è più variabile(=più informazione;) )
		imageData += Long.toBinaryString(Long.reverse(dataLast));
		Log.d("BIT STRING ID+DATE", imageData);
		// toBinaryString mi da una stringa di 41 bit, 
		// devo aggiungere altro per arrivare a 64 caratteri..		
		imageData += Long.toBinaryString(dataFirst);
		Log.d("BIT STRING FULL", imageData);	
		
	}

	/**
	 * Disegna l'icona in base alla stringa di zeri e uni.
	 * Per ogni uno presente si avrà un quadrato colorato.
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		deltaX = new Float(getWidth() / 8.0);
		deltaY = new Float(getHeight() / 8.0);
		canvas.drawRect(0, 0, getWidth(), getHeight(), pBorder);
		// disegnamo i rettangolini
		int index = 0;
		if (imageData != null) {
			// genera un icona 
//			for (int i = 0; i < 8; i++) {
//				for (int j = 0; j < 8; j++) {
//					canvas.drawLine(j * deltaX, 0, j * deltaX, getHeight(),	pInside);
//					canvas.drawLine(0, i * deltaY, getWidth(), i * deltaY, pInside);
//					char bit = imageData.charAt(index);
//					if (bit == '1') {
//						canvas.drawRect(j*deltaX, i*deltaY, (j+1)*deltaX, (i+1)*deltaY, pInside);
//					}
//					index++;					
//				}				
//			}
			// genera icona simmetrica
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 4; j++) {
					//disegna griglia
					canvas.drawLine(j * deltaX, 0, j * deltaX, getHeight(),	pInside);
					canvas.drawLine((4+j) * deltaX, 0, (4+j) * deltaX, getHeight(),	pInside);
					canvas.drawLine(0, i * deltaY, getWidth(), i * deltaY, pInside);
					char bit = imageData.charAt(index);
					if (bit == '1') {
						canvas.drawRect(j*deltaX, i*deltaY, (j+1)*deltaX, (i+1)*deltaY, pInside);
						canvas.drawRect(getWidth()-(j+1)*deltaX, i*deltaY, getWidth()-(j)*deltaX, (i+1)*deltaY, pInside);
					}
					index++;					
				}				
			}
		}
		//fine disegno
	}

}
