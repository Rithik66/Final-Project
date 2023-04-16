package com.project.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.project.myapplication.databinding.ActivityDoctorsVideoChatBinding;

public class DoctorsVideoChatActivity extends AppCompatActivity {

    ActivityDoctorsVideoChatBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoctorsVideoChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}