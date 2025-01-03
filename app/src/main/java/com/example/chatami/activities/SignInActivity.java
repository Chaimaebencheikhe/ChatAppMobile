package com.example.chatami.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatami.databinding.ActivitySignInBinding;
import com.example.chatami.utilities.Constants;
import com.example.chatami.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignInActivity extends AppCompatActivity {
    private ActivitySignInBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        if(preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
           Intent intent = new Intent (getApplicationContext(),MainActivity2.class);
           startActivity(intent);
           finish();
       }
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListeners();
    }

    private void setListeners() {
        binding.buttonAccount.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));
        binding.textcreateaccount.setOnClickListener(v ->{
if(isValisSignInDetais()){
    signIn();
}
        });
    }
    private void signIn(){
        loading(true);
        FirebaseFirestore database =FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL,binding.inputEmail.getText().toString())
               .whereEqualTo(Constants.KEY_PASSWORD,binding.inputPassword.getText().toString())
                .get()
                .addOnCompleteListener(task -> {

                    if(task.isSuccessful() && task.getResult() !=null
                        && task.getResult().getDocuments().size() > 0 ){
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                        preferenceManager.putString(Constants.KEY_image,documentSnapshot.getString(Constants.KEY_image));
                        Intent intent = new Intent (getApplicationContext(), MainActivity2.class);
                        intent.addFlags((Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        startActivity(intent);
                    }
                    else{
                            loading(false);
                            showToast("Incorrect Email or Password ");
                        }
                    });



    }


    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }
    private Boolean isValisSignInDetais(){
 if (binding.inputEmail.getText().toString().trim().isEmpty()) {
            showToast("Enter Email ");
            return false;

        }
 else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            showToast("Enter valid Email");

            return false;}

 else if (binding.inputPassword.getText().toString().trim().isEmpty()) {
                showToast("Enter Password ");
                return false;
    }
 else {
            return true;

        }
    }

    private void loading (Boolean isloading){
        if(isloading){
            binding.textcreateaccount.setVisibility(View.INVISIBLE);
            binding.progressbar.setVisibility(View.VISIBLE);

        }else{
            binding.textcreateaccount.setVisibility(View.VISIBLE);
            binding.progressbar.setVisibility(View.INVISIBLE);


        }}}