package com.example.thekra.groceryapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thekra.groceryapp.InventoryContract.InventoryEntry;
import com.squareup.picasso.Picasso;

import at.markushi.ui.CircleButton;

import static com.example.thekra.groceryapp.R.id.imageView;

public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EXISTING_LOADER = 0;
    private static int RESULT_LOAD_IMAGE = 1;
    private Uri currentProductUri;
    private EditText nameEditText;
    private EditText priceEditText;
    private TextView quantityTextView;
    ImageView image;
    ImageView productImage;
    Uri selectedImage;
    Button imageButton;
    int quantity = 0;
    private boolean productHasChange = false;
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            productHasChange = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Intent intent = getIntent();
        currentProductUri = intent.getData();

        if (currentProductUri == null) {
            setTitle(getString(R.string.label_Add));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.label_Edit));
            getSupportLoaderManager().initLoader(EXISTING_LOADER, null, this);
        }
        nameEditText = (EditText) findViewById(R.id.name_id);
        priceEditText = (EditText) findViewById(R.id.price_id);
        quantityTextView = (TextView) findViewById(R.id.quantity_id);
        image = (ImageView) findViewById(imageView);
        productImage = (ImageView) findViewById(R.id.product_image);
        imageButton = (Button) findViewById(R.id.image_button);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
        nameEditText.setOnTouchListener(touchListener);
        priceEditText.setOnTouchListener(touchListener);
        quantityTextView.setOnTouchListener(touchListener);
        productImage.setOnTouchListener(touchListener);

        CircleButton increase = (CircleButton) findViewById(R.id.increase);
        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity++;
                quantityTextView.setText(String.valueOf(quantity));
            }
        });
        CircleButton decrease = (CircleButton) findViewById(R.id.decrease);

        decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 0) {
                    quantity--;
                    quantityTextView.setText(String.valueOf(quantity));
                }
            }
        });
    }

    private void saveProduct() {
        String nameString = "";
        String priceString = "";
        String quantityString = "";
        String imageString = "";
        nameString = nameEditText.getText().toString().trim();
        Log.v("IMAGE2", "DDDDDDDD" + nameString);

        priceString = priceEditText.getText().toString().trim();
        Log.v("IMAGE2", "DDDDDDDD" + priceString);

        quantityString = quantityTextView.getText().toString();
        Log.v("IMAGE2", "DDDDDDDD" + quantityString);

        if(selectedImage==null){
            Toast.makeText(EditActivity.this,getString(R.string.imageUri_error),Toast.LENGTH_SHORT).show();
        }else {
            imageString= selectedImage.toString();
        }
        if (currentProductUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(imageString)) {
            return;
        }

        if (TextUtils.isEmpty(nameString)) {
            nameEditText.setError(getString(R.string.name_error));
        } else if (TextUtils.isEmpty(priceString)) {
            priceEditText.setError(getString(R.string.price_error));
        } else if (TextUtils.isEmpty(imageString)) {
            Toast.makeText(EditActivity.this, getString(R.string.image_error), Toast.LENGTH_SHORT).show();
            Log.v("IMAGE1", "DDDDDDDD" + imageString);
        } else {

            imageString = selectedImage.toString();
            Log.v("IMAGE2", "DDDDDDDD" + imageString);
            ContentValues values = new ContentValues();
            values.put(InventoryEntry.COLUMN_PRODUCT_NAME, nameString);
            values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, priceString);
            values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantityString);
            values.put(InventoryEntry.COLUMN_PRODUCT_IMAGE, imageString);
            if (currentProductUri == null) {
                Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
                if (newUri == null) {
                    Toast.makeText(this, getString(R.string.insert_fail), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.insert_successful), Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {

                int rowAffected = getContentResolver().update(currentProductUri, values, null, null);
                if (rowAffected == 0) {
                    Toast.makeText(this, getString(R.string.update_fail), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.update_successful), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_options, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                saveProduct();
                return true;
            case R.id.delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:

                if (!productHasChange) {
                    NavUtils.navigateUpFromSameTask(EditActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!productHasChange) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {InventoryEntry._ID, InventoryEntry.COLUMN_PRODUCT_NAME
                , InventoryEntry.COLUMN_PRODUCT_QUANTITY, InventoryEntry.COLUMN_PRODUCT_PRICE, InventoryEntry.COLUMN_PRODUCT_IMAGE};
        return new CursorLoader(this, currentProductUri, projection
                , null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1)
            return;
        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int quantotyIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            int priceIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
            int imageIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_IMAGE);

            final String name = cursor.getString(nameIndex);
            quantity = cursor.getInt(quantotyIndex);
            int price = cursor.getInt(priceIndex);
            String image = cursor.getString(imageIndex);
            Uri uri = Uri.parse(image);
            productImage.setImageURI(uri);
            nameEditText.setText(name);
            quantityTextView.setText(String.valueOf(quantity));
            priceEditText.setText(String.valueOf(price));
            selectedImage = uri;

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameEditText.setText("");
        quantityTextView.setText("");
        priceEditText.setText("");
        productImage.setImageURI(null);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.save_dialog));
        builder.setPositiveButton(getString(R.string.discard), discardButtonClickListener);
        builder.setNegativeButton(getString(R.string.keep), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null)
                    dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_dialog));
        builder.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(getString(R.string.Cancle), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null)
                    dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        if (currentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(currentProductUri, null, null);
            if (rowsDeleted == 0)
                Toast.makeText(this, getString(R.string.delete_fail), Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, getString(R.string.delete_successful), Toast.LENGTH_SHORT).show();
            finish();

        }
    }

    public void summary(View view) {
        String name = nameEditText.getText().toString().trim();
        String quantity = quantityTextView.getText().toString();
        String price = priceEditText.getText().toString().trim();
        String image = selectedImage.toString();
        String summary = name + "\n" + quantity + "\n" + price + "\n" + image ;

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.Order_supplier));
        intent.putExtra(Intent.EXTRA_TEXT, summary);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            selectedImage = data.getData();
            Log.v("RESULT", "SSSSSSSSs" + selectedImage);
            Picasso.with(EditActivity.this).load(selectedImage).into(productImage);

        }

    }


}