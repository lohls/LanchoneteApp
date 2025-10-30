package com.lorena.lanchonete;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    private DBHelper db;

    private RecyclerView recycler;
    private ProductAdapter adapter;
    private TextInputEditText etSearch;

    private final List<ProductItem> all = new ArrayList<>();
    private final List<ProductItem> filtered = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        db = new DBHelper(this);

        recycler = findViewById(R.id.recyclerProducts);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter(filtered, db, this::carregarProdutos);
        recycler.setAdapter(adapter);


        etSearch = findViewById(R.id.editSearch);
        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filtrar(s == null ? "" : s.toString());
                }
                @Override public void afterTextChanged(Editable s) {}
            });
        }

        FloatingActionButton fab = findViewById(R.id.fabAddProduct);
        if (fab != null) {
            fab.setOnClickListener(v -> startActivity(new Intent(this, ProductFormActivity.class)));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarProdutos();
    }

    private void carregarProdutos() {
        all.clear();
        Cursor c = db.getAllProducts();
        try {
            while (c.moveToNext()) {
                int id = c.getInt(0);
                String name = c.getString(1);
                double price = c.getDouble(2);
                int stock = c.getInt(3);
                all.add(new ProductItem(id, name, price, stock));
            }
        } finally {
            c.close();
        }
        filtrar(etSearch != null && etSearch.getText() != null ? etSearch.getText().toString() : "");
    }

    private void filtrar(String q) {
        filtered.clear();
        if (q == null || q.trim().isEmpty()) {
            filtered.addAll(all);
        } else {
            String needle = q.toLowerCase();
            for (ProductItem p : all) {
                if (p.name.toLowerCase().contains(needle)) {
                    filtered.add(p);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    static class ProductItem {
        int id; String name; double price; int stock;
        ProductItem(int id, String name, double price, int stock) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.stock = stock;
        }
    }
}
