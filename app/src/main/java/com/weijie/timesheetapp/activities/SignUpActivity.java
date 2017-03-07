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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.weijie.timesheetapp.R;
import com.weijie.timesheetapp.network.Controller;

import org.json.JSONException;
import org.json.JSONObject;

import mehdi.sakout.fancybuttons.FancyButton;
import okhttp3.Response;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = SignUpActivity.class.getSimpleName();
    EditText firstname;
    EditText lastname;
    EditText email_et;
    EditText password1;
    EditText password2;
    FancyButton fBCreate;
    ProgressDialog mProgress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();

        firstname = (EditText) findViewById(R.id.firstName);
        lastname = (EditText) findViewById(R.id.lastName);
        email_et = (EditText) findViewById(R.id.email_edit);
        password1 = (EditText) findViewById(R.id.pass_ed1);
        password2 = (EditText) findViewById(R.id.pass_ed2);
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Please wait...");

        fBCreate = (FancyButton) findViewById(R.id.create_account_bt);
        fBCreate.setOnClickListener(this);
    }

    public void goToLogin(View view) {
        onBackPressed();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.create_account_bt) {
            CreateUserAccount();
        }
    }

    private void CreateUserAccount() {
        if (validateForm()) {
            mProgress.show();
            FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email_et.getText().toString(), password1.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());


                            if (!task.isSuccessful()) {
                                mProgress.hide();
                                try {
                                    throw task.getException();
                                } catch(FirebaseAuthWeakPasswordException e) {
                                    password1.setError(getString(R.string.error_weak_password));
                                    password1.requestFocus();
                                } catch(FirebaseAuthUserCollisionException e) {
                                    email_et.setError(getString(R.string.error_user_exists));
                                    email_et.requestFocus();
                                } catch(Exception e) {
                                    Log.e(TAG, e.getMessage());
                                }
                                Toast.makeText(SignUpActivity.this, "Create Account Failed!",
                                        Toast.LENGTH_SHORT).show();
                            }
                            else {
                                // Add user info to backend db
                                Thread thread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        JSONObject json = new JSONObject();
                                        try {
                                            json.put("email", email_et.getText().toString());
                                            json.put("firstName", firstname.getText().toString());
                                            json.put("lastName", lastname.getText().toString());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        Response response = Controller.AppEvent(Controller.Action.ADD_NEW_USER, "", json);

                                        if (response.isSuccessful()) {
                                            SignUpActivity.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mProgress.hide();
                                                    Toast.makeText(SignUpActivity.this, "New Account Created!",
                                                            Toast.LENGTH_SHORT).show();
                                                    // Go back to login screen
                                                    Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                                    startActivity(intent);
                                                    SignUpActivity.this.finish();
                                                }
                                            });
                                        }
                                    }
                                });
                                thread.start();
                            }
                        }
                    });
        }
    }

    private boolean validateForm() {
        boolean valid = true;
        String firstName = firstname.getText().toString();
        String lastName = lastname.getText().toString();
        String userEmail = email_et.getText().toString();
        String pass = password1.getText().toString();
        String pass_conf = password2.getText().toString();

        String name_matcher = "^[a-zA-Z\\s]+";

        if (userEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            email_et.setError(getString(R.string.error_weak_password));
            valid = false;
        }
        else {
            email_et.setError(null);
        }

        if (firstName.isEmpty() || !firstName.matches(name_matcher)) {
            firstname.setError(getString(R.string.error_wrong_name));
            valid = false;
        }
        else {
            firstname.setError(null);
        }

        if (lastName.isEmpty() || !lastName.matches(name_matcher)) {
            lastname.setError(getString(R.string.error_wrong_name));
            valid = false;
        }
        else {
            lastname.setError(null);
        }

        if (pass.isEmpty() || pass.length() < 6 || pass.length() > 12) {
            password1.setError(getString(R.string.error_password_length));
            valid = false;
        }
        else {
            password1.setError(null);
        }

        if (pass_conf.isEmpty() || pass.length() < 6 || pass_conf.length() > 12) {
            password2.setError(getString(R.string.error_password_length));
            valid = false;
        }
        else  {
            if (pass_conf.equals(pass)) {
                password2.setError(null);
            }
            else {
                password2.setError(getString(R.string.error_mismatch_password));
                valid = false;
            }
        }

        return valid;
    }
}
