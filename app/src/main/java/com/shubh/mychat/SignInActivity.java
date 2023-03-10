package com.shubh.mychat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.shubh.mychat.Models.User;
import com.shubh.mychat.databinding.ActivitySignInBinding;

public class SignInActivity extends AppCompatActivity {
ActivitySignInBinding binding;
ProgressDialog progressDialog;
FirebaseAuth auth;
GoogleSignInClient mGoogleSignInClient;
FirebaseDatabase database;
//private static final String TAG = "SignInActivity";
//int AUTHUI_REQUEST_CODE =1001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        getSupportActionBar().hide();

        setContentView(binding.getRoot());


        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(SignInActivity.this);
        progressDialog.setTitle("Loging In");
        progressDialog.setMessage("Loging in to your account");
//      new code  configure google sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this , gso);

//        if (FirebaseAuth.getInstance().getCurrentUser()!=null){
//            startActivity(new Intent(this,MainActivity.class));
//            this.finish();
//
//        }


        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                auth.signInWithEmailAndPassword(binding.etEmail.getText().toString() , binding.etPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()){
                            Intent intent = new Intent(SignInActivity.this ,MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(SignInActivity.this,"Signed in", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });

        binding.clickToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });


        if (auth.getCurrentUser()!=null){
            Intent intent =new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
        }
        binding.btnSignInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               signIn();
            }
        });



    }
    int RC_SIGN_IN =65;
    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       if (requestCode==RC_SIGN_IN){
           Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
           try {
               GoogleSignInAccount account = task.getResult(ApiException.class);
               Log.d("TAG", "FirebaseAuthWthGoogle"+account.getId());
               firebaseAuthWithGoogle(account.getIdToken());
           }
           catch (ApiException  e) {
               Log.w("TAG", "Google Sign in failed", e);
           }

    }
}
private void firebaseAuthWithGoogle(String idToken){
    AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
    auth.signInWithCredential(firebaseCredential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("TAG", "signInWithCredential:success");
                FirebaseUser user = auth.getCurrentUser();
                User users = new User();
                users.setUserId(user.getUid());
                users.setUserName(user.getDisplayName());
                users.setProfilepic(user.getPhotoUrl().toString());

                database.getReference().child("User").child(user.getUid()).setValue(users);

                    Intent intent = new Intent(SignInActivity.this ,MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(SignInActivity.this,"Signed in with google", Toast.LENGTH_SHORT).show();

//                updateUI(user);
            } else {
                // If sign in fails, display a message to the user.
                Log.w("TAG", "signInWithCredential:failure", task.getException());
//                updateUI(null);
            }
        }
    });
}
////    new code
//    public void SignInActivity(View view){
//        List<AuthUI.IdpConfig> provider = Arrays.asList(
//          new AuthUI.IdpConfig.EmailBuilder().build()
//        );
//
//        Intent intent = AuthUI.getInstance()
//        .createSignInIntentBuilder()
//                .setAvailableProviders(provider)
//                .setTosAndPrivacyPolicyUrls("https://examole.com","https://examole.com")
//                .build();
//        startActivityForResult(intent ,AUTHUI_REQUEST_CODE);
//    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode , @Nullable Intent data) {
//
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode ==AUTHUI_REQUEST_CODE){
//            if (resultCode == RESULT_OK){
//
//            }
//        }
//    }
}