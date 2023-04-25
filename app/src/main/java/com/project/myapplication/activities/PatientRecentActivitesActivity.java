package com.project.myapplication.activities;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.project.myapplication.utilities.Constants.APPOINTMENT_BOOKED;
import static com.project.myapplication.utilities.Constants.KEY_COLLECTION_APPOINTMENTS;
import static com.project.myapplication.utilities.Constants.KEY_DATE;
import static com.project.myapplication.utilities.Constants.KEY_STATUS;
import static com.project.myapplication.utilities.Constants.KEY_TIME;
import static com.project.myapplication.utilities.Constants.KEY_USER;
import static com.project.myapplication.utilities.Constants.KEY_USER_ID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.project.myapplication.R;
import com.project.myapplication.adapters.AppointmentAdapter;
import com.project.myapplication.databinding.ActivityAppointmentBinding;
import com.project.myapplication.databinding.ActivityPatientRecentActivitesBinding;
import com.project.myapplication.listeners.AppointmentListener;
import com.project.myapplication.models.Appointment;
import com.project.myapplication.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class PatientRecentActivitesActivity extends AppCompatActivity implements AppointmentListener {

    ActivityPatientRecentActivitesBinding binding;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPatientRecentActivitesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        getAppointments();
        setListeners();
    }

    private void setListeners() {
        binding.backBtnImage.setOnClickListener(v -> onBackPressed());
    }

    private void getAppointments() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(KEY_COLLECTION_APPOINTMENTS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    if(task.isSuccessful() && task.getResult()!=null){
                        List<Appointment> appointments = new ArrayList<>();
                        for(QueryDocumentSnapshot queryDocumentSnapshot:task.getResult()){
                            if(!(queryDocumentSnapshot.getString("doctor").equals(preferenceManager.getString(KEY_USER_ID)))){
                                continue;
                            }
                            Appointment appointment = new Appointment();
                            appointment.time = queryDocumentSnapshot.getString(KEY_TIME);
                            appointment.date = queryDocumentSnapshot.getString(KEY_DATE);
                            appointment.status = queryDocumentSnapshot.getString(KEY_STATUS);
                            appointment.patient = queryDocumentSnapshot.getString("patient");
                            appointment.patientName = queryDocumentSnapshot.getString("patientName");
                            appointment.doctor = queryDocumentSnapshot.getString("doctor");
                            appointment.doctorName = queryDocumentSnapshot.getString("doctorName");
                            appointment.id = queryDocumentSnapshot.getId();
                            appointments.add(appointment);
                        }
                        if(appointments.size()>0){
                            AppointmentAdapter appointmentAdapter = new AppointmentAdapter(appointments,this);
                            binding.usersRecyclerView.setAdapter(appointmentAdapter);
                            binding.usersRecyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAppointmentClicked(Appointment appointment) {
        if(appointment.status.equals(APPOINTMENT_BOOKED)){
            Toast.makeText(getApplicationContext(), "Need to be closed", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "Already closed", Toast.LENGTH_SHORT).show();
        }
    }
}