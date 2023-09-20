package com.example.connect_data_base;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.example.connect_data_base.databinding.ActivityMainBinding;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private String encodedImage;
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
            encodedImage = null;
            showMessage("Create Successfully person with name :" + name);
        }
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
    private Boolean validData(){
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