package com.example.chatami.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.util.Patterns;

import com.example.chatami.R;
import com.example.chatami.databinding.ActivitySignInBinding;
import com.example.chatami.databinding.ActivitySignUpBinding;
import com.example.chatami.utilities.Constants;
import com.example.chatami.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
 ActivitySignUpBinding binding;
 private String encodedImage;
 private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();

    }
    private void setListeners() {
        binding.textsignup.setOnClickListener(v ->onBackPressed());
        binding.signupbtn.setOnClickListener(v ->{
              if(isValidSignUPDETAILS()) {
            signUp();
        }
        });
        binding.layoutimage.setOnClickListener(v ->{
            Intent intent = new Intent (Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
            pickImage.launch(intent);
                }
                );
    }
    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
    }

    private void signUp(){
loading(true);
FirebaseFirestore database =FirebaseFirestore.getInstance();
HashMap<String, Object > user = new HashMap<>();
user.put(Constants.KEY_NAME,binding.inputName.getText().toString());
user.put(Constants.KEY_EMAIL,binding.inputEmail.getText().toString());
user.put(Constants.KEY_PASSWORD,binding.iputPassword.getText().toString());
user.put(Constants.KEY_NAME,binding.inputName.getText().toString());
user.put(Constants.KEY_image,encodedImage);
database.collection(Constants.KEY_COLLECTION_USERS)
        .add(user)
        .addOnSuccessListener(documentReference -> {
            loading(false);
            preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
            preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
            preferenceManager.putString(Constants.KEY_NAME, binding.inputName.getText().toString());
            preferenceManager.putString(Constants.KEY_image,encodedImage);
            Intent intent = new Intent (getApplicationContext(), SignInActivity.class);
            intent.addFlags((Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK));
            startActivity(intent);

        })
        .addOnFailureListener(exception -> {
loading(false);
showToast(exception.getMessage());
        });






    }
    private String encodeImage(Bitmap bitmap){
int previewWidth =150;
int previewHeight = bitmap.getHeight() + previewWidth / bitmap.getWidth();
Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap,previewWidth,previewHeight,false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes,Base64.DEFAULT);


    }


    private final ActivityResultLauncher <Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result ->{
                if (result.getResultCode() == RESULT_OK){
                  if(result.getData() != null){
                   Uri imageUri  = result.getData().getData();
                   try{
                       InputStream inputStream = getContentResolver().openInputStream(imageUri);
                       Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                       binding.imageprofile.setImageBitmap(bitmap);
                       binding.textaddimage.setVisibility(View.GONE);
                       encodedImage = encodeImage(bitmap);
                   }
                   catch (FileNotFoundException e){
                       e.printStackTrace();
                   }
                  }
                }
            }
    );
    public boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        // Vérification simple : contient une '@' et un '.', avec quelques restrictions
        int atIndex = email.indexOf('@');
        int dotIndex = email.lastIndexOf('.');

        // Vérifications minimales
        if (atIndex < 1 || dotIndex < atIndex + 2 || dotIndex + 2 >= email.length()) {
            return false;
        }

        return true; // L'email est probablement valide
    }
    private Boolean isValidSignUPDETAILS() {
        if (encodedImage == null) {
            showToast("select profile image ");
            return false;
        } else if (binding
                .inputName.getText().toString().trim().isEmpty()) {
            showToast("Enter name ");
            return false;
        } else if (binding
                .inputEmail.getText().toString().trim().isEmpty()) {
            showToast("Enter Email ");
            return false;

        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            showToast("Enter valid Email");

            return false;
        } else if (binding
                .iputPassword.getText().toString().trim().isEmpty()) {
            showToast("Enter Password ");
            return false;

        } else if (binding.inputconfirmPassword.getText().toString().trim().isEmpty()) {
            showToast("Confirm your Password ");
            return false;

        } else if (!binding.iputPassword.getText().toString().equals(binding.inputconfirmPassword.getText().toString())) {
            showToast("Password and Confirm Password must be same ");
            return false;

        } else {
            return true;

        }
    }
    private void loading (Boolean isloading){
        if(isloading){
            binding.signupbtn.setVisibility(View.INVISIBLE);
            binding.progressbar.setVisibility(View.VISIBLE);

        }else{
            binding.signupbtn.setVisibility(View.VISIBLE);
            binding.progressbar.setVisibility(View.INVISIBLE);


        }
    }
}