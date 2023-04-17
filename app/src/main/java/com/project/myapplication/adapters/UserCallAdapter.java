package com.project.myapplication.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.myapplication.R;
import com.project.myapplication.databinding.ItemContainerVideoCallBinding;
import com.project.myapplication.models.User;
import com.project.myapplication.utilities.ToastUtility;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import java.util.Collections;
import java.util.List;

public class UserCallAdapter extends RecyclerView.Adapter<UserCallAdapter.UserViewHolder>{
    private List<User> users;
    Context context;

    public UserCallAdapter(List<User> users,Context context) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerVideoCallBinding itemContainerVideoCallBinding = ItemContainerVideoCallBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new UserViewHolder(itemContainerVideoCallBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setUserData(users.get(position),holder);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder{
        ItemContainerVideoCallBinding binding;
        ZegoSendCallInvitationButton btn;
        UserViewHolder(ItemContainerVideoCallBinding itemContainerVideoCallBinding){
            super(itemContainerVideoCallBinding.getRoot());
            binding = itemContainerVideoCallBinding;
        }
        void setUserData(User user, UserViewHolder holder){
            binding.textName.setText(user.name);
            binding.textEmail.setText(user.email);
            binding.imageProfile.setImageBitmap(getUserImage(user.image));
            btn = binding.videoCall;
            btn.setIsVideoCall(true);
            btn.setResourceID("zego_uikit_call");
            btn.setInvitees(Collections.singletonList(new ZegoUIKitUser(user.name)));
        }
    }

    private Bitmap getUserImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}
