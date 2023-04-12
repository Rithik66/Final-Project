package com.project.myapplication.activities;

import static com.project.myapplication.utilities.Constants.*;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.project.myapplication.databinding.ActivityChatBinding;
import com.project.myapplication.models.User;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;
    User recievedUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadRecievedDetails();
        setListeners();
    }

    private void loadRecievedDetails(){
        recievedUser = (User) getIntent().getSerializableExtra(KEY_USER);
        binding.userName.setText(recievedUser.name);
    }

    private void setListeners(){
        binding.backBtnImage.setOnClickListener(v -> onBackPressed());
    }
}