package com.lorena.lanchonete;

import android.content.Intent;               // IMPORTANTE
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ProductFormActivity extends AppCompatActivity {

    private TextInputEditText etName;
    private TextInputEditText etPrice;
    private TextInputEditText etStock;
    private DBHelper db;
    private MaterialButton btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_form);

        db = new DBHelper(this);

        etName  = findViewById(R.id.editNomeProduto);
        etPrice = findViewById(R.id.editPreco);
        etStock = findViewById(R.id.editQuantidade);
        btnSave = findViewById(R.id.btnSalvar);


        Intent i = getIntent();
        if (i != null && i.hasExtra("id")) {
            etName.setText(i.getStringExtra("name"));
            etPrice.setText(String.valueOf(i.getDoubleExtra("price", 0)));
            etStock.setText(String.valueOf(i.getIntExtra("stock", 0)));
            btnSave.setText("Atualizar Produto");
        }

        btnSave.setOnClickListener(v -> salvarProduto());
    }

    private void salvarProduto() {
        String nome = safeText(etName);
        String precoStr = safeText(etPrice);
        String estoqueStr = safeText(etStock);

        if (TextUtils.isEmpty(nome)) { etName.setError("Informe o nome"); etName.requestFocus(); return; }
        double preco;
        int estoque;
        try { preco = Double.parseDouble(precoStr.replace(",", ".")); }
        catch (Exception e) { etPrice.setError("Preço inválido"); etPrice.requestFocus(); return; }
        try { estoque = Integer.parseInt(estoqueStr); }
        catch (Exception e) { etStock.setError("Estoque inválido"); etStock.requestFocus(); return; }

        Intent i = getIntent();
        if (i != null && i.hasExtra("id")) {
            int id = i.getIntExtra("id", -1);
            db.updateProduct(id, nome, preco, estoque);
            Toast.makeText(this, "Produto atualizado!", Toast.LENGTH_SHORT).show();
        } else {
            db.addProduct(nome, preco, estoque);
            Toast.makeText(this, "Produto salvo!", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private String safeText(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }
}
