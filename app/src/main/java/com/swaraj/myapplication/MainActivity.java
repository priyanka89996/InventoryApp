package com.swaraj.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.swaraj.myapplication.adapter.ProductAdapter;
import com.swaraj.myapplication.data.ProductData;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Button btnRestock, btnReceivedStock;
    FloatingActionButton btnAdd;
    TextView tvTotalStock, tvNoDate;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ArrayList<ProductData> productData;
    int totalStockValue = 0;
    ImageView ivAudio;
    DialogBoxPopup dialog;
    Dialog loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.titlebar);
        dialog = new DialogBoxPopup();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("product");
        recyclerView = findViewById(R.id.rvProductList);
        btnRestock = findViewById(R.id.btnRestock);
        btnReceivedStock = findViewById(R.id.btnReceivedStock);
        btnAdd = findViewById(R.id.btnAdd);
        tvTotalStock = findViewById(R.id.tvTotalValue);
        tvNoDate = findViewById(R.id.tvNoDate);
        ivAudio = findViewById(R.id.ivAudio);
        ivAudio.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getData();

        ivAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RecordAudioActivity.class));
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddProductActivity.class));
            }
        });
        btnRestock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RestockActivity.class));
            }
        });
        btnReceivedStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ReceivedActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void getData() {
        productData = new ArrayList<>();
        showLoader("Getting Product Data...");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productData.clear();
                totalStockValue = 0;
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    //ProductData data = postSnapshot.getValue(ProductData.class);
                    Log.d("snapValue", "" + postSnapshot.child("name").getValue());
                    productData.add(new ProductData(String.valueOf(postSnapshot.child("name").getValue()),
                            String.valueOf(postSnapshot.child("barCode").getValue()),
                            String.valueOf(postSnapshot.child("imagePath").getValue()),
                            Integer.parseInt(postSnapshot.child("wPrice").getValue().toString()),
                            Integer.parseInt(postSnapshot.child("rPrice").getValue().toString()),
                            Integer.parseInt(postSnapshot.child("quantity").getValue().toString()),
                            String.valueOf(postSnapshot.child("videoPath").getValue())));
                    totalStockValue += Integer.parseInt(postSnapshot.child("quantity").getValue().toString()) *
                            Integer.parseInt(postSnapshot.child("wPrice").getValue().toString());
                }

                if (productData.size() > 0 && productData != null) {
                    recyclerView.setAdapter(new ProductAdapter(MainActivity.this, productData));
                    recyclerView.setVisibility(View.VISIBLE);
                    tvNoDate.setVisibility(View.GONE);
                    tvTotalStock.setVisibility(View.VISIBLE);
                    tvTotalStock.setText("Total Stock Value \n" + totalStockValue + "£");
                } else {
                    recyclerView.setVisibility(View.GONE);
                    tvNoDate.setVisibility(View.VISIBLE);
                    tvTotalStock.setVisibility(View.GONE);
                }

                loader.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showLoader(String title) {
        loader = new Dialog(MainActivity.this);

        loader.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loader.setContentView(R.layout.custom_loader_dialog);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(loader.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        TextView tvMessage = (TextView) loader.findViewById(R.id.tvMessage);
        tvMessage.setText("" + title);
        loader.getWindow().setAttributes(lp);
        loader.setCancelable(false);
        loader.setCanceledOnTouchOutside(false);
        loader.show();

    }
}