package com.example.chatami.adapters;
import android.util.Base64;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatami.databinding.ItemContainerBinding;
import com.example.chatami.listeners.UserListener;
import com.example.chatami.models.User;

import java.util.List;


public class UsersAdapters extends  RecyclerView.Adapter<UsersAdapters.UserViewHolder>{
     private final List<User> users;
    private final UserListener userListener;
    public UsersAdapters(List<User> users,UserListener userListener) {

            this.users = users;
            this.userListener= userListener;
        }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerBinding itemContainerBinding =ItemContainerBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false

        );
        return new UserViewHolder(itemContainerBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setUserData(users.get(position));

    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    class UserViewHolder extends RecyclerView.ViewHolder{
        ItemContainerBinding binding;
        UserViewHolder(ItemContainerBinding itemContainerBinding){
            super(itemContainerBinding.getRoot());
binding = itemContainerBinding;
        }
        void setUserData(User user){
            binding.textname.setText(user.name);
            binding.textemail.setText(user.email);
            binding.imageprofile.setImageBitmap(getUseImage(user.image));
            binding.getRoot().setOnClickListener(v -> userListener.onUserClicked(user));
        }
    }
    private Bitmap getUseImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}
