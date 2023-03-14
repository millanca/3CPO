package com.example.christopher.a3cpocompatibility;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VehiclesActivity extends AppCompatActivity {

    private Spinner spinnerSort;
    private EditText editTextSearch;
    private Button buttonSearch;
    private Button buttonHomepage;
    private Button buttonSearchAddress;
    private ListView listSearchListings;
    private ArrayList<Vehicle> searchList = new ArrayList<>();
    private TextView textViewAddress;

    private String searchKeyword;

    final DatabaseReference ListingDatabase = FirebaseDatabase.getInstance().getReference("Vehicles");
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser user = firebaseAuth.getCurrentUser();

    //address dialog
    private AddressResultReceiver receiver;
    private EditText editTextSearchAddressDialog;
    private Button buttonSearchAddressDialog;
    private ListView listViewAddressDialog;
    private Dialog addressDialog;
    private TextView textViewResultsAddressDialog;
    private Address querriedAddress;
    private boolean hasQuerried;

    private String foundAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicles);

        receiver = new AddressResultReceiver(new Handler());
        hasQuerried = false;

        editTextSearch = findViewById(R.id.editTextSearch);
        buttonSearch = findViewById(R.id.buttonSearch);
        buttonHomepage = findViewById(R.id.buttonHomepage);
        buttonSearchAddress = findViewById(R.id.buttonSearchAddress);
        textViewAddress = findViewById(R.id.textViewAddress);
        listSearchListings = findViewById(R.id.listSearchListings);
        spinnerSort = findViewById(R.id.spinnerSort);

        addItemsOnSpinner();
        searchKeyword = "";


        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                updateSearch();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        buttonHomepage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchKeyword = editTextSearch.getText().toString().trim();
                updateSearch();
                Toast.makeText(VehiclesActivity.this, "Searching", Toast.LENGTH_SHORT).show();
            }
        });

        /*listSearchListings.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // open the view listing layout for the selected search item
                Intent viewListingIntent = new Intent(getApplicationContext(), ViewListingActivity.class);
                Vehicle selectedListing = (Vehicle)adapterView.getItemAtPosition(position);
                viewListingIntent.putExtra("listing", selectedListing);
                try {
                    startActivity(viewListingIntent);
                } catch (Exception e) {
                    Toast.makeText(VehiclesActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }

            }
        });*/

        buttonSearchAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hasQuerried = false;
                textViewAddress.setText("");
                showAddressDialog();
            }
        });

    }
    public boolean withinProx(double qLat, double qLng, double cLat, double cLng){
        double distance = haversine(qLat, qLng, cLat, cLng);
        return distance < 15;
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateSearch();
    }

    private void updateSearch() {
        ListingDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                searchList.clear();
                for(DataSnapshot listingSnapshot : dataSnapshot.getChildren()) {
                    Vehicle listing = listingSnapshot.getValue(Vehicle.class);
                        if(listing.getPlate().contains(searchKeyword) || listing.getAddress().contains(searchKeyword) || listing.getModel().contains(searchKeyword)){
                            searchList.add(listing);
                        }
                }
                //call function to sort listings to display by the value specified in the spinner
                searchList = (ArrayList<Vehicle>) sortBySpinner(searchList, spinnerSort.getSelectedItem().toString());

                SearchVehicle adapter = new SearchVehicle(VehiclesActivity.this, searchList);
                listSearchListings.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void showAddressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_verify_address, null);

        listViewAddressDialog = view.findViewById(R.id.listViewAddressDialog);
        editTextSearchAddressDialog = view.findViewById(R.id.editTextSearchAddressDialog);
        buttonSearchAddressDialog =  view.findViewById(R.id.buttonSearchAddressDialog);
        textViewResultsAddressDialog = view.findViewById(R.id.textViewResultsAddressDialog);

        buttonSearchAddressDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String addressQuerry = editTextSearchAddressDialog.getText().toString();
                if(!addressQuerry.equals("")){
                    //fetch
                    startIntentService(addressQuerry);
                }
            }
        });

        addressDialog = builder.setView(view)
                .setTitle("Search Destination Address")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).create();
        addressDialog.show();
        listViewAddressDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView parent, View v, final int position, long id) {
                querriedAddress = (Address) parent.getItemAtPosition(position);
                updateWithQuerriedAddress();
                addressDialog.dismiss();
            }
        });
    }

    public void createAddressListViewDialog(ArrayList<Address> addrs){
        if(addrs == null || addrs.isEmpty()){
            textViewResultsAddressDialog.setText("No Results. Try to Be More Exact");
        }
        else{
            textViewResultsAddressDialog.setText("Results");
            AddressList adapter = new AddressList(VehiclesActivity.this, addrs);
            listViewAddressDialog.setAdapter(adapter);
        }

    }

    public void updateWithQuerriedAddress(){
        hasQuerried = true;
        ArrayList<String> addressFrags = new ArrayList<>();
        for(int i = 0; i <= querriedAddress.getMaxAddressLineIndex(); i++){
            addressFrags.add(querriedAddress.getAddressLine(i));
        }

        foundAddress = TextUtils.join(System.getProperty("line.separator"), addressFrags);
        if(foundAddress.length() < 50){
            textViewAddress.setText(foundAddress);
        }
        else{
            textViewAddress.setText(foundAddress.substring(0, 50));
        }

        updateSearch();
        SearchVehicle adapter = new SearchVehicle(VehiclesActivity.this, searchList);
        listSearchListings.setAdapter(adapter);
    }

    private void addItemsOnSpinner() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(this, R.array.sort_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(sortAdapter);
    }

    public List<Vehicle> sortBySpinner(List<Vehicle> list, String sortBy) {
        List<Vehicle> result = list;
        String column = "";
        String[] sortValues = getResources().getStringArray(R.array.sort_array);

        if(sortBy.equals(sortValues[0])) { //sort by newest
            Collections.reverse(result);
        }
        else if(sortBy.equals(sortValues[1])) { //sort by oldest
            //do nothing, default already sorted by oldest
        }
        return result;
    }

    private void startIntentService(String addr){
        Intent intent = new Intent(getApplicationContext(), GeocodeIntentService.class);
        intent.putExtra("RECEIVER", receiver);
        intent.putExtra("ADDRESS", addr);
        startService(intent);
    }

    private class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler){
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, final Bundle resultData){

            final ArrayList<Address> addrs = resultData.getParcelableArrayList("ADDRESSES");
            runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    createAddressListViewDialog(addrs);
                }
            });

        }
    }
    //source: haversine formula http://www.movable-type.co.uk/scripts/latlong.html
    public double haversine(double lat1, double lng1, double lat2, double lng2){
        double r = 6341; //6371 km
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double latChange = Math.toRadians(lat2-lat1);
        double lngChange = Math.toRadians(lng2 - lng1);

        double a = Math.sin(latChange/2) * Math.sin(latChange/2);
        a = a + (Math.cos(phi1) * Math.cos(phi2) * Math.sin(lngChange/2) * Math.sin(lngChange/2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = r * c;

        return d;
    }
}