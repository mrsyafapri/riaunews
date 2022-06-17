package com.uasappmob.riaunews.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;
import com.uasappmob.riaunews.R;
import com.uasappmob.riaunews.config.ServerConfig;
import com.uasappmob.riaunews.model.News;
import com.uasappmob.riaunews.response.ResponseNews;
import com.uasappmob.riaunews.rest.ApiClient;
import com.uasappmob.riaunews.rest.ApiInterface;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailNewsActivity extends AppCompatActivity {

    TextView tvTitle, tvCreatedAt, tvContent;
    ImageView imgCover;
    Button btnEdit, btnDelete;

    private static final String TAG = DetailNewsActivity.class.getSimpleName();
    private News news;
    private ApiInterface service;
    private ProgressDialog loading;

    public static void newInstance(Context context, News data) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(TAG + ".data", data);
        Intent intent = new Intent(context, DetailNewsActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_news);
        initViews();

        if (getIntent() != null) {
            news = getIntent().getParcelableExtra(TAG + ".data");
        }

        service = ApiClient.getClient().create(ApiInterface.class);

        Timestamp timestamp = Timestamp.valueOf(news.getCreatedAt());
        String createdAtFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm:ss").format(timestamp);

        tvTitle.setText(news.getTitle());
        Picasso.get()
                .load(ServerConfig.BASE_URL + "images/news/" + news.getCover())
                .into(imgCover);
        tvCreatedAt.setText(createdAtFormat);
        tvContent.setText(news.getContent());
        btnEdit.setOnClickListener(view -> UpdateNewsActivity.newInstance(this, news));
        btnDelete.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Apakah Anda yakin ingin menghapus berita ini?");
            builder.setPositiveButton("Ya", (dialogInterface, i) -> doDelete(news.getId()));
            builder.setNegativeButton("Tidak", (dialogInterface, i) -> dialogInterface.dismiss());
            builder.create().show();
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(news.getCategory());
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(DetailNewsActivity.this, ListNewsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DetailNewsActivity.this, ListNewsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tv_detail_title);
        imgCover = findViewById(R.id.img_detail_cover);
        tvCreatedAt = findViewById(R.id.tv_detail_created);
        tvContent = findViewById(R.id.tv_detail_content);
        btnEdit = findViewById(R.id.btn_edit_detail);
        btnDelete = findViewById(R.id.btn_delete_detail);
    }

    private void doDelete(String id) {
        loading = ProgressDialog.show(this, null, "Sedang menghapus berita", true, false);
        Call<ResponseNews<String>> call = service.deleteNews(id);
        call.enqueue(new Callback<ResponseNews<String>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseNews<String>> call, @NonNull Response<ResponseNews<String>> response) {
                ResponseNews<String> responseDelete = response.body();
                assert responseDelete != null;
                if (responseDelete.getStatus()) {
                    loading.dismiss();
                    Intent intent = new Intent(DetailNewsActivity.this, ListNewsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    Toast.makeText(DetailNewsActivity.this, responseDelete.getResult(), Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                } else {
                    loading.dismiss();
                    Toast.makeText(DetailNewsActivity.this, responseDelete.getResult(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseNews<String>> call, @NonNull Throwable t) {
                loading.dismiss();
                Log.e(TAG + ".errorDelete:", t.toString());
                Toast.makeText(DetailNewsActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}