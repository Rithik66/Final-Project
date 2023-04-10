package com.project.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.project.myapplication.databinding.ActivityMainBinding;
import static com.project.myapplication.utilities.Constants.*;
import com.project.myapplication.utilities.PreferenceManager;
import com.project.myapplication.utilities.ToastUtility;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    ToastUtility toastUtility;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        toastUtility = ToastUtility.getInstance(getApplicationContext());
        preferenceManager = new PreferenceManager(getApplicationContext());
        loadDoctorDetails();
        getToken();
        setListeners();
    }

    private void setListeners(){
        binding.imageSignOut.setOnClickListener(v -> signOut());
    }

    private void loadDoctorDetails(){
        binding.textName.setText(preferenceManager.getString(KEY_NAME));
        byte[] bytes = Base64.decode(preferenceManager.getString(KEY_IMAGE),Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        Glide.with(MainActivity.this).load(bitmap).into(binding.imageProfile);
    }

    public void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    public void updateToken(String token){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(KEY_COLLECTION_DOCTORS).document(preferenceManager.getString(KEY_USER_ID));
        documentReference.update(KEY_FCM_TOKEN,token)
                .addOnSuccessListener(unused -> toastUtility.shortToast("Token updated successfully"))
                .addOnFailureListener(e -> toastUtility.exceptionToast(e.getLocalizedMessage(),"MainActivity::updateToken"));
    }

    public void signOut(){
        toastUtility.shortToast("Signing out...");
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(KEY_COLLECTION_DOCTORS).document(preferenceManager.getString(KEY_USER_ID));
        HashMap<String,Object> updates = new HashMap<>();
        updates.put(KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clear();
                    startActivity(new Intent(getApplicationContext(),SignInActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> toastUtility.longToast("Unable to sign in"));
    }
}