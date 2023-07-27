package com.swaraj.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.HashMap;
import java.util.Locale;

public class AddProductActivity extends AppCompatActivity {

    EditText etBarCode, etProductName;
    Button btnAddProduct;
    Slider npWPrice, npRPrice, npQuantity;
    ImageView imageView, ivUploadVideo;
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
    ProgressDialog videoProgress;
    Uri videoUri;
    String barCode = "";
    HashMap<String, String> map;
    String uploadedVideoUrl = "";
    DialogBoxPopup dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.titlebar);
        dialog = new DialogBoxPopup();
        etBarCode = findViewById(R.id.etBarCode);
        etProductName = findViewById(R.id.etProductName);
        npWPrice = findViewById(R.id.npWholesalePrice);
        npRPrice = findViewById(R.id.npRetailPrice);
        npQuantity = findViewById(R.id.npQuantity);
        imageView = findViewById(R.id.ivUploadImage);
        ivUploadVideo = findViewById(R.id.ivUploadVideo);
        btnAddProduct = findViewById(R.id.btnAddProduct);
        tvWholeSalePrice = findViewById(R.id.tvWholesalePrice);
        tvRetailPrice = findViewById(R.id.tvRetailPrice);
        tvQuantity = findViewById(R.id.tvQuantity);

        npWPrice.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                Log.d("values", "" + (int) value);
                wPrice = (int) value;
                tvWholeSalePrice.setText("Wholesale Price(£) : " + wPrice);
            }
        });
        npRPrice.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                Log.d("values", "" + (int) value);
                rPrice = (int) value;
                tvRetailPrice.setText("Retail Price(£) : " + rPrice);
            }
        });
        npQuantity.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                Log.d("values", "" + (int) value);
                quantity = (int) value;
                tvQuantity.setText("Quantity : " + quantity);
            }
        });
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        progressDialog = new ProgressDialog(AddProductActivity.this);

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etBarCode.getText().toString().toString().equalsIgnoreCase("")) {
                    dialog.showToast("Enter Bar Code Number", AddProductActivity.this);
                    //Toast.makeText(AddProductActivity.this, "Enter Bar Code Number", Toast.LENGTH_SHORT).show();
                } else if (etBarCode.getText().toString().trim().length() < 12) {
                    dialog.showToast("Enter Correct Bar Code Number", AddProductActivity.this);
                    //Toast.makeText(AddProductActivity.this, "Enter Correct Bar Code Number", Toast.LENGTH_SHORT).show();
                } else if (etProductName.getText().toString().trim().equalsIgnoreCase("")) {
                    dialog.showToast("Enter Product Name", AddProductActivity.this);
                    //Toast.makeText(AddProductActivity.this, "Enter Product Name", Toast.LENGTH_SHORT).show();
                } else {
                    barCode = etBarCode.getText().toString().trim();
                    UploadImageFileToFirebaseStorage();
                }
            }
        });
        ivUploadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoProgress = new ProgressDialog(AddProductActivity.this);
                chooseImageOptions();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(mPermission) != PackageManager.PERMISSION_GRANTED) {
                        //Permission not granted. request permission
                        ActivityCompat.requestPermissions(AddProductActivity.this, new String[]{mPermission}, pic_id);
                    } else {
                        //Permission granted
                        dispatchTakePictureCamIntent();
                    }
                } else {
                    //API Level is below 23 -> runtime permission not required
                    dispatchTakePictureCamIntent();
                }
            }
        });

    }

    private void chooseFromGallery() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 5);
    }

    private void chooseCamera() {
        Intent i = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        // on below line starting an activity for result.
        startActivityForResult(i, 555);
    }

    public String GetFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    private String getfiletype(Uri videouri) {
        ContentResolver r = getContentResolver();
        // get the file type ,in this case its mp4
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(r.getType(videouri));
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
                                            , etBarCode.getText().toString().trim(), uri.toString(), wPrice, rPrice, quantity, uploadedVideoUrl);

                                    // Adding image upload id s child element into databaseReference.
                                    databaseReference.child(etBarCode.getText().toString().trim()).setValue(ProductData);
                                }
                            });

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
            dialog.showToast("Please Select Image or Add Image Name",AddProductActivity.this);
            //Toast.makeText(AddProductActivity.this, "Please Select Image or Add Image Name", Toast.LENGTH_LONG).show();

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

    private void dispatchTakePictureCamIntent() {
        //Create intent for the Camera
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        //{
        // Create the File
        File photoFile = null;
        try {
            photoFile = createImageFile(this.getApplicationContext());
        } catch (IOException ex) {
            //Error occurred while creating the File
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Uri photoUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(takePictureIntent, pic_id);
            FilePathUri = photoUri;
        }
        //}
    }

    public File createImageFile(Context context) throws IOException {
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
        if (requestCode == pic_id && resultCode == RESULT_OK) {
            if (FilePathUri != null) {
                //Set Uri to ImageView
                imageView.setImageURI(FilePathUri);
            }
        } else if (requestCode == 5) {
            videoUri = data.getData();
            videoProgress.setTitle("Uploading...");
            videoProgress.show();
            uploadvideo();
        }else if (requestCode == 555) {
            videoUri = data.getData();
            videoProgress.setTitle("Uploading...");
            videoProgress.show();
            uploadvideo();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == pic_id) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permission granted
                dispatchTakePictureCamIntent();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void chooseImageOptions() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.choose_camera_popup);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        TextView tvGallery = dialog.findViewById(R.id.tvGallery);
        TextView tvCamera = dialog.findViewById(R.id.tvCamera);
        AppCompatButton btnCancel = dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tvGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFromGallery();
                dialog.dismiss();
            }
        });

        tvCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseCamera();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void uploadvideo() {
        if (videoUri != null) {
            // save the selected video in Firebase storage
            final StorageReference reference = FirebaseStorage.getInstance().getReference("Files/" + System.currentTimeMillis() + "." + getfiletype(videoUri));
            reference.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful()) ;
                    // get the link of video
                    String downloadUri = uriTask.getResult().toString();
                    //DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Video");
                    map = new HashMap<>();
                    map.put("videolink", downloadUri);
                    uploadedVideoUrl = downloadUri;
                    //reference1.child("" + System.currentTimeMillis()).setValue(map);
                    //databaseReference.child(barCode).child("videoPath").setValue(map);
                    // Video uploaded successfully
                    // Dismiss dialog
                    videoProgress.dismiss();
                    Toast.makeText(AddProductActivity.this, "Video Uploaded!!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Error, Image not uploaded
                    videoProgress.dismiss();
                    Toast.makeText(AddProductActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                // Progress Listener for loading
                // percentage on the dialog box
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    // show the progress bar
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    videoProgress.setMessage("Uploaded " + (int) progress + "%");
                }
            });
        }
    }
}