package com.project.myapplication.activities;

import static com.project.myapplication.utilities.Constants.*;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    String conversationId;

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
        if(conversationId!=null){
            updateConversation(binding.sendMessage.getText().toString());
        }else{
            HashMap<String,Object> conversations = new HashMap<>();
            conversations.put(KEY_SENDER_ID,preferenceManager.getString(KEY_USER_ID));
            conversations.put(KEY_SENDER_NAME,preferenceManager.getString(KEY_NAME));
            conversations.put(KEY_SENDER_IMAGE,preferenceManager.getString(KEY_IMAGE));
            conversations.put(KEY_RECEIVER_ID,recievedUser.id);
            conversations.put(KEY_RECEIVER_NAME,recievedUser.name);
            conversations.put(KEY_RECEIVER_IMAGE,recievedUser.image);
            conversations.put(KEY_LAST_MESSAGE,binding.sendMessage.getText().toString());
            conversations.put(KEY_TIMESTAMP,new Date());
            addConversation(conversations);
        }
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
        if(conversationId==null){
            checkForConversation();
        }
    };

    private void addConversation(HashMap<String,Object> conversation){
        database.collection(KEY_COLLECTION_CONVERSATIONS)
                .add(conversation)
                .addOnSuccessListener(documentReference -> conversationId = documentReference.getId());
    }

    private void updateConversation(String message){
        DocumentReference documentReference = database.collection(KEY_COLLECTION_CONVERSATIONS)
                .document(conversationId);
        documentReference.update(KEY_LAST_MESSAGE,message,KEY_TIMESTAMP,new Date());
    }

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
        binding.layoutSend.setOnClickListener(v -> {
            if(binding.sendMessage.getText().toString()==null || binding.sendMessage.getText().toString().isEmpty())
                Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
            else sendMessage();
        });
    }

    private String getReadableDateAndTime(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a",Locale.getDefault()).format(date);
    }

    private void checkForConversation(){
        if(chatMessages.size()!=0){
            checkForConversationsRemotely(preferenceManager.getString(KEY_USER_ID),recievedUser.id);
            checkForConversationsRemotely(recievedUser.id,preferenceManager.getString(KEY_USER_ID));
        }
    }

    private void checkForConversationsRemotely(String senderId,String receiverId) {
        database.collection(KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(KEY_SENDER_ID,senderId)
                .whereEqualTo(KEY_RECEIVER_ID,receiverId)
                .get()
                .addOnCompleteListener(conversationOnCompleteListener);
    }

    private final OnCompleteListener<QuerySnapshot> conversationOnCompleteListener = task -> {
        if(task.isSuccessful() && task.getResult()!=null && task.getResult().getDocuments().size()>0){
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conversationId = documentSnapshot.getId();
        }
    };
}