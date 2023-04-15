package com.project.myapplication.activities;

import static com.project.myapplication.utilities.Constants.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.myapplication.databinding.ActivitySignUpBinding;
import com.project.myapplication.utilities.PreferenceManager;
import com.project.myapplication.utilities.ToastUtility;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private PreferenceManager preferenceManager;
    ToastUtility toastUtility;
    private String encodedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toastUtility = ToastUtility.getInstance(this);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners(){
        binding.textSignIn.setOnClickListener(v -> onBackPressed());
        binding.buttonSignUp.setOnClickListener(v -> {
            if(isValidSignUp()){
                signUp();
            }
        });
        binding.layoutImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
    }

    private void signUp(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String,Object> user = new HashMap<>();
        user.put(KEY_NAME,binding.inputName.getText().toString());
        user.put(KEY_EMAIL,binding.inputEmail.getText().toString());
        user.put(KEY_PASSWORD,binding.inputPassword.getText().toString());
        user.put(KEY_IMAGE,encodedImage);
        database.collection(KEY_COLLECTION_DOCTORS)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    loading(false);
                    preferenceManager.putBoolean(KEY_IS_SIGNED_IN,true);
                    preferenceManager.putString(KEY_USER_ID,documentReference.getId());
                    preferenceManager.putString(KEY_NAME,binding.inputName.getText().toString());
                    preferenceManager.putString(KEY_IMAGE,encodedImage);
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }).addOnFailureListener(exception -> {
                    loading(false);
                    toastUtility.exceptionToast(exception.getLocalizedMessage(),"SignUpActivity::signUp");
                });
    }

    private String encodeImage(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap,previewWidth,previewHeight,false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes,Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK){
                    if(result.getData()!=null){
                        Uri imageUri = result.getData().getData();
                        try{
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            Glide.with(SignUpActivity.this).load(bitmap).into(binding.imageProfile);
                            binding.textImage.setVisibility(View.GONE);
                            encodedImage = encodeImage(bitmap);
                        }catch(Exception e){
                            toastUtility.exceptionToast(e.getLocalizedMessage(),"SignUpActivity::pickImage");
                        }
                    }
                }
            }
    );

    private boolean isValidSignUp(){
        if(encodedImage==null){
            toastUtility.shortToast("Select profile image");
            return false;
        }else if(binding.inputName.getText().toString().trim().isEmpty()){
            toastUtility.shortToast("Enter name");
            return false;
        }else if(binding.inputEmail.getText().toString().trim().isEmpty()){
            toastUtility.shortToast("Enter email");
            return false;
        }else if(binding.inputPassword.getText().toString().trim().isEmpty()){
            toastUtility.shortToast("Enter password");
            return false;
        }else if(binding.inputConfirmPassword.getText().toString().trim().isEmpty()){
            toastUtility.shortToast("Confirm your password");
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()){
            toastUtility.shortToast("Enter a valid email");
            return false;
        }else if(!binding.inputPassword.getText().toString().equals(binding.inputConfirmPassword.getText().toString())){
            toastUtility.shortToast("Password and confirm password must be same");
            return false;
        }
        return true;
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.buttonSignUp.setVisibility(View.INVISIBLE);
        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSignUp.setVisibility(View.VISIBLE);
        }
    }
}