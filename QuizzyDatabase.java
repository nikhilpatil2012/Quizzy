package tronbox.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
//
public class QuizzyDatabase extends SQLiteOpenHelper {
	
	public QuizzyDatabase(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String dataTable="create table QUIZDATA( "
				+ "id"
				+ " integer primary key autoincrement,"
				+ "CATAGORY text,CATAGORY_NAME text,CATAGORY_IMG_PATH text,CHAP_ID text,CHAP_NAME text, CHAP_IMG_PATH text); ";

		String scoreTable="create table SCOREDATA("
				+ "id"
				+ " integer primary key autoincrement,"
				+ "CATAGORY text,CATAGORY_NAME text,CHAP_ID text,CHAP_NAME text,GLOBAL_RANK text,TOTAL_SCORE text,CORRECT_QUES text, INCORRECT_QUES text); ";

		db.execSQL(dataTable);
		db.execSQL(scoreTable);
	}
	
	public void insertUpdatesQUIZDATA(String CATAGORY,String CATAGORY_NAME,String CATAGORY_IMG_PATH,String CHAP_ID, String CHAP_NAME, String CHAP_IMG_PATH) {
		ContentValues cv = new ContentValues();
		cv.put("CATAGORY", CATAGORY);
		cv.put("CATAGORY_NAME", CATAGORY_NAME);
		cv.put("CATAGORY_IMG_PATH", CATAGORY_IMG_PATH);
		cv.put("CHAP_ID", CHAP_ID);
		cv.put("CHAP_NAME", CHAP_NAME);
		cv.put("CHAP_IMG_PATH", CHAP_IMG_PATH);
		
		getWritableDatabase().insert("QUIZDATA", null, cv);
	}

	public void insertUpdatesSCOREDATA(String CATAGORY,String CATAGORY_NAME,String CHAP_ID,String CHAP_NAME,String GLOBAL_RANK,String TOTAL_SCORE, String CORRECT_QUES, String INCORRECT_QUES) {
		ContentValues cv = new ContentValues();
		cv.put("CATAGORY", CATAGORY);
		cv.put("CATAGORY_NAME", CATAGORY_NAME);
		cv.put("CHAP_ID", CHAP_ID);
		cv.put("CHAP_NAME", CHAP_NAME);
		cv.put("GLOBAL_RANK", GLOBAL_RANK);
		cv.put("TOTAL_SCORE", TOTAL_SCORE);
		cv.put("CORRECT_QUES", CORRECT_QUES);
		cv.put("INCORRECT_QUES", INCORRECT_QUES);
		
		getWritableDatabase().insert("SCOREDATA", null, cv);
	}
	
	public Cursor getUpdatesQUIZDATA() {
		
		Cursor c = getWritableDatabase().rawQuery("select * from QUIZDATA", null);
		
		return c;
	}

	public Cursor getUpdatesSCOREDATA() {
		
		Cursor c = getWritableDatabase().rawQuery("select * from SCOREDATA", null);
		
		return c;
	}

	public boolean updateGlobalRank(String CATAGORY,String CHAP_ID, String GLOBAL_RANK) {
	    Cursor c = getDataSCOREDATA(CATAGORY, CHAP_ID);
	    c.moveToFirst();
	    String rowId = c.getString(0);
	    c.close();
	    ContentValues args = new ContentValues();
	    args.put("GLOBAL_RANK", GLOBAL_RANK);
	    return getWritableDatabase().update("SCOREDATA", args, "id='"+rowId+"'", null) > 0;
	  }
	
	
	public boolean updateFullScore(String CATAGORY,String CHAP_ID, String GLOBAL_RANK,String TOTAL_SCORE,String CORRECT_QUES,String INCORRECT_QUES) {
	    Cursor c = getDataSCOREDATA(CATAGORY, CHAP_ID);
	    c.moveToFirst();
	    String rowId = c.getString(0);
	    c.close();
	    ContentValues args = new ContentValues();
	    args.put("GLOBAL_RANK", GLOBAL_RANK);
	    args.put("TOTAL_SCORE", TOTAL_SCORE);
	    args.put("CORRECT_QUES", CORRECT_QUES);
	    args.put("INCORRECT_QUES", INCORRECT_QUES);
	    return getWritableDatabase().update("SCOREDATA", args, "id='"+rowId+"'", null) > 0;
	  }
	
	public Cursor getDataSCOREDATA(String CATAGORY,String CHAP_ID) {
		Cursor c = getWritableDatabase().rawQuery("select * from SCOREDATA where CATAGORY='"+CATAGORY+"' AND CHAP_ID ='"+CHAP_ID+"'", null);
		return c;
	}
	
	public String getGlobalRank(String CATAGORY,String CHAP_ID) {
	    Cursor c = getDataSCOREDATA(CATAGORY, CHAP_ID);
	    c.moveToFirst();
	    String rank = c.getString(c.getColumnIndex("GLOBAL_RANK"));
	    c.close();
	    return rank;
	  }
	
	
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		
		
	}
	
}