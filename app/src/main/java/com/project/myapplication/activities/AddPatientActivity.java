package com.project.myapplication.activities;

import static com.project.myapplication.utilities.Constants.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.myapplication.R;
import com.project.myapplication.databinding.ActivityAddPatientBinding;
import com.project.myapplication.utilities.PreferenceManager;
import com.project.myapplication.utilities.ToastUtility;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;

public class AddPatientActivity extends AppCompatActivity {

    private ActivityAddPatientBinding binding;
    private PreferenceManager preferenceManager;
    private DatePickerDialog datePickerDialog;

    ArrayAdapter<CharSequence> adapterItems;
    ToastUtility toastUtility;
    private String encodedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toastUtility = ToastUtility.getInstance(this);
        binding = ActivityAddPatientBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());
        adapterItems = ArrayAdapter.createFromResource(this,R.array.blood_group,R.layout.list_item);
        adapterItems.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.dropdown.setAdapter(adapterItems);
        setListeners();
    }

    private void setListeners(){
        binding.backBtnImage.setOnClickListener(v -> onBackPressed());
        binding.buttonAddPatient.setOnClickListener(v -> {
            if(isValidDetails()){
                addPatients();
            }
        });
        binding.layoutImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
        binding.datePicker.setOnClickListener(v -> {
            setDatePickerDialog();
        });
        binding.dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ToastUtility.getInstance(getApplicationContext()).shortToast((String) parent.getItemAtPosition(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ToastUtility.getInstance(getApplicationContext()).shortToast("AddPatientActivity::setListeners");
            }
        });
    }

    private void setDatePickerDialog(){
        datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String day = dayOfMonth<10?"0"+dayOfMonth:dayOfMonth+"";
            String month1 = month<10?"0"+month:month+"";
            String date = day+"-"+month1+"-"+year;
            binding.dateText.setText(date);
        },2000,1,1);
        datePickerDialog.show();
    }

    private void addPatients(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String,Object> patient = new HashMap<>();
        patient.put(KEY_NAME,binding.inputName.getText().toString());
        patient.put(KEY_EMAIL,binding.inputEmail.getText().toString());
        patient.put(KEY_IMAGE,encodedImage);
        patient.put(KEY_DATE_OF_BIRTH,binding.dateText.getText().toString());
        patient.put(KEY_BLOOD_GROUP,binding.dropdown.getSelectedItem().toString());
        patient.put(KEY_AADHAR_NUMBER,binding.aadharNumber.getText().toString());
        patient.put(KEY_CONTACT_NUMBER,binding.contactNumber.getText().toString());
        database.collection(KEY_COLLECTION_PATIENTS)
            .add(patient)
            .addOnSuccessListener(documentReference -> {
                loading(false);
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                toastUtility.shortToast("Patient added successfully");
                startActivity(intent);
            }).addOnFailureListener(exception -> {
                loading(false);
                toastUtility.exceptionToast(exception.getLocalizedMessage(),"SignUpActivity::signUp");
            });
    }

    private String encodeImage(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap,previewWidth,previewHeight,false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes,Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if(result.getResultCode() == RESULT_OK){
                if(result.getData()!=null){
                    Uri imageUri = result.getData().getData();
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        Glide.with(AddPatientActivity.this).load(bitmap).into(binding.imageProfile);
                        binding.textImage.setVisibility(View.GONE);
                        encodedImage = encodeImage(bitmap);
                    } catch(Exception e) {
                        toastUtility.exceptionToast(e.getLocalizedMessage(),"SignUpActivity::pickImage");
                    }
                }
            }
        }
    );

    private boolean isValidDetails(){
        if(encodedImage==null){
            toastUtility.shortToast("Select profile image");
            return false;
        }else if(binding.inputName.getText().toString().trim().isEmpty()){
            toastUtility.shortToast("Enter name");
            return false;
        }else if(binding.inputEmail.getText().toString().trim().isEmpty()){
            toastUtility.shortToast("Enter email");
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()){
            toastUtility.shortToast("Enter a valid email");
            return false;
        }else if(binding.dateText.getText().toString().trim().equals("Date")){
            toastUtility.shortToast("Select a date");
            return false;
        }else if(binding.contactNumber.getText().toString().trim().isEmpty() || binding.contactNumber.getText().toString().trim().length()!=10){
            toastUtility.shortToast("Enter a valid mobile number");
            return false;
        }else if(binding.aadharNumber.getText().toString().trim().isEmpty() || binding.aadharNumber.getText().toString().trim().length()!=16){
            toastUtility.shortToast("Enter a valid aadhar number");
            return false;
        }
        return true;
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.buttonAddPatient.setVisibility(View.INVISIBLE);
        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonAddPatient.setVisibility(View.VISIBLE);
        }
    }
}