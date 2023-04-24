package com.project.myapplication.activities;

import static com.project.myapplication.utilities.Constants.KEY_AADHAR_NUMBER;
import static com.project.myapplication.utilities.Constants.KEY_BLOOD_GROUP;
import static com.project.myapplication.utilities.Constants.KEY_COLLECTION_PATIENTS;
import static com.project.myapplication.utilities.Constants.KEY_CONTACT_NUMBER;
import static com.project.myapplication.utilities.Constants.KEY_DATE_OF_BIRTH;
import static com.project.myapplication.utilities.Constants.KEY_EMAIL;
import static com.project.myapplication.utilities.Constants.KEY_IMAGE;
import static com.project.myapplication.utilities.Constants.KEY_NAME;
import static com.project.myapplication.utilities.Constants.KEY_USER;
import static com.project.myapplication.utilities.Constants.KEY_USER_ID;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.project.myapplication.adapters.PatientsAdapter;
import com.project.myapplication.databinding.ActivityPatientsBinding;
import com.project.myapplication.listeners.PatientListener;
import com.project.myapplication.models.Patient;
import com.project.myapplication.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class PatientsActivity extends AppCompatActivity implements PatientListener {
    ActivityPatientsBinding binding;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPatientsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());
        getUsers();
        setListeners();
    }

    private void setListeners() {
        binding.backBtnImage.setOnClickListener(v -> onBackPressed());
    }

    private void getUsers() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(KEY_COLLECTION_PATIENTS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = preferenceManager.getString(KEY_USER_ID);
                    if(task.isSuccessful() && task.getResult()!=null){
                        List<Patient> patients = new ArrayList<>();
                        for(QueryDocumentSnapshot queryDocumentSnapshot:task.getResult()){
                            if(currentUserId.equals(queryDocumentSnapshot.getId())) continue;
                            Patient patient = new Patient();
                            patient.name = queryDocumentSnapshot.getString(KEY_NAME);
                            patient.email = queryDocumentSnapshot.getString(KEY_EMAIL);
                            patient.image = queryDocumentSnapshot.getString(KEY_IMAGE);
                            patient.bloodGroup = queryDocumentSnapshot.getString(KEY_BLOOD_GROUP);
                            patient.contactNumber = queryDocumentSnapshot.getString(KEY_CONTACT_NUMBER);
                            patient.dateOfBirth = queryDocumentSnapshot.getString(KEY_DATE_OF_BIRTH);
                            patient.aadharNumber = queryDocumentSnapshot.getString(KEY_AADHAR_NUMBER);
                            patient.id = queryDocumentSnapshot.getId();
                            patients.add(patient);
                        }
                        if(patients.size()>0){
                            PatientsAdapter patientsAdapter = new PatientsAdapter(patients,this);
                            binding.patientsRecyclerView.setAdapter(patientsAdapter);
                            binding.patientsRecyclerView.setVisibility(View.VISIBLE);
                        }else{
                            showErrorMessage();
                        }
                    }
                    else{
                        showErrorMessage();
                    }
                });
    }

    private void showErrorMessage(){
        binding.textErrorMessage.setText("No user(s) available");
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPatientClicked(Patient patient) {
        Intent intent = new Intent(getApplicationContext(), ViewPatientDetailsActivity.class);
        intent.putExtra(KEY_USER,patient);
        startActivity(intent);
    }
}