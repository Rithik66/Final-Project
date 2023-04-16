package com.project.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.project.myapplication.databinding.ActivityPatientVideoChatBinding;

public class PatientVideoChatActivity extends AppCompatActivity {

    ActivityPatientVideoChatBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPatientVideoChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}