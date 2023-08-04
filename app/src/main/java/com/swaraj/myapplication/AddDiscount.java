package com.swaraj.myapplication;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.swaraj.myapplication.data.Discounts;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.Calendar;

public class AddDiscount extends AppCompatActivity {

    EditText DisBarCode, Percentage,BuyQuantity,FreeQuantity,Amount,ProductBarcode, StartDate, EndDate;
    int option;

    LinearLayout LinearLayout;

    Button btnAddDis;
    RadioGroup radioGroup;


    DialogBoxPopup dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_discount);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.titlebar);
        dialog = new DialogBoxPopup();

        DisBarCode = findViewById(R.id.etDisCode);
        radioGroup = findViewById(R.id.radioGroup);
        Percentage = findViewById(R.id.Percentage);
        BuyQuantity= findViewById(R.id.BuyQuantity);
        FreeQuantity= findViewById(R.id.FreeQuantity);
        LinearLayout = findViewById(R.id.LlQuantitys);
        Amount= findViewById(R.id.Amount);
        ProductBarcode= findViewById(R.id.ProductBarcode);
        StartDate= findViewById(R.id.StartDate);
        EndDate= findViewById(R.id.EndDate);
        btnAddDis= findViewById(R.id.btnAddDis);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Check which radio button was selected
                switch (checkedId) {
                    case R.id.RBpercentage:
                        Percentage.setVisibility(View.VISIBLE);

                        LinearLayout.setVisibility(View.GONE);
                        ProductBarcode.setVisibility(View.GONE);
                        Amount.setVisibility(View.GONE);

                        option = 0;
                        break;
                    case R.id.RBbogo:
                        LinearLayout.setVisibility(View.VISIBLE);
                        ProductBarcode.setVisibility(View.VISIBLE);

                        Percentage.setVisibility(View.GONE);
                        Amount.setVisibility(View.GONE);

                        option = 1;
                        break;
                    case R.id.RBm:
                        Amount.setVisibility(View.VISIBLE);


                        ProductBarcode.setVisibility(View.GONE);
                        Percentage.setVisibility(View.GONE);
                        LinearLayout.setVisibility(View.GONE);

                        option =2;

                }
            }
        });

        btnAddDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddDis();
            }
        });

        StartDate.setOnTouchListener((v, event) -> {
            if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                showDatePickerDialog(StartDate);
            }
            return false;
        });
        EndDate.setOnTouchListener((v, event) -> {
            if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                showDatePickerDialog(EndDate);
            }
            return false;
        });


    }


    private void showDatePickerDialog(EditText date) {
        // Get the current date as default
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a DatePickerDialog and set the initial date to the current date
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                AddDiscount.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Handle the selected date here
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    date.setText(selectedDate);
                },
                year, month, day
        );

        // Show the date picker dialog
        datePickerDialog.show();
    }


    private String extractDate(String text) {
        // Define the regex pattern for the date format "dd/MM/yyyy"
        String regPat = "\\b\\d{1,2}/\\d{1,2}/\\d{4}\\b";

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

    public  void AddDis(){
        String dbc= DisBarCode.getText().toString();
        String p=Percentage.getText().toString();
        String bq = BuyQuantity.getText().toString();
        String fq = FreeQuantity.getText().toString();
        String a = Amount.getText().toString();
        String pb = ProductBarcode.getText().toString();

        String sd =StartDate.getText().toString();
        String ed = EndDate.getText().toString();


        if (dbc.isEmpty()){
            dialog.showToast("Enter Bar Code Number", AddDiscount.this);
            return;
        } else if (sd.isEmpty()) {
            dialog.showToast("Enter a start Date", AddDiscount.this);
            return;

        } else if (ed.isEmpty()) {
            dialog.showToast("Enter a End Date", AddDiscount.this);
            return;
        } else if (extractDate(sd) == null || extractDate(ed) == null) {
            dialog.showToast("Enter both Dates in the dd/mm/yyyy format", AddDiscount.this);
            return;
        } else if (CheckDiscountCode(dbc) == null) {
            dialog.showToast("Discount Code must start with a letter", AddDiscount.this);
            return;
        }

        if (option == 0){
            if(p.isEmpty()){ dialog.showToast("Enter discount Percentage", AddDiscount.this);return;}
        } else if (option == 1) {
            if(bq.isEmpty() || fq.isEmpty() ||pb.isEmpty() ){ dialog.showToast("Enter All Details for the Buy One Get One Offer", AddDiscount.this);return;}
        } else if (option == 2) {
            if(a.isEmpty()){dialog.showToast("Enter Discount Amount", AddDiscount.this); return;}
        }else {
            dialog.showToast("Select A Discount Type", AddDiscount.this);
            return;
        }



        Discounts discount;
        if (option == 0){
            discount = new Discounts("Percentage",p,"","",sd,ed);
        } else if (option == 1) {
            discount = new Discounts("Buy one Get One",bq,fq,pb,sd,ed);
        } else {
            discount = new Discounts( "Money Off", a, "", "", sd, ed);
        }

        FirebaseDatabase.getInstance().getReference("Discounts").child(dbc)
                .setValue(discount).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                    }
                });




    }
}