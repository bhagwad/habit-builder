package com.bhagwad.habit;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;
import com.bhagwad.habit.HabitDefinitions.HabitColumns;

public class HabitContentProvider extends ContentProvider {

	private static final String DATABASE_NAME = "habits.db";
	private static final int DATABASE_VERSION = 1;

	private static final UriMatcher sUriMatcher;
	private static final int HABIT_LIST = 0;
	private static final int HABIT_OCCURANCE = 1;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);

		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			db.execSQL("CREATE TABLE " + HabitDefinitions.TABLE_HABITS + " (" 
			+ HabitColumns._ID + " INTEGER PRIMARY KEY, "
			+ HabitColumns.HABIT_NAME + " TEXT, " 
			+ HabitColumns.HABIT_GOAL + " TEXT, "
			+ HabitColumns.HABIT_LONGEST + " TEXT, " 
			+ "UNIQUE ("+HabitColumns.HABIT_NAME
			+ ") ON CONFLICT IGNORE"
			+ ");");

			db.execSQL("CREATE TABLE " + HabitDefinitions.TABLE_HABITS_RECORD + " (" 
			+ HabitColumns._ID + " INTEGER PRIMARY KEY, "
			+ HabitColumns.HABIT_NAME + " TEXT, " 
			+ HabitColumns.HABIT_OCCURRENCE + " TEXT, "
			+ "UNIQUE ("+HabitColumns.HABIT_NAME+","+HabitColumns.HABIT_OCCURRENCE
			+ ") ON CONFLICT IGNORE, "
			+ "CONSTRAINT fk_habitname FOREIGN KEY (" + HabitColumns.HABIT_NAME + ") REFERENCES " + HabitDefinitions.TABLE_HABITS + "("+ HabitColumns.HABIT_NAME + ") "
			+ "ON DELETE CASCADE"
			+ ");");

		}
		
		@Override
		public void onOpen(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			super.onOpen(db);
			
			if (!db.isReadOnly()) {
		        // Enable foreign key constraints
		        db.execSQL("PRAGMA foreign_keys=ON;");
		    }
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub

		}

	}

	private DatabaseHelper mOpenHelper;

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {

		String mTableName = setTableName(uri);
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		int rowsAffected = db.delete(mTableName, selection, selectionArgs);

		if (rowsAffected > 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}

		return rowsAffected;

	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {

		ContentValues values;

		if (initialValues == null)
			values = new ContentValues();
		else
			values = new ContentValues(initialValues);

		String mTableName = setTableName(uri);

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		long rowId = db.insertWithOnConflict(mTableName, HabitColumns._ID, values, SQLiteDatabase.CONFLICT_IGNORE);

		Uri mAddedUri = ContentUris.withAppendedId(uri, rowId);

		if (rowId > 0) {
			getContext().getContentResolver().notifyChange(mAddedUri, null);
		}

		return mAddedUri;
	}

	private String setTableName(Uri uri) {

		String mTableName;

		switch (sUriMatcher.match(uri)) {

		case HABIT_LIST:
			mTableName = HabitDefinitions.TABLE_HABITS;
			break;

		case HABIT_OCCURANCE:
			mTableName = HabitDefinitions.TABLE_HABITS_RECORD;
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);

		}

		return mTableName;
	}

	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		switch (sUriMatcher.match(uri)) {

		case HABIT_LIST:
			qb.setTables(HabitDefinitions.TABLE_HABITS);
			break;

		case HABIT_OCCURANCE:
			qb.setTables(HabitDefinitions.TABLE_HABITS_RECORD);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);

		}
		
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

		c.setNotificationUri(getContext().getContentResolver(), uri);

		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	static {

		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(HabitDefinitions.AUTHORITY, HabitDefinitions.TABLE_HABITS, HABIT_LIST);
		sUriMatcher.addURI(HabitDefinitions.AUTHORITY, HabitDefinitions.TABLE_HABITS_RECORD, HABIT_OCCURANCE);

	}

}