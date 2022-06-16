package com.uasappmob.riaunews.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.stetho.Stetho;
import com.squareup.picasso.Picasso;
import com.uasappmob.riaunews.R;
import com.uasappmob.riaunews.config.ServerConfig;
import com.uasappmob.riaunews.utils.SessionManager;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    TextView tvFullName, tvEmail, tvPhoneNumber;
    CircleImageView photoProfile;
    Button btnCreateNews, btnListNews, btnAbout, btnLogout;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvFullName = findViewById(R.id.tv_name);
        tvEmail = findViewById(R.id.tv_email);
        tvPhoneNumber = findViewById(R.id.tv_phone_number);
        photoProfile = findViewById(R.id.imgPhotoProfile);
        btnCreateNews = findViewById(R.id.btnCreateNews);
        btnListNews = findViewById(R.id.btnListNews);
        btnAbout = findViewById(R.id.btnAbout);
        btnLogout = findViewById(R.id.btnLogout);

        Stetho.initializeWithDefaults(this);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
            finish();
        }

        HashMap<String, String> user = sessionManager.getUserDetail();

        tvFullName.setText(user.get("name"));
        tvPhoneNumber.setText(user.get("phone_number"));
        tvEmail.setText(user.get("email"));
        Picasso.get()
                .load(ServerConfig.BASE_URL + "images/users/" + user.get("image"))
                .into(photoProfile);

        btnCreateNews.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, CreateNewsActivity.class);
            startActivity(intent);
        });

        btnListNews.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ListNewsActivity.class);
            startActivity(intent);
        });

        btnAbout.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(view -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Apakah Anda yakin ingin logout?");
            alertDialogBuilder.setPositiveButton("Ya", (dialogInterface, i) -> {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                sessionManager.logoutUser();
                startActivity(intent);
                finish();
            });

            alertDialogBuilder.setNegativeButton("Tidak", (dialogInterface, i) -> {
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Ingin keluar dari aplikasi?");
        alertDialogBuilder.setMessage("Klik ya untuk keluar")
                .setCancelable(false)
                .setPositiveButton("Ya", (dialog, id) -> {
                    moveTaskToBack(true);
                    super.onDestroy();
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                })
                .setNegativeButton("Tidak", (dialog, id) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}