package com.example.echo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText emailReg, passwordReg, confirmPass;
    TextView loginReg, count;
    Button signInBtnReg;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    String currUid;
    ProgressBar progressBar;
    FirebaseUser user;
    boolean isVerified = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailReg = findViewById(R.id.email_register);
        passwordReg = findViewById(R.id.password_register);
        loginReg = findViewById(R.id.login_now);
        signInBtnReg = findViewById(R.id.register_btn);
        progressBar = findViewById(R.id.progress_register);
        count = findViewById(R.id.count);
        confirmPass = findViewById(R.id.confirm_pass_register);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        currUid = auth.getUid();

        loginReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //count.setText("1");

        signInBtnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email = emailReg.getText().toString().trim();
                String pass = passwordReg.getText().toString().trim();
                String confirmPassStr = confirmPass.getText().toString().trim();
                //String name = nameReg.getText().toString().trim();



                    if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(confirmPassStr)) {

                        if (pass.compareTo(confirmPassStr) == 0) {
                                auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            //Toast.makeText(RegisterActivity.this, "Passwords not matching", Toast.LENGTH_SHORT).show();
                                            user = auth.getCurrentUser();

                                            assert user != null;

                                            user.sendEmailVerification()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(RegisterActivity.this,
                                                                        "Verification email sent to " + user.getEmail(),
                                                                        Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(RegisterActivity.this, DetailsActivity.class);
                                                                intent.putExtra("userId", String.valueOf(user));
                                                                startActivity(intent);
                                                                finish();
                                                            } else {
                                                                Toast.makeText(RegisterActivity.this,
                                                                        "Verification email not sent" + user.getEmail(),
                                                                        Toast.LENGTH_SHORT).show();
                                                            }

                                                        }
                                                    });

                                        }
                                    }
                                });
                            }

                                /*
                                user = auth.getCurrentUser();

                                assert user != null;

                                user.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(RegisterActivity.this,
                                                            "Verification email sent to " + user.getEmail(),
                                                            Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(RegisterActivity.this,
                                                            "Verification email not sent" + user.getEmail(),
                                                            Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });

                                 */

                                //count.setText("0");
                                //Toast.makeText(RegisterActivity.this, "Verification failed", Toast.LENGTH_SHORT).show();

                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this, "Passwords not matching", Toast.LENGTH_SHORT).show();
                        }

            }
        });

    }



    void sendToDetails(){
        Intent intent = new Intent(RegisterActivity.this, DetailsActivity.class);
        startActivity(intent);
        finish();
    }
}