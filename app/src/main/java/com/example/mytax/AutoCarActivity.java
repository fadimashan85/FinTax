
/*
* A class for Automatic Distance measurement (Gps) and adding the record to the database with
* required fields
* */

package com.example.mytax;
import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class AutoCarActivity extends DrawerBarActivity implements CompoundButton.OnCheckedChangeListener {

    // For location services
    private static final String TAG = AutoCarActivity.class.getSimpleName();
    TextView textView;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private SettingsClient mSettingsClient;
    private LocationCallback mLocationCallback;
    private LocationSettingsRequest mLocationSettingsRequest;
    private Location mCurrentLocation;
    private DatabaseReference mDatabase;
    final int REQUEST_CHECK_SETTINGS = 1;
    private FirebaseAuth mAuth;
    final int REQUEST_LOCATION = 2;
    public Boolean locUpdates;
    public Boolean useGPS;  // pref: use_device_location
    private ToggleButton switch1;
    SharedPreferences preferences;

    static Double lat1 = null;
    static Double lon1 = null;
    static Double lat2 = null;
    static Double lon2 = null;
    static Double distance = 0.0;
    static int status = 0;
    String distanceTracker;
    TextView distance_tracker;
    public DatePickerDialog.OnDateSetListener mDateSetListener;
    public DatePickerDialog.OnDateSetListener eDateSetListener;
    private Boolean mRequestingLocationUpdates;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    private final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    private final static String KEY_LOCATION = "location";
    private final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";

    // display DSObjectFragment when MainActivity first loads
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        FirebaseApp.initializeApp(this);
        textView = (TextView) findViewById(R.id.distance);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("mainDb");
        switch1=findViewById(R.id.switch1);
        switch1.setOnCheckedChangeListener(this);
        distance_tracker = findViewById(R.id.distance_tracker);
        mRequestingLocationUpdates = false;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        useGPS = preferences.getBoolean("use_device_location", false);
        locUpdates = false;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);
        loginToFirebase();
        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();
        loginToFirebase();



        Thread t = new Thread(){
            @Override
            public void run() {
                while (!isInterrupted()) {

                    try {
                        Thread.sleep(100);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                distance_tracker.setText(String.valueOf(distance));
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();


    }


    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        KEY_REQUESTING_LOCATION_UPDATES);
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            updateUI();
        }
    }


    // Start Fused Location services
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Creates a callback for receiving location events.
     */
    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                mCurrentLocation = locationResult.getLastLocation();
                updateLocationUI();
            }
        };
    }


    private void buildLocationSettingsRequest() {
        // get current locations settings of user's device
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    private void updateLocationUI() {

        final String path = getString(R.string.firebase_path);
        if (mCurrentLocation != null) {

            if (status == 0) {
                lat1 = mCurrentLocation.getLatitude();
                lon1 = mCurrentLocation.getLongitude();
            } else if ((status % 2) != 0) {
                lat2 = mCurrentLocation.getLatitude();
                lon2 = mCurrentLocation.getLongitude();
                distance += distanceBetweenTwoPoint(lat1, lon1, lat2, lon2);
            } else if ((status % 2) == 0) {
                lat1 = mCurrentLocation.getLatitude();
                lon1 = mCurrentLocation.getLongitude();
                distance += distanceBetweenTwoPoint(lat2, lon2, lat1, lon1);
            }
            status++;
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
            ref.setValue(distance);

            if (mCurrentLocation != null) {
                Log.d(TAG, "location update " + mCurrentLocation);
                //ref.setValue(location);
            }

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        mRequestingLocationUpdates = false;
                        updateUI();
                        break;
                }
                break;
        }
    }


    /**
     * Handles the Start Updates button and requests start of location updates. Does nothing if
     * updates have already been requested.
     */
    public void startUpdatesButtonHandler(View view) {
        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
            startLocationUpdates();
        }
    }

    /**
     * Handles the Stop Updates button, and requests removal of location updates.
     */

    public void stopUpdatesButtonHandler(View view) {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        stopLocationUpdates();
    }

    /**
     * Requests location updates from the FusedLocationApi. Note: we don't call this unless location
     * runtime permission has been granted.
     */
    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        updateUI();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(AutoCarActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(AutoCarActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                mRequestingLocationUpdates = false;
                        }

                        updateUI();
                    }
                });
    }

    /**
     * Updates all UI fields.
     */
    private void updateUI() {
        updateLocationUI();
    }





    private void stopLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            Log.d(TAG, "stopLocationUpdates: updates never requested, no-op.");
            return;
        }

        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mRequestingLocationUpdates = false;
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Within {@code onPause()}, we remove location updates. Here, we resume receiving
        // location updates if the user has requested them.
        if (mRequestingLocationUpdates && checkPermissions()) {
            startLocationUpdates();
        } else if (!checkPermissions()) {
            requestPermissions();
        }

        updateUI();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Remove location updates to save battery.
        stopLocationUpdates();
    }


    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        super.onSaveInstanceState(savedInstanceState);
    }



    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        /*Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();*/
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(AutoCarActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(AutoCarActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mRequestingLocationUpdates) {
                    Log.i(TAG, "Permission granted, updates requested, starting location updates");
                    startLocationUpdates();
                }
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }


    // Set new latitude and longitude based on location results
    public void setLatLong(Location location) {
        double lastLat = location.getLatitude();
        double lastLong = location.getLongitude();
        if (status == 0) {
            lat1 = location.getLatitude();
            lon1 = location.getLongitude();
        } else if ((status % 2) != 0) {
            lat2 = location.getLatitude();
            lon2 = location.getLongitude();
            distance += distanceBetweenTwoPoint(lat1, lon1, lat2, lon2);
        } else if ((status % 2) == 0) {
            lat1 = location.getLatitude();
            lon1 = location.getLongitude();
            distance += distanceBetweenTwoPoint(lat2, lon2, lat1, lon1);

            String totalDistance = new Double(distance).toString();
            textView.setText(totalDistance);
        }
        status++;
        final String path = getString(R.string.firebase_path);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
        ref.setValue(distance);


        if (location != null) {
            Log.d(TAG, "location update " + location);
            //ref.setValue(location);
        }

        SharedPreferences.Editor edit = preferences.edit();
        edit.putString("last_gps_lat", String.valueOf(lastLat));
        edit.putString("last_gps_long", String.valueOf(lastLong));
        //edit.putString("Distance", String.valueOf(distance));
        edit.apply();
    }

    double distanceBetweenTwoPoint(double srcLat, double srcLng, double desLat, double desLng) {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(desLat - srcLat);
        double dLng = Math.toRadians(desLng - srcLng);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(srcLat))
                * Math.cos(Math.toRadians(desLat)) * Math.sin(dLng / 2)
                * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;

        double meterConversion = 1609;

        return (int) (dist * meterConversion);
    }


    private void loginToFirebase() {
        // Functionality coming next step

        String email = "dishabahre@gmail.com";
        String password = "dishabahre09";
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
                email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>(){
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "firebase auth success");

                    createLocationCallback();

                } else {
                    Log.d(TAG, "firebase auth failed");
                }
            }
        });
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (switch1.isChecked()){
            mRequestingLocationUpdates = true;
            startLocationUpdates();

        }else{
            stopLocationUpdates();
            submitRecord();
            distance = 0.0;

        }

    }

    //create dialog box for adding distance travelled on a specified date and
    //add it to the database
    public void submitRecord() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View myView = inflater.inflate(R.layout.activity_car_add, null);
        myDialog.setView(myView);
        final AlertDialog dialog = myDialog.create();

        dialog.setCancelable(false);

        final EditText distance = myView.findViewById(R.id.edit_text_distance);
        final TextView startDate = myView.findViewById(R.id.text_view_startDate);
        final EditText origin = myView.findViewById(R.id.edit_text_origin);
        final EditText destination = myView.findViewById(R.id.edit_text_destination);
        final EditText purpose = myView.findViewById(R.id.edit_text_purpose);
        final EditText amount = myView.findViewById(R.id.edit_text_amount);
        final Button btnCancel = myView.findViewById(R.id.btnCancel);
        final Button btnAdd = myView.findViewById(R.id.btnSave);

        distance.addTextChangedListener(new TextWatcher() {

            // the user's changes are saved here
            public void onTextChanged(CharSequence c, int start, int before, int count) {
            }
            public void beforeTextChanged(CharSequence c, int start, int count, int after) {

                // this space intentionally left blank
            }

            public void afterTextChanged(Editable c) {
                // on text change evaluate corresponding SEK the  distance travelled
                if((distance.getText().toString()).isEmpty()){

                    distance.setError("Empty");
                }
                else if ( Double.parseDouble(distance.getText().toString())< 5 || distance.getText().toString().isEmpty()
                        ||startDate.getText().toString().isEmpty()) {
                    distance.setError("Please provide all the inputs or your distance is less than 5 Km");

                    return;
                }else{
                    double k = Double.parseDouble(distance.getText().toString());
                    Double d = k * 1.85;
                    String mam = String.format("%.2f", d);
                    amount.setText(" " + mam + "  kr");
                }

            }


        });

        //Date picker
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        AutoCarActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = month + " " + day + " " + year;
                startDate.setText(date);
            }
        };


        //gets the distance from  Gps for saving
        String mgpsDistance = distance_tracker.getText().toString().trim();
        Double kmValue =Double.parseDouble(mgpsDistance)/1000; //Change the distance from meter to killometers
        distance.setText(kmValue.toString());

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                String mDistance = distance.getText().toString().trim();
                String mStartDate = startDate.getText().toString().trim();
                String mOrgin = origin.getText().toString().trim();
                String mDestination = destination.getText().toString().trim();
                String mPurpose = purpose.getText().toString().trim();
                String mAmount = amount.getText().toString().trim();

                if(mDistance.isEmpty()|| mStartDate.startsWith("S")){
                    Toast.makeText(getApplicationContext(), "Please provide all the inputs", Toast.LENGTH_SHORT).show();
                    return;
                }

                //parse the date using a method from Salary class
                String sDate =Salary.dateFormatter(mStartDate);

                mAuth = FirebaseAuth.getInstance();
                FirebaseUser mUser=mAuth.getCurrentUser();
                String uid=mUser.getUid();
                Car car = new Car(mDistance, sDate, mOrgin, mDestination, mPurpose, mAmount);
                mDatabase.child(uid).child("cardb").child(car.getStartDate()).setValue(car);
                Toast.makeText(getApplicationContext(), "Record added", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                Intent intent = new Intent(getApplicationContext(),CarMain.class);
                        startActivity(intent);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}



