package com.br.bancodigital.cobrar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.br.bancodigital.R;
import com.br.bancodigital.helper.FirebaseHelper;
import com.br.bancodigital.model.Cobranca;
import com.br.bancodigital.transferencia.SelecionarUsuarioActivity;
import com.br.bancodigital.transferencia.TransferirUsuarioActivity;

import java.util.Locale;

public class CobrarFormActivity extends AppCompatActivity {

    private CurrencyEditText edtValor;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cobrar_form);

        iniciaComponentes();

        configToolbar();

    }

    public void continuar(View view){

        double valor = (double) edtValor.getRawValue() / 100;

        if(valor >= 10){

            Cobranca cobranca = new Cobranca();
            cobranca.setIdEmitente(FirebaseHelper.getIdFirebase());
            cobranca.setValor(valor);

            Intent intent = new Intent(this, SelecionarUsuarioActivity.class);
            intent.putExtra("cobranca", cobranca);
            startActivity(intent);

        }else {
            showDialog();
        }

    }

    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(
                this, R.style.CustomAlertDialog
        );

        View view = getLayoutInflater().inflate(R.layout.layout_dialog_info, null);

        TextView textTitulo = view.findViewById(R.id.textTitulo);
        textTitulo.setText("Atenção");

        TextView mensagem = view.findViewById(R.id.textMensagem);
        mensagem.setText("Valor mínimo para recebimento é de maior ou igual a R$ 10,00");

        Button btnOK = view.findViewById(R.id.btnOK);
        btnOK.setOnClickListener(v -> dialog.dismiss());

        builder.setView(view);

        dialog = builder.create();
        dialog.show();

    }

    private void configToolbar(){
        TextView textTitulo = findViewById(R.id.textTitulo);
        textTitulo.setText("Cobrar");

        findViewById(R.id.ibVoltar).setOnClickListener(v -> finish());
    }

    private void iniciaComponentes(){
        edtValor = findViewById(R.id.edtValor);
        edtValor.setLocale(new Locale("PT", "br"));
    }

}