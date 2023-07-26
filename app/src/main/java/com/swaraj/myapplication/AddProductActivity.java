package com.swaraj.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.slider.Slider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.swaraj.myapplication.data.ProductData;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddProductActivity extends AppCompatActivity {

    EditText etBarCode, etProductName;
    Button btnAddProduct;
    Slider npWPrice, npRPrice, npQuantity;
    ImageView imageView;
    int wPrice = 1, rPrice = 1, quantity = 1;
    private static final int pic_id = 123;
    String Storage_Path = "product_Images/";
    String Database_Path = "product";
    StorageReference storageReference;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    Uri FilePathUri;
    TextView tvWholeSalePrice, tvRetailPrice, tvQuantity;
    private String mPermission = Manifest.permission.CAMERA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.titlebar);
        etBarCode = findViewById(R.id.etBarCode);
        etProductName = findViewById(R.id.etProductName);
        npWPrice = findViewById(R.id.npWholesalePrice);
        npRPrice = findViewById(R.id.npRetailPrice);
        npQuantity = findViewById(R.id.npQuantity);
        imageView = findViewById(R.id.ivUploadImage);
        btnAddProduct = findViewById(R.id.btnAddProduct);
        tvWholeSalePrice = findViewById(R.id.tvWholesalePrice);
        tvRetailPrice = findViewById(R.id.tvRetailPrice);
        tvQuantity = findViewById(R.id.tvQuantity);

        npWPrice.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                Log.d("values",""+(int)value);
                wPrice = (int)value;
                tvWholeSalePrice.setText("Wholesale Price(£) : "+wPrice);
            }
        });
        npRPrice.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                Log.d("values",""+(int)value);
                rPrice = (int)value;
                tvRetailPrice.setText("Retail Price(£) : "+rPrice);
            }
        });
        npQuantity.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                Log.d("values",""+(int)value);
                quantity = (int)value;
                tvQuantity.setText("Quantity : "+quantity);
            }
        });
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        progressDialog = new ProgressDialog(AddProductActivity.this);

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etBarCode.getText().toString().toString().equalsIgnoreCase("")) {
                    Toast.makeText(AddProductActivity.this, "Enter Bar Code Number", Toast.LENGTH_SHORT).show();
                } else if (etBarCode.getText().toString().trim().length() < 12) {
                    Toast.makeText(AddProductActivity.this, "Enter Correct Bar Code Number", Toast.LENGTH_SHORT).show();
                } else if (etProductName.getText().toString().trim().equalsIgnoreCase("")) {
                    Toast.makeText(AddProductActivity.this, "Enter Product Name", Toast.LENGTH_SHORT).show();
                } else {
                    UploadImageFileToFirebaseStorage();
                }
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if (checkSelfPermission(mPermission) != PackageManager.PERMISSION_GRANTED)
                    {
                        //Permission not granted. request permission
                        ActivityCompat.requestPermissions(AddProductActivity.this, new String[]{mPermission}, pic_id);
                    } else
                    {
                        //Permission granted
                        dispatchTakePictureCamIntent();
                    }
                } else
                {
                    //API Level is below 23 -> runtime permission not required
                    dispatchTakePictureCamIntent();
                }
            }
        });

    }


    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    public void UploadImageFileToFirebaseStorage() {

        Log.d("pathuri", String.valueOf(FilePathUri));
        // Checking whether FilePathUri Is empty or not.
        if (FilePathUri != null) {

            // Setting progressDialog Title.
            progressDialog.setTitle("Data is Uploading...");

            // Showing progressDialog.
            progressDialog.show();

            // Creating second StorageReference.
            StorageReference storageReference2nd = storageReference.child(Storage_Path + System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));

            // Adding addOnSuccessListener to second StorageReference.
            storageReference2nd.putFile(FilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference2nd.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    progressDialog.dismiss();

                                    // Showing toast message after done uploading.
                                    Toast.makeText(getApplicationContext(), "Data Uploaded Successfully ", Toast.LENGTH_LONG).show();
                                    finish();
                                    @SuppressWarnings("VisibleForTests")
                                    ProductData ProductData = new ProductData(etProductName.getText().toString().trim()
                                            , etBarCode.getText().toString().trim(), uri.toString(), wPrice, rPrice, quantity);

                                    // Adding image upload id s child element into databaseReference.
                                    databaseReference.child(etBarCode.getText().toString().trim()).setValue(ProductData);
                                }
                            });

                            /*Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            // Hiding the progressDialog after done uploading.
                            progressDialog.dismiss();

                            // Showing toast message after done uploading.
                            Toast.makeText(getApplicationContext(), "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();

                            if(urlTask.isSuccessful()) {
                                @SuppressWarnings("VisibleForTests")
                                ProductData ProductData = new ProductData(etProductName.getText().toString().trim()
                                        , etBarCode.getText().toString().trim(), urlTask.getResult().toString(), wPrice, rPrice, quantity);

                                // Adding image upload id s child element into databaseReference.
                                databaseReference.child(etBarCode.getText().toString().trim()).setValue(ProductData);
                            }*/
                        }
                    })
                    // If something goes wrong .
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            // Hiding the progressDialog.
                            progressDialog.dismiss();

                            // Showing exception erro message.
                            Toast.makeText(AddProductActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })

                    // On progress change upload time.
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            // Setting progressDialog Title.
                            progressDialog.setTitle("Data is Uploading...");

                        }
                    });
        } else {

            Toast.makeText(AddProductActivity.this, "Please Select Image or Add Image Name", Toast.LENGTH_LONG).show();

        }
    }
    public void captureImage() {
        int camerspermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if (camerspermission == PackageManager.PERMISSION_GRANTED) {
            Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(camera_intent, pic_id);
        } else {
            Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
        }
    }
    private void dispatchTakePictureCamIntent()
    {
        //Create intent for the Camera
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
       // if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        //{
            // Create the File
            File photoFile = null;
            try
            {
                photoFile = createImageFile(this.getApplicationContext());
            } catch (IOException ex)
            {
                //Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null)
            {
                Uri photoUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, pic_id);
                FilePathUri = photoUri;
            }
        //}
    }
    public File createImageFile(Context context) throws IOException
    {
        //Create Image Name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmsss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //noinspection UnnecessaryLocalVariable
        File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);

        return imageFile;
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == pic_id && resultCode == RESULT_OK)
        {
            if (FilePathUri != null)
            {
                //Set Uri to ImageView
                imageView.setImageURI(FilePathUri);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == pic_id)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                //Permission granted
                dispatchTakePictureCamIntent();
            } else
            {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}