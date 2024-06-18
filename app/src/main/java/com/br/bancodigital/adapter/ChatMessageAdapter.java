package com.br.bancodigital.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.br.bancodigital.R;
import com.br.bancodigital.model.ChatMessage;

import java.util.List;

public class ChatMessageAdapter extends ArrayAdapter<ChatMessage> {
    private List<ChatMessage> messages;

    public ChatMessageAdapter(@NonNull Context context, int resource, @NonNull List<ChatMessage> objects) {
        super(context, resource, objects);
        this.messages = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_message, parent, false);
        }

        ChatMessage chatMessage = messages.get(position);

        TextView messageText = convertView.findViewById(R.id.message_text);
        TextView senderText = convertView.findViewById(R.id.sender_text);

        messageText.setText(chatMessage.getMessage());
        senderText.setText(chatMessage.getSender());

        return convertView;
    }
}
