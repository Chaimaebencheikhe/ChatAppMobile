package com.example.chatami.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;


import com.example.chatami.adapters.UsersAdapters;
import com.example.chatami.databinding.ActivityUsersBinding;
import com.example.chatami.listeners.UserListener;
import com.example.chatami.models.User;
import com.example.chatami.utilities.Constants;
import com.example.chatami.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends BaseActivity implements UserListener {
private ActivityUsersBinding binding ;
private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
binding =ActivityUsersBinding.inflate(getLayoutInflater());
setContentView(binding.getRoot());
preferenceManager = new PreferenceManager(getApplicationContext());
getUsers();
    }
    private  void setListeners() {
            binding.imageback.setOnClickListener(v -> onBackPressed());
        }

        private void getUsers(){
    loading(true);
        FirebaseFirestore database= FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<User> users = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (currentUserId.equals(queryDocumentSnapshot.getId())) {
                                continue;
                            }
                            User user = new User();
                            user.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                            user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                            user.image = queryDocumentSnapshot.getString(Constants.KEY_image);
                            user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                            user.id=queryDocumentSnapshot.getId();
                            users.add(user);
                        }
                        if (users.size() > 0) {
                            UsersAdapters usersAdapters = new UsersAdapters(users,this);
                            binding.usersReceiycleview.setAdapter(usersAdapters);
                            binding.usersReceiycleview.setVisibility(View.VISIBLE);

                        } else {
                            showErrorMessage();
                        }}
                    else{
                        showErrorMessage();
                    }
                });
    }
    private void showErrorMessage() {
        binding.texterrormessage.setText(String.format("%s", "No User Available "));
    }
    private void loading (Boolean isloading){
        if(isloading){
            binding.progressbar.setVisibility(View.VISIBLE);

        }else{
            binding.progressbar.setVisibility(View.INVISIBLE);


        }}

    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
        intent.putExtra(Constants.KEY_USER,user);
        startActivity(intent);
        finish();

    }
}
