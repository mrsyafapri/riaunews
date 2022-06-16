package com.uasappmob.riaunews.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.uasappmob.riaunews.R;
import com.uasappmob.riaunews.adapter.NewsAdapter;
import com.uasappmob.riaunews.listener.OnDeleteClickListener;
import com.uasappmob.riaunews.listener.OnDetailClickListener;
import com.uasappmob.riaunews.listener.OnUpdateClickListener;
import com.uasappmob.riaunews.model.News;
import com.uasappmob.riaunews.response.ResponseNews;
import com.uasappmob.riaunews.rest.ApiClient;
import com.uasappmob.riaunews.rest.ApiInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListNewsActivity extends AppCompatActivity implements OnDetailClickListener, OnUpdateClickListener, OnDeleteClickListener {

    private static final String TAG = ListNewsActivity.class.getSimpleName();

    private TextView tvEmptyNews;
    private RecyclerView rvData;
    private FloatingActionButton fabAdd;
    private ProgressDialog loading;
    private NewsAdapter adapter;
    private ApiInterface service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_news);
        initViews();

        // Initialization adapter
        adapter = new NewsAdapter(this);
        rvData.setLayoutManager(new LinearLayoutManager(this));
        service = ApiClient.getClient().create(ApiInterface.class);

        rvData.setAdapter(adapter);
        fabAdd.setOnClickListener(view -> {
            Intent intent = new Intent(ListNewsActivity.this, CreateNewsActivity.class);
            startActivity(intent);
        });
        loadData();

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(ListNewsActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ListNewsActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    private void loadData() {
        loading = ProgressDialog.show(this, null, "Sedang memuat data", true, false);
        Call<ResponseNews<List<News>>> call = service.getNews();
        call.enqueue(new Callback<ResponseNews<List<News>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseNews<List<News>>> call, @NonNull Response<ResponseNews<List<News>>> response) {
                ResponseNews<List<News>> responseRead = response.body();
                assert responseRead != null;
                if (responseRead.getStatus()) {
                    loading.dismiss();
                    adapter.addAll(responseRead.getResult());
                    initListener();
                } else {
                    loading.dismiss();
                    tvEmptyNews.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseNews<List<News>>> call, @NonNull Throwable t) {
                loading.dismiss();
                Log.e(TAG + ".error", t.toString());
                Toast.makeText(ListNewsActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initListener() {
        adapter.setOnDetailClickListener(this);
        adapter.setOnDeleteClickListener(this);
        adapter.setOnUpdateClickListener(this);
    }

    private void initViews() {
        tvEmptyNews = findViewById(R.id.tv_empty_news);
        rvData = findViewById(R.id.rv_news);
        fabAdd = findViewById(R.id.fab_add);
    }

    private void doDelete(int position, String id) {
        loading = ProgressDialog.show(this, null, "Sedang menghapus berita", true, false);
        Call<ResponseNews<String>> call = service.deleteNews(id);
        call.enqueue(new Callback<ResponseNews<String>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseNews<String>> call, @NonNull Response<ResponseNews<String>> response) {
                ResponseNews<String> responseDelete = response.body();
                assert responseDelete != null;
                if (responseDelete.getStatus()) {
                    loading.dismiss();
                    adapter.remove(position);
                    Toast.makeText(ListNewsActivity.this, responseDelete.getResult(), Toast.LENGTH_SHORT).show();
                    if (adapter.getItemCount() == 0)
                        tvEmptyNews.setVisibility(View.VISIBLE);
                } else {
                    loading.dismiss();
                    Toast.makeText(ListNewsActivity.this, responseDelete.getResult(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseNews<String>> call, @NonNull Throwable t) {
                loading.dismiss();
                Log.e(TAG + ".errorDelete", t.toString());
                Toast.makeText(ListNewsActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDeleteClick(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah Anda yakin ingin menghapus berita ini?");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> doDelete(position, adapter.getNews(position).getId()));
        builder.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
        builder.create().show();
    }

    @Override
    public void onUpdateClick(int position) {
        News data = adapter.getNews(position);
        UpdateNewsActivity.newInstance(this, data);
    }

    @Override
    public void onDetailClick(int position) {
        News data = adapter.getNews(position);
        DetailNewsActivity.newInstance(this, data);
    }
}