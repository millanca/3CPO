package com.example.christopher.a3cpocompatibility;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class SearchProperty extends ArrayAdapter<Property> {

    private Activity context;
    private List<Property> searchList;

    public SearchProperty(Activity context, List<Property> listingList){
        super(context, R.layout.search_listings, listingList);
        this.context = context;
        this.searchList = listingList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.search_listings, null, true);

        TextView textViewName = listViewItem.findViewById(R.id.textViewSearchListingName);
        TextView textViewAddress = listViewItem.findViewById(R.id.textViewSearchListingAddress);
        TextView textViewPrice = listViewItem.findViewById(R.id.textViewSearchListingPrice);

        Property listing = searchList.get(position);

        textViewName.setText(listing.getName());
        textViewAddress.setText(listing.getAddress());
        textViewPrice.setText("");

        return listViewItem;
    }
}
