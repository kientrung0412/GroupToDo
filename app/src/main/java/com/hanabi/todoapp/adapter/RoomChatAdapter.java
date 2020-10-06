package com.hanabi.todoapp.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hanabi.todoapp.models.Message;
import com.hanabi.todoapp.models.RoomChat;
import com.hanabi.todoapp.R;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class RoomChatAdapter extends RecyclerView.Adapter<RoomChatAdapter.HolderRoomChat> {

    private LayoutInflater layoutInflater;
    private ArrayList<RoomChat> data;
    private OnClickRoomChatListener listener;

    public RoomChatAdapter(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
    }

    public void setListener(OnClickRoomChatListener listener) {
        this.listener = listener;
    }

    public void setData(ArrayList<RoomChat> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HolderRoomChat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.view_room_chat, parent, false);
        return new HolderRoomChat(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderRoomChat holder, int position) {
        final RoomChat roomChat = data.get(position);
        holder.bindView(roomChat);
        if (listener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClickRoomChat(roomChat);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onClickLongRoomChat(roomChat);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }


    public class HolderRoomChat extends RecyclerView.ViewHolder {

        private TextView tvLastContent, tvNameRoom, tvTimeChat;
        private CircleImageView civImageRoom;

        public HolderRoomChat(@NonNull View itemView) {
            super(itemView);
            tvLastContent = itemView.findViewById(R.id.tv_last_content_chat);
            tvNameRoom = itemView.findViewById(R.id.tv_name_room);
            tvTimeChat = itemView.findViewById(R.id.tv_time_chat);
            civImageRoom = itemView.findViewById(R.id.civ_avatar_room);
        }

        private void bindView(RoomChat roomChat) {
            Message lastMessage = roomChat.getMessages().get(roomChat.getMessages().size() - 1);
            DateFormat dateFormat = new SimpleDateFormat("dd/MM HH:mm");
            Date date = lastMessage.getCreatedAt();
            String dateStr = dateFormat.format(date);

            tvLastContent.setText(lastMessage.getContent());
            tvTimeChat.setText(dateStr);
        }
    }

    public interface OnClickRoomChatListener {
        void onClickRoomChat(RoomChat roomChat);

        void onClickLongRoomChat(RoomChat roomChat);
    }

}
