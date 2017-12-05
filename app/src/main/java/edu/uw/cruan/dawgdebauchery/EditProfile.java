package edu.uw.cruan.dawgdebauchery;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.volley.toolbox.NetworkImageView;

public class EditProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        NetworkImageView profilePic = (NetworkImageView) findViewById(R.id.profile_picture);
        profilePic.setDefaultImageResId(R.drawable.profile_default);

        Button closeButton = (Button) findViewById(R.id.profile_close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: save info and send user to MainActivity
            }
        });
    }


}
