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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class AddVehicleActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    DatabaseReference vehicleDatabase;
    DatabaseReference customerDatabase;
    DatabaseReference propertyDatabase;

    private ArrayList<Property> searchList = new ArrayList<>();

    String addressQuerry;

    private TextView editFirstName;
    private TextView editLastName;
    private TextView editDriverLicense;
    private Button buttonFindAddress;
    private TextView editVehicleBrand;
    private TextView editVehicleModel;
    private TextView editColor;
    private TextView editLicensePlate;
    private TextView editRegistration;
    private TextView editInsuranceType;
    private Button buttonCreateVehicle;

    //address dialog
    private AddressResultReceiver receiver;
    private EditText editTextSearchAddressDialog;
    private Button buttonSearchAddressDialog;
    private ListView listViewAddressDialog;
    private Dialog addressDialog;
    private TextView textViewResultsAddressDialog;
    private Property foundProperty;
    private boolean hasFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        firebaseAuth = FirebaseAuth.getInstance();
        vehicleDatabase = FirebaseDatabase.getInstance().getReference("Vehicles");
        customerDatabase = FirebaseDatabase.getInstance().getReference("Customers");
        propertyDatabase = FirebaseDatabase.getInstance().getReference("Properties");

        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editDriverLicense = findViewById(R.id.editDriverLicense);
        //editVehicleCustomerCode = findViewById(R.id.editVehicleCustomerCode);
        buttonFindAddress = findViewById(R.id.buttonFindAddress);
        editVehicleBrand = findViewById(R.id.editVehicleBrand);
        editVehicleModel = findViewById(R.id.editVehicleModel);
        editColor = findViewById(R.id.editColor);
        editLicensePlate = findViewById(R.id.editLicensePlate);
        editRegistration = findViewById(R.id.editRegistration);
        editInsuranceType = findViewById(R.id.editInsuranceType);

        buttonCreateVehicle = findViewById(R.id.buttonCreateVehicle);
        buttonCreateVehicle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createVehicle();
            }
        });

        buttonFindAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddressDialog();
            }
        });

        hasFound = false;
        receiver = new AddressResultReceiver(new Handler());
    }

    private void createVehicle(){
        if(hasFound){
            // get the values from all the fields and save them to the Firebase db
            String firstName = editFirstName.getText().toString().trim();
            String lastName = editLastName.getText().toString().trim();
            String driverLicense = editDriverLicense.getText().toString().trim();
            //String CustomerCode = editVehicleCustomerCode.getText().toString().trim();
            String vehicleAddress = foundProperty.getAddress();//buttonFindAddress.getText().toString().trim();
            String vehicleBrand = editVehicleBrand.getText().toString().trim();
            String vehicleModel = editVehicleModel.getText().toString().trim();
            String vehicleColor = editColor.getText().toString().trim();
            String vehicleLicensePlate = editLicensePlate.getText().toString().trim();
            String vehicleRegistration = editRegistration.getText().toString().trim();
            String vehicleInsuranceType = editInsuranceType.getText().toString().trim();

            FirebaseUser user = firebaseAuth.getCurrentUser(); //get user

            if(TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(driverLicense) || TextUtils.isEmpty(vehicleBrand) || TextUtils.isEmpty(vehicleModel) || TextUtils.isEmpty(vehicleColor) || TextUtils.isEmpty(vehicleLicensePlate) || TextUtils.isEmpty(vehicleRegistration) || TextUtils.isEmpty(vehicleInsuranceType)) {
                Toast.makeText(AddVehicleActivity.this, "Please Enter All Fields", Toast.LENGTH_SHORT).show();
            } else {
                String customerId = vehicleDatabase.push().getKey();
                String vehicleId = vehicleDatabase.push().getKey();

                Customer newCustomer = new Customer(customerId,firstName,lastName,driverLicense);

                Vehicle newVehicle = new Vehicle(vehicleId, newCustomer, vehicleAddress, vehicleBrand, vehicleModel, vehicleColor, vehicleLicensePlate, vehicleRegistration, vehicleInsuranceType, new Date());

                customerDatabase.child(customerId).setValue(newCustomer);
                vehicleDatabase.child(vehicleId).setValue(newVehicle);
                foundProperty.addVehicle(newVehicle);
                propertyDatabase.child(foundProperty.getPropertyId()).setValue(foundProperty);

                Toast.makeText(AddVehicleActivity.this, "Created Vehicle", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AddVehicleActivity.this, MainActivity.class));
            }
        }
        else{
            Toast.makeText(AddVehicleActivity.this, "Find Address First!", Toast.LENGTH_SHORT).show();
        }
    }
    public void showAddressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_verify_address_for_spot, null);

        listViewAddressDialog = view.findViewById(R.id.listViewAddressDialogForSpot);
        editTextSearchAddressDialog = view.findViewById(R.id.editTextSearchAddressDialogForSpot);
        buttonSearchAddressDialog =  view.findViewById(R.id.buttonSearchAddressDialogForSpot);
        textViewResultsAddressDialog = view.findViewById(R.id.textViewResultsAddressDialogForSpot);

        buttonSearchAddressDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addressQuerry = editTextSearchAddressDialog.getText().toString();
                if(!addressQuerry.equals("")){
                    //startIntentService(addressQuerry);
                    updateSearch();
                    Toast.makeText(AddVehicleActivity.this, "Searching", Toast.LENGTH_SHORT).show();
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
                foundProperty = (Property) parent.getItemAtPosition(position);
                updateWithFoundProperty();
                addressDialog.dismiss();
            }
        });
    }

    private void updateSearch() {
        propertyDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                searchList.clear();
                for(DataSnapshot listingSnapshot : dataSnapshot.getChildren()) {
                    Property listing = listingSnapshot.getValue(Property.class);
                    if(listing.getCustomerCode().contains(addressQuerry)){
                        searchList.add(listing);
                    }
                }
                SearchProperty adapter = new SearchProperty(AddVehicleActivity.this, searchList);
                listViewAddressDialog.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /*public void createPropertyListViewDialog(ArrayList<Property> addrs){
        if(addrs == null || addrs.isEmpty()){
            textViewResultsAddressDialog.setText("No Results. Try to Be More Exact");
        }
        else{
            textViewResultsAddressDialog.setText("Results");
            PropertyList adapter = new PropertyList(AddVehicleActivity.this, addrs);
            listViewAddressDialog.setAdapter(adapter);
        }

    }*/

    public void createAddressListViewDialog(ArrayList<Address> addrs){
        if(addrs == null || addrs.isEmpty()){
            textViewResultsAddressDialog.setText("No Results. Try to Be More Exact");
        }
        else{
            textViewResultsAddressDialog.setText("Results");
            AddressList adapter = new AddressList(AddVehicleActivity.this, addrs);
            listViewAddressDialog.setAdapter(adapter);
        }

    }

    public void updateWithFoundProperty(){
        hasFound = true;
        buttonFindAddress.setText(foundProperty.getName());
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
}
