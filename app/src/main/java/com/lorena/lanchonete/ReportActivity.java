package com.lorena.lanchonete;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReportActivity extends AppCompatActivity {

    private DBHelper db;
    private TextView tvRevenue, tvItems, tvSalesCount;
    private RecyclerView recycler;
    private RecentSalesAdapter adapter;
    private final List<String> lines = new ArrayList<>();
    private final NumberFormat BRL = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        db = new DBHelper(this);

        tvRevenue    = findViewById(R.id.tvRevenue);
        tvItems      = findViewById(R.id.tvItems);
        tvSalesCount = findViewById(R.id.tvSalesCount);
        recycler     = findViewById(R.id.recyclerRecent);


        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecentSalesAdapter(lines);
        recycler.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarTotaisESales();
    }

    private void carregarTotaisESales() {
        try {
            double revenue = db.getTotalRevenue();
            int items      = db.getTotalItemsSold();
            int salesCount = db.getSalesCount();

            tvRevenue.setText("Faturamento: " + BRL.format(revenue));
            tvItems.setText("Itens vendidos: " + items);
            tvSalesCount.setText("Qtde de vendas: " + salesCount);

            lines.clear();

            Cursor c = db.getRecentSales(20);
            if (c != null) {
                try {
                    int idxName  = c.getColumnIndex("product_name");
                    int idxQty   = c.getColumnIndex("quantity");
                    int idxTotal = c.getColumnIndex("total");
                    int idxDate  = c.getColumnIndex("date"); //

                    while (c.moveToNext()) {
                        String name = idxName >= 0 ? c.getString(idxName) : "";
                        int qty     = idxQty  >= 0 ? c.getInt(idxQty)    : 0;
                        double tot  = idxTotal>= 0 ? c.getDouble(idxTotal): 0.0;
                        String when = idxDate >= 0 ? c.getString(idxDate) : "";

                        String linha = name + "  •  " + qty + " un  •  "
                                + java.text.NumberFormat.getCurrencyInstance(
                                new java.util.Locale("pt","BR")
                        ).format(tot);
                        if (!when.isEmpty()) linha += "  •  " + when;
                        lines.add(linha);
                    }
                } finally {
                    c.close();
                }
            }

            adapter.notifyDataSetChanged();
        } catch (Throwable t) {

            Toast.makeText(this, "Falha ao carregar relatório", Toast.LENGTH_SHORT).show();
            t.printStackTrace();
        }
    }


    static class RecentSalesAdapter extends RecyclerView.Adapter<RecentSalesAdapter.VH> {

        private final List<String> data;
        RecentSalesAdapter(List<String> data) { this.data = data; }

        @Override public VH onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            android.view.View v = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_recent_sale, parent, false);
            return new VH(v);
        }

        @Override public void onBindViewHolder(VH h, int position) {
            h.tv.setText(data.get(position));
        }

        @Override public int getItemCount() { return data.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tv;
            VH(android.view.View itemView) {
                super(itemView);
                tv = itemView.findViewById(R.id.tvLine);
            }
        }
    }
}
