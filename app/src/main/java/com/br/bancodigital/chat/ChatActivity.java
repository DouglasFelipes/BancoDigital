package com.br.bancodigital.chat;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.br.bancodigital.R;
import com.br.bancodigital.adapter.ChatMessageAdapter;
import com.br.bancodigital.model.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ListView listViewChat;
    private EditText editTextMessage;
    private Button buttonSend;
    private DatabaseReference chatRef;
    private List<ChatMessage> chatMessages;
    private ChatMessageAdapter adapter;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        chatRef = FirebaseDatabase.getInstance().getReference("chats");

        listViewChat = findViewById(R.id.listViewChat);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);

        chatMessages = new ArrayList<>();
        adapter = new ChatMessageAdapter(this, R.layout.chat_message, chatMessages);
        listViewChat.setAdapter(adapter);

        buttonSend.setOnClickListener(v -> sendMessage());

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatMessages.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatMessage chatMessage = snapshot.getValue(ChatMessage.class);
                    chatMessages.add(chatMessage);
                }
                adapter.notifyDataSetChanged();
                listViewChat.setSelection(chatMessages.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChatActivity.this, "Failed to load chat messages.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage() {
        String message = editTextMessage.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            return;
        }

        ChatMessage chatMessage = new ChatMessage(message, currentUser.getEmail());
        chatRef.push().setValue(chatMessage);
        editTextMessage.setText("");
    }
}
