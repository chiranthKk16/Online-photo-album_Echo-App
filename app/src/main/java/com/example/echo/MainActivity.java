package com.example.echo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText email, password;
    TextView register;
    Button signInBtn;
    DatabaseReference reference;
    String userId;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.email_login);
        password = findViewById(R.id.password_login);
        register = findViewById(R.id.register_now);
        signInBtn = findViewById(R.id.sing_in_btn);
        progressBar = findViewById(R.id.progress_sign_in);

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("users");
        userId = mAuth.getUid();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToRegister();
            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            private static final String TAG = "1";

            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String emailStr = email.getText().toString().trim();
                String passStr = password.getText().toString().trim();

                /*
                FirebaseUser user = mAuth.getCurrentUser();
                assert user != null;
                user.sendEmailVerification()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(MainActivity.this,
                                            "Verification email sent to " + user.getEmail(),
                                            Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(MainActivity.this,
                                            "Verification email not sent" + user.getEmail(),
                                            Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                if(user.isEmailVerified()){
                    progressBar.setVisibility(View.GONE);
                    mAuth.signInWithEmailAndPassword(emailStr, passStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                sendToHome();
                            }
                        }
                    });
                    //Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();

                }

                 */


                if(!TextUtils.isEmpty(emailStr) && !TextUtils.isEmpty(passStr)) {
                    mAuth.signInWithEmailAndPassword(emailStr, passStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                assert user != null;
                                if(user.isEmailVerified()){
                                    progressBar.setVisibility(View.GONE);
                                    //Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                                    sendToHome();
                                }else{
                                    //Toast.makeText(MainActivity.this, "Verification failed", Toast.LENGTH_SHORT).show();
                                    //assert user != null;
                                    user.sendEmailVerification()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(MainActivity.this,
                                                                "Verification email sent to " + user.getEmail(),
                                                                Toast.LENGTH_SHORT).show();
                                                    }else{
                                                        Toast.makeText(MainActivity.this,
                                                                "Verification email not sent" + user.getEmail(),
                                                                Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            });
                                    if(user.isEmailVerified()) {
                                        progressBar.setVisibility(View.GONE);
                                        //Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                                        sendToHome();
                                    }else{
                                        Toast.makeText(MainActivity.this, "Verification failed", Toast.LENGTH_SHORT).show();
                                    }
                                }



                                //progressBar.setVisibility(View.GONE);
                                //Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                                //sendToHome();

                            } else {
                                progressBar.setVisibility(View.GONE);
                                String error = task.getException().getMessage();
                                Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Please fill out the fields", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    void sendToHome(){
        Intent intent = new Intent(MainActivity.this, MainPageEcho.class);
        startActivity(intent);
        finish();
    }

    void sendToRegister(){
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            sendToHome();
        }
    }
}