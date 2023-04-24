package com.project.myapplication.activities;

import static com.project.myapplication.utilities.Constants.KEY_COLLECTION_DOCTORS;
import static com.project.myapplication.utilities.Constants.KEY_EMAIL;
import static com.project.myapplication.utilities.Constants.KEY_FCM_TOKEN;
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
import com.project.myapplication.adapters.UserCallAdapter;
import com.project.myapplication.adapters.UsersAdapter;
import com.project.myapplication.databinding.ActivityDoctorsVideoChatBinding;
import com.project.myapplication.listeners.UserListener;
import com.project.myapplication.listeners.VideoCallListener;
import com.project.myapplication.models.User;
import com.project.myapplication.utilities.PreferenceManager;
import com.project.myapplication.utilities.ToastUtility;

import java.util.ArrayList;
import java.util.List;

public class DoctorsVideoChatActivity extends AppCompatActivity implements VideoCallListener {

    ActivityDoctorsVideoChatBinding binding;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoctorsVideoChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
        getUsers();
    }

    private void setListeners() {
        binding.backBtnImage.setOnClickListener(v -> onBackPressed());
    }

    private void getUsers() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(KEY_COLLECTION_DOCTORS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = preferenceManager.getString(KEY_USER_ID);
                    if(task.isSuccessful() && task.getResult()!=null){
                        List<User> users = new ArrayList<>();
                        for(QueryDocumentSnapshot queryDocumentSnapshot:task.getResult()){
                            if(currentUserId.equals(queryDocumentSnapshot.getId())) continue;
                            User user = new User();
                            user.name = queryDocumentSnapshot.getString(KEY_NAME);
                            user.email = queryDocumentSnapshot.getString(KEY_EMAIL);
                            user.image = queryDocumentSnapshot.getString(KEY_IMAGE);
                            user.token = queryDocumentSnapshot.getString(KEY_FCM_TOKEN);
                            user.id = queryDocumentSnapshot.getId();
                            users.add(user);
                        }
                        if(users.size()>0){
                            UserCallAdapter usersAdapter = new UserCallAdapter(users,getApplicationContext(),this);
                            binding.usersRecyclerView.setAdapter(usersAdapter);
                            binding.usersRecyclerView.setVisibility(View.VISIBLE);
                        }else{
                            showErrorMessage();
                        }
                    }
                    else{
                        showErrorMessage();
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

    private void showErrorMessage(){
        binding.textErrorMessage.setText("No user(s) available");
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCallBtnClicked(User user) {
        Intent intent = new Intent(getApplicationContext(),OutgoingActivity.class);
        intent.putExtra("user",user);
        startActivity(intent);
    }
}