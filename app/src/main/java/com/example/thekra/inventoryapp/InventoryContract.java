package com.example.thekra.inventoryapp;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;



public final class InventoryContract {
    private InventoryContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.example.thekra.inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH = "inventory";

    public static final class InventoryEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH);
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;
        public static final String TABLE_NAME = "products";
        public static final String COLUMN_PRODUCT_NAME="name";
        public static final String COLUMN_PRODUCT_QUANTITY="quantity";
        public static final String COLUMN_PRODUCT_PRICE="price";
        public static final String COLUMN_PRODUCT_IMAGE="image";
        public static final String _ID = BaseColumns._ID;

    }
}
