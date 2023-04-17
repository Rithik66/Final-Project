package com.project.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.project.myapplication.R;
import com.project.myapplication.databinding.ActivityViewPatientDetailsBinding;

public class ViewPatientDetailsActivity extends AppCompatActivity {

    ActivityViewPatientDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewPatientDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners() {
        binding.backBtnImage.setOnClickListener(v -> onBackPressed());
    }
}