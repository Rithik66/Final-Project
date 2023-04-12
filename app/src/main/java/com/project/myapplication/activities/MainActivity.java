package com.project.myapplication.activities;

import static android.view.ViewGroup.LayoutParams.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.project.myapplication.R;
import com.project.myapplication.databinding.ActivityMainBinding;

import static com.project.myapplication.utilities.Constants.*;
import com.project.myapplication.utilities.PreferenceManager;
import com.project.myapplication.utilities.ToastUtility;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    ToastUtility toastUtility;
    Dialog dialog;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        toastUtility = ToastUtility.getInstance(getApplicationContext());
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.logout_dialog);
        dialog.getWindow().setLayout(MATCH_PARENT,WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        preferenceManager = new PreferenceManager(getApplicationContext());
        loadDoctorDetails();
        getToken();
        setListeners();
    }

    private void setListeners(){
        binding.imageSignOut.setOnClickListener(v -> {
            MaterialCardView cancel = dialog.findViewById(R.id.cancelButton);
            MaterialCardView logout = dialog.findViewById(R.id.acceptLogoutButton);
            ImageView imageView = dialog.findViewById(R.id.imageLogout);
            Glide.with(MainActivity.this).load(ContextCompat.getDrawable(MainActivity.this, R.drawable.logout)).into(imageView);
            logout.setOnClickListener(i -> signOut());
            cancel.setOnClickListener(i -> dialog.dismiss());
            dialog.show();
        });
        binding.fabNewChat.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),UsersActivity.class)));
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
                .addOnFailureListener(e -> toastUtility.exceptionToast(e.getLocalizedMessage(),"MainActivity::updateToken"));
    }

    public void signOut(){
        toastUtility.shortToast("Signing out...");
        try {
            Thread.sleep(1000);
        }catch(Exception e){
            toastUtility.exceptionToast(e.getLocalizedMessage(),"MainActivity::signOut");
        }
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
        dialog.dismiss();
    }
}