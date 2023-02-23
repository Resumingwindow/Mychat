package com.shubh.mychat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.shubh.mychat.databinding.ActivitySignInBinding;

public class SignInActivity extends AppCompatActivity {
ActivitySignInBinding binding;
ProgressDialog progressDialog;
FirebaseAuth auth;
//private static final String TAG = "SignInActivity";
//int AUTHUI_REQUEST_CODE =1001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        getSupportActionBar().hide();

        setContentView(binding.getRoot());


        auth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(SignInActivity.this);
        progressDialog.setTitle("Loging In");
        progressDialog.setMessage("Loging in to your account");
//      new code  configure google sign in
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
                Toast.makeText(SignInActivity.this, "We are working in it", Toast.LENGTH_SHORT).show();
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