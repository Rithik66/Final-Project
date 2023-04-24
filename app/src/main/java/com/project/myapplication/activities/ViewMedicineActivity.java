package com.project.myapplication.activities;

import static com.project.myapplication.utilities.Constants.KEY_COLLECTION_APPOINTMENTS;
import static com.project.myapplication.utilities.Constants.KEY_COLLECTION_MEDICINES;
import static com.project.myapplication.utilities.Constants.KEY_DATE;
import static com.project.myapplication.utilities.Constants.KEY_NAME;
import static com.project.myapplication.utilities.Constants.KEY_STATUS;
import static com.project.myapplication.utilities.Constants.KEY_TIME;
import static com.project.myapplication.utilities.Constants.KEY_USER_ID;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.project.myapplication.adapters.AppointmentAdapter;
import com.project.myapplication.adapters.MedicinesAdapter;
import com.project.myapplication.databinding.ActivityViewMedicineBinding;
import com.project.myapplication.models.Appointment;
import com.project.myapplication.models.Medicines;
import com.project.myapplication.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class ViewMedicineActivity extends AppCompatActivity {

    ActivityViewMedicineBinding binding;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewMedicineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
        getMedicines();
    }

    private void setListeners() {
        binding.addMedicines.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(),AddMedicine.class));
            finish();
        });
        binding.backBtnImage.setOnClickListener(v -> onBackPressed());
    }

    private void getMedicines() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(KEY_COLLECTION_MEDICINES)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    if(task.isSuccessful() && task.getResult()!=null){
                        List<Medicines> medicines = new ArrayList<>();
                        for(QueryDocumentSnapshot queryDocumentSnapshot:task.getResult()){
                            Medicines medicine = new Medicines();
                            medicine.name = queryDocumentSnapshot.getString(KEY_NAME);
                            medicine.quantity = queryDocumentSnapshot.getString("quantity");
                            medicines.add(medicine);
                        }
                        if(medicines.size()>0){
                            MedicinesAdapter medicinesAdapter = new MedicinesAdapter(medicines);
                            binding.usersRecyclerView.setAdapter(medicinesAdapter);
                            binding.usersRecyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }
    private void loading(Boolean isLoading){
        if(isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
}