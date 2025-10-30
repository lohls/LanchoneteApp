package com.lorena.lanchonete;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "lanchonete.db";
    private static final int DB_VERSION = 3;
    private static final String T_PRODUCTS = "products";
    private static final String T_SALES = "sales";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + T_PRODUCTS + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name TEXT NOT NULL UNIQUE," +
                        "price REAL NOT NULL," +
                        "stock INTEGER NOT NULL DEFAULT 0" +
                        ");"
        );

        db.execSQL(
                "CREATE TABLE " + T_SALES + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "product_id INTEGER," +
                        "product_name TEXT," +
                        "quantity INTEGER," +
                        "total REAL," +
                        "date TEXT DEFAULT CURRENT_TIMESTAMP," +
                        "FOREIGN KEY(product_id) REFERENCES " + T_PRODUCTS + "(id) ON DELETE SET NULL" +
                        ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + T_SALES);
        db.execSQL("DROP TABLE IF EXISTS " + T_PRODUCTS);
        onCreate(db);
    }


    public long addProduct(String name, double price, int stock) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("price", price);
        cv.put("stock", stock);
        return db.insert(T_PRODUCTS, null, cv);
    }

    public Cursor getAllProducts() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT id, name, price, stock FROM " + T_PRODUCTS + " ORDER BY id DESC", null);
    }

    public int updateProduct(int id, String name, double price, int stock) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("price", price);
        cv.put("stock", stock);
        return db.update(T_PRODUCTS, cv, "id=?", new String[]{String.valueOf(id)});
    }

    public int deleteProduct(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(T_PRODUCTS, "id=?", new String[]{String.valueOf(id)});
    }


    public long insertOrUpdateProduct(String name, double price, int stock) {
        Integer id = getProductIdByName(name);
        if (id != null) {
            return updateProduct(id, name, price, stock);
        } else {
            return addProduct(name, price, stock);
        }
    }

    public Integer getProductIdByName(String name) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id FROM " + T_PRODUCTS + " WHERE name=? LIMIT 1", new String[]{name});
        Integer id = null;
        if (c.moveToFirst()) id = c.getInt(0);
        c.close();
        return id;
    }

    public Double getProductPriceById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT price FROM " + T_PRODUCTS + " WHERE id=? LIMIT 1", new String[]{String.valueOf(id)});
        Double price = null;
        if (c.moveToFirst()) price = c.getDouble(0);
        c.close();
        return price;
    }

    public Integer getStockById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT stock FROM " + T_PRODUCTS + " WHERE id=? LIMIT 1", new String[]{String.valueOf(id)});
        Integer stock = null;
        if (c.moveToFirst()) stock = c.getInt(0);
        c.close();
        return stock;
    }

    public int updateStock(int id, int newStock) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("stock", newStock);
        return db.update(T_PRODUCTS, cv, "id=?", new String[]{String.valueOf(id)});
    }

    public long addSale(String productName, int quantity, double total) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("product_name", productName);
        cv.put("quantity", quantity);
        cv.put("total", total);
        return db.insert(T_SALES, null, cv);
    }

    public long insertSale(Integer productId, String productName, int quantity, double total) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        if (productId != null) cv.put("product_id", productId);
        cv.put("product_name", productName);
        cv.put("quantity", quantity);
        cv.put("total", total);
        return db.insert(T_SALES, null, cv);
    }

    public Cursor getAllSales() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT id, product_id, product_name, quantity, total, date FROM " + T_SALES + " ORDER BY id DESC", null);
    }


    public double getTotalRevenue() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT IFNULL(SUM(total),0) FROM " + T_SALES, null);
        double total = 0;
        if (c.moveToFirst()) total = c.getDouble(0);
        c.close();
        return total;
    }

    public int getTotalItemsSold() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT IFNULL(SUM(quantity),0) FROM " + T_SALES, null);
        int total = 0;
        if (c.moveToFirst()) total = c.getInt(0);
        c.close();
        return total;
    }

    public Cursor getRecentSales(int limit) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(
                "SELECT product_name, quantity, total, date FROM " + T_SALES + " ORDER BY id DESC LIMIT " + limit,
                null
        );
    }

    public int getSalesCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + T_SALES, null);
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }

    public double getAverageTicket() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT IFNULL(AVG(total),0) FROM " + T_SALES, null);
        double avg = 0;
        if (c.moveToFirst()) avg = c.getDouble(0);
        c.close();
        return avg;
    }
}
