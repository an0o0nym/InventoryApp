package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Created by an0o0nym on 19/07/17.
 */

public class DetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int EXISTING_PRODUCT_LOADER = 0;
    private Uri mCurrentProductUri;

    private TextView nameView;
    private TextView priceView;
    private TextView qtyView;
    private ImageView imgView;

    private String suppEmail;
    private String productName;
    private int qty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_detail);

        nameView = (TextView) findViewById(R.id.item_name);
        priceView = (TextView) findViewById(R.id.item_price);
        qtyView = (TextView) findViewById(R.id.item_qty);
        imgView = (ImageView) findViewById(R.id.item_img);

        findViewById(R.id.plus_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean subtract = false;
                updateQty(subtract);
            }
        });

        findViewById(R.id.minus_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean subtract = true;
                updateQty(subtract);
            }
        });

        findViewById(R.id.order_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String subject = String.format(getString(R.string.email_subject), productName);
                String body = String.format(getString(R.string.email_body), productName);
                sendEmail(subject, body);
            }
        });

        findViewById(R.id.delete_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getProductDeletionDialog();
            }
        });

        getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
    }

    private void getProductDeletionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
        builder.setMessage(getString(R.string.delete_dialog_prompt));
        builder.setPositiveButton(getString(R.string.delete_dialog_positive),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteProduct();
                    }
                });

        builder.setNegativeButton(getString(R.string.delete_dialog_negative),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (dialogInterface != null) {
                            dialogInterface.dismiss();
                        }
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        if (mCurrentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.delete_product_error),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.delete_product_success),
                        Toast.LENGTH_SHORT).show();
                // return to main view
                finish();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_NAME,
                ProductEntry.COLUMN_PRICE,
                ProductEntry.COLUMN_QTY,
                ProductEntry.COLUMN_IMG,
                ProductEntry.COLUMN_SUPPLIER_EMAIL
        };

        return new CursorLoader(this,
                mCurrentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(cursor.moveToFirst()) {
            int colIdxName = cursor.getColumnIndex(ProductEntry.COLUMN_NAME);
            int colIdxPrice = cursor.getColumnIndex(ProductEntry.COLUMN_PRICE);
            int colIdxQty = cursor.getColumnIndex(ProductEntry.COLUMN_QTY);
            int colIdxImg = cursor.getColumnIndex(ProductEntry.COLUMN_IMG);
            int colIdxSuppEmail = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_EMAIL);

            productName = cursor.getString(colIdxName);
            nameView.setText(productName);
            priceView.setText(ProductCursorAdapter.formatCurrency(cursor.getInt(colIdxPrice)));
            qtyView.setText(cursor.getString(colIdxQty));

            qty = Integer.parseInt(qtyView.getText().toString());
            if(!TextUtils.isEmpty(cursor.getString(colIdxImg))) {
                Uri productImgUri = Uri.parse(cursor.getString(colIdxImg));
                imgView.setImageURI(productImgUri);
            }
            suppEmail = cursor.getString(colIdxSuppEmail);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameView.setText("");
        priceView.setText("");
        qtyView.setText("");
        suppEmail = "";
        imgView.setImageResource(R.drawable.placeholder);
    }

    private void updateQty(boolean subtract) {
        ContentValues contentvalues = new ContentValues();

        if (qty > 0) {
            contentvalues.put(ProductEntry.COLUMN_QTY, subtract ? --qty : ++qty);
            int rowsUpdated = getContentResolver().update(mCurrentProductUri,
                    contentvalues, null, null);

            if (rowsUpdated == 0) {
                Toast.makeText(this, getString(R.string.update_qty_error),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.sell_below_zero),
                    Toast.LENGTH_SHORT).show();
        }


    }

    private void sendEmail (String subject, String body) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {suppEmail});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
