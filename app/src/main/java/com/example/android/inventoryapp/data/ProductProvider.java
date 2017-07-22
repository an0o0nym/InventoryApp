package com.example.android.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.android.inventoryapp.R;
import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Created by an0o0nym on 19/07/17.
 */

public class ProductProvider extends ContentProvider{
    private static final int PRODUCTS = 100;
    private static final int PRODUCT_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private ProductDbHelper mDbHelper;

    static {
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, PRODUCTS);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cur;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                cur = db.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cur = db.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException(getContext().getString(R.string.read_error_uri) + uri);
        }

        cur.setNotificationUri(getContext().getContentResolver(), uri);
        return cur;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch(match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException(String.format(
                        getContext().getString(R.string.insert_error_uri), uri));
        }
    }

    private Uri insertProduct(Uri uri, ContentValues contentValues) {
        Context context = getContext();
        boolean isInserted = true;
        boolean isValid = productIsValid(contentValues, isInserted);

        if (isValid) {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            long id = db.insert(ProductEntry.TABLE_NAME, null, contentValues);

            if (id == -1) {
                Toast.makeText(context, context.getString(R.string.insert_error),
                        Toast.LENGTH_SHORT).show();
            }

            context.getContentResolver().notifyChange(uri, null);
            return ContentUris.withAppendedId(uri, id);
        } else {
            return null;
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch(match) {
            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException(String.format(
                        getContext().getString(R.string.update_error_uri), uri));
        }
    }

    private int updateProduct(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        if (contentValues.size() == 0) {
            return 0;
        }

        boolean isInserted = false;
        boolean isValid = productIsValid(contentValues, isInserted);

        if (isValid) {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            int rowsUpdated = db.update(ProductEntry.TABLE_NAME, contentValues, selection, selectionArgs);

            if (rowsUpdated != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }

            return rowsUpdated;
        } else {
            return 0;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                rowsDeleted = db.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException(String.format(
                        getContext().getString(R.string.delete_error_uri), uri));
        }

       if (rowsDeleted != 0) {
           getContext().getContentResolver().notifyChange(uri, null);
       }
       return rowsDeleted;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException(String.format(getContext()
                        .getString(R.string.get_type_error_uri), uri, Integer.toString(match)));
        }
    }

    private boolean productIsValid (ContentValues contentValues, boolean isInserted) {
        Context context = getContext();

        String colImg = ProductEntry.COLUMN_IMG;
        if (isInserted || contentValues.containsKey(colImg)) {
            String imgPath = contentValues.getAsString(colImg);
            if (TextUtils.isEmpty(imgPath)) {
                Toast.makeText(context, context.getString(R.string.img_validation),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        String colName = ProductEntry.COLUMN_NAME;
        if (isInserted || contentValues.containsKey(colName)) {
            String name = contentValues.getAsString(colName);
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(context, context.getString(R.string.name_validation),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        String colPrice = ProductEntry.COLUMN_PRICE;
        if (isInserted || contentValues.containsKey(colPrice)) {
            Integer price = contentValues.getAsInteger(colPrice);
            if (price == null || price < 0) {
                Toast.makeText(context, context.getString(R.string.price_validation),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        String colQty = ProductEntry.COLUMN_QTY;
        if (isInserted || contentValues.containsKey(colQty)) {
            Integer qty = contentValues.getAsInteger(colQty);
            if (qty == null || qty < 0) {
                Toast.makeText(context, context.getString(R.string.qty_validation),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        String colSuppEmail = ProductEntry.COLUMN_SUPPLIER_EMAIL;
        if (isInserted || contentValues.containsKey(colSuppEmail)) {
            String supplierEmail = contentValues.getAsString(colSuppEmail);
            if (TextUtils.isEmpty(supplierEmail) || !supplierEmail.contains("@")) {
                Toast.makeText(context, context.getString(R.string.supp_email_validation),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }


}
