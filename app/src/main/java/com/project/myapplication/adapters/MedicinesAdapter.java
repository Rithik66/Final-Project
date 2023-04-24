package com.project.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.myapplication.databinding.ItemAppointmentContainerBinding;
import com.project.myapplication.databinding.ItemContainerMedicineBinding;
import com.project.myapplication.listeners.AppointmentListener;
import com.project.myapplication.models.Appointment;
import com.project.myapplication.models.Medicines;

import java.util.List;

public class MedicinesAdapter extends RecyclerView.Adapter<MedicinesAdapter.MedicinesViewHolder>{
    private List<Medicines> medicines;

    public MedicinesAdapter(List<Medicines> medicines) {
        this.medicines = medicines;
    }

    @NonNull
    @Override
    public MedicinesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerMedicineBinding itemContainerMedicineBinding = ItemContainerMedicineBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new MedicinesViewHolder(itemContainerMedicineBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicinesViewHolder holder, int position) {
        holder.setUserData(medicines.get(position));
    }

    @Override
    public int getItemCount() {
        return medicines.size();
    }

    class MedicinesViewHolder extends RecyclerView.ViewHolder{
        ItemContainerMedicineBinding binding;
        MedicinesViewHolder(ItemContainerMedicineBinding itemContainerMedicineBinding){
            super(itemContainerMedicineBinding.getRoot());
            binding = itemContainerMedicineBinding;
        }
        void setUserData(Medicines medicine){
            binding.name.setText(medicine.name);
            binding.quantity.setText(medicine.quantity);
        }
    }
}