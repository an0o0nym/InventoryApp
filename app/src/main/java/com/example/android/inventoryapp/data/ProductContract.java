package com.example.android.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by an0o0nym on 19/07/17.
 */

public class ProductContract {

    // Prevent from accidentally instantiating this class
    private ProductContract() {}

    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCTS = "products";

    public static final class ProductEntry implements BaseColumns {
        public final static String TABLE_NAME = "products";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        /**
         * Unique ID number for product.
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the product.
         *
         * Type: TEXT
         */
        public final static String COLUMN_NAME = "name";

        /**
         * Price of the product. Assuming there are 2 decimal places.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRICE = "price";

        /**
         * Quantity of the product in stock.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_QTY = "qty";

        /**
         * Uri for the product's image.
         *
         * Type: TEXT
         */
        public final static String COLUMN_IMG = "image";

        /**
         * Email address of the supplier for particular product.
         *
         * Type: TEXT
         */
        public final static String COLUMN_SUPPLIER_EMAIL = "supplier_email";
    }
}
