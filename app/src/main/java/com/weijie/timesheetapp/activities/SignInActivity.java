package com.weijie.timesheetapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.weijie.timesheetapp.R;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = SignInActivity.class.getSimpleName();
    EditText mEmail;
    EditText mPassword;
    TextView create_account;
    ProgressDialog mProgress;
    LoginButton mLoginButton;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private CallbackManager callbackManager;
    private ProfileTracker mProfileTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_in);
        getSupportActionBar().hide();

        mEmail = (EditText) findViewById(R.id.email_et);
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

                }
                else {
                    Toast.makeText(getApplicationContext(), "You've logged out",
                            Toast.LENGTH_SHORT).show();
                }

            }
        };

        mLoginButton = (LoginButton) findViewById(R.id.fb_login_button);
        setupFacebookLogin();
    }

    private void setupFacebookLogin() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        mLoginButton.setReadPermissions("public_profile");
        mLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {

                handleFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                LoginManager.getInstance().logOut();
                Toast.makeText(getApplicationContext(), "You cancelled to login", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "There is an error when login to your Facebook Account, Please try again", Toast.LENGTH_LONG).show();
            }
        });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgress!= null && mProgress.isShowing())
            mProgress.dismiss();
    }

    public void SignIn(View view) {
        if (validateForm()) {
            String email = mEmail.getText().toString();
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
                            mProgress.hide();
                            if (!task.isSuccessful()) {
                                try {
                                    throw task.getException();
                                } catch(FirebaseAuthInvalidUserException e) {
                                    mEmail.setError(getString(R.string.error_user_not_exists));
                                    mEmail.requestFocus();
                                } catch(FirebaseAuthInvalidCredentialsException e) {
                                    mPassword.setError(getString(R.string.error_incorrect_password));
                                    mPassword.requestFocus();
                                } catch(Exception e) {
                                    Log.e(TAG, e.getMessage());
                                }
                                Toast.makeText(SignInActivity.this, "Authorize failed!",
                                        Toast.LENGTH_SHORT).show();
                            }

                            // [START_EXCLUDE]
                            if (task.isSuccessful()) {
                                Toast.makeText(SignInActivity.this, "Authorize Succeed!",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), HomepageActivity.class);
                                startActivity(intent);
                                SignInActivity.this.finish();
                            }

                        }
                    });

        }
    }

    private boolean validateForm() {
        boolean valid = true;
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmail.setError(getString(R.string.error_invalid_email));
            valid = false;
        }
        else {
            mEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 25) {
            mPassword.setError(getString(R.string.error_password_length));
            valid = false;
        }
        else {
            mPassword.setError(null);
        }
        return valid;
    }

    public void goToSignUp(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        mProgress.show();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            mProgress.hide();
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            mProgress.hide();

                            if(Profile.getCurrentProfile() == null) {
                                mProfileTracker = new ProfileTracker() {
                                    @Override
                                    protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                                        Log.v("facebook - profile", currentProfile.getFirstName());
                                        Intent i = new Intent(getApplicationContext(), HomepageActivity.class);
                                        startActivity(i);
                                        mProfileTracker.stopTracking();
                                        SignInActivity.this.finish();
                                    }
                                };
                            }
                            else {
                                Profile profile = Profile.getCurrentProfile();
                                Log.v("facebook - profile", profile.getFirstName());
                                Intent i = new Intent(getApplicationContext(), HomepageActivity.class);
                                startActivity(i);
                                SignInActivity.this.finish();
                            }
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,
                resultCode, data);
    }
}
