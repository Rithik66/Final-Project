package com.project.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.project.myapplication.databinding.ActivityVideoChatHomeBinding;

public class VideoChatHome extends AppCompatActivity {

    ActivityVideoChatHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoChatHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners(){
        binding.cardDoctors.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(),DoctorsVideoChatActivity.class));
        });
        binding.cardPatients.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(),PatientVideoChatActivity.class));
        });
        binding.backBtnImage.setOnClickListener(v -> onBackPressed());
    }
}