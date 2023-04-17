package com.project.myapplication.activities;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.project.myapplication.utilities.Constants.KEY_COLLECTION_DOCTORS;
import static com.project.myapplication.utilities.Constants.KEY_FCM_TOKEN;
import static com.project.myapplication.utilities.Constants.KEY_IMAGE;
import static com.project.myapplication.utilities.Constants.KEY_NAME;
import static com.project.myapplication.utilities.Constants.KEY_USER_ID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Application;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.myapplication.R;
import com.project.myapplication.databinding.ActivityHomeBinding;
import com.project.myapplication.utilities.PreferenceManager;
import com.project.myapplication.utilities.ToastUtility;
import com.zegocloud.uikit.prebuilt.call.config.ZegoNotificationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;

import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;
    PreferenceManager preferenceManager;
    Dialog dialog;

    ToastUtility toastUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        toastUtility = ToastUtility.getInstance(getApplicationContext());
        dialog = new Dialog(HomeActivity.this);
        dialog.setContentView(R.layout.logout_dialog);
        dialog.getWindow().setLayout(MATCH_PARENT,WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        preferenceManager = new PreferenceManager(getApplicationContext());
        getDoctorDetails();
        setListeners();
        startNewService();
    }

    private void startNewService() {
        Application application = getApplication();
        long appID = 1650569032;
        String appSign ="0558de15d80ffa7f4c7141eb750d1cb6ebc0122bc7a38122e9f7ead6c043f03a";
        String userID = preferenceManager.getString(KEY_NAME);
        String userName =userID;

        ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();
        callInvitationConfig.notifyWhenAppRunningInBackgroundOrQuit = true;
        ZegoNotificationConfig notificationConfig = new ZegoNotificationConfig();
        notificationConfig.sound = "zego_uikit_sound_call";
        notificationConfig.channelID = "CallInvitation";
        notificationConfig.channelName = "CallInvitation";
        ZegoUIKitPrebuiltCallInvitationService.init(getApplication(), appID, appSign, userID, userName,callInvitationConfig);
    }

    private void getDoctorDetails(){
        binding.textName.setText(preferenceManager.getString(KEY_NAME));
        byte[] bytes = Base64.decode(preferenceManager.getString(KEY_IMAGE),Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        Glide.with(HomeActivity.this).load(bitmap).into(binding.imageProfile);
    }
    private void setListeners(){
        binding.imageSignOut.setOnClickListener(v -> {
            MaterialCardView cancel = dialog.findViewById(R.id.cancelButton);
            MaterialCardView logout = dialog.findViewById(R.id.acceptLogoutButton);
            ImageView imageView = dialog.findViewById(R.id.imageLogout);
            Glide.with(HomeActivity.this).load(ContextCompat.getDrawable(HomeActivity.this, R.drawable.logout)).into(imageView);
            logout.setOnClickListener(i -> signOut());
            cancel.setOnClickListener(i -> dialog.dismiss());
            dialog.show();
        });
        binding.openChat.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),MainActivity.class)));
        binding.videoChat.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),VideoChatHome.class)));
        binding.addPatient.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),AddPatientActivity.class)));
        binding.viewAllPatients.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), PatientsActivity.class)));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ZegoUIKitPrebuiltCallInvitationService.unInit();
    }
}