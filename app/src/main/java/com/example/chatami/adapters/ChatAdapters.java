package com.example.chatami.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatami.databinding.ItemContainerReveidMessageBinding;
import com.example.chatami.databinding.ItemContainerSentMessageBinding;
import com.example.chatami.models.ChatMessage;

import java.util.List;

public class ChatAdapters extends  RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<ChatMessage> chatMessages;
    private final Bitmap receiverProfileImage;
    private final String  senderId;


     public static final int VIEW_TYPE_SENT =1;
    public static final int VIEW_TYPE_RECEIVED =2;

    public ChatAdapters(List<ChatMessage> chatMessages, Bitmap receiverProfileImage, String senderId) {
        this.chatMessages = chatMessages;
        this.receiverProfileImage = receiverProfileImage;
        this.senderId = senderId;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_SENT){
            return new sentMessageViewHolder(
                    ItemContainerSentMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );

        }else {
            return new ReceivedMessageViewHolder(
                    ItemContainerReveidMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
);
    }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            ((sentMessageViewHolder) holder).setData(chatMessages.get(position));
        } else {
            ((ReceivedMessageViewHolder) holder).setData(chatMessages.get(position), receiverProfileImage);
        }
    }
    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
    if(chatMessages.get(position).senderId.equals(senderId)){
   return VIEW_TYPE_SENT;
    }
    else
    {
    return VIEW_TYPE_RECEIVED;
}
    }

    static class sentMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerSentMessageBinding binding;

        public sentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding) {
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }

        void setData(ChatMessage chatMessage) {
            binding.textmessage.setText(chatMessage.message);
            binding.textdatetime.setText(chatMessage.Datetime);

        }
    }
        static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
            private final ItemContainerReveidMessageBinding binding;

            ReceivedMessageViewHolder(ItemContainerReveidMessageBinding itemContainerReveidMessageBinding) {
                super(itemContainerReveidMessageBinding.getRoot());
                binding = itemContainerReveidMessageBinding;

            }

            void setData(ChatMessage chatMessage, Bitmap receiverProfileImage) {
                binding.textmessage.setText(chatMessage.message);
                binding.textdatetime.setText(chatMessage.Datetime);
                binding.imageprofile.setImageBitmap(receiverProfileImage);


            }}
    }
