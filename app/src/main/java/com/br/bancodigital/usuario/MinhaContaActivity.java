package com.br.bancodigital.usuario;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.br.bancodigital.R;
//import com.br.bancodigital.app.MainActivity;
import com.br.bancodigital.helper.FirebaseHelper;
import com.br.bancodigital.model.Usuario;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class MinhaContaActivity extends AppCompatActivity {

    private final int REQUEST_GALERIA = 100;

    private EditText edtNome;
    private EditText edtEmail;
    private EditText edtTelefone;
    private ProgressBar progressBar;
    private ImageView imagemPerfil;

    private String caminhoImagem;

    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minha_conta);

        configToolbar();

        iniciaComponentes();

        configDados();

        configCliques();

    }

    private void configCliques(){
        imagemPerfil.setOnClickListener(v -> abrirGaleria() );
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALERIA);
    }


    public void validaDados(View view) {

        String nome = edtNome.getText().toString();
        String telefone = edtTelefone.getText().toString();

        if (!nome.isEmpty()) {
            if (!telefone.isEmpty()) {

                ocultarTeclado();

                progressBar.setVisibility(View.VISIBLE);

                usuario.setNome(nome);
                usuario.setTelefone(telefone);

                if(caminhoImagem != null){
                    salvarImagemFirebase();
                }else {
                    salvarDadosUsuario();
                }

            } else {
                edtTelefone.requestFocus();
                edtTelefone.setError("Informe seu telefone.");
            }
        } else {
            edtNome.requestFocus();
            edtNome.setError("Informe seu nome.");
        }

    }

    private void salvarImagemFirebase(){

        StorageReference storageReference = FirebaseHelper.getStorageReference()
                .child("imagens")
                .child("perfil")
                .child(FirebaseHelper.getIdFirebase() + ".JPEG");

        UploadTask uploadTask = storageReference.putFile(Uri.parse(caminhoImagem));
        uploadTask.addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnCompleteListener(task -> {

            usuario.setUrlImagem(task.getResult().toString());
            salvarDadosUsuario();

        })).addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());

    }

    private void salvarDadosUsuario(){
        DatabaseReference usuarioRef = FirebaseHelper.getDatabaseReference()
                .child("usuarios")
                .child(usuario.getId());
        usuarioRef.setValue(usuario).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(this, "Informações salvas com sucesso.", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Não foi possível salvar as informações.", Toast.LENGTH_SHORT).show();
            }
            progressBar.setVisibility(View.GONE);
        });
    }

    private void configDados() {
        usuario = (Usuario) getIntent().getSerializableExtra("usuario");

        edtNome.setText(usuario.getNome());
        edtTelefone.setText(usuario.getTelefone());
        edtEmail.setText(usuario.getEmail());

        if(usuario.getUrlImagem() != null){
            Picasso.get().load(usuario.getUrlImagem())
                    .placeholder(R.drawable.loading)
                    .into(imagemPerfil);
        }

        progressBar.setVisibility(View.GONE);
    }

    private void configToolbar() {
        TextView textTitulo = findViewById(R.id.textTitulo);
        textTitulo.setText("Perfil");

        findViewById(R.id.ibVoltar).setOnClickListener(v -> finish());
    }

    private void iniciaComponentes() {
        edtNome = findViewById(R.id.edtNome);
        edtEmail = findViewById(R.id.edtEmail);
        edtTelefone = findViewById(R.id.edtTelefone);
        progressBar = findViewById(R.id.progressBar);
        imagemPerfil = findViewById(R.id.imagemPerfil);
    }

    // Oculta o teclado do dispositivo
    private void ocultarTeclado() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(edtNome.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_GALERIA){

                Bitmap bitmap;

                Uri imagemSelecionada = data.getData();
                caminhoImagem = data.getData().toString();

                if(Build.VERSION.SDK_INT < 28){

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagemSelecionada);
                        imagemPerfil.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }else {

                    ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), imagemSelecionada);
                    try {
                        bitmap = ImageDecoder.decodeBitmap(source);
                        imagemPerfil.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }
        }

    }

}