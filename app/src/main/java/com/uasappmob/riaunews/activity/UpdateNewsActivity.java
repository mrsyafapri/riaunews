package com.uasappmob.riaunews.activity;

import static android.text.TextUtils.isEmpty;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.squareup.picasso.Picasso;
import com.uasappmob.riaunews.R;
import com.uasappmob.riaunews.config.ServerConfig;
import com.uasappmob.riaunews.model.News;
import com.uasappmob.riaunews.response.ResponseCreate;
import com.uasappmob.riaunews.rest.ApiClient;
import com.uasappmob.riaunews.rest.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateNewsActivity extends AppCompatActivity {

    EditText etTitle, etContent;
    Spinner spCategory;
    CardView cvCover;
    ImageView imgCover;
    Button btnUpdate;

    private ProgressDialog progressDialog;
    private static final String TAG = UpdateNewsActivity.class.getSimpleName();
    private ApiInterface service;
    private News news;

    public static void newInstance(Context context, News data) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(TAG + ".data", data);
        Intent intent = new Intent(context, UpdateNewsActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_news);

        if (getIntent() != null) {
            news = getIntent().getParcelableExtra(TAG + ".data");
        }

        etTitle = findViewById(R.id.et_title);
        spCategory = findViewById(R.id.sp_category);
        etContent = findViewById(R.id.et_content);
        cvCover = findViewById(R.id.cv_cover);
        imgCover = findViewById(R.id.img_cover);
        btnUpdate = findViewById(R.id.btnUpdate);

        etTitle.setText(news.getTitle());
        spCategory.setSelection(getIndex(spCategory, news.getCategory()));
        etContent.setText(news.getContent());
        Picasso.get()
                .load(ServerConfig.BASE_URL + "images/news/" + news.getCover())
                .into(imgCover);

        service = ApiClient.getClient().create(ApiInterface.class);

        btnUpdate.setOnClickListener(view -> {
            String title = etTitle.getText().toString();
            String content = etContent.getText().toString();
            if (isEmpty(title))
                etTitle.setError("Must not be empty");
            else if (isEmpty(content))
                etContent.setError("Must not be empty");
            else
                updateNews();
        });
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(UpdateNewsActivity.this, ListNewsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(UpdateNewsActivity.this, ListNewsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void updateNews() {
        Log.d(TAG, "Menjalankan method updateNews");
        progressDialog = ProgressDialog.show(this, null, "Sedang mengupdate account", true, false);
        String title = etTitle.getText().toString();
        String category = spCategory.getSelectedItem().toString();
        String content = etContent.getText().toString();

        Call<ResponseCreate> call = service.updateNews(news.getId(), title, category, content);
        call.enqueue(new Callback<ResponseCreate>() {
            @Override
            public void onResponse(@NonNull Call<ResponseCreate> call, @NonNull Response<ResponseCreate> response) {
                Log.d(TAG, "berhasil memanggil api");
                ResponseCreate responseCreate = response.body();
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    Log.d(TAG, "success mendapatkan api");
                    Intent i = new Intent(UpdateNewsActivity.this, ListNewsActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    Toast.makeText(UpdateNewsActivity.this, responseCreate.getMessage(), Toast.LENGTH_SHORT).show();
                    startActivity(i);
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseCreate> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Log.e(TAG, t.getLocalizedMessage());
                Toast.makeText(getApplicationContext(), "Create account failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int getIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }
        return 0;
    }
}
