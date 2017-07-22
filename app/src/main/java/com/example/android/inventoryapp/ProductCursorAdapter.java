package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

import java.math.BigDecimal;
import java.text.NumberFormat;

/**
 * Created by an0o0nym on 19/07/17.
 */

public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    public static String formatCurrency(int value) {
        BigDecimal priceDecimal = new BigDecimal(value)
                .setScale(2, BigDecimal.ROUND_FLOOR)
                .divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
        NumberFormat cf = NumberFormat.getCurrencyInstance();
        return cf.format(priceDecimal);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        final int pos = cursor.getPosition();

        TextView nameView = (TextView) view.findViewById(R.id.item_name);
        TextView priceView = (TextView) view.findViewById(R.id.item_price);
        TextView qtyView = (TextView) view.findViewById(R.id.item_qty);
        ImageView imgView = (ImageView) view.findViewById(R.id.item_img);

        final int colIdxId = cursor.getColumnIndex(ProductEntry._ID);
        int colIdxName = cursor.getColumnIndex(ProductEntry.COLUMN_NAME);
        int colIdxPrice = cursor.getColumnIndex(ProductEntry.COLUMN_PRICE);
        final int colIdxQty = cursor.getColumnIndex(ProductEntry.COLUMN_QTY);
        int colIdxImg = cursor.getColumnIndex(ProductEntry.COLUMN_IMG);

        nameView.setText(cursor.getString(colIdxName));
        priceView.setText(formatCurrency(cursor.getInt(colIdxPrice)));
        qtyView.setText(cursor.getString(colIdxQty));

        if (!TextUtils.isEmpty(cursor.getString(colIdxImg))) {
            Uri mUri = Uri.parse(cursor.getString(colIdxImg));
            imgView.setImageURI(mUri);
        }

        view.findViewById(R.id.sell_btn).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues contentValues = new ContentValues();
                cursor.moveToPosition(pos);
                int qty = cursor.getInt(colIdxQty);
                if (qty > 0) {
                    contentValues.put(ProductEntry.COLUMN_QTY, --qty);
                    Uri productUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI,
                            cursor.getInt(colIdxId));
                    context.getContentResolver().update(productUri, contentValues, null, null);
                } else {
                    Toast.makeText(context, context.getString(R.string.sell_below_zero),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
