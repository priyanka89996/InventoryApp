package com.swaraj.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.swaraj.myapplication.adapter.EmployeeAdapter;
import com.swaraj.myapplication.adapter.OrderSummeryAdapter;
import com.swaraj.myapplication.data.Discounts;
import com.swaraj.myapplication.data.OrderData;
import com.swaraj.myapplication.data.ProductData;
import com.swaraj.myapplication.data.User;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmployeeActivity extends AppCompatActivity {

    AutoCompleteTextView etBarCode;
    TextView tvProductName, tvNoData, tvRetailPrice, tvTotalItems, tvTotalAmount;
    Button btnTill, btnAddToCart, btnCheckout, btnLogout;
    RecyclerView recyclerView;
    LinearLayout llCartData, llDisplayItemData, llBottom, llNoData, llAddToCart;
    ConstraintLayout llTill;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference getEmployeeItems;
    DatabaseReference employeeItems;
    ArrayList<ProductData> productData, changedProductQuantity;
    ArrayList<ProductData> cartProductData;
    ArrayList<Discounts> DiscountData= new ArrayList<>();
    ProgressDialog progressDialog;
    Intent intent;
    String userName = "employee";
    int totalItems = 0, totalAmount = 0;
    boolean threadTrigger = false;

    Dialog loader;
    ArrayAdapter adapter;
    ArrayList<String> barCodes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.titlebar);
        intent = getIntent();
        if (intent != null) {
            userName = intent.getStringExtra("name");
        }

        cartProductData = new ArrayList<>();
        productData = new ArrayList<>();
        changedProductQuantity = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("product");
        employeeItems = FirebaseDatabase.getInstance().getReference(userName);
        getEmployeeItems = FirebaseDatabase.getInstance().getReference(userName);
        progressDialog = new ProgressDialog(EmployeeActivity.this);
        etBarCode = findViewById(R.id.etBarCode);

        tvProductName = findViewById(R.id.tvProductName);
        tvRetailPrice = findViewById(R.id.tvPrice);
        btnTill = findViewById(R.id.btnTill);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnCheckout = findViewById(R.id.btnCheckoutItems);
        llTill = findViewById(R.id.llTill);
        llNoData = findViewById(R.id.llNoData);
        recyclerView = findViewById(R.id.rvCartItem);
        tvNoData = findViewById(R.id.tvNoDate);
        llCartData = findViewById(R.id.llCartData);
        llDisplayItemData = findViewById(R.id.llSearchItemDisplay);
        llAddToCart = findViewById(R.id.llAddtoCart);
        llBottom = findViewById(R.id.llBottom);
        tvTotalItems = findViewById(R.id.tvTotalItems);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btnTill.setVisibility(View.GONE);
        llTill.setVisibility(View.GONE);
        btnLogout = findViewById(R.id.BtnLogout);
        btnLogout.setVisibility(View.VISIBLE);
        showLoader("Getting Cart Data...");
        //progressDialog.setTitle("Getting Cart Data...");
        //progressDialog.show();

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
                        llNoData.setVisibility(View.GONE);
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
                        llTill.setVisibility(View.GONE);
                        loader.dismiss();
                        //progressDialog.dismiss();
                        totalItems = 0;
                        totalAmount = 0;
                        for (int i = 0; i < cartProductData.size(); i++) {
                            totalItems = cartProductData.size();
                            totalAmount += cartProductData.get(i).getrPrice();
                        }
                        tvTotalItems.setText("Total Item : " + totalItems);
                        tvTotalAmount.setText("Total Amount : " + totalAmount + "£");
                    }
                } else {
                    Log.d("notdata", "jaka");
                    btnTill.setVisibility(View.VISIBLE);
                    llTill.setVisibility(View.VISIBLE);
                    loader.dismiss();
                    ///progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loader.dismiss();
            }
        });
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loader.dismiss();
                showLoader("Please Wait");
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    //ProductData data = postSnapshot.getValue(ProductData.class);
                    Log.d("snapValue", "" + postSnapshot.child("name").getValue());
                    barCodes.add(String.valueOf(postSnapshot.child("barCode").getValue()));
                }
                //String barcode[] = barCodes.toArray(new String[barCodes.size()]);
                adapter = new ArrayAdapter(EmployeeActivity.this,android.R.layout.simple_list_item_1,barCodes.toArray());
                etBarCode.setAdapter(adapter);
                loader.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loader.dismiss();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });

        btnTill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnTill.setVisibility(View.GONE);
                llTill.setVisibility(View.GONE);
                llCartData.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                tvNoData.setVisibility(View.VISIBLE);
                llNoData.setVisibility(View.VISIBLE);
                btnCheckout.setVisibility(View.GONE);
                llBottom.setVisibility(View.GONE);
                etBarCode.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etBarCode, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvNoData.setVisibility(View.GONE);
                llNoData.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                cartProductData.addAll(productData);
                String barCode = productData.get(0).getBarCode();
                cartProductData.remove(cartProductData.size() - 1);
                boolean checkExistRecord = false;
                int changedItemValue = 0;
                for (int i = 0; i < cartProductData.size(); i++) {
                    if (cartProductData.get(i).getBarCode().equalsIgnoreCase(barCode)) {
                        //cartProductData.remove(cartProductData.size()-1);
                        cartProductData.get(i).setQuantity(cartProductData.get(i).getQuantity() + 1);
                        changedItemValue = i;
                        checkExistRecord = true;
                        break;
                    } else {
                        //checkExistRecord = false;
                    }
                }
                if (!checkExistRecord) {
                    cartProductData.addAll(productData);
                    employeeItems.child(productData.get(0).getBarCode()).setValue(productData);
                } else {
                    Log.d("changedItemValue", "" + changedItemValue);
                    changedProductQuantity.clear();
                    changedProductQuantity.add(cartProductData.get(changedItemValue));
                    employeeItems.child(productData.get(0).getBarCode()).setValue(changedProductQuantity);
                }
                recyclerView.setAdapter(new EmployeeAdapter(EmployeeActivity.this, cartProductData));
                llDisplayItemData.setVisibility(View.GONE);
                etBarCode.setFocusable(true);
                etBarCode.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etBarCode, InputMethodManager.SHOW_IMPLICIT);
                etBarCode.setText("");
                btnCheckout.setVisibility(View.VISIBLE);
                llBottom.setVisibility(View.VISIBLE);
                //employeeItems.child(productData.get(0).getBarCode()).setValue(productData);
                totalItems = 0;
                totalAmount = 0;
                int dis = 0;

                for (int i = 0; i < cartProductData.size(); i++) {
                    totalItems = cartProductData.size();
                    if(cartProductData.get(i).getName().equalsIgnoreCase("percentage")){
                        dis += 1;
                    }else{
                        totalAmount += cartProductData.get(i).getrPrice() * cartProductData.get(i).getQuantity();
                    }

                }
                for (int i = 0; i < cartProductData.size(); i++) {
                    totalItems = cartProductData.size();
                    if(cartProductData.get(i).getName().equalsIgnoreCase("percentage")){
                        double temp = totalAmount * (cartProductData.get(i).getrPrice()/100.0);
                        double temp2 = totalAmount - temp;
                        totalAmount = (int) temp2;

                    }else{
                        dis -=1;
                    }

                }


                tvTotalItems.setText("Total Item : " + totalItems);
                tvTotalAmount.setText("Total Amount : " + totalAmount + "£");
            }
        });
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                orderSummery();
            }
        });

        etBarCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equalsIgnoreCase("")) {
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
                    String s = etBarCode.getText().toString();
                    if (etBarCode.getText().toString().trim().equalsIgnoreCase("") | etBarCode.getText().toString().trim().length() < 12) {
                        showToast("Enter correct bar code ");
                        //Toast.makeText(EmployeeActivity.this, "Enter correct bar code ", Toast.LENGTH_SHORT).show();
                    } else if (CheckDiscountCode(s)!= null) {
                        getDiscount(s);
                        return false;
                    } else {
                        getData(etBarCode.getText().toString().trim());
                    }
                    return true;
                }
                return false;
            }
        });
    }

    public void checkOutData() {
        for (int i = 0; i < cartProductData.size(); i++) {

            if(cartProductData.get(i).getName().equalsIgnoreCase("percentage") || cartProductData.get(i).getName().equalsIgnoreCase("Money Off")){
                continue;
            }


            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("product").child(cartProductData.get(i).getBarCode());

            int cartQuantity = cartProductData.get(i).getQuantity();
            databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    productData.clear();

                    productData.add(snapshot.getValue(ProductData.class));
                    if (productData != null) {

                        //Log.d("snapdata1", "" + productData.get(0).getQuantity());
                        productData.get(0).setQuantity(productData.get(0).getQuantity() - cartQuantity);
                        int quantity = productData.get(0).getQuantity();
                        //Log.d("snapdata2", "" + productData.get(0).getQuantity());
                        DatabaseReference updateQuantity = FirebaseDatabase.getInstance().getReference("product").child(productData.get(0).getBarCode()).child("quantity");
                        updateQuantity.setValue(quantity);
                        employeeItems.child(productData.get(0).getBarCode()).removeValue();
                        cartProductData.clear();
                        recyclerView.setAdapter(new EmployeeAdapter(EmployeeActivity.this, cartProductData));
                        tvNoData.setVisibility(View.VISIBLE);
                        llNoData.setVisibility(View.VISIBLE);
                        llAddToCart.setVisibility(View.GONE);
                        llDisplayItemData.setVisibility(View.GONE);
                        threadTrigger = true;
                        llBottom.setVisibility(View.GONE);
                        totalAmount = 0;
                        totalItems = 0;
                        etBarCode.setText("");
                        etBarCode.setFocusable(true);
                        etBarCode.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(etBarCode, InputMethodManager.SHOW_IMPLICIT);
                        Toast.makeText(EmployeeActivity.this, "Order Successfully", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void getData(String barCode) {

        showLoader("Getting Product Data...");
        //progressDialog.setTitle("Getting Product Data...");
        //progressDialog.show();

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
                                1,
                                ""));
                        tvProductName.setText("" + String.valueOf(postSnapshot.child("name").getValue()));
                        tvRetailPrice.setText("Price : " + String.valueOf(postSnapshot.child("rPrice").getValue()));

                        break;
                    } else {
                        isFound = false;
                    }

                }if (isFound == true) {
                    if (productData.get(0).getQuantity() > 0) {
                        llDisplayItemData.setVisibility(View.VISIBLE);
                        llCartData.setVisibility(View.VISIBLE);
                        llAddToCart.setVisibility(View.VISIBLE);
                        if (etBarCode.getText().toString().trim().equalsIgnoreCase("")) {
                            llDisplayItemData.setVisibility(View.GONE);
                            llAddToCart.setVisibility(View.GONE);
                            threadTrigger = false;
                        }
                    } else {
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
                //progressDialog.dismiss();
                loader.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast("Error Occured");
            }
        });
    }


    public void showToast(String title) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_box);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        // dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,
        // LayoutParams.WRAP_CONTENT);
        TextView tvTitle = (TextView) dialog.findViewById(R.id.tvTitleAlert);
        tvTitle.setText(title);
        Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                etBarCode.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etBarCode, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        dialog.show();
    }

    public void orderSummery() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.product_summery);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        int quantity = 0, price = 0;
        TextView tvTotalItem = (TextView) dialog.findViewById(R.id.tvTotalItems);
        TextView tvTotalAmount = (TextView) dialog.findViewById(R.id.tvTotalAmount);
        TextView tvTotalQuantity = (TextView) dialog.findViewById(R.id.tvTotalQuantity);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        Button btnConfirm = (Button) dialog.findViewById(R.id.btnConfirm);
        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.rvProductSummery);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new OrderSummeryAdapter(this, cartProductData));
        tvTotalItem.setText("Total Items : " + cartProductData.size());
        int dis = 0;
        for (int i = 0; i < cartProductData.size(); i++) {
            quantity += cartProductData.get(i).getQuantity();
            if(cartProductData.get(i).getName().equalsIgnoreCase("percentage")){
                dis += 1;
            }else{
                price += cartProductData.get(i).getrPrice() * cartProductData.get(i).getQuantity();
            }
        }
        for (int i = 0; i < cartProductData.size(); i++) {
            if(cartProductData.get(i).getName().equalsIgnoreCase("percentage")){
                double temp = price * (cartProductData.get(i).getrPrice()/100.0);
                double temp2 = price- temp;
                price = (int) temp2;

            }else{
                dis -=1;
            }

        }
        tvTotalQuantity.setText("Total Quantity : " + quantity);
        tvTotalAmount.setText("Total Amount : " + price);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordData();
                checkOutData();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void recordData(){
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("placedOrders");
        int quantity = 0;
        int price = 0;
        int dis = 0;
        List<OrderData> basketItems = new ArrayList<>();
        for (int i = 0; i < cartProductData.size(); i++) {
            quantity += cartProductData.get(i).getQuantity();
            if(cartProductData.get(i).getName().equalsIgnoreCase("percentage")){
                dis += 1;
            }else{
                price += cartProductData.get(i).getrPrice() * cartProductData.get(i).getQuantity();
            }

            basketItems.add(new OrderData( cartProductData.get(i).getName(), cartProductData.get(i).getBarCode(), cartProductData.get(i).getrPrice(),
                    cartProductData.get(i).getQuantity()));

        }
        for (int i = 0; i < cartProductData.size(); i++) {
            totalItems = cartProductData.size();
            if(cartProductData.get(i).getName().equalsIgnoreCase("percentage")) {
                double temp = price * (cartProductData.get(i).getrPrice() / 100.0);
                double temp2 = price - temp;
                price = (int) temp2;
            }else{
                dis -=1;
            }

        }


        Map<String, Object> basketMap = new HashMap<>();
        for (int i = 0; i < basketItems.size(); i++) {
            OrderData item = basketItems.get(i);
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("Name", item.getName());
            itemMap.put("Barcode", item.getBarCode());
            itemMap.put("Price", item.getrPrice());
            itemMap.put("Quantity", item.getQuantity());
            basketMap.put("Item " + (i + 1), itemMap);
        }
        basketMap.put("TotalCost", price);
        basketMap.put("TotalQuantity", quantity);

        databaseReference2.push().setValue(basketMap);

    }

    public void showLoader(String title) {
        loader = new Dialog(EmployeeActivity.this);

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

    private String CheckDiscountCode(String text) {
        // Define the regex pattern for the date format "dd/MM/yyyy"
        String regPat = "\\b[A-Za-z][A-Za-z0-9]{11}\\b";

        // Create a Pattern object
        Pattern pat = Pattern.compile(regPat);

        // Create a Matcher object and find the date in the text
        Matcher matcher = pat.matcher(text);

        if (matcher.find()) {
            // Return the matched date
            return matcher.group();
        } else {
            // Return null if no date is found
            return null;
        }
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    public void getDiscount(String barCode) {


        firebaseDatabase.getReference("Discounts")
                .child(barCode)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productData.clear();
                        boolean isFound = false;
                        int quantity = 0;
                        Discounts d = snapshot.getValue(Discounts.class);
                        if (d != null) {
                            isFound = true;
                            tvProductName.setText(d.Type);


                            Log.d("changedItemValue", "["+d.Type+"]");

                            if(d.Type.equalsIgnoreCase("percentage")){
                                tvRetailPrice.setText(d.Data + "% off");
                                productData.add(new ProductData(d.Type,barCode,"https://firebasestorage.googleapis.com/v0/b/mystockapp-2e88d.appspot.com/o/product_Images%2Fpng-clipart-red-and-white-special-discount-icon-special-discount-sign-miscellaneous-discount-signs-thumbnail.png?alt=media&token=30f8af3a-e2d0-4efc-bdfe-1b2613be2106",0,
                                        Integer.parseInt(d.Data),1," "));

                            } else if (d.Type.equalsIgnoreCase( "Buy one Get One")){
                                tvRetailPrice.setText("buy "+d.Data +" Get "+d.ExtraData+ " off");
                                productData.add(new ProductData(d.Type,barCode,"https://firebasestorage.googleapis.com/v0/b/mystockapp-2e88d.appspot.com/o/product_Images%2Fpng-clipart-red-and-white-special-discount-icon-special-discount-sign-miscellaneous-discount-signs-thumbnail.png?alt=media&token=30f8af3a-e2d0-4efc-bdfe-1b2613be2106",Integer.parseInt(d.ExtraData),
                                        Integer.parseInt(d.Data),1," "));

                            }else{
                                tvRetailPrice.setText("£" + d.Data + " off");
                                productData.add(new ProductData(d.Type,barCode,"https://firebasestorage.googleapis.com/v0/b/mystockapp-2e88d.appspot.com/o/product_Images%2Fpng-clipart-red-and-white-special-discount-icon-special-discount-sign-miscellaneous-discount-signs-thumbnail.png?alt=media&token=30f8af3a-e2d0-4efc-bdfe-1b2613be2106",0,
                                        (0-Integer.parseInt(d.Data)),1," "));

                            }


                        }else {
                            isFound = false;
                            showToast("Discount not found");
                        }

                        if (isFound == true) {
                            if (productData.get(0).getQuantity() > 0) {
                                llDisplayItemData.setVisibility(View.VISIBLE);
                                llCartData.setVisibility(View.VISIBLE);
                                llAddToCart.setVisibility(View.VISIBLE);
                                if (etBarCode.getText().toString().trim().equalsIgnoreCase("")) {
                                    llDisplayItemData.setVisibility(View.GONE);
                                    llAddToCart.setVisibility(View.GONE);
                                    threadTrigger = false;
                                }
                            } else {
                                showToast("Stock is finished for this item");
                                //Toast.makeText(EmployeeActivity.this, "", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            showToast("No Product found with this bar code");
                            //Toast.makeText(EmployeeActivity.this, "No Product found with this bar code", Toast.LENGTH_SHORT).show();
                        }
                   }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }
}