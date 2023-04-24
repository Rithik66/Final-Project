package com.project.myapplication.activities;

import static android.Manifest.permission.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.os.Bundle;

import com.project.myapplication.R;
import com.project.myapplication.databinding.ActivityCallBinding;
import com.project.myapplication.utilities.CustomJavascriptInterface;
import com.project.myapplication.utilities.ToastUtility;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.UUID;


public class CallActivity extends AppCompatActivity {

    ActivityCallBinding binding;
    String uniqueId;
    boolean isPeerConnected = false;
    boolean isAudio = true;
    boolean isVideo = true;
    String createdBy;
    boolean pageExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupWebView();
        setListeners();
    }

    private void setupWebView() {
        binding.webView.setWebChromeClient(new WebChromeClient(){
           @Override
           public void onPermissionRequest(PermissionRequest request) {
               super.onPermissionRequest(request);
               request.grant(request.getResources());
           }
       });

        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        binding.webView.addJavascriptInterface(new CustomJavascriptInterface(this),"Android");

        loadVideoCall();
    }

    private void loadVideoCall() {
        String filePath = "file:android_asset/call.html";
        binding.webView.loadUrl(filePath);

        binding.webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                initializePeer();
            }
        });
    }

    private void initializePeer() {
        uniqueId = UUID.randomUUID().toString();
        callJavaScriptFunction("javascript:init(\""+uniqueId+"\")");
    }

    public void callJavaScriptFunction(String function){
        binding.webView.post(new Runnable() {
            @Override
            public void run() {
                binding.webView.evaluateJavascript(function,null);
            }
        });
    }

    private void setListeners() {
        binding.mic.setOnClickListener(v -> {
            isAudio = !isAudio;
            callJavaScriptFunction("javascript:toggleAudio(\""+isAudio+"\")");
            if(!isAudio){
                binding.mic.setImageResource(R.drawable.ic_mic_off);
            }else{
                binding.mic.setImageResource(R.drawable.ic_mic);
            }
        });
    }
    /*
    * final int PERMISSION_REQ_ID = 12;
    final String[] REQUESTED_PERMISSIONS = {RECORD_AUDIO,CAMERA};
    private final String appId = "50dd32a5dcd74c0ea1bba8d7045ac88b  ";
    private String channelName = "sample";
    private String token = "007eJxTYMhc5D5rc8my9d5/tz9KqW1JipjLGHhyob2nsNqSYt3qt8cVGEwNUlKMjRJNU5JTzE2SDVITDZOSEi1SzA1MTBOTLSySmq1tUxoCGRkynXcxMTJAIIjPxlCcmFuQk8rAAADCjyCc";
    private int uid = 0;
    private boolean isJoined = false;

    private RtcEngine agoraEngine;
    private SurfaceView localSurfaceView;
    private SurfaceView remoteSurfaceView;

    ActivityCallBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        if (!checkSelfPermission()) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);
        }
        setupVideoSDKEngine();
    }

    private void setupVideoSDKEngine() {
        try {
            RtcEngineConfig config = new RtcEngineConfig();
            config.mContext = getBaseContext();
            config.mAppId = appId;
            config.mEventHandler = mRtcEventHandler;
            agoraEngine = RtcEngine.create(config);
            // By default, the video module is disabled, call enableVideo to enable it.
            agoraEngine.enableVideo();
        } catch (Exception e) {
            showMessage(e.toString());
        }
    }
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        // Listen for the remote host joining the channel to get the uid of the host.
        public void onUserJoined(int uid, int elapsed) {
            showMessage("Remote user joined " + uid);

            // Set the remote video view
            runOnUiThread(() -> setupRemoteVideo(uid));
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            isJoined = true;
            showMessage("Joined Channel " + channel);
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            showMessage("Remote user offline " + uid + " " + reason);
            runOnUiThread(() -> remoteSurfaceView.setVisibility(View.GONE));
        }
    };

    private void setupRemoteVideo(int uid) {
        FrameLayout container = findViewById(R.id.remote_video_view_container);
        remoteSurfaceView = new SurfaceView(getBaseContext());
        remoteSurfaceView.setZOrderMediaOverlay(true);
        container.addView(remoteSurfaceView);
        agoraEngine.setupRemoteVideo(new VideoCanvas(remoteSurfaceView, VideoCanvas.RENDER_MODE_FIT, uid));
        // Display RemoteSurfaceView.
        remoteSurfaceView.setVisibility(View.VISIBLE);
    }

    private void setupLocalVideo() {
        FrameLayout container = findViewById(R.id.local_video_view_container);
        // Create a SurfaceView object and add it as a child to the FrameLayout.
        localSurfaceView = new SurfaceView(getBaseContext());
        container.addView(localSurfaceView);
        // Call setupLocalVideo with a VideoCanvas having uid set to 0.
        agoraEngine.setupLocalVideo(new VideoCanvas(localSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, 0));
    }

    public void joinChannel(View view) {
        if (checkSelfPermission()) {
            ChannelMediaOptions options = new ChannelMediaOptions();

            // For a Video call, set the channel profile as COMMUNICATION.
            options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION;
            // Set the client role as BROADCASTER or AUDIENCE according to the scenario.
            options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
            // Display LocalSurfaceView.
            setupLocalVideo();
            localSurfaceView.setVisibility(View.VISIBLE);
            // Start local preview.
            agoraEngine.startPreview();
            // Join the channel with a temp token.
            // You need to specify the user ID yourself, and ensure that it is unique in the channel.
            agoraEngine.joinChannel(token, channelName, uid, options);
        } else {
            Toast.makeText(getApplicationContext(), "Permissions was not granted", Toast.LENGTH_SHORT).show();
        }
    }

    public void leaveChannel(View view) {
        if (!isJoined) {
            showMessage("Join a channel first");
        } else {
            agoraEngine.leaveChannel();
            showMessage("You left the channel");
            // Stop remote video rendering.
            if (remoteSurfaceView != null) remoteSurfaceView.setVisibility(View.GONE);
            // Stop local video rendering.
            if (localSurfaceView != null) localSurfaceView.setVisibility(View.GONE);
            isJoined = false;
        }
    }

    private boolean checkSelfPermission(){
        return !(ContextCompat.checkSelfPermission(
                        this,REQUESTED_PERMISSIONS[0])!= PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                        this,REQUESTED_PERMISSIONS[1])!= PackageManager.PERMISSION_GRANTED);
    }

    void showMessage(String message) {
        runOnUiThread(() ->
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show());
    }

    private void setListeners() {
        binding.JoinButton.setOnClickListener(v -> joinCall());
        binding.LeaveButton.setOnClickListener(v -> cancelCall());
    }

    private void cancelCall() {
        if(!isJoined){
            showMessage("Join a channel first");
        }else{
            agoraEngine.leaveChannel();
            showMessage("You left the channel");
            if(remoteSurfaceView!=null) remoteSurfaceView.setVisibility(View.GONE);
            if(localSurfaceView!=null) localSurfaceView.setVisibility(View.GONE);
            isJoined = false;
        }
    }

    private void joinCall() {
        if(checkSelfPermission()){
            ChannelMediaOptions option = new ChannelMediaOptions();
            option.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION;
            option.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
            setupLocalVideo();
            localSurfaceView.setVisibility(View.VISIBLE);
            agoraEngine.startPreview();
            agoraEngine.joinChannel(token,channelName,uid,option);
        }else{
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        agoraEngine.stopPreview();
        agoraEngine.leaveChannel();

        // Destroy the engine in a sub-thread to avoid congestion
        new Thread(() -> {
            RtcEngine.destroy();
            agoraEngine = null;
        }).start();
    }
    *
    * */
}