package com.example.christopher.a3cpocompatibility;

import android.app.Activity;
import android.location.Address;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PropertyList extends ArrayAdapter<Property> {

    private Activity context;
    private List<Property> addressList;

    public PropertyList(Activity context, List<Property> addressList){
        super(context, R.layout.address_item, addressList);
        this.context = context;
        this.addressList = addressList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.address_item, null, true);

        TextView textViewAddressLine = listViewItem.findViewById(R.id.textViewAddressLine);

        Property address = addressList.get(position);

        ArrayList<String> addressFrags = new ArrayList<>();
        for(int i = 0; i <= 6; i++){
            addressFrags.add(address.getAddress());
        }
        textViewAddressLine.setText(TextUtils.join(System.getProperty("line.separator"), addressFrags));
        return listViewItem;
    }
}