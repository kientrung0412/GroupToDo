package com.hanabi.todoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.QuerySnapshot;
import com.hanabi.todoapp.dao.Database;
import com.hanabi.todoapp.dao.FriendDao;
import com.hanabi.todoapp.dao.UserDao;
import com.hanabi.todoapp.models.Friend;
import com.hanabi.todoapp.models.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddFriendActivity extends AppCompatActivity implements View.OnClickListener {

    private MaterialToolbar toolbar;
    private MaterialButton btnSearch, btnSendInvitation, btnRemoveInvitation, btnSendMessage;
    private EditText edtEmail;
    private CircleImageView civAvatar;
    private TextView tvName;
    private LinearLayout llProfileSearch;
    private ProgressBar pbLoading;

    private UserDao userDao;
    private FriendDao friendDao;
    private Friend friend;
    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        initViews();
        userDao = new UserDao();
    }

    private void bindViews() {
        userDao.searchFriendByEmail(edtEmail.getText().toString().trim());
        userDao.setDataQuery(user -> {
            if (user != null) {
                llProfileSearch.setVisibility(View.VISIBLE);
                tvName.setText(user.getDisplayName());
                Glide.with(civAvatar).load(user.getPhotoUrl()).into(civAvatar);

                // kiem tra ban be
                friendDao.isFriend(Arrays.asList(Database.getFirebaseUser().getUid(), user.getUid()));
                friendDao.setGetData(snapshot -> {
                    //chua gui loi moi
                    if (snapshot.isEmpty()) {
                        this.friend = null;
                        btnSendMessage.setVisibility(View.GONE);
                        btnRemoveInvitation.setVisibility(View.GONE);
                        btnSendInvitation.setVisibility(View.VISIBLE);
                        return;
                    }
                    this.friend = new Friend();
                    Friend friend = snapshot.getDocuments().get(0).toObject(Friend.class);
                    this.friend = friend;
                    //da gui nhun chua dong y
                    if (friend.getStatus() == Friend.FRIEND_STATUS_Q) {
                        btnSendMessage.setVisibility(View.GONE);
                    } else {
                        //da la ban be
                        btnSendMessage.setVisibility(View.VISIBLE);
                    }
                    btnRemoveInvitation.setVisibility(View.VISIBLE);
                    btnSendInvitation.setVisibility(View.GONE);
                });
            }
        });

    }

    private void initViews() {
        friendDao = new FriendDao();

        toolbar = findViewById(R.id.tb_main);
        btnSearch = findViewById(R.id.btn_search);
        btnSendInvitation = findViewById(R.id.btn_send_invitation);
        btnRemoveInvitation = findViewById(R.id.btn_remove_invitation);
        btnSendMessage = findViewById(R.id.btn_send_message);
        edtEmail = findViewById(R.id.edt_email);
        civAvatar = findViewById(R.id.civ_avatar);
        tvName = findViewById(R.id.tv_name);
        llProfileSearch = findViewById(R.id.ll_profile_search);
        pbLoading = findViewById(R.id.pb_loading);

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(this);
        btnSearch.setOnClickListener(this);
        btnSendInvitation.setOnClickListener(this);
        btnRemoveInvitation.setOnClickListener(this);
        btnSendMessage.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_search:
                friend = null;
                edtEmail.clearFocus();
                bindViews();
                break;
            case R.id.btn_remove_invitation:
                friendDao.declineFriendInvitation(friend);
                bindViews();
                break;
            case R.id.btn_send_message:
                bindViews();
                break;
            case R.id.btn_send_invitation:
                friendDao.sendFriendInvitations(friend);
                bindViews();
                break;
            case -1:
                finish();
                break;
        }
    }
}