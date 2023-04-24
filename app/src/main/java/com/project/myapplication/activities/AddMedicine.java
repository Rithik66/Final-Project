package com.project.myapplication.activities;

import static com.project.myapplication.utilities.Constants.*;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.firestore.FirebaseFirestore;
import com.project.myapplication.databinding.ActivityAddMedicineBinding;
import com.project.myapplication.utilities.PreferenceManager;
import com.project.myapplication.utilities.ToastUtility;

import java.util.HashMap;

public class AddMedicine extends AppCompatActivity {

    ActivityAddMedicineBinding binding;
    PreferenceManager preferenceManager;
    ToastUtility toastUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddMedicineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        toastUtility = ToastUtility.getInstance(getApplicationContext());
        setListeners();
    }

    private void setListeners() {
        binding.backBtnImage.setOnClickListener(v -> onBackPressed());
        binding.buttonAddMedicine.setOnClickListener(v -> {
            if(isValidDetails()){
                addMedicine();
            }
        });
    }

    private boolean isValidDetails(){
        if(binding.inputName.getText().toString().trim().isEmpty()){
            toastUtility.shortToast("Enter a name");
            return false;
        }else if(binding.inputQuantity.getText().toString().trim().isEmpty()){
            toastUtility.shortToast("Quantity cannot be empty");
            return false;
        }
        return true;
    }

    private void addMedicine(){
        try {
            loading(true);
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            HashMap<String,Object> medicine = new HashMap<>();
            medicine.put(KEY_NAME,binding.inputName.getText().toString());
            medicine.put("quantity",binding.inputQuantity.getText().toString());
            medicine.put("addedById",preferenceManager.getString(KEY_USER_ID));
            medicine.put("addedByName",preferenceManager.getString(KEY_NAME));
            database.collection(KEY_COLLECTION_MEDICINES)
                    .add(medicine)
                    .addOnSuccessListener(documentReference -> {
                        loading(false);
                        toastUtility.shortToast("Medicine added successfully");
                        startActivity(new Intent(getApplicationContext(),ViewMedicineActivity.class));
                        finish();
                    }).addOnFailureListener(exception -> {
                        loading(false);
                        toastUtility.exceptionToast(exception.getLocalizedMessage(),"SignUpActivity::signUp");
                    });
        }catch (Exception e){
            toastUtility.longToast(e.getLocalizedMessage());
        }
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.buttonAddMedicine.setVisibility(View.INVISIBLE);
        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonAddMedicine.setVisibility(View.VISIBLE);
        }
    }
}