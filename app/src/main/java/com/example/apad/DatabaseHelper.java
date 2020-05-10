package com.example.apad;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(@Nullable Context context) {
        super(context, "notes", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE notes (ID INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(255) NOT NULL, path VARCHAR(1024) NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS notes");
        onCreate(db);
    }

    public int saveData(int id, String name, String path) {
        SQLiteDatabase dbWrite = this.getWritableDatabase();

        if (id > 0) { // update
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", name);
            contentValues.put("path", path);

            if (dbWrite.update("notes", contentValues, "id = ?", new String[] {String.valueOf(id)}) == -1) {
                return 0;
            }
        } else { // insert
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", name);
            contentValues.put("path", path);

            id = (int) dbWrite.insert("notes", null, contentValues);
            if (id == -1) {
                return 0;
            }
        }

        return id;
    }

    public ArrayList<HashMap<String, Object>> retrieveData() {
        ArrayList<HashMap<String, Object>> result = new ArrayList<>();
        SQLiteDatabase dbRead = this.getReadableDatabase();

        Cursor c = dbRead.rawQuery("SELECT * FROM notes", null);
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                HashMap<String, Object> row = new HashMap<>();

                row.put("id", c.getInt(0));
                row.put("name", c.getString(1));
                row.put("path", c.getString(2));

                result.add(row);
            }
        }

        c.close();
        dbRead.close();

        return result;
    }
}
