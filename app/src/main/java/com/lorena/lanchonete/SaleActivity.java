package com.lorena.lanchonete;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class SaleActivity extends AppCompatActivity {

    private TextInputEditText etProduto;
    private TextInputEditText etQuantidade;
    private TextInputEditText etValorTotal;
    private DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);

        db = new DBHelper(this);

        etProduto    = findViewById(R.id.editProdutoVenda);
        etQuantidade = findViewById(R.id.editQuantidadeVenda);
        etValorTotal = findViewById(R.id.editValorTotal);

        MaterialButton btn = findViewById(R.id.btnRegistrarVenda);
        btn.setOnClickListener(v -> registrarVenda());
    }

    private void registrarVenda() {
        String prod = text(etProduto);
        String qtdStr = text(etQuantidade);
        String totalStr = text(etValorTotal);

        if (TextUtils.isEmpty(prod)) { etProduto.setError("Informe o produto"); return; }

        int qtd;
        try {
            qtd = Integer.parseInt(qtdStr);
            if (qtd <= 0) { etQuantidade.setError("Quantidade > 0"); return; }
        } catch (Exception e) {
            etQuantidade.setError("Quantidade inválida");
            return;
        }

        Integer productId = db.getProductIdByName(prod);
        Double price = (productId != null) ? db.getProductPriceById(productId) : null;

        double total;
        if (price != null) {
            total = price * qtd;
        } else {
            try {
                total = Double.parseDouble(totalStr.replace(",", "."));
                if (total <= 0) { etValorTotal.setError("Valor > 0"); return; }
            } catch (Exception e) {
                etValorTotal.setError("Informe o valor total (produto não cadastrado)");
                return;
            }
        }

        long res = db.insertSale(productId, prod, qtd, total);
        if (res < 0) {
            Toast.makeText(this, "Falha ao registrar venda", Toast.LENGTH_SHORT).show();
            return;
        }

        if (productId != null) {
            Integer stock = db.getStockById(productId);
            if (stock != null) {
                int novo = stock - qtd;
                if (novo < 0) novo = 0;
                db.updateStock(productId, novo);
            }
        }

        Toast.makeText(this, "Venda registrada!", Toast.LENGTH_SHORT).show();
        etProduto.setText(""); etQuantidade.setText(""); etValorTotal.setText("");
        etProduto.requestFocus();
    }

    private String text(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }
}
