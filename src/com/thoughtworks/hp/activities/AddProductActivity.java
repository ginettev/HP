package com.thoughtworks.hp.activities;

import android.app.Activity;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import com.thoughtworks.hp.R;
import com.thoughtworks.hp.adapters.ProductListAdapter;
import com.thoughtworks.hp.datastore.HpDatabase;
import com.thoughtworks.hp.datastore.ProductTable;
import com.thoughtworks.hp.models.Product;

import java.util.ArrayList;
import java.util.List;

public class AddProductActivity extends Activity implements TextWatcher {

    private ProductTable productTable;
    private SQLiteOpenHelper database;

    private ProductListAdapter autoSuggestAdapter;
    private List<Product> autoSuggestedProductList = new ArrayList<Product>();

    private ProductListAdapter productListAdapter;
    private List<Product> toBuyProductList = new ArrayList<Product>();

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.add_product);

        initDependencies();
        initToBuyListView();
        initAutoSuggestListView();
        attachSelfAsTextWatcherToSearchBox();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {}
        finally {
            if(this.database != null) this.database.close();
        }
    }

    private void initDependencies() {
        this.database = new HpDatabase(this);
        this.productTable = new ProductTable(this.database);
    }

    private void initToBuyListView() {
        this.productListAdapter = new ProductListAdapter(this, R.layout.product_line_item, toBuyProductList);
        ListView toBuyProductListView = (ListView) this.findViewById(R.id.to_buy_product_view);
        toBuyProductListView.setAdapter(this.productListAdapter);
    }

    private void initAutoSuggestListView() {
        this.autoSuggestAdapter = new ProductListAdapter(this, R.layout.product_auto_suggest_line_item, autoSuggestedProductList);
        ListView autoSuggestListView = (ListView) this.findViewById(R.id.auto_suggest_list);
        autoSuggestListView.setAdapter(this.autoSuggestAdapter);
        autoSuggestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                toBuyProductList.add(autoSuggestedProductList.get(position));
                productListAdapter.notifyDataSetChanged();
                autoSuggestedProductList.clear();
                autoSuggestAdapter.notifyDataSetChanged();
                ((EditText)AddProductActivity.this.findViewById(R.id.searchBox)).setText("");

            }
        });
    }

    private List<Product> findMatchingProducts(String nameFragment) {
        return productTable.findByMatchingName(nameFragment);
    }

    private void attachSelfAsTextWatcherToSearchBox() {
        ((EditText)this.findViewById(R.id.searchBox)).addTextChangedListener(this);
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        if (charSequence == null || charSequence.length() < 1) return;
        autoSuggestedProductList.clear();
        autoSuggestedProductList.addAll(findMatchingProducts(charSequence.toString()));
        autoSuggestAdapter.notifyDataSetChanged();
    }

    @Override
    public void afterTextChanged(Editable editable) { }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) { }
}