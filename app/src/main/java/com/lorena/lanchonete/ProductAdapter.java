package com.lorena.lanchonete;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.VH> {

    public interface OnChangedListener { void onChanged(); }

    private final List<ProductListActivity.ProductItem> data;
    private final DBHelper db;
    private final OnChangedListener listener;

    public ProductAdapter(List<ProductListActivity.ProductItem> data, DBHelper db, OnChangedListener listener) {
        this.data = data;
        this.db = db;
        this.listener = listener;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        ProductListActivity.ProductItem p = data.get(position);
        h.title.setText(p.name);
        h.subtitle.setText("R$ " + String.format("%.2f", p.price) + "  •  Estoque: " + p.stock);

        h.btnMore.setOnClickListener(v -> {
            PopupMenu menu = new PopupMenu(v.getContext(), h.btnMore);
            menu.getMenu().add(v.getContext().getString(R.string.editar));
            menu.getMenu().add(v.getContext().getString(R.string.excluir));
            menu.setOnMenuItemClickListener(item -> {
                String title = String.valueOf(item.getTitle());
                if (title.equals(v.getContext().getString(R.string.editar))) {
                    Intent i = new Intent(v.getContext(), ProductFormActivity.class);
                    i.putExtra("id", p.id);
                    i.putExtra("name", p.name);
                    i.putExtra("price", p.price);
                    i.putExtra("stock", p.stock);
                    v.getContext().startActivity(i);
                    return true;
                } else if (title.equals(v.getContext().getString(R.string.excluir))) {
                    db.deleteProduct(p.id);
                    Toast.makeText(v.getContext(), R.string.produto_excluido, Toast.LENGTH_SHORT).show();
                    if (listener != null) listener.onChanged();
                    return true;
                }
                return false;
            });
            menu.show();
        });
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView title, subtitle;
        ImageView btnMore;
        VH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);
            subtitle = itemView.findViewById(R.id.tvSubtitle);
            btnMore = itemView.findViewById(R.id.btnMore);
        }
    }
}
