package com.lorena.lanchonete;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import com.google.android.material.appbar.MaterialToolbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button btnProducts = findViewById(R.id.btnProducts);
        Button btnVendas = findViewById(R.id.btnVendas);
        Button btnRelatorios = findViewById(R.id.btnRelatorios);

        btnProducts.setOnClickListener(v ->
                startActivity(new Intent(this, ProductListActivity.class)));

        btnVendas.setOnClickListener(v ->
                startActivity(new Intent(this, SaleActivity.class)));

        btnRelatorios.setOnClickListener(v ->
                startActivity(new Intent(this, ReportActivity.class)));
    }
}
