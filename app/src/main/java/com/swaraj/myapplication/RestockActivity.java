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
    DialogBoxPopup dialog;
    Dialog loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restock);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.titlebar);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("product");

        recyclerView = findViewById(R.id.rvRestockList);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        tvNoData = findViewById(R.id.tvNoDate);
        dialog = new DialogBoxPopup();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getData();

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoader("Placing Order...");

                for (int i = 0; i < productData.size(); i++) {

                    if (arrQuantity.get(i) != productData.get(i).getQuantity()) {
                        Log.d("updated order", "" + productData.get(i).getName());
                        databaseReference = FirebaseDatabase.getInstance().getReference("RestockOrders");
                        ProductData ProductData = new ProductData(productData.get(i).getName(), productData.get(i).getBarCode(),
                                productData.get(i).getImagePath(), productData.get(i).getwPrice(), productData.get(i).getrPrice(), productData.get(i).getQuantity(), productData.get(i).getVideoPath());

                        // Adding image upload id s child element into databaseReference.
                        databaseReference.child(productData.get(i).getBarCode()).setValue(ProductData);

                    } else {
                        Log.d("not order", "" + productData.get(i).getName());
                    }
                }
                loader.dismiss();
                Toast.makeText(RestockActivity.this, "Order Placed Successfully", Toast.LENGTH_SHORT).show();
                finish();

            }
        });
    }

    public void getData() {
        productData = new ArrayList<>();
        arrQuantity = new ArrayList<>();
        showLoader("Getting Product Data...");

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
                                Integer.parseInt(postSnapshot.child("quantity").getValue().toString()),
                                String.valueOf(postSnapshot.child("videoPath").getValue())));
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
                loader.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RestockActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showLoader(String title) {
        loader = new Dialog(RestockActivity.this);

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