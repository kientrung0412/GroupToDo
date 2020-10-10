package com.hanabi.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.hanabi.todoapp.dao.Database;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private MaterialToolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationViewl;
    private SearchView searchView;
    private LinearLayout lnNavHeader;

    private TextView tvEmail, tvName;
    private CircleImageView civAvatar;

    private MyToDoFragment myToDoFragment = new MyToDoFragment();
    private GroupToDoFragment groupToDoFragment = new GroupToDoFragment();
    private RoomChatFragment roomChatFragment = new RoomChatFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        toolbar = findViewById(R.id.tb_main);
        drawerLayout = findViewById(R.id.dl_main);
        navigationViewl = findViewById(R.id.nav_view);
        lnNavHeader = navigationViewl.getHeaderView(0).findViewById(R.id.ln_nav_header);
        tvEmail = lnNavHeader.findViewById(R.id.tv_email);
        tvName = lnNavHeader.findViewById(R.id.tv_name);
        civAvatar = lnNavHeader.findViewById(R.id.civ_avatar);

        tvEmail.setText(Database.getFirebaseUser().getEmail());
        tvName.setText(Database.getFirebaseUser().getDisplayName());
        Glide.with(civAvatar).load(Database.getFirebaseUser().getPhotoUrl()).into(civAvatar);

        navigationViewl.setNavigationItemSelectedListener(this);
        toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_baseline_more_vert_24, null));
        toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24);
        lnNavHeader.setOnClickListener(this);
        setSupportActionBar(toolbar);
        setupDrawer();
        initFragment();
        showFragment(myToDoFragment);
    }

    private void initFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fl_main, myToDoFragment);
        transaction.add(R.id.fl_main, roomChatFragment);

        transaction.commit();
    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(myToDoFragment);
        transaction.hide(roomChatFragment);
        transaction.show(fragment);
//        transaction.replace(R.id.fl_main, fragment);
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
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.top_app_bar, menu);
//        searchView = (SearchView) menu.findItem(R.id.it_search).getActionView();
//        setupSearchBar();
        return true;
    }

    private void setupSearchBar() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.it_my_todo:
                showFragment(myToDoFragment);
                break;
            case R.id.it_group_todo:
                showFragment(groupToDoFragment);
                break;
            case R.id.it_chat:
                showFragment(roomChatFragment);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ln_nav_header:

                break;
        }
    }

    public MaterialToolbar getToolbar() {
        return toolbar;
    }
}