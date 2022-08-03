package com.example.echo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainPageEcho extends AppCompatActivity {

    ImageView logOut;
    View addAlbum;
    FirebaseAuth auth;
    FirebaseUser user;
    ImageView imageTime;
    TextView greetingTime, nameTime, greetTimeRight, nameTimeRight;
    DatabaseReference reference, databaseReference;
    String userName;
    RecyclerView mainRecycler;
    MainRecyclerAdapter recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page_echo);

        logOut = findViewById(R.id.logout);
        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        imageTime = findViewById(R.id.greeting_image);
        greetingTime = findViewById(R.id.time_of_day);
        nameTime = findViewById(R.id.name_main);
        addAlbum = findViewById(R.id.ad_album);
        mainRecycler = findViewById(R.id.main_recycler);
        nameTimeRight = findViewById(R.id.name_main_right);
        greetTimeRight = findViewById(R.id.time_of_day_right);

        auth = FirebaseAuth.getInstance();

        String uid = auth.getUid();

        reference = FirebaseDatabase.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child(uid).child("albums");

        addAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view;
                LayoutInflater inflater = LayoutInflater.from(MainPageEcho.this);
                view = inflater.inflate(R.layout.custom_dialog, null);
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainPageEcho.this);
                dialog.setView(view);
                
                final EditText albumName = view.findViewById(R.id.custom_prompt_editText);

                dialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //DatabaseReference databaseReference = reference.child(uid).child(albumName.getText().toString().trim());
                        //databaseReference.setValue(albumName.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                           // @Override
                            //public void onComplete(@NonNull Task<Void> task) {
                            //    Toast.makeText(MainPageEcho.this, "Album added", Toast.LENGTH_SHORT).show();
                            //}
                        //})
                        String albumId = databaseReference.push().getKey();
                        HashMap<String, String> newMap = new HashMap<>();
                        newMap.put("album", albumName.getText().toString().trim());
                        newMap.put("image", "something");
                        newMap.put("itemId", albumId);
                        newMap.put("where", "main");
                        databaseReference.child(albumId).setValue(newMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    //Toast.makeText(MainPageEcho.this, "Album created", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        Intent intent = new Intent(MainPageEcho.this, AddPics.class);
                        intent.putExtra("album_name", albumName.getText().toString().trim());
                        intent.putExtra("albumId", albumId);
                        startActivity(intent);
                    }
                });
                dialog.show();
            }
        });

        Calendar calendar = Calendar.getInstance();
        int TimeOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        if(TimeOfDay >= 0 && TimeOfDay < 12){
            imageTime.setImageResource(R.drawable.morning);
            greetingTime.setText("Good morning,");
        }else if(TimeOfDay >=12 && TimeOfDay <16){
            imageTime.setImageResource(R.drawable.afternoon);
            greetingTime.setText("Good afternoon,");
        }else if(TimeOfDay >=16 && TimeOfDay <20){
            imageTime.setImageResource(R.drawable.evening);
            greetTimeRight.setVisibility(View.VISIBLE);
            nameTimeRight.setVisibility(View.VISIBLE);
            greetTimeRight.setText("Good evening,");
            greetingTime.setVisibility(View.GONE);
            nameTime.setVisibility(View.GONE);
        }else if(TimeOfDay >=20 && TimeOfDay < 24){
            imageTime.setImageResource(R.drawable.night);
            greetingTime.setText("Good night,");
            greetTimeRight.setVisibility(View.GONE);
            nameTimeRight.setVisibility(View.GONE);
            greetingTime.setVisibility(View.VISIBLE);
            nameTime.setVisibility(View.VISIBLE);
        }

        reference.child("users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userName = snapshot.child("name").getValue().toString();
                nameTime.setText(userName);
                nameTimeRight.setText(userName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainPageEcho.this);
                builder.setTitle("Logout?");
                builder.setMessage("Are you sure you want to logout?");
                builder.setIcon(R.drawable.ic_power);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        auth.signOut();
                        Intent intent = new Intent(MainPageEcho.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();

            }
        });

        FirebaseRecyclerOptions<AlbumIndivdual> options = new FirebaseRecyclerOptions.Builder<AlbumIndivdual>()
                .setQuery(databaseReference, AlbumIndivdual.class)
                .build();
        recyclerAdapter = new MainRecyclerAdapter(options, getApplicationContext());
        mainRecycler.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        mainRecycler.setAdapter(recyclerAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        recyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        recyclerAdapter.stopListening();
    }
}