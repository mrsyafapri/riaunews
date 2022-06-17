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
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

public class CreateNewsActivity extends AppCompatActivity {

    EditText etTitle, etContent;
    Spinner spCategory;
    CardView cvCover;
    ImageView imgCover;
    Button btnSave;

    private ApiInterface service;
    private ProgressDialog progressDialog;
    private String selectedImage;
    private final CharSequence[] options = {"Camera", "Gallery", "Cancel"};
    private static final String TAG = CreateNewsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_news);

        etTitle = findViewById(R.id.et_create_title);
        spCategory = findViewById(R.id.sp_create_category);
        etContent = findViewById(R.id.et_create_content);
        cvCover = findViewById(R.id.cv_create_cover);
        imgCover = findViewById(R.id.img_create_cover);
        btnSave = findViewById(R.id.btnSave);

        service = ApiClient.getClient().create(ApiInterface.class);
        requirePermission();
        cvCover.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNewsActivity.this);
            builder.setTitle("Pilih Foto Cover Berita");
            builder.setItems(options, (dialogInterface, which) -> {
                if (options[which].equals("Camera")) {
                    Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePic, 0);
                } else if (options[which].equals("Gallery")) {
                    Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(gallery, 1);
                } else {
                    dialogInterface.dismiss();
                }
            });
            builder.show();
        });
        btnSave.setOnClickListener(view -> {
            String title = etTitle.getText().toString();
            String content = etContent.getText().toString();
            if (isEmpty(title))
                etTitle.setError("Tidak boleh kosong");
            else if (isEmpty(content))
                etContent.setError("Tidak boleh kosong");
            else
                createNews();
        });
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void createNews() {
        progressDialog = ProgressDialog.show(this, null, "Sedang membuat Berita", true, false);
        String strTitle = etTitle.getText().toString();
        String strCategory = spCategory.getSelectedItem().toString();
        String strContent = etContent.getText().toString();

        RequestBody title = RequestBody.create(MediaType.parse("multipart/form-data"), strTitle);
        RequestBody category = RequestBody.create(MediaType.parse("multipart/form-data"), strCategory);
        RequestBody content = RequestBody.create(MediaType.parse("multipart/form-data"), strContent);
        MultipartBody.Part image;
        if (selectedImage != null) {
            File file = new File(Uri.parse(selectedImage).getPath());
            RequestBody rbImage = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            image = MultipartBody.Part.createFormData("sendimage", file.getName(), rbImage);
        } else {
            RequestBody rbImage = RequestBody.create(MediaType.parse("multipart/form-data"), "");
            image = MultipartBody.Part.createFormData("sendimage", "", rbImage);
        }

        Call<ResponseCreate> call = service.createNews(title, category, content, image);
        call.enqueue(new Callback<ResponseCreate>() {
            @Override
            public void onResponse(@NonNull Call<ResponseCreate> call, @NonNull Response<ResponseCreate> response) {
                ResponseCreate responseCreate = response.body();
                assert responseCreate != null;
                if (responseCreate.getStatus()) {
                    progressDialog.dismiss();
                    Intent i = new Intent(CreateNewsActivity.this, ListNewsActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    Toast.makeText(CreateNewsActivity.this, responseCreate.getMessage(), Toast.LENGTH_SHORT).show();
                    startActivity(i);
                    finish();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(CreateNewsActivity.this, responseCreate.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseCreate> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Log.e(TAG, t.getLocalizedMessage());
                Toast.makeText(getApplicationContext(), "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(CreateNewsActivity.this, ListNewsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CreateNewsActivity.this, ListNewsActivity.class);
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
                        selectedImage = FileUtils.getPath(CreateNewsActivity.this, getImageUri(CreateNewsActivity.this, image));
                        imgCover.setImageBitmap(image);
                    }
                    break;
                case 1:
                    if (data != null) {
                        Uri image = data.getData();
                        selectedImage = FileUtils.getPath(CreateNewsActivity.this, image);
                        Picasso.get()
                                .load(image)
                                .into(imgCover);
                    }
                    break;
            }
        }
    }

    public Uri getImageUri(Context context, Bitmap bitmap) {
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "news", "");
        return Uri.parse(path);
    }

    public void requirePermission() {
        ActivityCompat.requestPermissions(CreateNewsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }
}
