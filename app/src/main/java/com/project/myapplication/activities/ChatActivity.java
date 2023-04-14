package com.project.myapplication.activities;

import static com.project.myapplication.utilities.Constants.*;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.myapplication.adapters.ChatAdapter;
import com.project.myapplication.databinding.ActivityChatBinding;
import com.project.myapplication.models.ChatMessage;
import com.project.myapplication.models.User;
import com.project.myapplication.utilities.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.*;


public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;
    User recievedUser;
    List<ChatMessage> chatMessages;
    ChatAdapter chatAdapter;
    PreferenceManager preferenceManager;
    FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadRecievedDetails();
        setListeners();
        init();
        listenMessages();
    }



    private void sendMessage(){
        HashMap<String,Object> message = new HashMap<>();
        message.put(KEY_SENDER_ID,preferenceManager.getString(KEY_USER_ID));
        message.put(KEY_RECEIVER_ID,recievedUser.id);
        message.put(KEY_MESSAGE,binding.sendMessage.getText().toString());
        message.put(KEY_TIMESTAMP,new Date());
        database.collection(KEY_COLLECTION_CHAT).add(message);
        binding.sendMessage.setText(null);
    }

    private void loadRecievedDetails(){
        recievedUser = (User) getIntent().getSerializableExtra(KEY_USER);
        binding.userName.setText(recievedUser.name);
    }

    private void init(){
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages,getUserImage(recievedUser.image),preferenceManager.getString(KEY_USER_ID));
        binding.recyclerView.setAdapter(chatAdapter);
        database = FirebaseFirestore.getInstance();

    }

    private final EventListener<QuerySnapshot> eventListener = (value,error)->{
        if(error!=null){
            return;
        }
        if(value!=null){
            int count = chatMessages.size();
            for (DocumentChange documentChange:value.getDocumentChanges()){
                if (documentChange.getType()== DocumentChange.Type.ADDED){
                    ChatMessage chatMessage= new ChatMessage();
                    chatMessage.senderId = documentChange.getDocument().getString(KEY_SENDER_ID);
                    chatMessage.recieverId = documentChange.getDocument().getString(KEY_RECEIVER_ID);
                    chatMessage.message = documentChange.getDocument().getString(KEY_MESSAGE);
                    chatMessage.dateTime = getReadableDateAndTime(documentChange.getDocument().getDate(KEY_TIMESTAMP));
                    chatMessage.dateObject = documentChange.getDocument().getDate(KEY_TIMESTAMP);
                    chatMessages.add(chatMessage);
                }
            }
            Collections.sort(chatMessages,(obj1,obj2)->obj1.dateObject.compareTo(obj2.dateObject));
            if (count==0) chatAdapter.notifyDataSetChanged();
            else{
                chatAdapter.notifyItemRangeInserted(chatMessages.size(),chatMessages.size());
                binding.recyclerView.smoothScrollToPosition(chatMessages.size()-1);
            }
            binding.recyclerView.setVisibility(View.VISIBLE);
        }
        binding.progressBar.setVisibility(View.GONE);
    };

    private void listenMessages(){
        database.collection(KEY_COLLECTION_CHAT)
                .whereEqualTo(KEY_SENDER_ID,preferenceManager.getString(KEY_USER_ID))
                .whereEqualTo(KEY_RECEIVER_ID,recievedUser.id)
                .addSnapshotListener(eventListener);
        database.collection(KEY_COLLECTION_CHAT)
                .whereEqualTo(KEY_SENDER_ID,recievedUser.id)
                .whereEqualTo(KEY_RECEIVER_ID,preferenceManager.getString(KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private Bitmap getUserImage(String encodedImage){
        byte[] bytes = android.util.Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }

    private void setListeners(){
        binding.backBtnImage.setOnClickListener(v -> onBackPressed());
        binding.layoutSend.setOnClickListener(v -> sendMessage());
    }

    private String getReadableDateAndTime(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a",Locale.getDefault()).format(date);
    }
}