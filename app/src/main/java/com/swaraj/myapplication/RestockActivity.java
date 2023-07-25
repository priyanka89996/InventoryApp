package com.swaraj.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.swaraj.myapplication.adapter.RestockAdapter;
import com.swaraj.myapplication.data.ProductData;

import java.util.ArrayList;

public class RestockActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Button btnPlaceOrder;
    TextView tvNoData;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ArrayList<ProductData> productData;
    ArrayList<ProductData> temProductData;
    ArrayList<Integer> arrQuantity;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restock);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("product");
        progressDialog = new ProgressDialog(RestockActivity.this);
        recyclerView = findViewById(R.id.rvRestockList);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        tvNoData = findViewById(R.id.tvNoDate);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getData();

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setTitle("Placing Order...");
                progressDialog.show();
                for (int i = 0; i < productData.size(); i++) {

                    if (arrQuantity.get(i) != productData.get(i).getQuantity()) {
                        Log.d("updated order", "" + productData.get(i).getName());
                        databaseReference = FirebaseDatabase.getInstance().getReference("placedOrders");
                        ProductData ProductData = new ProductData(productData.get(i).getName(),productData.get(i).getBarCode(),
                                productData.get(i).getImagePath(),productData.get(i).getwPrice(),productData.get(i).getrPrice(),productData.get(i).getQuantity());

                        // Adding image upload id s child element into databaseReference.
                        databaseReference.child(productData.get(i).getBarCode()).setValue(ProductData);

                    } else {
                        Log.d("not order", "" + productData.get(i).getName());
                    }
                }
                Toast.makeText(RestockActivity.this, "Order Placed Successfully", Toast.LENGTH_SHORT).show();
                finish();
                progressDialog.dismiss();

            }
        });
    }

    public void getData() {
        productData = new ArrayList<>();
        arrQuantity = new ArrayList<>();
        progressDialog.setTitle("Getting Product Data...");
        progressDialog.show();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    //ProductData data = postSnapshot.getValue(ProductData.class);
                    Log.d("snapValue", "" + postSnapshot.child("name").getValue());
                    if (Integer.parseInt(postSnapshot.child("quantity").getValue().toString()) <= 5) {
                        productData.add(new ProductData(String.valueOf(postSnapshot.child("name").getValue()),
                                String.valueOf(postSnapshot.child("barCode").getValue()),
                                String.valueOf(postSnapshot.child("imagePath").getValue()),
                                Integer.parseInt(postSnapshot.child("wPrice").getValue().toString()),
                                Integer.parseInt(postSnapshot.child("rPrice").getValue().toString()),
                                Integer.parseInt(postSnapshot.child("quantity").getValue().toString())));
                        arrQuantity.add(Integer.parseInt(postSnapshot.child("quantity").getValue().toString()));
                    }

                }
                //temProductData.addAll(productData);
                if (productData.size() > 0 && productData != null) {
                    recyclerView.setAdapter(new RestockAdapter(RestockActivity.this, productData));
                    recyclerView.setVisibility(View.VISIBLE);
                    tvNoData.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    tvNoData.setVisibility(View.VISIBLE);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RestockActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
            }
        });
    }
}