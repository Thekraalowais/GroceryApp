package com.example.thekra.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.example.thekra.inventoryapp.InventoryContract.InventoryEntry;




public class InventoryAdapter extends CursorAdapter {

    public InventoryAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView nameText = (TextView) view.findViewById(R.id.name_item);
        final TextView quantityText = (TextView) view.findViewById(R.id.quantity_item);
        TextView priceText = (TextView) view.findViewById(R.id.price_item);
        Button decrease = (Button) view.findViewById(R.id.decrease_button);


        final int nameIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
        int qauntityIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
        int priceIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
        int idIndex = cursor.getColumnIndex(InventoryEntry._ID);
        String productName = cursor.getString(nameIndex);
        nameText.setText(productName);

        final int productQuantity = cursor.getInt(qauntityIndex);
        quantityText.setText(String.valueOf(productQuantity));
        final int id=cursor.getInt(idIndex);
        decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(productQuantity>0) {
                    int newQuantity=productQuantity-1;
                    quantityText.setText(String.valueOf(newQuantity));
                    Uri currrentProduct= ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
                    ContentValues values = new ContentValues();
                    values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);
                    context.getContentResolver().update(currrentProduct, values, null, null);

                }
            }
        });
        int productPrice = cursor.getInt(priceIndex);
        priceText.setText(String.valueOf(productPrice));
    }

}
