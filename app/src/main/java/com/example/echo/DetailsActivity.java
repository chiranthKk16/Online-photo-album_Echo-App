package com.example.echo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class DetailsActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    FirebaseAuth auth;
    EditText nameText;
    String user, currUser;
    Button goBtn;
    ImageView close;
    FirebaseUser userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        currUser = auth.getCurrentUser().toString();

        getIntentData();

        userId = auth.getCurrentUser();

        nameText = findViewById(R.id.register_name);
        close = findViewById(R.id.close);
        goBtn = findViewById(R.id.go_btn);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userId.delete();
                sendToRegister();
            }
        });

        goBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = auth.getUid();
                userId.reload();

                if (TextUtils.isEmpty(uid)) {
                    Toast.makeText(DetailsActivity.this, "Empty id", Toast.LENGTH_SHORT).show();
                }

                if (!TextUtils.isEmpty(nameText.getText().toString().trim())) {

                    if(user.compareTo(currUser) == 0) {
                        //Toast.makeText(DetailsActivity.this, "Same ID", Toast.LENGTH_SHORT).show();

                        if(userId.isEmailVerified()) {
                            HashMap<String, String> map = new HashMap<>();
                            map.put("id", uid);
                            map.put("name", nameText.getText().toString().trim());

                            DatabaseReference userRef = databaseReference.child("users");
                            userRef.child(uid).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(DetailsActivity.this, "Registered", Toast.LENGTH_SHORT).show();
                                        sendToHome();
                                    } else {
                                        Toast.makeText(DetailsActivity.this, "Not Registered", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(DetailsActivity.this, "not yet verified please try again", Toast.LENGTH_SHORT).show();
                        }


                    }else{
                        //Toast.makeText(DetailsActivity.this, "Different ID", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(DetailsActivity.this, "Please enter name", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    void getIntentData(){
        if(getIntent().hasExtra("userId")){
            user = getIntent().getStringExtra("userId");
        }
    }

    void sendToHome(){
        Intent intent = new Intent(DetailsActivity.this, MainPageEcho.class);
        startActivity(intent);
        finish();
    }
    void sendToRegister(){
        Intent intent = new Intent(DetailsActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }
}