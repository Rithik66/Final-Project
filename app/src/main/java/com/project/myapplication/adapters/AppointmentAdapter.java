package com.project.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.myapplication.databinding.ItemAppointmentContainerBinding;
import com.project.myapplication.listeners.AppointmentListener;
import com.project.myapplication.models.Appointment;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>{
    private List<Appointment> appointments;
    AppointmentListener appointmentListener;

    public AppointmentAdapter(List<Appointment> appointments,AppointmentListener appointmentListener) {
        this.appointments = appointments;
        this.appointmentListener=appointmentListener;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAppointmentContainerBinding itemAppointmentContainerBinding = ItemAppointmentContainerBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new AppointmentViewHolder(itemAppointmentContainerBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        holder.setUserData(appointments.get(position));
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    class AppointmentViewHolder extends RecyclerView.ViewHolder{
        ItemAppointmentContainerBinding binding;
        AppointmentViewHolder(ItemAppointmentContainerBinding itemAppointmentContainerBinding){
            super(itemAppointmentContainerBinding.getRoot());
            binding = itemAppointmentContainerBinding;
        }
        void setUserData(Appointment appointments){
            binding.status.setText(appointments.status);
            binding.name.setText(appointments.patientName);
            binding.dateTime.setText(appointments.date+" "+appointments.time);

            binding.appointmentui.setOnClickListener(v -> appointmentListener.onAppointmentClicked(appointments));
        }
    }
}