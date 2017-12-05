package edu.uw.cruan.dawgdebauchery;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity  {
    private FirebaseAuth mAuth;
    public static final String TAG = "LoginActivity";
    public static final String EMAIL_EXTRA_KEY = "email_key";



    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);

        // TODO Reimplement the below code when we get the login logic sorted out with Firebase.
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button registerButton = (Button) findViewById(R.id.login_register_button);
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                openRegistration();
            }
        });

        // TODO Delete this code when the login functionality is implemented
//        mEmailSignInButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                startActivity(intent);
//            }
//        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void openRegistration() {
        Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
        intent.putExtra(EMAIL_EXTRA_KEY, mEmailView.getText().toString());
        startActivity(intent);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        String password = mPasswordView.getText().toString();
        String email = mEmailView.getText().toString();

        boolean validEmail = isEmailValid(email);
        boolean validPassword = isPasswordValid(password);

        if(validEmail && validPassword) {
            Log.v(TAG, "Email & Password valid, logging in");
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.v(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);

                            } else {
                                Exception e = task.getException();
                                if(e instanceof FirebaseAuthInvalidUserException) {
                                    //have them create an account
                                    Log.v(TAG, "Invalid user");
                                    final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                                            "Account not found. Would you like to create an account?", Snackbar.LENGTH_LONG);
                                    snackbar.setAction("Create account", new View.OnClickListener() {

                                        @Override
                                        public void onClick(View view) {
                                            openRegistration();
                                        }
                                    });
                                    snackbar.show();

                                }else {
                                    // If sign in fails, display a message to the user.
                                    Log.v(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        }else if(!validEmail){
            Toast.makeText(this, "Invalid Email, must be a '@uw.edu' domain", Toast.LENGTH_LONG).show();
        }else if(!validPassword) {
            Toast.makeText(this, "Invalid password: must be at least 8 characters long", Toast.LENGTH_LONG).show();
        }

    }

    private boolean isEmailValid(String email) {
        return email.endsWith("@uw.edu");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 8;
    }

}

