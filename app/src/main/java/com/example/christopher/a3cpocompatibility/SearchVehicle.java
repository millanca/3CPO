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

public class SearchVehicle extends ArrayAdapter<Vehicle> {

    private Activity context;
    private List<Vehicle> searchList;

    public SearchVehicle(Activity context, List<Vehicle> listingList){
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

        Vehicle listing = searchList.get(position);

        textViewName.setText(listing.getPlate());
        textViewAddress.setText(listing.getOwner().getFirstName()+" "+listing.getOwner().getLastName());
        textViewPrice.setText(listing.getBrand()+" "+listing.getModel()+", "+listing.getColor());

        return listViewItem;
    }
}
