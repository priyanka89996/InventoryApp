package com.swaraj.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.swaraj.myapplication.adapter.ReceivedAdapter;
import com.swaraj.myapplication.data.ProductData;

import java.util.ArrayList;

public class ReceivedActivity extends AppCompatActivity implements UpdateProductInterface {

    RecyclerView recyclerView;
    TextView tvNoDate;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, updateRecordRef;
    ArrayList<ProductData> productData;
    UpdateProductInterface productInterface;
    ReceivedAdapter adapter;
    Dialog loader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.titlebar);
        adapter = new ReceivedAdapter(ReceivedActivity.this);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("RestockOrders");
        updateRecordRef = firebaseDatabase.getReference("product");
        recyclerView = findViewById(R.id.rvReceivedOrder);
        tvNoDate = findViewById(R.id.tvNoDate);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productInterface = new UpdateProductInterface() {
            @Override
            public void onClick(ProductData product, int position) {
                showLoader("Getting Order Data...");

                Log.d("updateStock", "" + product.getQuantity());
                updateRecordRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            //ProductData data = postSnapshot.getValue(ProductData.class);
                            Log.d("snapValue", "" + postSnapshot.child("name").getValue());
                            if (String.valueOf(postSnapshot.child("barCode").getValue()).equalsIgnoreCase(product.getBarCode())) {
                                int quantity = product.getQuantity() + Integer.parseInt(postSnapshot.child("quantity").getValue().toString());
                                Log.d("quantity",""+quantity);
                                product.setQuantity(quantity);
                                updateRecordRef.child(product.getBarCode()).setValue(product);
                                break;
                            }
                        }
                        loader.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        loader.dismiss();
                    }
                });

                databaseReference.child(product.getBarCode()).removeValue();
                productData.remove(position);
                if (productData.size() > 0 && productData != null) {
                    recyclerView.setAdapter(new ReceivedAdapter(ReceivedActivity.this, productData, productInterface));
                    recyclerView.setVisibility(View.VISIBLE);
                    tvNoDate.setVisibility(View.GONE);

                } else {
                    recyclerView.setVisibility(View.GONE);
                    tvNoDate.setVisibility(View.VISIBLE);
                    finish();
                }
            }
        };
        getData();

    }

    public void getData() {
        productData = new ArrayList<>();
        showLoader("Getting Order Data...");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productData.clear();
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
                }
                if (productData.size() > 0 && productData != null) {
                    recyclerView.setAdapter(new ReceivedAdapter(ReceivedActivity.this, productData, productInterface));
                    recyclerView.setVisibility(View.VISIBLE);
                    tvNoDate.setVisibility(View.GONE);

                } else {
                    recyclerView.setVisibility(View.GONE);
                    tvNoDate.setVisibility(View.VISIBLE);
                }
                loader.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReceivedActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateStock() {

    }

    @Override
    public void onClick(ProductData productData, int po) {
        Log.d("received", "" + productData.getName());
    }
    public void showLoader(String title) {
        loader = new Dialog(ReceivedActivity.this);

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