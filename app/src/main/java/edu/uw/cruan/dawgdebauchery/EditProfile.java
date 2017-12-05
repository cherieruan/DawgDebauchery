package edu.uw.cruan.dawgdebauchery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class EditProfile extends AppCompatActivity {
    private StorageReference mStorageReference;
    private static final int PROFILE_PIC_INTENT = 16;
    private DatabaseReference mDatabaseReference;
    private static final String TAG = "EditProfile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        final NetworkImageView profilePic = (NetworkImageView) findViewById(R.id.profile_picture);
        profilePic.setDefaultImageResId(R.drawable.profile_default);

        Button closeButton = (Button) findViewById(R.id.profile_close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: save info and send user to MainActivity
            }
        });

        mStorageReference = FirebaseStorage.getInstance().getReference();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.choose_profile_pic);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PROFILE_PIC_INTENT);
            }
        });


        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDatabaseReference.child("Users")
                .child(uID)
                .child("profile_pic")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.v(TAG, "Profile pic changed -> updating locally");
                        String imageURL = (String) dataSnapshot.getValue();
                        profilePic.setImageUrl(imageURL,
                                RequestSingleton.getInstance(getApplicationContext()).imageLoader);
                        
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        //TODO add event listeners for firstName, lastName, and bio to update editTexts
        mDatabaseReference.child("Users").child(uID).child("firstName")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PROFILE_PIC_INTENT && resultCode == RESULT_OK) {
            Log.v(TAG, "Retrieved Photo");
            Uri uri = data.getData();
            StorageReference filePath = mStorageReference.child("photos").child(uri.getLastPathSegment());
            filePath.putFile(uri).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                    Toast.makeText(EditProfile.this, "Failed to upload photo", Toast.LENGTH_SHORT).show();
                }
            })
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    DatabaseReference profile_pic = mDatabaseReference.child("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("profile_pic");

                    Log.v(TAG, "Uploaded Photo");
                }
            });
        }
    }


}
