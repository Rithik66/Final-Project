package com.project.myapplication.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.myapplication.databinding.ItemContainerPatientBinding;
import com.project.myapplication.listeners.PatientListener;
import com.project.myapplication.models.Patient;

import java.util.List;

public class PatientsAdapter extends RecyclerView.Adapter<PatientsAdapter.PatientsViewHolder>{
    private List<Patient> patients;
    private PatientListener patientListener;

    public PatientsAdapter(List<Patient> patients, PatientListener patientListener) {
        this.patients = patients;
        this.patientListener = patientListener;
    }

    @NonNull
    @Override
    public PatientsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerPatientBinding itemContainerPatientBinding = ItemContainerPatientBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new PatientsViewHolder(itemContainerPatientBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientsViewHolder holder, int position) {
        holder.setUserData(patients.get(position));
    }

    @Override
    public int getItemCount() {
        return patients.size();
    }

    class PatientsViewHolder extends RecyclerView.ViewHolder{
        ItemContainerPatientBinding binding;
        PatientsViewHolder(ItemContainerPatientBinding itemContainerPatientBinding){
            super(itemContainerPatientBinding.getRoot());
            binding = itemContainerPatientBinding;
        }
        void setUserData(Patient patient){
            binding.textName.setText(patient.name);
            binding.textEmail.setText(patient.email);
            binding.imageProfile.setImageBitmap(getUserImage(patient.image));
            binding.getRoot().setOnClickListener(v -> patientListener.onPatientClicked(patient));
        }
    }

    private Bitmap getUserImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}
