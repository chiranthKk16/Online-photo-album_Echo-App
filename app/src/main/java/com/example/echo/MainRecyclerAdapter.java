package com.example.echo;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
//import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class MainRecyclerAdapter extends FirebaseRecyclerAdapter<AlbumIndivdual, MainRecyclerAdapter.albumViewHolder>{

 /**
  * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
  * {@link FirebaseRecyclerOptions} for configuration options.
  *
  * @param options
  */

 Context context;
 DatabaseReference reference, albumReference;
 FirebaseAuth auth;
 StorageReference storageReference;

 public MainRecyclerAdapter(@NonNull FirebaseRecyclerOptions<AlbumIndivdual> options, Context context) {
  super(options);
  this.context = context;
 }

 @Override
 protected void onBindViewHolder(@NonNull albumViewHolder holder, int position, @NonNull AlbumIndivdual model) {
    auth = FirebaseAuth.getInstance();
    String uid = auth.getUid();
    albumReference = FirebaseDatabase.getInstance().getReference().child(uid).child("albums");
    String albumName = model.getAlbum();
    reference = FirebaseDatabase.getInstance().getReference().child(uid);

     //ImagePopup imagePopup = new ImagePopup(context);
     //imagePopup.setHideCloseIcon(true);
     //imagePopup.setImageOnClickClose(true);

    //StorageReference placeHolderRef = storageReference.child("placeholder/placeholderalbum.jpg");
    //String placeHolder = placeHolderRef.getPath();
    if(!model.getImage().equals("no")) {
        holder.pics.setImageURI(Uri.parse(model.getImage()));
    }else{
        holder.pics.setImageResource(R.drawable.placeholderalbum);
    }

    //String where = model.getWhere();

    if("main".equalsIgnoreCase(model.getWhere())){
        holder.albumName.setText(model.getAlbum());
        holder.albumName.setVisibility(View.VISIBLE);
    }
     //if(where.contentEquals("addPic")){
         //holder.albumName.setVisibility(View.GONE);
     //}
    /*else if(model.getWhere().equals("addPic")){
        holder.albumName.setVisibility(View.GONE);
    }

     */

     //imagePopup.initiatePopupWithPicasso(model.getImage());
    // holder.pics.setImageURI(Uri.parse(model.getImage()));

    holder.pics.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Toast.makeText(context, model.getItemId(), Toast.LENGTH_SHORT).show();
            if(model.getWhere().equals("main")){
                Intent intent = new Intent(context, AddPics.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("albumId", model.getItemId());
                intent.putExtra("album_name", model.getAlbum());
                context.startActivity(intent);
            }else if(model.getWhere().equals("addPic")){
                Dialog builder = new Dialog(v.getRootView().getContext(), R.style.AppBaseThemeDialog);
                builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
                builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                builder.setContentView(R.layout.image_pop);
                ImageView popImage = builder.findViewById(R.id.pop_up_view);
                popImage.setImageURI(Uri.parse(model.getImage()));
                builder.show();

                //holder.pics.setImageURI(Uri.parse(model.getImage()));
                //imagePopup.viewPopup();
                //Toast.makeText(context, model.getItemId(), Toast.LENGTH_SHORT).show();
                //reference.child(model.getAlbum()).child(model.getItemId()).removeValue();
            }
        }
    });

    holder.pics.setOnLongClickListener(new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
            builder.setTitle("Delete?");
            builder.setMessage("Are you sure you want to delete?");
            builder.setIcon(R.drawable.ic_delete);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(model.getWhere().equals("addPic")) {
                        reference.child(model.getAlbum()).child(model.getItemId()).removeValue();
                        /*
                        StorageReference imgPath = FirebaseStorage.getInstance(model.getImageUrl()).getReference();
                        imgPath.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(context, "delete from storage", Toast.LENGTH_SHORT).show();
                            }
                        });
                        
                         */
                    }else if(model.getWhere().equals("main")){
                        albumReference.child(model.getItemId()).removeValue();
                        reference.child(model.getAlbum()).removeValue();
                    }
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
            return false;

        }
    });

    //Glide.with(context).load(model.getImage()).into(holder.pics);
 }

 @NonNull
 @Override
 public albumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
  View view;
  view = LayoutInflater.from(parent.getContext()).inflate(R.layout.individual_pics, parent, false);

  //view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_individual, parent, false);

  return new albumViewHolder(view);
 }

 public static class albumViewHolder extends RecyclerView.ViewHolder {

  ImageView pics;
  TextView albumName;

  public albumViewHolder(@NonNull View itemView) {
   super(itemView);
   pics = itemView.findViewById(R.id.individual_imageView);
   albumName = itemView.findViewById(R.id.album_name_individual);
  }
 }
}
