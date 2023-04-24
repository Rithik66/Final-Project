package com.project.myapplication.activities;

import static com.project.myapplication.utilities.Constants.KEY_USER;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import com.project.myapplication.databinding.ActivityOutgoingBinding;
import com.project.myapplication.firebase.MessagingService;
import com.project.myapplication.models.User;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.URL;

import im.zego.zpns.internal.receiver.FCMMessageService;

public class OutgoingActivity extends AppCompatActivity {
    ActivityOutgoingBinding binding;
    User recievedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOutgoingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadRecievedDetails();
        checkStatus();
    }

    private void checkStatus() {
        URL serverUrl;
        try {
            serverUrl = new URL("https://meet.jit.si");
            JitsiMeetConferenceOptions defaultOptions = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(serverUrl)
                    .build();
            JitsiMeet.setDefaultConferenceOptions(defaultOptions);
            JitsiMeetConferenceOptions start = new JitsiMeetConferenceOptions.Builder()
                    .setRoom("Video Call")
                    .build();
            JitsiMeetActivity.launch(getApplicationContext(),start);
        }catch (Exception e){

        }
    }


    private void loadRecievedDetails(){
        recievedUser = (User) getIntent().getSerializableExtra("user");
    }

    private Bitmap getUserImage(String encodedImage){
        byte[] bytes = android.util.Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}