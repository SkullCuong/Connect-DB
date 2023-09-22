package com.example.connect_data_base;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.connect_data_base.databinding.ActivityMainBinding;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private String encodedImage;
    private DatePickerDialog datePickerDialog;
    private int minAge = 16;
    private int maxAge = 80;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners() {
        binding.saveDetails.setOnClickListener(v -> {
            insertPerson();
        });
        binding.inputDateOfBirth.setOnClickListener(v ->{
            Calendar calendar = Calendar.getInstance();
            int currentDay  = calendar.get(Calendar.DAY_OF_MONTH);
            int currentYear = calendar.get(Calendar.YEAR);
            int currentMonth  = calendar.get(Calendar.MONTH);
            datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        binding.inputDateOfBirth.setText(day + "/" + (month + 1) + "/" +year);
                }
            },currentYear,currentMonth,currentDay);
            datePickerDialog.show();
        });

        binding.layoutImage.setOnClickListener(v ->{
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
        binding.ViewDetails.setOnClickListener(v ->{
            Intent intent = new Intent(this, ListActivity.class);
            startActivity(intent);
        });
    }
    private void insertPerson() {
        if(validData()){
            String name = binding.inputName.getText().toString().trim();
            String email = binding.inputEmail.getText().toString().trim();
            String dob = binding.inputDateOfBirth.getText().toString().trim();
            String address = binding.inputAddress.getText().toString().trim();
            Person person = new Person(name,email,dob,address,encodedImage);
            PersonDB.getInstance(this).personDao().insertPerson(person);
            binding.inputName.setText("");
            binding.inputEmail.setText("");
            binding.inputDateOfBirth.setText("");
            binding.inputAddress.setText("");
            binding.profileImage.setImageBitmap(null);
            binding.textAddImage.setVisibility(View.VISIBLE);
            encodedImage = null;
            showMessage("Create Successfully person with name :" + name);
        }
    }
    private Boolean checkValidDate()  {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = null;
        try {
            date = sdf.parse(binding.inputDateOfBirth.getText().toString().trim());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentYear = calendar.get(Calendar.YEAR);
        calendar.setTime(date);
        int age = currentYear - calendar.get(Calendar.YEAR);
        if(currentMonth < calendar.get(Calendar.MONTH) + 1 || (currentMonth == calendar.get(Calendar.MONTH) + 1  && currentDay < calendar.get(Calendar.DAY_OF_MONTH))){
            age--;
        }
        if(age >= minAge && age <= maxAge){
            return true;
        }
        return false;
    }
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            // here I used it to execute an action and the return result
            new ActivityResultContracts.StartActivityForResult(),
            // it it Lambda ()
            result -> {
                //check if success or not (RESULT_OK is used to point that this action run without error)
                if(result.getResultCode() == RESULT_OK){
                    // check value
                    if(result.getData() != null){
                        // get date of image choosed
                        Uri imageUri = result.getData().getData();
                        try {
                            // Input is Image from URL
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            // use it to convert Uri from string to bitmap
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            // diplay image choosed
                            binding.profileImage.setImageBitmap(bitmap);
                            binding.textAddImage.setVisibility(View.GONE);
                            // encrypt this image into string base64
                            encodedImage = encodedImage(bitmap);
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private Boolean validData()  {
        if(encodedImage == null){
            showMessage("Please Select Profile Image");
            return false;
        }
        else if (binding.inputName.getText().toString().trim().isEmpty()){
            showMessage("Please Input Name");
            return false;
        }
        else if (binding.inputEmail.getText().toString().trim().isEmpty()){
            showMessage("Please Input Email");
            return false;
        }
        else if (binding.inputDateOfBirth.getText().toString().trim().isEmpty()){
            showMessage("Please Input Date");
            return false;
        }
        else if (binding.inputAddress.getText().toString().trim().isEmpty()){
            showMessage("Please Input Address");
            return false;
        } else if (!checkValidDate()){
            showMessage("Please input valid Date");
            return false;
        }
        return true;
    }
    private String encodedImage(Bitmap bitmap){
        // Bitmap lớp thể hiện một bức ảnh
        // First we determine the size
        // we fixed width
        int previewWidth = 150;
        // Calculate the height to the image not changed too much (biến dạng nhiều)
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        // then we can review the image with these size but in small size then it is easy to store and transfer
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        //ByteArrayOutputStream, here I used it to store image after compressing
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // using preview image to compress with the quality 50 and finally it will store in byteArrayOutputStream
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        // then transfer to byte arrays
        byte[] bytes = byteArrayOutputStream.toByteArray();
        // use Base64 encrypt bytes array to string based64
        return Base64.encodeToString(bytes, Base64.DEFAULT);
        // the result we will be used to transfer or store under the string
    }
    private  void showMessage(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}