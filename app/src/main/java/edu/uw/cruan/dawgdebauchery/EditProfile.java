package edu.uw.cruan.dawgdebauchery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mBio;
    private NetworkImageView mProfilePic;
    private Button mCloseButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mProfilePic = (NetworkImageView) findViewById(R.id.profile_picture);
        mProfilePic.setDefaultImageResId(R.drawable.profile_default);

        mFirstName = (EditText) findViewById(R.id.name_container);
        mLastName = (EditText) findViewById(R.id.last_name_container);
        mBio = (EditText) findViewById(R.id.bio_container);

        mCloseButton = (Button) findViewById(R.id.profile_close_btn);
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveInfo();
            }
        });

        mStorageReference = FirebaseStorage.getInstance().getReference();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.choose_profile_pic);

//        fab.setImageResource(R.drawable.ic_edit_profile_pic);

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

        DatabaseReference user =  mDatabaseReference.child("Users").child(uID);

        user.child("imgUrl")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.v(TAG, "Profile pic changed -> updating locally");
                        String imageURL = (String) dataSnapshot.getValue();
                        mProfilePic.setImageUrl(imageURL,
                                RequestSingleton.getInstance(getApplicationContext()).imageLoader);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        //TODO add event listeners for firstName, lastName, and bio to update editTexts
        user.child("firstName")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mFirstName.setText((String) dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        user.child("lastName")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mLastName.setText((String) dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        user.child("bio")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mBio.setText((String) dataSnapshot.getValue());
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
                    Log.v(TAG, "Returned image url: " + taskSnapshot.getDownloadUrl().toString());
                    DatabaseReference profile_pic = mDatabaseReference.child("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("imgUrl");
                    profile_pic.setValue(taskSnapshot.getDownloadUrl().toString());

                    Log.v(TAG, "Uploaded Photo");
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        saveInfo();
//        Toast.makeText(this, "NO!", Toast.LENGTH_SHORT).show();
    }

    private void saveInfo() {
        boolean profileComplete = mFirstName.getText().toString().length() != 0
                && mLastName.getText().toString().length() != 0
                && mBio.getText().toString().length() != 0
                && !mProfilePic.getDrawable().equals(getDrawable(R.drawable.profile_default));
        if(profileComplete) {
            Toast.makeText(this, "Saving profile...", Toast.LENGTH_SHORT).show();

            String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DatabaseReference user = mDatabaseReference.child("Users").child(uID);
            user.child("firstName").setValue(mFirstName.getText().toString());
            user.child("lastName").setValue(mLastName.getText().toString());
            user.child("bio").setValue(mBio.getText().toString());

            startActivity(new Intent(this, MainActivity.class));
        }else {
            Toast.makeText(this, "Finish your profile, damnit!", Toast.LENGTH_SHORT).show();
//                    "You must finish your profile before continuing!"
        }
    }
}
