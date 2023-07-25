package com.swaraj.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.swaraj.myapplication.adapter.EmployeeAdapter;
import com.swaraj.myapplication.data.ProductData;

import java.util.ArrayList;

public class EmployeeActivity extends AppCompatActivity {

    EditText etBarCode;
    TextView tvProductName, tvNoData, tvRetailPrice;
    Button btnTill, btnAddToCart, btnCheckout;
    RecyclerView recyclerView;
    LinearLayout llCartData, llDisplayItemData, llBottom;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference getEmployeeItems;
    DatabaseReference employeeItems;
    ArrayList<ProductData> productData;
    ArrayList<ProductData> cartProductData;
    ProgressDialog progressDialog;
    Intent intent;
    String userName = "employee";
    int totalItems=0,totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);
        intent = getIntent();
        if (intent != null) {
            userName = intent.getStringExtra("name");
        }
        cartProductData = new ArrayList<>();
        productData = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("product");
        employeeItems = FirebaseDatabase.getInstance().getReference(userName);
        getEmployeeItems = FirebaseDatabase.getInstance().getReference(userName);
        progressDialog = new ProgressDialog(EmployeeActivity.this);
        etBarCode = findViewById(R.id.etBarCode);
        etBarCode.requestFocus();
        tvProductName = findViewById(R.id.tvProductName);
        tvRetailPrice = findViewById(R.id.tvPrice);
        btnTill = findViewById(R.id.btnTill);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnCheckout = findViewById(R.id.btnCheckoutItems);
        recyclerView = findViewById(R.id.rvCartItem);
        tvNoData = findViewById(R.id.tvNoDate);
        llCartData = findViewById(R.id.llCartData);
        llDisplayItemData = findViewById(R.id.llSearchItemDisplay);
        llBottom = findViewById(R.id.llBottom);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btnTill.setVisibility(View.GONE);
        progressDialog.setTitle("Getting Cart Data...");
        progressDialog.show();

        getEmployeeItems.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productData.clear();
                if (snapshot.getValue() != null) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        for (DataSnapshot post : postSnapshot.getChildren()) {
                            ProductData data = post.getValue(ProductData.class);
                            productData.add(data);
                        }

                    }
                    if (productData != null && productData.size() > 0) {
                        tvNoData.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        cartProductData.addAll(productData);
                        recyclerView.setAdapter(new EmployeeAdapter(EmployeeActivity.this, cartProductData));
                        llDisplayItemData.setVisibility(View.GONE);
                        etBarCode.setFocusable(true);
                        etBarCode.requestFocus();
                        btnCheckout.setVisibility(View.VISIBLE);
                        llBottom.setVisibility(View.VISIBLE);
                        llCartData.setVisibility(View.VISIBLE);
                        btnTill.setVisibility(View.GONE);
                        progressDialog.dismiss();
                    }
                } else {
                    Log.d("notdata", "jaka");
                    btnTill.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        btnTill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnTill.setVisibility(View.GONE);
                llCartData.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                tvNoData.setVisibility(View.VISIBLE);
                btnCheckout.setVisibility(View.GONE);
                llBottom.setVisibility(View.GONE);
            }
        });
        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvNoData.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                cartProductData.addAll(productData);
                recyclerView.setAdapter(new EmployeeAdapter(EmployeeActivity.this, cartProductData));
                llDisplayItemData.setVisibility(View.GONE);
                etBarCode.setFocusable(true);
                etBarCode.requestFocus();
                etBarCode.setText("");
                btnCheckout.setVisibility(View.VISIBLE);
                llBottom.setVisibility(View.VISIBLE);
                employeeItems.child(productData.get(0).getBarCode()).setValue(productData);
            }
        });
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < cartProductData.size(); i++) {

                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("product").child(cartProductData.get(i).getBarCode());

                    int finalI = i;
                    databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            productData.clear();

                            productData.add(snapshot.getValue(ProductData.class));
                            if (productData != null) {
                                Log.d("snapdata1", "" + productData.get(0).getQuantity());
                                productData.get(0).setQuantity(productData.get(0).getQuantity() - 1);
                                int quantity = productData.get(0).getQuantity();
                                Log.d("snapdata2", "" + productData.get(0).getQuantity());
                                DatabaseReference updateQuantity = FirebaseDatabase.getInstance().getReference("product").child(productData.get(0).getBarCode()).child("quantity");
                                updateQuantity.setValue(quantity);
                                employeeItems.child(productData.get(0).getBarCode()).removeValue();
                                cartProductData.clear();
                                recyclerView.setAdapter(new EmployeeAdapter(EmployeeActivity.this, cartProductData));
                                tvNoData.setVisibility(View.VISIBLE);
                                llDisplayItemData.setVisibility(View.GONE);
                                etBarCode.setText("");
                                etBarCode.setFocusable(true);
                                etBarCode.requestFocus();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
        etBarCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(s.toString().equalsIgnoreCase("")){
                        llDisplayItemData.setVisibility(View.GONE);
                    }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etBarCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (etBarCode.getText().toString().trim().equalsIgnoreCase("") | etBarCode.getText().toString().trim().length() < 12) {
                        showToast("Enter correct bar code ");
                        //Toast.makeText(EmployeeActivity.this, "Enter correct bar code ", Toast.LENGTH_SHORT).show();
                    } else {
                        getData(etBarCode.getText().toString().trim());
                    }
                    return true;
                }
                return false;
            }
        });
    }


    public void getData(String barCode) {

        progressDialog.setTitle("Getting Product Data...");
        progressDialog.show();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productData.clear();
                boolean isFound = false;
                int quantity = 0;
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    //ProductData data = postSnapshot.getValue(ProductData.class);
                    Log.d("snapValue", "" + postSnapshot.child("name").getValue());
                    if (barCode.equalsIgnoreCase(String.valueOf(postSnapshot.child("barCode").getValue()))) {
                        isFound = true;
                        productData.add(new ProductData(String.valueOf(postSnapshot.child("name").getValue()),
                                String.valueOf(postSnapshot.child("barCode").getValue()),
                                String.valueOf(postSnapshot.child("imagePath").getValue()),
                                Integer.parseInt(postSnapshot.child("wPrice").getValue().toString()),
                                Integer.parseInt(postSnapshot.child("rPrice").getValue().toString()),
                                1));
                        tvProductName.setText("" + String.valueOf(postSnapshot.child("name").getValue()));
                        tvRetailPrice.setText("Price : " + String.valueOf(postSnapshot.child("rPrice").getValue()));

                        break;
                    } else {
                        isFound = false;
                    }

                }
                if (isFound == true) {
                    if(productData.get(0).getQuantity() > 0) {
                        llDisplayItemData.setVisibility(View.VISIBLE);
                    }else{
                        showToast("Stock is finished for this item");
                        //Toast.makeText(EmployeeActivity.this, "", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showToast("No Product found with this bar code");
                    //Toast.makeText(EmployeeActivity.this, "No Product found with this bar code", Toast.LENGTH_SHORT).show();
                }
                /*if (productData.size() > 0 && productData != null) {
                    recyclerView.setAdapter(new ProductAdapter(EmployeeActivity.this, productData));
                    recyclerView.setVisibility(View.VISIBLE);
                    tvNoData.setVisibility(View.GONE);

                } else {
                    recyclerView.setVisibility(View.GONE);
                    tvNoData.setVisibility(View.VISIBLE);
                }*/
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast("Error Occured");
            }
        });
    }
    public void showToast(String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(title)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}