package com.sannacode.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.sannacode.DB.Model.Contacts;

import java.util.ArrayList;
import java.util.List;

public class DBHelperContact extends SQLiteOpenHelper {

    public static final int DATEBASE_VERSION = 3;
    public static final String DATEBASE_NAME_CONTACT = "ContactDB";
    public static final String TABLE_CONTACT_CONTACTS = "DBContact";

    public static final String KEY_ID = "ID";
    public static final String KEY_NAME = "Name";
    public static final String KEY_EMAIL = "Email";
    public static final String KEY_NUMBER = "Number";
    private static final String TAG = "DB";

    public DBHelperContact(Context context) {
        super(context, DATEBASE_NAME_CONTACT, null, DATEBASE_VERSION);
    }

    public static final String CREATE_TABLE_CONTACT = "CREATE TABLE " + TABLE_CONTACT_CONTACTS + " ( "
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_NAME + " TEXT, "
            + KEY_EMAIL + " TEXT, "
            + KEY_NUMBER + " TEXT)";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CONTACT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACT_CONTACTS);
        db.execSQL(CREATE_TABLE_CONTACT);
    }

    public void addContact(Contacts contacts) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(KEY_NAME, contacts.getName());
        value.put(KEY_NUMBER, contacts.getNumber());
        value.put(KEY_EMAIL, contacts.getEmail());

        Long id = db.insert(TABLE_CONTACT_CONTACTS, null, value);
        db.close();

        Log.d(TAG, "user inserted" + id);
    }

    public Contacts getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACT_CONTACTS, new String[] { KEY_ID, KEY_NAME, KEY_EMAIL, KEY_NUMBER }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        Contacts user = new Contacts(cursor.getString(0), cursor.getString(1), cursor.getString(2));
        return user;
    }


    public List<Contacts> getAllContacts() {
        List<Contacts> contactsList = new ArrayList<Contacts>();
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACT_CONTACTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Contacts contacts = new Contacts();
                contacts.setId(Integer.parseInt(cursor.getString(0)));
                contacts.setName(cursor.getString(1));
                contacts.setEmail(cursor.getString(2));
                contacts.setNumber(cursor.getString(3));

                contactsList.add(contacts);
            } while (cursor.moveToNext());
        }
        return contactsList;
    }

    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTACT_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }

    public int updateContatct(Contacts contacts) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contacts.getName());

        return db.update(TABLE_CONTACT_CONTACTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(contacts.getNumber()) });
    }

    public void deleteConatct(Contacts contacts) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACT_CONTACTS, KEY_ID + " = ?", new String[] { String.valueOf(contacts.getId()) });
        db.close();
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACT_CONTACTS, null, null);
        db.close();
    }
}
