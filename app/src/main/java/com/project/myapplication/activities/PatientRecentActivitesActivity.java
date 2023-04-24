package com.project.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.project.myapplication.databinding.ActivityPatientRecentActivitesBinding;

public class PatientRecentActivitesActivity extends AppCompatActivity {

    ActivityPatientRecentActivitesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPatientRecentActivitesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}