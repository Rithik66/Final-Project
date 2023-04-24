package com.project.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.project.myapplication.databinding.ActivityIncomingBinding;
import com.project.myapplication.databinding.ActivityOutgoingBinding;

public class IncomingActivity extends AppCompatActivity {
    ActivityIncomingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIncomingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}