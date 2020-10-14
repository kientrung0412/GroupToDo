package com.hanabi.todoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hanabi.todoapp.dao.Database;

import java.util.Date;

public class ProflieActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proflie);

        btnLogout = findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        FirebaseAuth.getInstance().signOut();
        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

    }
}