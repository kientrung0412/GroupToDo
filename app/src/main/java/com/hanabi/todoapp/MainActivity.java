package com.hanabi.todoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.work.BackoffPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hanabi.todoapp.dao.TodoDao;
import com.hanabi.todoapp.dialog.SortBottomSheetDialog;
import com.hanabi.todoapp.models.User;
import com.hanabi.todoapp.service.TestService;
import com.hanabi.todoapp.service.TodoService;
import com.hanabi.todoapp.works.LoopWork;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, OnSuccessListener<Void>, OnFailureListener, SortBottomSheetDialog.OnClickMenuListener {
    public static final int REQUEST_CODE_OFF = 1;
    public static final String EXTRA_DETAIL_TODO = "extra.DETAIL_TODO";
    private static final String TAG = "MainActivity";

    private WorkManager workManager;

    private MaterialToolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationViewl;
    private LinearLayout lnNavHeader;
    private RelativeLayout rlHome;

    private TextView tvEmail, tvName;
    private CircleImageView civAvatar;
    private TodoService service;

    private SortBottomSheetDialog sortBottomSheetDialog;
    private static ToDoFragment toDoFragment = new ToDoFragment(Calendar.getInstance().getTime());
    private static AllTodoFragment allTodoFragment = new AllTodoFragment();
    private static TodoBookmarkFragment bookmarkFragment = new TodoBookmarkFragment();
    private FirebaseMessaging fcm = FirebaseMessaging.getInstance();

    private JobScheduler jobScheduler;

    public static ToDoFragment getToDoFragment() {
        return toDoFragment;
    }

    public static AllTodoFragment getAllTodoFragment() {
        return allTodoFragment;
    }

    private int indexNav = R.id.it_today_todo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
//        startJob();
        setupWork();
        setupService();
    }

    private void startJob() {
        jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

        ComponentName componentName = new ComponentName(this, TestService.class);
        JobInfo jobInfo = new JobInfo.Builder(0412, componentName)
                .setPeriodic(1000 * 60 * 15)
                .setRequiresCharging(false)
                .setPersisted(true)
                .build();

        if (jobScheduler.schedule(jobInfo) == JobScheduler.RESULT_SUCCESS) {
            Log.e(TAG, "startJob: " + "s");
        } else {
            Log.e(TAG, "startJob: " + "f");
        }

    }

    private void setupService() {
        Intent intent = new Intent(this, TodoService.class);
        startService(intent);
        bindService(intent, connection, BIND_AUTO_CREATE);

        fcm.subscribeToTopic("demo").addOnSuccessListener(this).addOnFailureListener(this);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            TodoService.TodoBinder binder = (TodoService.TodoBinder) iBinder;
            service = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Toast.makeText(service, "error", Toast.LENGTH_SHORT).show();
        }
    };

    private void setupWork() {
        workManager = WorkManager.getInstance(this);
        PeriodicWorkRequest periodicWorkLoop =
                new PeriodicWorkRequest.Builder(LoopWork.class, 1, TimeUnit.DAYS, 10, TimeUnit.MINUTES)
                        .setBackoffCriteria(
                                BackoffPolicy.LINEAR,
                                PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
                                TimeUnit.MILLISECONDS)
                        .build();
        workManager.enqueue(periodicWorkLoop);
    }


    private void initViews() {
        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra(LoginActivity.EXTRA_USER);
        setTitle(toDoFragment.getTitleToolBar());
        sortBottomSheetDialog = new SortBottomSheetDialog(this, this);
        toolbar = findViewById(R.id.tb_main);
        drawerLayout = findViewById(R.id.dl_main);
        navigationViewl = findViewById(R.id.nav_view);
        rlHome = findViewById(R.id.rl_home);

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
        transaction.add(R.id.fl_main, bookmarkFragment);
        transaction.add(R.id.fl_main, allTodoFragment);
        transaction.commit();
    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(toDoFragment);
        transaction.hide(bookmarkFragment);
        transaction.hide(allTodoFragment);
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
                indexNav = R.id.it_today_todo;
                break;
            case R.id.it_bookmark_todo:
                showFragment(bookmarkFragment);
                setTitle(bookmarkFragment.getTitleToolBar());
                indexNav = R.id.it_bookmark_todo;
                break;
            case R.id.it_list_todo:
                showFragment(allTodoFragment);
                setTitle(allTodoFragment.getTitleToolBar());
                indexNav = R.id.it_list_todo;
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

    }

    @Override
    public void onFailure(@NonNull Exception e) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.it_sort:
                sortBottomSheetDialog.show();
                break;
        }
        return true;
    }

    @Override
    public void clickDate() {
        switch (indexNav) {
            case R.id.it_today_todo:
                toDoFragment.getAdapterNew().sortByCreatedAt();
                toDoFragment.getAdapterDone().sortByCreatedAt();
                break;
            case R.id.it_bookmark_todo:
                bookmarkFragment.getAdapter().sortByCreatedAt();
                break;
            case R.id.it_list_todo:
                allTodoFragment.getAdapterDone().sortByCreatedAt();
                allTodoFragment.getAdapterNew().sortByCreatedAt();
                break;
        }
    }

    @Override
    public void clickBookmark() {
        switch (indexNav) {
            case R.id.it_today_todo:
                toDoFragment.getAdapterNew().sortByBookmark();
                toDoFragment.getAdapterDone().sortByBookmark();
                break;
            case R.id.it_bookmark_todo:
                bookmarkFragment.getAdapter().sortByBookmark();
                break;
            case R.id.it_list_todo:
                allTodoFragment.getAdapterDone().sortByBookmark();
                allTodoFragment.getAdapterNew().sortByBookmark();
                break;
        }
    }

    @Override
    public void clickAZ() {
        switch (indexNav) {
            case R.id.it_today_todo:
                toDoFragment.getAdapterNew().sortByContent();
                toDoFragment.getAdapterDone().sortByContent();
                break;
            case R.id.it_bookmark_todo:
                bookmarkFragment.getAdapter().sortByContent();
                break;
            case R.id.it_list_todo:
                allTodoFragment.getAdapterDone().sortByContent();
                allTodoFragment.getAdapterNew().sortByContent();
                break;
        }
    }
}