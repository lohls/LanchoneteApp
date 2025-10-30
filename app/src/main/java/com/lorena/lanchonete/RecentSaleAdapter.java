package com.lorena.lanchonete;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecentSaleAdapter extends RecyclerView.Adapter<RecentSaleAdapter.VH> {

    private Cursor cursor;

    public void submitCursor(Cursor c) {
        if (this.cursor != null && !this.cursor.isClosed()) {
            this.cursor.close();
        }
        this.cursor = c;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return (cursor == null) ? 0 : cursor.getCount();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sale, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        if (cursor == null || cursor.isClosed()) return;
        if (!cursor.moveToPosition(position)) return;

        String name = cursor.getString(1);
        int qty     = cursor.getInt(2);
        double total= cursor.getDouble(3);
        long ts     = cursor.getLong(4);

        h.title.setText(name + "  x" + qty);
        h.subtitle.setText(String.format(Locale.getDefault(),
                "Total: R$ %.2f  •  %s",
                total,
                new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date(ts))));
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView title, subtitle;
        VH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvSaleTitle);
            subtitle = itemView.findViewById(R.id.tvSaleSubtitle);
        }
    }
}
