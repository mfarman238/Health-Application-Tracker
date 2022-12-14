package com.app.healthtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG  = "GoogleLogin";
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        SignInButton signInButton= findViewById(R.id.googleSignIn);
        signInButton.setOnClickListener(view -> googleSignInOnClick(view));
    }
    public void loginOnClick(View view){
        TextInputLayout tMobile=findViewById(R.id.tMobile);
        TextInputLayout tPassword=findViewById(R.id.tPassword);
        String mobile=tMobile.getEditText().getText().toString().trim();
        String password=tPassword.getEditText().getText().toString().trim();
        tMobile.setErrorEnabled(mobile.isEmpty());
        tPassword.setErrorEnabled(password.isEmpty());
        tMobile.setError(mobile.isEmpty()?tMobile.getErrorContentDescription():null);
        tPassword.setError(password.isEmpty()?tPassword.getErrorContentDescription():null);
        if(mobile.isEmpty()||password.isEmpty()){
            Toast.makeText(getApplicationContext(),R.string.enter_all_values,Toast.LENGTH_SHORT).show();
            return;
        }else if(mobile.length()!=10){
            Toast.makeText(getApplicationContext(),R.string.enter_valid_mobile,Toast.LENGTH_SHORT).show();
            return;
        }
        DialogUtil.showProgress(LoginActivity.this);
        DatabaseReference db= FirebaseDatabase.getInstance().getReference("register/"+mobile);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DialogUtil.hideProgress();
                if(snapshot.exists()){
                    Map<String, String> map  = (Map) snapshot.getValue();
                    if(password.equals(map.get("password"))){
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(),"Wrong creditials",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Not yet registered",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                DialogUtil.hideProgress();
                Toast.makeText(getApplicationContext(),error.getMessage()+"",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void registerOnClick(View view){
        startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
        finish();
    }

    public void googleSignInOnClick(View view){
        Toast.makeText(getApplicationContext(),"google sign in",Toast.LENGTH_SHORT).show();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void googleUpdateUI(FirebaseUser currentUser){
    if(currentUser!=null){
        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
    }
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        googleUpdateUI(currentUser);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            googleUpdateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            googleUpdateUI(null);
                        }
                    }
                });
    }

}