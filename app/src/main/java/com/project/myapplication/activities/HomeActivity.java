package com.project.myapplication.activities;

import static com.project.myapplication.utilities.Constants.KEY_IMAGE;
import static com.project.myapplication.utilities.Constants.KEY_NAME;
import static com.project.myapplication.utilities.Constants.KEY_USER_ID;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import com.bumptech.glide.Glide;
import com.project.myapplication.databinding.ActivityHomeBinding;
import com.project.myapplication.utilities.PreferenceManager;
import com.zegocloud.uikit.prebuilt.call.config.ZegoNotificationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;

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
        startNewService();
    }

    private void startNewService() {
        Application application = getApplication();
        long appID = 1650569032;
        String appSign ="0558de15d80ffa7f4c7141eb750d1cb6ebc0122bc7a38122e9f7ead6c043f03a";
        String userID = preferenceManager.getString(KEY_USER_ID);
        String userName =preferenceManager.getString(KEY_NAME);

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
        binding.openChat.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),MainActivity.class)));
        binding.videoChat.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),VideoChatHome.class)));
        binding.addPatient.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),AddPatientActivity.class)));
        binding.viewAllPatients.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), PatientsActivity.class)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ZegoUIKitPrebuiltCallInvitationService.unInit();
    }
}