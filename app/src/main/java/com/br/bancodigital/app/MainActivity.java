package com.br.bancodigital.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.br.bancodigital.R;
import com.br.bancodigital.adapter.ExtratoAdapter;
import com.br.bancodigital.autenticacao.LoginActivity;
import com.br.bancodigital.chat.ChatActivity;
import com.br.bancodigital.dashboard.DashboardActivity;
import com.br.bancodigital.deposito.DepositoFormActivity;
import com.br.bancodigital.extrato.ExtratoActivity;
import com.br.bancodigital.cobrar.CobrarFormActivity;
import com.br.bancodigital.helper.FirebaseHelper;
import com.br.bancodigital.helper.GetMask;
import com.br.bancodigital.model.Extrato;
import com.br.bancodigital.model.Notificacao;
import com.br.bancodigital.model.Usuario;
import com.br.bancodigital.notificacoes.NotificacoesActivity;
import com.br.bancodigital.recarga.RecargaFormActivity;
import com.br.bancodigital.transferencia.TransferenciaFormActivity;
import com.br.bancodigital.usuario.MinhaContaActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final List<Notificacao> notificacaoList = new ArrayList<>();

    private final List<Extrato> extratoListTemp = new ArrayList<>();
    private final List<Extrato> extratoList = new ArrayList<>();

    private ExtratoAdapter extratoAdapter;
    private RecyclerView rvExtrato;

    private TextView textSaldo;
    private ProgressBar progressBar;
    private TextView textInfo;
    private ImageView imagemPerfil;
    private TextView textNotificacao;

    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iniciaComponentes();

        configCliques();

        configRv();

    }

    @Override
    protected void onStart() {
        super.onStart();

        recuperaDados();
    }

    private void recuperaNotificacoes() {
        DatabaseReference notificacaoRef = FirebaseHelper.getDatabaseReference()
                .child("notificacoes")
                .child(FirebaseHelper.getIdFirebase());

        // Adiciona um filtro para buscar apenas notificações não lidas
        Query query = notificacaoRef.orderByChild("lida").equalTo(false);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notificacaoList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Notificacao notificacao = ds.getValue(Notificacao.class);
                    notificacaoList.add(notificacao);
                }

                if (notificacaoList.isEmpty()) {
                    textNotificacao.setText("0");
                    textNotificacao.setVisibility(View.GONE);
                } else {
                    textNotificacao.setText(String.valueOf(notificacaoList.size()));
                    textNotificacao.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Falha ao carregar os dados: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void configRv() {
        rvExtrato.setLayoutManager(new LinearLayoutManager(this));
        rvExtrato.setHasFixedSize(true);
        extratoAdapter = new ExtratoAdapter(extratoList, getBaseContext());
        rvExtrato.setAdapter(extratoAdapter);
    }

    private void recuperaExtrato() {
        DatabaseReference extratoRef = FirebaseHelper.getDatabaseReference()
                .child("extratos")
                .child(FirebaseHelper.getIdFirebase());
        extratoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    extratoList.clear();
                    extratoListTemp.clear();

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Extrato extrato = ds.getValue(Extrato.class);
                        extratoListTemp.add(extrato);
                    }

                    textInfo.setText("");
                } else {
                    textInfo.setText("Nenhuma movimentação encontrada.");
                }

                Collections.reverse(extratoListTemp);

                for (int i = 0; i < extratoListTemp.size(); i++) {
                    if (i <= 5) {
                        extratoList.add(extratoListTemp.get(i));
                    }
                }

                progressBar.setVisibility(View.GONE);
                extratoAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void recuperaDados() {

        recuperaUsuario();

        recuperaExtrato();

        recuperaNotificacoes();

    }

    private void recuperaUsuario() {
        DatabaseReference usuarioRef = FirebaseHelper.getDatabaseReference()
                .child("usuarios")
                .child(FirebaseHelper.getIdFirebase());
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usuario = snapshot.getValue(Usuario.class);
                configDados();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configDados() {
        textSaldo.setText(getString(R.string.text_valor, GetMask.getValor(usuario.getSaldo())));

        if (usuario.getUrlImagem() != null) {
            Picasso.get().load(usuario.getUrlImagem())
                    .placeholder(R.drawable.loading)
                    .into(imagemPerfil);
        }

        textInfo.setText("");
        progressBar.setVisibility(View.GONE);
    }

    private void configCliques() {
        findViewById(R.id.cardDeposito).setOnClickListener(v ->
                redirecionaUsuario(DepositoFormActivity.class));

        imagemPerfil.setOnClickListener(v -> perfilUsuario());

        findViewById(R.id.cardRecarga).setOnClickListener(v ->
                redirecionaUsuario(RecargaFormActivity.class));

        findViewById(R.id.cardTransferir).setOnClickListener(v ->
                redirecionaUsuario(TransferenciaFormActivity.class));

        findViewById(R.id.cardDeslogar).setOnClickListener(v -> {
            FirebaseHelper.getAuth().signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        });

        findViewById(R.id.cardExtrato).setOnClickListener(v ->
                redirecionaUsuario(ExtratoActivity.class)
        );

        findViewById(R.id.textVerTodas).setOnClickListener(v ->
                redirecionaUsuario(ExtratoActivity.class)
        );

        findViewById(R.id.btnNotificacao).setOnClickListener(v ->
                redirecionaUsuario(NotificacoesActivity.class));

        findViewById(R.id.cardCobrar).setOnClickListener(v ->
                redirecionaUsuario(CobrarFormActivity.class));

        findViewById(R.id.cardMinhaConta).setOnClickListener(v -> perfilUsuario());

        findViewById(R.id.cardChat).setOnClickListener(v -> {
            Intent intent = new Intent(this, ChatActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.buttonSendEmail).setOnClickListener(v -> sendEmailToSupport());

        findViewById(R.id.buttonDashboard).setOnClickListener(v -> {
            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
        });

    }

    private void redirecionaUsuario(Class clazz){
        startActivity(new Intent(this, clazz));
    }

    private void sendEmailToSupport() {
        String[] recipients = new String[]{"douglas.feliperod@gmail.com"};
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", recipients[0], null));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Suporte ao Cliente");
        intent.putExtra(Intent.EXTRA_TEXT, "Escreva sua mensagem aqui...");
        try {
            startActivity(Intent.createChooser(intent, "Enviar e-mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Não há aplicativo de e-mail instalado.", Toast.LENGTH_SHORT).show();
        }
    }

    private void perfilUsuario() {
        if (usuario != null) {
            Intent intent = new Intent(this, MinhaContaActivity.class);
            intent.putExtra("usuario", usuario);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Ainda estamos recuperando as informações.", Toast.LENGTH_SHORT).show();
        }
    }

    private void iniciaComponentes() {
        textSaldo = findViewById(R.id.textSaldo);
        progressBar = findViewById(R.id.progressBar);
        textInfo = findViewById(R.id.textInfo);
        rvExtrato = findViewById(R.id.rvExtrato);
        imagemPerfil = findViewById(R.id.imagemPerfil);
        textNotificacao = findViewById(R.id.textNotificacao);
    }

}