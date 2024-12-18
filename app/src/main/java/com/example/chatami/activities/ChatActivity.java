package com.example.chatami.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Base64;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chatami.R;
import com.example.chatami.adapters.ChatAdapters;
import com.example.chatami.databinding.ActivityChatBinding;
import com.example.chatami.models.ChatMessage;
import com.example.chatami.models.User;
import com.example.chatami.utilities.Constants;
import com.example.chatami.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ChatActivity extends BaseActivity {
private ActivityChatBinding binding;
private User receiverUser;
private ChatAdapters chatAdapter;
private List<ChatMessage> chatMessages;
private PreferenceManager preferenceManager;
private FirebaseFirestore database ;
private Boolean isReceiverAvailable = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        loadReceiverdetails();
        init();
        listenMessages();
    }
    private void init(){
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapters(
         chatMessages,
                getBitmapFromEncodedString(receiverUser.image),
                preferenceManager.getString(Constants.KEY_USER_ID)
        );
        binding.receiycle.setAdapter(chatAdapter);
        database =FirebaseFirestore.getInstance();
    }

private void sendMessage() {
    HashMap<String, Object> message = new HashMap<>();
    message.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
    message.put(Constants.KEY_RECEIVER_ID, receiverUser.id);
    message.put(Constants.KEY_MESSAGE, binding.inputmessage.getText().toString());
    message.put(Constants.KEY_TIMESTAMP, new Date());
    database.collection(Constants.KEY_COLLECTION_CHAT).add(message);
    binding.inputmessage.setText(null);

}

private void listenAvailabilityofReceiver(){
        database.collection(Constants.KEY_COLLECTION_USERS).document(
                receiverUser.id

        ).addSnapshotListener(ChatActivity.this,(value, error) -> {
        if(error!= null){
        return;
        }if(value!=null){
            if(value.getLong(Constants.KEY_AVAILABLE)!=null){
                int availability = Objects.requireNonNull(
                        value.getLong(Constants.KEY_AVAILABLE)

                ).intValue();
                isReceiverAvailable =availability ==1;
            }
            }
        if(isReceiverAvailable){
            binding.texavailable.setVisibility(View.VISIBLE);
        }
        else{
            binding.texavailable.setVisibility(View.GONE);

        }
        });
}


private void listenMessages(){
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_RECEIVER_ID,receiverUser.id)
    .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID,receiverUser.id)
                .whereEqualTo(Constants.KEY_RECEIVER_ID,preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
}
private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
    if (error != null) {
        return;

    }
    if (value != null) {
        int count = chatMessages.size();
        for (DocumentChange documentChange : value.getDocumentChanges()) {
            if (documentChange.getType() == DocumentChange.Type.ADDED) {
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                chatMessage.receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                chatMessage.message = documentChange.getDocument().getString(Constants.KEY_MESSAGE);
                chatMessage.Datetime = getRedableDatTime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                chatMessages.add(chatMessage);
            }

        }
        Collections.sort(chatMessages,(obj1,obj2)-> obj1.dateObject.compareTo(obj2.dateObject));
        if(count == 0){
            chatAdapter.notifyDataSetChanged();
        }
else{
    chatAdapter.notifyItemRangeInserted(chatMessages.size(),chatMessages.size());
    binding.receiycle.smoothScrollToPosition(chatMessages.size()-1);
        }
binding.receiycle.setVisibility(View.VISIBLE);
    }
    binding.progressbar.setVisibility(View.GONE);

};

    private Bitmap getBitmapFromEncodedString(String encodedMessage){
        byte[] bytes = Base64.decode(encodedMessage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
    private void loadReceiverdetails(){
        receiverUser =(User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.textname.setText(receiverUser.name);
    }
    private void setListeners(){
        binding.imagback.setOnClickListener(v -> onBackPressed());
        binding.layoutsend.setOnClickListener(v -> sendMessage());
    }
    private String getRedableDatTime(Date date){
        return new SimpleDateFormat("MMMM dd,yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    @Override
    protected void onResume() {
        super.onResume();
        listenAvailabilityofReceiver();
    }
}