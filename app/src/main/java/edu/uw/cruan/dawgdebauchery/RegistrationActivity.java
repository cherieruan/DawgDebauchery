package edu.uw.cruan.dawgdebauchery;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Intent intent = getIntent();
        String email = intent.getStringExtra(LoginActivity.EMAIL_EXTRA_KEY);
        EditText emailET = (EditText) findViewById(R.id.register_email);
        emailET.setText(email);

        EditText passwordET = (EditText) findViewById(R.id.register_password);
        EditText passwordVerificationET = (EditText) findViewById(R.id.register_password_verification);

        passwordVerificationET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptRegistration();
                    return true;
                }
                return false;
            }
        });

        Button registerButton = (Button) findViewById(R.id.create_account_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegistration();
            }
        });
    }

    private void attemptRegistration() {
        EditText emailET = (EditText) findViewById(R.id.register_email);
        EditText passwordET = (EditText) findViewById(R.id.register_password);
        EditText passwordVerificationET = (EditText) findViewById(R.id.register_password_verification);

        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String passwordVerification = passwordVerificationET.getText().toString();
        if(!email.endsWith("@uw.edu")) {
            Toast.makeText(this, "Email must be a valid @uw.edu account", Toast.LENGTH_SHORT);
        }else if(password.length() < 8) {
            Toast.makeText(this, "Password must be at least 8 characters long!", Toast.LENGTH_SHORT).show();
        }else if(!password.equals(passwordVerification)) {
            Toast.makeText(this, "Passwords must match!", Toast.LENGTH_SHORT).show();
        }else {
            //actually send to firebase
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(RegistrationActivity.this, "Authentication failed." + task.getException(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                                finish();
                            }
                        }
                    });
        }
    }


}
