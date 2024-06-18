package com.br.bancodigital.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.br.bancodigital.R;
import com.br.bancodigital.helper.GetMask;
import com.br.bancodigital.model.Extrato;

import java.util.List;

public class ExtratoDashboardAdapter extends RecyclerView.Adapter<ExtratoDashboardAdapter.MyViewHolder> {

    private final List<Extrato> extratoList;
    private final Context context;

    public ExtratoDashboardAdapter(List<Extrato> extratoList, Context context) {
        this.extratoList = extratoList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_extrato, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Extrato extrato = extratoList.get(position);

        String icon = extrato.getOperacao().substring(0, 1);
        holder.textIcon.setText(icon);
        holder.textOperacao.setText(extrato.getOperacao());
        holder.textData.setText(GetMask.getDate(extrato.getData(), 4));
        holder.textValor.setText(context.getString(R.string.text_valor, GetMask.getValor(extrato.getValor())));

        if (extrato.getTipo().equals("ENTRADA")) {
            holder.textIcon.setBackgroundResource(R.drawable.bg_entrada);
        } else {
            holder.textIcon.setBackgroundResource(R.drawable.bg_saida);
        }
    }

    @Override
    public int getItemCount() {
        return extratoList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textIcon, textOperacao, textData, textValor;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textIcon = itemView.findViewById(R.id.textIcon);
            textOperacao = itemView.findViewById(R.id.textOperacao);
            textData = itemView.findViewById(R.id.textData);
            textValor = itemView.findViewById(R.id.textValor);
        }
    }
}
