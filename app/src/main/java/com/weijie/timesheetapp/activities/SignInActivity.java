package com.weijie.timesheetapp.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.weijie.timesheetapp.R;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = SignInActivity.class.getSimpleName();
    EditText mUsername;
    EditText mPassword;
    TextView create_account;
    ProgressDialog mProgress;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_in);

        mUsername = (EditText) findViewById(R.id.username_et);
        mPassword = (EditText) findViewById(R.id.pw_et);
        create_account = (TextView) findViewById(R.id.goToSignUp);
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Please wait...");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(SignInActivity.this, user.getEmail(),
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(SignInActivity.this, "No User!",
                            Toast.LENGTH_SHORT).show();
                }

                updateUI(user);
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void updateUI(FirebaseUser user) {

    }

    public void SignIn(View view) {
        if (validateForm()) {
            String email = mUsername.getText().toString();
            String pass = mPassword.getText().toString();
            mProgress.show();

            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInWithEmail:failed", task.getException());
                                Toast.makeText(SignInActivity.this, "Authorize failed!",
                                        Toast.LENGTH_SHORT).show();
                            }

                            // [START_EXCLUDE]
                            if (task.isSuccessful()) {
                                Toast.makeText(SignInActivity.this, "Authorize Succeed!",
                                        Toast.LENGTH_SHORT).show();
                            }
                            mProgress.hide();
                        }
                    });

        }
        else {
            Toast.makeText(this, "Please check your input", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateForm() {
        return true;
    }
}
