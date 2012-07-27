package com.thoughtworks.hp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.thoughtworks.hp.R;
import com.thoughtworks.hp.models.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductListAdapter extends ArrayAdapter<Product> {

    private List<Product> productList = new ArrayList<Product>();
    private Context context;

    private static class ViewHolder {
        TextView productName;
        TextView productUOM;
        TextView productPrice;
    }

    public ProductListAdapter(Context context, int textViewResourceId, List<Product> products) {
        super(context, textViewResourceId, products);
        this.context = context;
        this.productList = products;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) setTagWithViewHolder(convertView);

        holder = (ViewHolder) convertView.getTag();

        Product product = productList.get(position);
        if(product != null) populateProductDetailsForLineItem(holder, product);

        return convertView;
    }

    private void populateProductDetailsForLineItem(ViewHolder holder, Product product) {
        TextView productNameTextView = holder.productName;
        productNameTextView.setText(product.getName());

        TextView productCostTextView = holder.productPrice;
        productCostTextView.setText(product.getCostAsString());

        TextView productUOMTextView = holder.productUOM;
        productUOMTextView.setText(product.getUom());
    }

    private void setTagWithViewHolder(View convertView) {
        ViewHolder holder = new ViewHolder();
        LayoutInflater view = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = view.inflate(R.layout.product_line_item, null);

        holder.productName = (TextView) convertView.findViewById(R.id.product_name);
        holder.productPrice = (TextView) convertView.findViewById(R.id.product_price);
        holder.productUOM = (TextView) convertView.findViewById(R.id.product_uom);

        convertView.setTag(holder);
    }

}
