package com.br.bancodigital.model;

import android.view.View;
import android.view.ViewStructure;

import com.br.bancodigital.helper.FirebaseHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;

import java.io.Serializable;

public class Notificacao implements Serializable {

    private String id;
    private String idEmitente;
    private String idDestinario;
    private String idOperacao;
    private long data;
    private String operacao; // COBRANCA ou TRANSFERENCIA
    private boolean lida;

    public Notificacao() {
        DatabaseReference notificacaoRef = FirebaseHelper.getDatabaseReference();
        setId(notificacaoRef.push().getKey());
    }

    public void enviar() {
        DatabaseReference notificacaoRef = FirebaseHelper.getDatabaseReference()
                .child("notificacoes")
                .child(getIdDestinario())
                .child(getId());
        notificacaoRef.setValue(this).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DatabaseReference notificacaoUpdate = notificacaoRef
                        .child("data");
                notificacaoUpdate.setValue(ServerValue.TIMESTAMP);
            }
        });
    }

    public void salvar(){
        DatabaseReference notificacaoRef = FirebaseHelper.getDatabaseReference()
                .child("notificacoes")
                .child(FirebaseHelper.getIdFirebase())
                .child(this.id)
                .child("lida");
        notificacaoRef.setValue(!this.lida);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdEmitente() {
        return idEmitente;
    }

    public void setIdEmitente(String idEmitente) {
        this.idEmitente = idEmitente;
    }

    public String getIdDestinario() {
        return idDestinario;
    }

    public void setIdDestinario(String idDestinario) {
        this.idDestinario = idDestinario;
    }

    public String getIdOperacao() {
        return idOperacao;
    }

    public void setIdOperacao(String idOperacao) {
        this.idOperacao = idOperacao;
    }

    public long getData() {
        return data;
    }

    public void setData(long data) {
        this.data = data;
    }

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }

    public boolean isLida() {
        return lida;
    }

    public void setLida(boolean lida) {
        this.lida = lida;
    }
}
