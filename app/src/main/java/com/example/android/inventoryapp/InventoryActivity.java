package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;


import com.example.android.inventoryapp.data.ProductContract;
import com.example.android.inventoryapp.data.ProductContract.ProductEntry;
import com.example.android.inventoryapp.data.ProductDbHelper;

import static android.R.attr.id;
import static android.R.id.empty;


public class InventoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int PRODUCT_LOADER = 0;
    ProductCursorAdapter mCursorAdapter;
    private View loadView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_activity);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InventoryActivity.this, AddProductActivity.class);
                startActivity(intent);
            }
        });

        loadView = findViewById(R.id.load_view);
        View emptyView = findViewById(R.id.empty_view);

        // set empty view
        ListView productListView = (ListView) findViewById(R.id.list);
        productListView.setEmptyView(emptyView);

        // set adapter
        mCursorAdapter = new ProductCursorAdapter(InventoryActivity.this, null);
        productListView.setAdapter(mCursorAdapter);

        // set onclick listener for list items
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                Intent intent = new Intent(InventoryActivity.this, DetailActivity.class);
                Uri currentProduct = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
                intent.setData(currentProduct);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_NAME,
                ProductEntry.COLUMN_PRICE,
                ProductEntry.COLUMN_QTY,
                ProductEntry.COLUMN_IMG
        };

        return new CursorLoader(this,
                ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        loadView.setVisibility(View.GONE);
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    private void insertProducts() {
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_NAME, "Product #1");
        values.put(ProductEntry.COLUMN_PRICE, 200);
        values.put(ProductEntry.COLUMN_QTY, 10);
        values.put(ProductEntry.COLUMN_SUPPLIER_EMAIL, "test@example.com");


        getContentResolver().insert(ProductEntry.CONTENT_URI, values);
    }
}
