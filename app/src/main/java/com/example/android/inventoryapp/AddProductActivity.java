package com.example.android.inventoryapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Created by an0o0nym on 21/07/17.
 */

public class AddProductActivity extends AppCompatActivity{
    private static final int CHOOSE_IMAGE_REQUEST = 0;

    private String imgPath;
    private ImageView imgView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_activity);

        imgView = (ImageView) findViewById(R.id.item_img);

        findViewById(R.id.choose_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageChooser();
            }
        });

        findViewById(R.id.save_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String productName = getEditViewText(R.id.item_name);
                String productPrice = getEditViewText(R.id.item_price);
                String productQty = getEditViewText(R.id.item_qty);
                String suppEmail = getEditViewText(R.id.item_supplier);

                saveProduct(productName, productPrice, productQty, suppEmail);
            }
        });
    }

    public void openImageChooser() {
        Intent intent;

        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }

        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri mProductImgUri = data.getData();
                imgView.setImageURI(mProductImgUri);
                imgPath = mProductImgUri.toString();
            }
        }
    }

    private String getEditViewText(int viewID) {
        return ((EditText) findViewById(viewID)).getText().toString().trim();
    }

    private void saveProduct(String productName, String productPrice, String productQty,
                             String suppEmail) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ProductEntry.COLUMN_NAME, productName);

        if (!TextUtils.isEmpty(productPrice)) {
            contentValues.put(ProductEntry.COLUMN_PRICE, Integer.parseInt(productPrice));
        }

        if (!TextUtils.isEmpty(productQty)) {
            contentValues.put(ProductEntry.COLUMN_QTY, Integer.parseInt(productQty));
        }

        contentValues.put(ProductEntry.COLUMN_SUPPLIER_EMAIL, suppEmail);

        if (!TextUtils.isEmpty(imgPath)) {
            contentValues.put(ProductEntry.COLUMN_IMG, imgPath);
        }

        Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, contentValues);
        if (newUri != null) {
            Toast.makeText(AddProductActivity.this,
                    getString(R.string.insert_product_success),
                    Toast.LENGTH_SHORT)
                    .show();
            finish();
        }
    }
}
