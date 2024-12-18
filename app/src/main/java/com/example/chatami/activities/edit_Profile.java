package com.example.chatami.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatami.databinding.ActivityEditProfileBinding;
import com.example.chatami.utilities.Constants;
import com.example.chatami.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;

public class edit_Profile extends AppCompatActivity {

    private ActivityEditProfileBinding binding;
    private PreferenceManager preferenceManager;
    private String encodedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());
        loadUserDetails();
        setListeners();
    }

    private void loadUserDetails() {
        // Charger les données utilisateur depuis PreferenceManager
        binding.inputName.setText(preferenceManager.getString(Constants.KEY_NAME));
        binding.inputEmail.setText(preferenceManager.getString(Constants.KEY_EMAIL));
        String image = preferenceManager.getString(Constants.KEY_image);
        if (image != null) {
            byte[] bytes = Base64.decode(image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            binding.imageProfile.setImageBitmap(bitmap);
        }
    }

    private void setListeners() {
        binding.textChangeImage.setOnClickListener(v -> pickImage.launch(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)));
        binding.buttonSave.setOnClickListener(v -> saveChanges());
    }

    private void saveChanges() {
        if (!validateInputs()) {
            return;  // Si la validation échoue, on ne sauvegarde pas les données
        }

        // Mettre à jour les données dans Firestore
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        String userId = preferenceManager.getString(Constants.KEY_USER_ID);

        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_NAME, binding.inputName.getText().toString());
        updates.put(Constants.KEY_EMAIL, binding.inputEmail.getText().toString());
        if (!binding.inputPassword.getText().toString().trim().isEmpty()) {
            updates.put(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString());
        }
        if (encodedImage != null) {
            updates.put(Constants.KEY_image, encodedImage);
            preferenceManager.putString(Constants.KEY_image, encodedImage);
        }

        database.collection(Constants.KEY_COLLECTION_USERS)
                .document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    showToast("Profile Updated Successfully");
                    preferenceManager.putString(Constants.KEY_NAME, binding.inputName.getText().toString());
                    preferenceManager.putString(Constants.KEY_EMAIL, binding.inputEmail.getText().toString());
                    finish();
                })
                .addOnFailureListener(e -> showToast("Failed to update profile"));
    }

    private boolean validateInputs() {
        // Vérification du champ Nom
        if (binding.inputName.getText().toString().trim().isEmpty()) {
            showToast("Please enter your name.");
            return false;
        }

        // Vérification du champ Email
        String email = binding.inputEmail.getText().toString().trim();
        if (email.isEmpty()) {
            showToast("Please enter your email.");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Please enter a valid email address.");
            return false;
        }

        // Vérification du champ Mot de passe
        if (binding.inputPassword.getText().toString().trim().isEmpty()) {
            showToast("Please enter your password.");
            return false;
        }

        return true;
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        binding.imageProfile.setImageBitmap(bitmap);
                        encodedImage = encodeImage(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
    );

    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
        byte[] bytes = outputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
