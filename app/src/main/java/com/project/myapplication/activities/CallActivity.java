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
        binding.webView.post(() -> binding.webView.evaluateJavascript(function,null));
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
}