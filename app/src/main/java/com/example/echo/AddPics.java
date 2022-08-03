package com.example.echo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.SiliCompressor;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class AddPics extends AppCompatActivity {

    ImageView back;
    TextView albumNameView, albumIdView, albumtempId;
    FirebaseAuth auth;
    DatabaseReference reference, databaseReference, albumReference;
    StorageReference storageReference;
    String albumName;
    RecyclerView recyclerView;
    StorageReference imageUrl;
    View addPic;
    Uri imageUri;
    String albumId, uid;
    String getImageUrl;
    String thumbUrlGlobal;
    MainRecyclerAdapter recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pics);

        back = findViewById(R.id.back_album_add);
        albumNameView = findViewById(R.id.album_name_add);
        addPic = findViewById(R.id.add_album_pics);
        recyclerView = findViewById(R.id.add_pics_recycler);
        albumIdView = findViewById(R.id.album_id_add);
        albumtempId = findViewById(R.id.album_temp_id_add);

        getIntentExtra();

        auth = FirebaseAuth.getInstance();

        uid = auth.getUid();

        reference  = FirebaseDatabase.getInstance().getReference().child(uid).child(albumNameView.getText().toString().trim());
        albumReference = FirebaseDatabase.getInstance().getReference().child(uid).child("albums").child(albumIdView.getText().toString());

        storageReference  = FirebaseStorage.getInstance().getReference().child(uid);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (ContextCompat.checkSelfPermission(AddPics.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AddPics.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    }else{
                        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                                .setFixAspectRatio(false)
                                .start(AddPics.this);

                    }
                }
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference().child(uid).child(albumNameView.getText().toString().trim());

        //recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        FirebaseRecyclerOptions<AlbumIndivdual> options =
                new FirebaseRecyclerOptions.Builder<AlbumIndivdual>()
                        .setQuery(databaseReference, AlbumIndivdual.class)
                        //.setLifecycleOwner(AllUsers.this)
                        .build();
        recyclerAdapter = new MainRecyclerAdapter(options, getApplicationContext());
        //recyclerView.setHasFixedSize(true);
        recyclerAdapter.setHasStableIds(true);
        recyclerView.setItemViewCacheSize(0);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(recyclerAdapter);


    }

    void getIntentExtra(){
        if(getIntent().hasExtra("album_name") && getIntent().hasExtra("albumId")){
            albumName = getIntent().getStringExtra("album_name");
            albumId = getIntent().getStringExtra("albumId");
            albumIdView.setText(albumId);
            albumNameView.setText(albumName);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            //getIntentExtra();

            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                Uri resultUri = result.getUri();

                //final String thumbUrl;
                File thumb_path = new File(resultUri.getPath());

                Bitmap compressedFile = null;
                try {
                    compressedFile = new Compressor(this).setQuality(50).compressToBitmap(thumb_path);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                //ByteArrayOutputStream baos = new ByteArrayOutputStream();
                //compressedFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                //byte[] thumb_bit = baos.toByteArray();

                String itemId = reference.push().getKey();

                //StorageReference thumbRef = storageReference.child("thumbnail").child(albumNameView.getText().toString().trim()).child(String.valueOf(imageUri) + ".jpg");

                StorageReference imageStorage = storageReference.child(albumNameView.getText().toString().trim()).child(String.valueOf(imageUri) + ".jpg");
                imageStorage.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            imageUrl = FirebaseStorage.getInstance().getReference().child(uid).child(albumName + "/" + imageUri + ".jpg");
                            getImageUrl = imageUrl.toString();
                            //Toast.makeText(AddPics.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AddPics.this, "Not Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                HashMap<String, String> newImgMap = new HashMap<>();
                newImgMap.put("album", albumNameView.getText().toString());
                newImgMap.put("image", String.valueOf(imageUri));
                newImgMap.put("itemId", itemId);
                newImgMap.put("where", "addPic");
                //newImgMap.put("imageUrl", getImageUrl);
                //newImgMap.put("thumb", thumbUrlGlobal);

                reference.child(itemId).setValue(newImgMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Toast.makeText(AddPics.this, "Saved to db", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AddPics.this, "Not saved to db", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                    Map<String, String> albumMap = new HashMap<>();
                    albumMap.put("album", albumNameView.getText().toString().trim());
                    albumMap.put("image", String.valueOf(imageUri));
                    albumMap.put("itemId", albumIdView.getText().toString().trim());
                    albumMap.put("where", "main");

                    albumReference.setValue(albumMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddPics.this, "Album added Pics", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    //Toast.makeText(AddPics.this, "match", Toast.LENGTH_SHORT).show();

                /*
                Map<String, String> albumMap = new HashMap<>();
                albumMap.put("album", albumNameView.getText().toString().trim());
                albumMap.put("image", String.valueOf(imageUri));
                albumMap.put("itemId", albumItemIdFinal);
                albumMap.put("where", "main");

                albumReference.child(albumItemIdFinal).setValue(albumMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            //Toast.makeText(MainPageEcho.this, "Album added", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                /*
                HashMap<String, String> newImgMap = new HashMap<>();
                newImgMap.put("album", albumName);
                newImgMap.put("image", String.valueOf(imageUri));
                newImgMap.put("itemId", itemId);

                reference.child(itemId).setValue(newImgMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(AddPics.this, "Saved to db", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(AddPics.this, "Not saved to db", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                 */
                }

            }

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