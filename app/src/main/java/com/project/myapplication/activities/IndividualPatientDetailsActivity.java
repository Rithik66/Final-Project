package com.project.myapplication.activities;

import static com.project.myapplication.utilities.Constants.KEY_USER;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import com.project.myapplication.databinding.ActivityIndividualPatientDetailsBinding;
import com.project.myapplication.models.Patient;
import com.project.myapplication.utilities.ToastUtility;

public class IndividualPatientDetailsActivity extends AppCompatActivity {

    ActivityIndividualPatientDetailsBinding binding;
    Patient recievedPatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIndividualPatientDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadPatientDetails();
        setListeners();
    }

    private void setListeners() {
        binding.backBtnImage.setOnClickListener(v -> onBackPressed());
    }

    private void loadPatientDetails() {
        recievedPatient = (Patient) getIntent().getSerializableExtra(KEY_USER);
        ToastUtility.getInstance(getApplicationContext()).shortToast("IN");
        binding.userName.setText(recievedPatient.name);
        binding.name.setText(recievedPatient.name);
        binding.imageProfile.setImageBitmap(getUserImage(recievedPatient.image));
        binding.bg.setText(recievedPatient.bloodGroup);
        binding.aadhar.setText(recievedPatient.aadharNumber);
        binding.contact.setText(recievedPatient.contactNumber);
        binding.dob.setText(recievedPatient.dateOfBirth);
        binding.email.setText(recievedPatient.email);
    }

    private Bitmap getUserImage(String encodedImage){
        byte[] bytes = android.util.Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}