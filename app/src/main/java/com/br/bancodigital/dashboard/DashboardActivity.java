package com.br.bancodigital.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.br.bancodigital.R;
import com.br.bancodigital.adapter.ExtratoDashboardAdapter;
import com.br.bancodigital.model.Extrato;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = "DashboardActivity";
    private TextView textViewTotalEntrada, textViewTotalSaida;
    private PieChart pieChart;
    private RecyclerView recyclerView;
    private ExtratoDashboardAdapter extratoAdapter;
    private List<Extrato> extratoList = new ArrayList<>();
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private double totalEntrada = 0.0;
    private double totalSaida = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        textViewTotalEntrada = findViewById(R.id.textViewTotalEntrada);
        textViewTotalSaida = findViewById(R.id.textViewTotalSaida);
        pieChart = findViewById(R.id.pieChart);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        extratoAdapter = new ExtratoDashboardAdapter(extratoList, this);
        recyclerView.setAdapter(extratoAdapter);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("extratos").child(userId);

            carregarDados();
        } else {
            Toast.makeText(this, "Usuário não está autenticado.", Toast.LENGTH_SHORT).show();
        }
    }

    private void carregarDados() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalEntrada = 0.0;
                totalSaida = 0.0;
                extratoList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Extrato extrato = ds.getValue(Extrato.class);

                    if (extrato != null) {
                        String tipo = extrato.getTipo();
                        Double valor = extrato.getValor();

                        if (tipo != null && valor != null) {
                            if ("ENTRADA".equals(tipo)) {
                                totalEntrada += valor;
                            } else if ("SAIDA".equals(tipo)) {
                                totalSaida += valor;
                            }
                            extratoList.add(extrato);
                        } else {
                            Log.e(TAG, "Tipo ou Valor de Extrato é nulo");
                        }
                    } else {
                        Log.e(TAG, "Extrato é nulo");
                    }
                }

                textViewTotalEntrada.setText(String.format(Locale.getDefault(), "Total de Entradas: R$ %.2f", totalEntrada));
                textViewTotalSaida.setText(String.format(Locale.getDefault(), "Total de Saídas: R$ %.2f", totalSaida));

                configurarGrafico(totalEntrada, totalSaida);
                extratoAdapter.notifyDataSetChanged(); // Notifica o adapter de mudanças nos dados
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Erro ao carregar dados: ", databaseError.toException());
                Toast.makeText(DashboardActivity.this, "Erro ao carregar dados.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configurarGrafico(double totalEntrada, double totalSaida) {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float) totalEntrada, "Entradas"));
        entries.add(new PieEntry((float) totalSaida, "Saídas"));

        PieDataSet dataSet = new PieDataSet(entries, "Transações");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.invalidate(); // refresh
    }
}
