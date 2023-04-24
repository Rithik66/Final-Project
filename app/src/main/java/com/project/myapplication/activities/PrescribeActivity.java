package com.project.myapplication.activities;

import static com.project.myapplication.utilities.Constants.*;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.project.myapplication.databinding.ActivityPrescribeBinding;
import com.project.myapplication.models.Appointment;

public class PrescribeActivity extends AppCompatActivity {

    ActivityPrescribeBinding binding;
    Appointment appointment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrescribeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    private void loadRecievedDetails(){
        appointment = (Appointment) getIntent().getSerializableExtra(KEY_USER);
    }
}