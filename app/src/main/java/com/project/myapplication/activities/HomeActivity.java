package com.project.myapplication.activities;

import static com.project.myapplication.utilities.Constants.KEY_IMAGE;
import static com.project.myapplication.utilities.Constants.KEY_NAME;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import com.bumptech.glide.Glide;
import com.project.myapplication.databinding.ActivityHomeBinding;
import com.project.myapplication.utilities.PreferenceManager;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        getDoctorDetails();
        setListeners();
    }

    private void getDoctorDetails(){
        binding.textName.setText(preferenceManager.getString(KEY_NAME));
        byte[] bytes = Base64.decode(preferenceManager.getString(KEY_IMAGE),Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        Glide.with(HomeActivity.this).load(bitmap).into(binding.imageProfile);
    }
    private void setListeners(){
        binding.openChat.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),MainActivity.class)));
        binding.videoChat.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),VideoChatHome.class)));
        binding.addPatient.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),AddPatientActivity.class)));
    }
}