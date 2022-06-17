package com.uasappmob.riaunews.activity;

import static android.text.TextUtils.isEmpty;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import com.squareup.picasso.Picasso;
import com.uasappmob.riaunews.R;
import com.uasappmob.riaunews.response.ResponseCreate;
import com.uasappmob.riaunews.rest.ApiClient;
import com.uasappmob.riaunews.rest.ApiInterface;
import com.uasappmob.riaunews.utils.FileUtils;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    EditText etUsername, etPassword, etName, etEmail, etPhoneNumber;
    CardView cvProfile;
    ImageView imgProfile;
    Button btnRegister;

    private ApiInterface service;
    private ProgressDialog progressDialog;
    private String selectedImage;
    private final CharSequence[] options = {"Camera", "Gallery", "Cancel"};
    private static final String TAG = RegisterActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.et_register_username);
        etPassword = findViewById(R.id.et_register_password);
        etName = findViewById(R.id.et_register_name);
        etEmail = findViewById(R.id.et_register_email);
        etPhoneNumber = findViewById(R.id.et_register_phoneNumber);
        cvProfile = findViewById(R.id.cv_register_profile);
        imgProfile = findViewById(R.id.img_register_profile);
        btnRegister = findViewById(R.id.btn_register);

        service = ApiClient.getClient().create(ApiInterface.class);
        requirePermission();
        cvProfile.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
            builder.setTitle("Pilih Foto Profil");
            builder.setItems(options, (dialog, which) -> {
                if (options[which].equals("Camera")) {
                    Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePic, 0);
                } else if (options[which].equals("Gallery")) {
                    Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(gallery, 1);
                } else {
                    dialog.dismiss();
                }
            });
            builder.show();
        });
        btnRegister.setOnClickListener(view -> {
            String strUsername = etUsername.getText().toString();
            String strPassword = etPassword.getText().toString();
            String strName = etName.getText().toString();
            String strPhoneNumber = etPhoneNumber.getText().toString();
            String strEmail = etEmail.getText().toString();
            if (isEmpty(strUsername))
                etUsername.setError("Tidak boleh kosong");
            else if (isEmpty(strPassword))
                etPassword.setError("Tidak boleh kosong");
            else if (isEmpty(strName))
                etName.setError("Tidak boleh kosong");
            else if (isEmpty(strPhoneNumber))
                etPhoneNumber.setError("Tidak boleh kosong");
            else if (isEmpty(strEmail))
                etEmail.setError("Tidak boleh kosong");
            else
                register();
        });
    }

    public void register() {
        progressDialog = ProgressDialog.show(this, null, "Sedang membuat akun", true, false);
        String strUsername = etUsername.getText().toString();
        String strPassword = etPassword.getText().toString();
        String strName = etName.getText().toString();
        String strPhoneNumber = etPhoneNumber.getText().toString();
        String strEmail = etEmail.getText().toString();

        RequestBody username = RequestBody.create(MediaType.parse("multipart/form-data"), strUsername);
        RequestBody password = RequestBody.create(MediaType.parse("multipart/form-data"), strPassword);
        RequestBody name = RequestBody.create(MediaType.parse("multipart/form-data"), strName);
        RequestBody email = RequestBody.create(MediaType.parse("multipart/form-data"), strEmail);
        RequestBody phone_number = RequestBody.create(MediaType.parse("multipart/form-data"), strPhoneNumber);
        MultipartBody.Part image;
        if (selectedImage != null) {
            File file = new File(Uri.parse(selectedImage).getPath());
            RequestBody rbImage = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            image = MultipartBody.Part.createFormData("sendimage", file.getName(), rbImage);
        } else {
            RequestBody rbImage = RequestBody.create(MediaType.parse("multipart/form-data"), "");
            image = MultipartBody.Part.createFormData("sendimage", "", rbImage);
        }

        Call<ResponseCreate> call = service.register(username, password, name, phone_number, email, image);
        call.enqueue(new Callback<ResponseCreate>() {
            @Override
            public void onResponse(@NonNull Call<ResponseCreate> call, @NonNull Response<ResponseCreate> response) {
                ResponseCreate responseRegister = response.body();
                assert responseRegister != null;
                if (responseRegister.getStatus()) {
                    progressDialog.dismiss();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    Toast.makeText(RegisterActivity.this, responseRegister.getMessage(), Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, responseRegister.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseCreate> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Log.e(TAG, "onFailure:" + t.getLocalizedMessage());
                Toast.makeText(getApplicationContext(), "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    if (data != null) {
                        Bitmap image = (Bitmap) data.getExtras().get("data");
                        selectedImage = FileUtils.getPath(RegisterActivity.this, getImageUri(RegisterActivity.this, image));
                        imgProfile.setImageBitmap(image);
                    }
                    break;
                case 1:
                    if (data != null) {
                        Uri image = data.getData();
                        selectedImage = FileUtils.getPath(RegisterActivity.this, image);
                        Picasso.get().load(image).into(imgProfile);
                    }
                    break;
            }
        }
    }

    public Uri getImageUri(Context context, Bitmap bitmap) {
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "user", "");
        return Uri.parse(path);
    }

    public void requirePermission() {
        ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }
}
