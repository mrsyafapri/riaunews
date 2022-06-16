package com.uasappmob.riaunews.activity;

import static android.text.TextUtils.isEmpty;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.uasappmob.riaunews.R;
import com.uasappmob.riaunews.model.User;
import com.uasappmob.riaunews.response.ResponseLogin;
import com.uasappmob.riaunews.rest.ApiClient;
import com.uasappmob.riaunews.rest.ApiInterface;
import com.uasappmob.riaunews.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin;
    TextView tvRegister;

    private ApiInterface service;
    private ProgressDialog progressDialog;
    private SessionManager sessionManager;
    private static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.et_login_username);
        etPassword = findViewById(R.id.et_login_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);

        service = ApiClient.getClient().create(ApiInterface.class);
        sessionManager = new SessionManager(this);

        btnLogin.setOnClickListener(view -> {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            if (isEmpty(username))
                etUsername.setError("Tidak boleh kosong");
            else if (isEmpty(password))
                etPassword.setError("Tidak boleh kosong");
            else
                loginUser();
        });
        tvRegister.setOnClickListener(view -> registerUser());
    }

    private void loginUser() {
        progressDialog = ProgressDialog.show(this, null, "Sedang Login", true, false);
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        service.login(username, password).enqueue(new Callback<ResponseLogin>() {
            @Override
            public void onResponse(@NonNull Call<ResponseLogin> call, @NonNull Response<ResponseLogin> response) {
                ResponseLogin responseLogin = response.body();
                assert responseLogin != null;
                if (responseLogin.getStatus()) {
                    progressDialog.dismiss();
                    User userLoggedIn = responseLogin.getUser();
                    sessionManager.createLoginSession(userLoggedIn);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    Toast.makeText(LoginActivity.this, responseLogin.getMessage(), Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, responseLogin.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseLogin> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Log.e(TAG, "onFailure:" + t.getLocalizedMessage());
                Toast.makeText(LoginActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerUser() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}
