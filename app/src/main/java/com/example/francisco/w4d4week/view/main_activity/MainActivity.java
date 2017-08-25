package com.example.francisco.w4d4week.view.main_activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.francisco.w4d4week.R;
import com.example.francisco.w4d4week.injection.main_activity.DaggerMainActivityComponent;
import com.example.francisco.w4d4week.model.LocationAddress;
import com.example.francisco.w4d4week.view.maps_activity.MapsActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements MainActivityContract.View {
    private static final String TAG = "MainActivity";

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 10;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    private Location currentLocation;

    @Inject
    MainActivityPresenter presenter;

    @BindView(R.id.tv1)
    TextView tv1;

    @BindView(R.id.tv2)
    TextView tv2;

    @BindView(R.id.et1)
    EditText et1;

    @BindView(R.id.et2)
    EditText et2;

    @BindView(R.id.rv)
    RecyclerView rvList;

    RecyclerView.LayoutManager layoutManager;
    RecyclerView.ItemAnimator itemAnimator;
    ListAdapter listAdapter;

    ArrayList<LocationAddress> list = new ArrayList<>();

    GoogleApiClient mGoogleApiClient;
    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setupDaggerComponent();
        presenter.attachView(this);
        presenter.setContext(getApplicationContext());

        presenter.checkPermissions(MY_PERMISSIONS_REQUEST_LOCATION);

        layoutManager = new LinearLayoutManager(getApplicationContext());
        itemAnimator = new DefaultItemAnimator();
        rvList.setLayoutManager(layoutManager);
        rvList.setItemAnimator(itemAnimator);

        listAdapter = new ListAdapter(list);
        rvList.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();

        initGoogleAPIClient();
        showSettingDialog();
    }

    public void Continue() {

    }



    private void setupDaggerComponent(){
        DaggerMainActivityComponent.create().inject(this);
    }

    @OnClick({R.id.btnAddLocation, R.id.btnGoToSecond, R.id.btnGetLocation})
    public void OnClick(View view){
        switch (view.getId()){
            case R.id.btnAddLocation:
                try {
                    Location location = new Location("dummyprovider");
                    location.setLatitude(Double.parseDouble(et1.getText().toString()));
                    location.setLongitude(Double.parseDouble(et2.getText().toString()));
                    presenter.MakeRestCall(location);

                }catch(Exception ex){
                    Toast.makeText(this, "Introduce a valid latitude and longitude", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnGoToSecond:
                String[] list2 = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    list2[i] = list.get(i).getAddress();
                }
                Intent intent = new Intent(this, MapsActivity.class);
                intent.putExtra("addresses",list2);
                startActivity(intent);

                break;

            case R.id.btnGetLocation:
                presenter.getLocation();
                break;
        }
    }

    @Override
    public void SendResultRestCall(String address) {
        if(tv2.getText().toString().equals("") && currentLocation != null) {
            Log.d(TAG, "SendResultRestCall 1: ");
            tv2.setText(address);
            list.add( new LocationAddress(String.valueOf(currentLocation.getLatitude()),String.valueOf(currentLocation.getLongitude()), address));
        }
        else {
            if(!et1.getText().toString().equals("") && !et2.getText().toString().equals("")) {
                list.add(new LocationAddress(et1.getText().toString(), et2.getText().toString(), address));
                et1.setText("");
                et2.setText("");
            }
        }
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void sendLocation(Location location) {
        try {
            tv1.setText(location.toString());
            this.currentLocation = location;
            presenter.MakeRestCall(currentLocation);
        }catch(Exception ex){}
    }

    @Override
    public void showError(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /* Initiate Google API Client  */
    private void initGoogleAPIClient() {
        //Without Google API Client Auto Location Dialog will not work
        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void showSettingDialog() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//Setting priotity of Location request to high
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);//5 sec Time interval for location update
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient to show dialog always when GPS is off

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        Continue();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            Toast.makeText(MainActivity.this, "GPS needs to be enabled to use this app", Toast.LENGTH_LONG).show();
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        Toast.makeText(this, "GPS is Enabled in your device", Toast.LENGTH_LONG).show();

                        Continue();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        finish();
                        break;
                    default:
                        break;
                }
                break;
        }
    }
}
