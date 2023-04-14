package com.project.myapplication.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.project.myapplication.databinding.ContainerRecieveMessageBinding;
import com.project.myapplication.databinding.ContainerSendMessageBinding;
import com.project.myapplication.models.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    List<ChatMessage> chatMessages;
    Bitmap receiverProfile;
    String senderId;

    public ChatAdapter(List<ChatMessage> chatMessages, Bitmap receiverProfile, String senderId) {
        this.chatMessages = chatMessages;
        this.receiverProfile = receiverProfile;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==VIEW_TYPE_SENT){
            return new SentMessageViewHolder(
                    ContainerSendMessageBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
            ));
        }else{
            return new RecievedMessageViewHolder(
                    ContainerRecieveMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    ));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position)==VIEW_TYPE_SENT){
            ((SentMessageViewHolder)holder).setData(chatMessages.get(position));
        }else{
            ((RecievedMessageViewHolder)holder).setData(chatMessages.get(position),receiverProfile);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(chatMessages.get(position).senderId.equals(senderId)){
            return VIEW_TYPE_SENT;
        }else{
            return VIEW_TYPE_RECEIVED;
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        ContainerSendMessageBinding binding;
        public SentMessageViewHolder(ContainerSendMessageBinding containerSendMessageBinding) {
            super(containerSendMessageBinding.getRoot());
            binding = containerSendMessageBinding;
        }
        void setData(ChatMessage chatMessage){
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
        }
    }
    static class RecievedMessageViewHolder extends RecyclerView.ViewHolder {
        ContainerRecieveMessageBinding binding;
        public RecievedMessageViewHolder(ContainerRecieveMessageBinding containerRecieveMessageBinding) {
            super(containerRecieveMessageBinding.getRoot());
            binding = containerRecieveMessageBinding;
        }
        void setData(ChatMessage chatMessage,Bitmap receivedProfile){
            binding.textMessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);
            binding.chatProfile.setImageBitmap(receivedProfile);
        }
    }
}
