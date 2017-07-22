package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Created by an0o0nym on 19/07/17.
 */

public class ProductDbHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "inventory";

    private static final int DATABASE_VERSION = 2;

    public ProductDbHelper (Context c) {
        super(c, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder("CREATE TABLE " + ProductEntry.TABLE_NAME);
        sb.append("(");
        sb.append(ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(ProductEntry.COLUMN_NAME + " TEXT NOT NULL, ");
        sb.append(ProductEntry.COLUMN_PRICE + " INTEGER NOT NULL, ");
        sb.append(ProductEntry.COLUMN_QTY + " INTEGER NOT NULL DEFAULT 0, ");
        sb.append(ProductEntry.COLUMN_IMG + " TEXT, ");
        sb.append(ProductEntry.COLUMN_SUPPLIER_EMAIL + " TEXT NOT NULL");
        sb.append(");");

        db.execSQL(sb.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // Delete table from the db completely.
        db.execSQL("DROP TABLE IF EXISTS " + ProductEntry.TABLE_NAME);
        // Recreate table in the db.
        onCreate(db);
    }
}
