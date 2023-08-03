package com.swaraj.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.swaraj.myapplication.data.User;

public class LoginActivity extends AppCompatActivity {

    EditText etUserName, etPassword;
    LinearLayout btnLogin,btnRegister;
    TextView tvError;
    DialogBoxPopup dialog;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        if (mAuth.getCurrentUser() != null) {
            database.getReference("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            if (user != null) {
                                if(user.IsAdmin){
                                    showAdminActivity();
                                }else {
                                    showEmployeeActivity(user.FirstName);
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            return;
        }

        dialog = new DialogBoxPopup();
        etUserName = findViewById(R.id.etUserName);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.llLogin);
        btnRegister = findViewById(R.id.OpenRegister);
        tvError = findViewById(R.id.tvError);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.titlebar);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToRegister();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticateUser();

            }
        });
    }

    private void authenticateUser() {
        EditText etLoginEmail = findViewById(R.id.etUserName);
        EditText etLoginPassword = findViewById(R.id.etPassword);

        String email = etLoginEmail.getText().toString();
        String password = etLoginPassword.getText().toString();

        if (email.isEmpty()) {
            tvError.setVisibility(View.GONE);
            dialog.showToast("Enter user name", LoginActivity.this);
            return;
        } else if(password.isEmpty()){
            dialog.showToast("Enter password",LoginActivity.this);
            return;
        }else{
            tvError.setVisibility(View.VISIBLE);
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            database.getReference("users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            User user = snapshot.getValue(User.class);
                                            if (user != null) {
                                                if(user.IsAdmin){
                                                    showAdminActivity();
                                                }else {
                                                    showEmployeeActivity(user.FirstName);
                                                }

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void switchToRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("name","reg");
        startActivity(intent);

    }

    private void showEmployeeActivity(String name) {
        Toast.makeText(LoginActivity.this, "logged In",
                Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, EmployeeActivity.class);
        intent.putExtra("name",name);
        startActivity(intent);
        finish();
    }

    private void showAdminActivity() {
        Toast.makeText(LoginActivity.this, " Admin logged In",
                Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }
}