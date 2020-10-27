package com.hanabi.todoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.work.BackoffPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hanabi.todoapp.models.User;
import com.hanabi.todoapp.service.RemindService;
import com.hanabi.todoapp.works.LoopWork;

import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, OnSuccessListener<Void>, OnFailureListener {

    public static final int REQUEST_CODE_OFF = 1;

    public static final String EXTRA_DETAIL_TODO = "extra.DETAIL_TODO";

    private MaterialToolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationViewl;
    private LinearLayout lnNavHeader;

    private TextView tvEmail, tvName;
    private CircleImageView civAvatar;

    private ToDoFragment toDoFragment = new ToDoFragment();
    private FirebaseMessaging fcm = FirebaseMessaging.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setupWork();
        setupService();
    }

    private void setupService() {
//        Intent intent = new Intent(this, RemindService.class);
//        startService(intent);
        fcm.subscribeToTopic("demo").addOnSuccessListener(this).addOnFailureListener(this);
    }


    private void setupWork() {
        PeriodicWorkRequest periodicWork =
                new PeriodicWorkRequest.Builder(LoopWork.class, 1, TimeUnit.DAYS, 15, TimeUnit.MINUTES)
                        .setBackoffCriteria(
                                BackoffPolicy.LINEAR,
                                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                                TimeUnit.MILLISECONDS)
                        .build();
        WorkManager.getInstance(this).enqueue(periodicWork);
    }

    private void initViews() {
        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra(LoginActivity.EXTRA_USER);
        setTitle(toDoFragment.getTitleToolBar());
        toolbar = findViewById(R.id.tb_main);
        drawerLayout = findViewById(R.id.dl_main);
        navigationViewl = findViewById(R.id.nav_view);
        lnNavHeader = navigationViewl.getHeaderView(0).findViewById(R.id.ln_nav_header);
        tvEmail = lnNavHeader.findViewById(R.id.tv_email);
        tvName = lnNavHeader.findViewById(R.id.tv_name);
        civAvatar = lnNavHeader.findViewById(R.id.civ_avatar);

        tvEmail.setText(user.getEmail());
        tvName.setText(user.getDisplayName());
        Glide.with(civAvatar).load(user.getPhotoUrl()).into(civAvatar);

        navigationViewl.setNavigationItemSelectedListener(this);
        toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_baseline_more_vert_24, null));
        toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24);
        lnNavHeader.setOnClickListener(this);
        setSupportActionBar(toolbar);
        setupDrawer();
        initFragment();
        showFragment(toDoFragment);
    }

    private void initFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fl_main, toDoFragment);
        transaction.commit();
    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(toDoFragment);
        transaction.show(fragment);
        transaction.commit();
    }

    private void setupDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigaton_content_open, R.string.navigaton_content_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {

//        if (myToDoFragment.getLlAddTodo().getVisibility() == View.VISIBLE) {
//            myToDoFragment.getLlAddTodo().setVisibility(View.GONE);
//            myToDoFragment.getFabAdd().setVisibility(View.VISIBLE);
//            return;
//        }

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.it_today_todo:
                showFragment(toDoFragment);
                setTitle(toDoFragment.getTitleToolBar());
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ln_nav_header:
                Intent intent = new Intent(this, ProflieActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OFF) {
            if (resultCode == Activity.RESULT_OK) {
                data.getExtras();
            }
        }
    }

    @Override
    public void onSuccess(Void aVoid) {
        Toast.makeText(this, "done", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
    }
}