package com.project.myapplication.activities;

import static com.project.myapplication.utilities.Constants.KEY_IMAGE;
import static com.project.myapplication.utilities.Constants.KEY_NAME;
import static com.project.myapplication.utilities.Constants.KEY_USER;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;

import com.bumptech.glide.Glide;
import com.project.myapplication.R;
import com.project.myapplication.databinding.ActivityViewPatientDetailsBinding;
import com.project.myapplication.models.Patient;
import com.project.myapplication.models.User;
import com.project.myapplication.utilities.ToastUtility;

import java.text.DecimalFormat;
import java.util.Random;

public class ViewPatientDetailsActivity extends AppCompatActivity {

    ActivityViewPatientDetailsBinding binding;
    Patient recievedPatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewPatientDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        loadPatientDetails();
    }

    private void loadPatientDetails(){
        recievedPatient = (Patient) getIntent().getSerializableExtra(KEY_USER);
        binding.userName.setText(recievedPatient.name);
        binding.viewPatientDetails.setOnClickListener(v -> {
            ToastUtility.getInstance(getApplicationContext()).shortToast("OUT");
            Intent intent = new Intent(getApplicationContext(),IndividualPatientDetailsActivity.class);
            intent.putExtra(KEY_USER,recievedPatient);
            startActivity(intent);
        });
        binding.viewPatientRecentActivities.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(),PatientRecentActivitesActivity.class);
            intent.putExtra(KEY_USER,recievedPatient);
            startActivity(intent);
        });

        Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                Random random = new Random();
                double randomDecimal = random.nextDouble() * 6 + 93;
                String formattedDecimal = decimalFormat.format(randomDecimal);

                DecimalFormat decimalFormat1 = new DecimalFormat("0.0");
                Random random1 = new Random();
                double randomDecimal1 = random.nextDouble() * 3 + 96;
                String formattedDecimal1 = decimalFormat1.format(randomDecimal1);
                binding.spo2.setText(formattedDecimal);
                binding.temperature.setText(formattedDecimal1+"F");
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnable);
    }

    private void setListeners() {
        binding.backBtnImage.setOnClickListener(v -> onBackPressed());
    }
}