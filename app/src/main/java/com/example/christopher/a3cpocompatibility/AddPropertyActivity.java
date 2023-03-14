package com.example.christopher.a3cpocompatibility;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AddPropertyActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    DatabaseReference propertyDatabase;

    private TextView editName;
    private Button buttonFindAddress;
    private TextView editNumParkingSpaces;
    private CheckBox customDecalCheck;
    private TextView editCustomerCode;
    private Button buttonCreateProperty;

    //address dialog
    private AddPropertyActivity.AddressResultReceiver receiver;
    private EditText editTextSearchAddressDialog;
    private Button buttonSearchAddressDialog;
    private ListView listViewAddressDialog;
    private Dialog addressDialog;
    private TextView textViewResultsAddressDialog;
    private Address foundAddress;
    private boolean hasFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_property);

        firebaseAuth = FirebaseAuth.getInstance();
        propertyDatabase = FirebaseDatabase.getInstance().getReference("Properties");

        editName = findViewById(R.id.editName);
        buttonFindAddress = findViewById(R.id.buttonFindAddress);
        editNumParkingSpaces = findViewById(R.id.editNumParkingSpaces);
        customDecalCheck = findViewById(R.id.customDecalCheck);
        editCustomerCode = findViewById(R.id.editCustomerCode);

        buttonCreateProperty = findViewById(R.id.buttonCreateProperty);
        buttonCreateProperty.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createProperty();
            }
        });

        buttonFindAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddressDialog();
            }
        });

        hasFound = false;
        receiver = new AddPropertyActivity.AddressResultReceiver(new Handler());
    }

    private void createProperty(){
        if(hasFound){
            // get the values from all the fields and save them to the Firebase db
            String name = editName.getText().toString().trim();
            String propertyAddress = buttonFindAddress.getText().toString().trim();
            int numParkingSpaces = Integer.parseInt(editNumParkingSpaces.getText().toString().trim());
            Boolean customDecal = customDecalCheck.isChecked();
            String customerCode = editCustomerCode.getText().toString().trim();

            FirebaseUser user = firebaseAuth.getCurrentUser(); //get user

            if(TextUtils.isEmpty(name) || TextUtils.isEmpty(editNumParkingSpaces.getText().toString().trim()) || TextUtils.isEmpty(customerCode)) {
                Toast.makeText(AddPropertyActivity.this, "Please Enter All Fields", Toast.LENGTH_SHORT).show();
            } else {
                String propertyId = propertyDatabase.push().getKey();

                Property newProperty = new Property(propertyId,name,propertyAddress,numParkingSpaces,customDecal,customerCode);
                newProperty.setLatLng(foundAddress.getLatitude(),foundAddress.getLongitude());

                propertyDatabase.child(propertyId).setValue(newProperty);

                Toast.makeText(AddPropertyActivity.this, "Created Property", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AddPropertyActivity.this, MainActivity.class));
            }
        }
        else{
            Toast.makeText(AddPropertyActivity.this, "Find Address First!", Toast.LENGTH_SHORT).show();
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
                String addressQuerry = editTextSearchAddressDialog.getText().toString();
                if(!addressQuerry.equals("")){
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
                foundAddress = (Address) parent.getItemAtPosition(position);
                updateWithFoundAddress();
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
            AddressList adapter = new AddressList(AddPropertyActivity.this, addrs);
            listViewAddressDialog.setAdapter(adapter);
        }

    }

    public void updateWithFoundAddress(){
        hasFound = true;
        ArrayList<String> addressFrags = new ArrayList<>();
        for(int i = 0; i <= foundAddress.getMaxAddressLineIndex(); i++){
            addressFrags.add(foundAddress.getAddressLine(i));
        }
        buttonFindAddress.setText(TextUtils.join(System.getProperty("line.separator"), addressFrags));
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
