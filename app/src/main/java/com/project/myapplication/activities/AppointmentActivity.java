package com.project.myapplication.activities;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import static com.project.myapplication.utilities.Constants.*;

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
import com.project.myapplication.adapters.UsersAdapter;
import com.project.myapplication.databinding.ActivityAppointmentBinding;
import com.project.myapplication.listeners.AppointmentListener;
import com.project.myapplication.listeners.UserListener;
import com.project.myapplication.models.Appointment;
import com.project.myapplication.models.User;
import com.project.myapplication.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class AppointmentActivity extends AppCompatActivity implements AppointmentListener {
    ActivityAppointmentBinding binding;
    Dialog dialog;
    PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAppointmentBinding.inflate(getLayoutInflater());
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
            Toast.makeText(AppointmentActivity.this, "Need to be closed", Toast.LENGTH_SHORT).show();

            dialog = new Dialog(AppointmentActivity.this);
            dialog.setContentView(R.layout.logout_dialog);
            dialog.getWindow().setLayout(MATCH_PARENT,WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false);
            dialog.getWindow().getAttributes().windowAnimations = R.style.animation;

            MaterialCardView cancel = dialog.findViewById(R.id.cancelButton);
            MaterialCardView ok = dialog.findViewById(R.id.ok);
            ImageView imageView = dialog.findViewById(R.id.imageLogout);
            Glide.with(AppointmentActivity.this).load(ContextCompat.getDrawable(AppointmentActivity.this, R.drawable.logout)).into(imageView);
            ok.setOnClickListener(i -> {
                Intent intent = new Intent(getApplicationContext(),PrescribeActivity.class);
                intent.putExtra(KEY_USER,appointment);
                startActivity(intent);
                finish();
            });
            cancel.setOnClickListener(i -> dialog.dismiss());
            dialog.show();
        }else{
            Toast.makeText(AppointmentActivity.this, "Already closed", Toast.LENGTH_SHORT).show();
        }
    }
}