package it.unive.dais.cevid.appace.component;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Objects;

import it.unive.dais.cevid.appace.R;
import it.unive.dais.cevid.appace.geo.Site;
import it.unive.dais.cevid.datadroid.lib.parser.CsvParser;
import it.unive.dais.cevid.datadroid.lib.parser.ParserException;
import it.unive.dais.cevid.datadroid.lib.util.MapManager;

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {

    protected static final int REQUEST_CHECK_SETTINGS = 500;
    protected static final int PERMISSIONS_REQUEST_ACCESS_BOTH_LOCATION = 501;
    private static final String TAG = "MapsActivity";
    private static final LatLng DEFAULT_INITIAL_POSITION = new LatLng(45.4079700, 11.8858600);  // centre of Padova, Italy

    protected GoogleMap gMap;

    protected ImageButton button_here, button_car;

    protected FusedLocationProviderClient fusedLocationClient;
    @Nullable
    protected LatLng currentPosition = null;
    @Nullable
    protected Marker hereMarker = null;
    @Nullable
    private List<Marker> markers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // inizializza le preferenze
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // trova gli oggetti che rappresentano i bottoni e li salva come campi d'istanza
        button_here = (ImageButton) findViewById(R.id.button_here);
        button_car = (ImageButton) findViewById(R.id.button_car);

        // API per i servizi di localizzazione
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // inizializza la mappa asincronamente
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // quando viene premito il pulsante HERE viene eseguito questo codice
        button_here.setOnClickListener(v -> {
            Log.d(TAG, "here button clicked");
            gpsCheck();
            updateCurrentPosition();
            if (hereMarker != null) hereMarker.remove();
            if (currentPosition != null) {
                MarkerOptions opts = new MarkerOptions();
                opts.position(currentPosition);
                opts.title(getString(R.string.marker_title));
                hereMarker = gMap.addMarker(opts);
                if (gMap != null)
                    goToInitialPosition();
            } else
                Log.d(TAG, "no current position available");
        });
    }


    // ciclo di vita
    //

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyMapSettings();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gMap != null) {
            gMap.clear();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        @SuppressWarnings("unused") final LocationSettingsStates states = LocationSettingsStates.fromIntent(intent);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // inserire codice qui
                        break;
                    case Activity.RESULT_CANCELED:
                        // o qui
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_BOTH_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "permissions granted: ACCESS_FINE_LOCATION + ACCESS_COARSE_LOCATION");
                } else {
                    Log.e(TAG, "permissions not granted: ACCESS_FINE_LOCATION + ACCESS_COARSE_LOCATION");
                    Snackbar.make(this.findViewById(R.id.map), R.string.msg_permissions_not_granted, Snackbar.LENGTH_LONG);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.maps_with_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.MENU_SETTINGS:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.MENU_ABOUT:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }
        return false;
    }

    // onConnection callbacks
    //

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "location service connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "location service connection suspended");
        Toast.makeText(this, R.string.conn_suspended, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "location service connection lost");
        Toast.makeText(this, R.string.conn_failed, Toast.LENGTH_LONG).show();
    }

    // maps callbacks
    //
    //

    @Override
    public void onMapClick(LatLng latLng) {
        // nascondi il pulsante della navigazione (non quello di google maps, ma il nostro pulsante custom)
        button_car.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public void onCameraMoveStarted(int reason) {
//        setHereButtonVisibility();
    }

//    public void setHereButtonVisibility() {
//        if (gMap != null) {
//            if (gMap.getCameraPosition().target < SettingsActivity.getZoomThreshold(this)) {
//                button_here.setVisibility(View.INVISIBLE);
//            } else {
//                button_here.setVisibility(View.VISIBLE);
//            }
//        }
//    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_BOTH_LOCATION);
        } else {
            gMap.setMyLocationEnabled(true);
        }

        gMap.setOnMapClickListener(this);
        gMap.setOnMapLongClickListener(this);
        gMap.setOnCameraMoveStartedListener(this);
        gMap.setOnMarkerClickListener(this);
        gMap.setOnInfoWindowClickListener(this);

        UiSettings uis = gMap.getUiSettings();
        uis.setZoomGesturesEnabled(true);
        uis.setMyLocationButtonEnabled(true);
        gMap.setOnMyLocationButtonClickListener(
                () -> {
                    gpsCheck();
                    return false;
                });
        uis.setCompassEnabled(true);
        uis.setZoomControlsEnabled(true);
        uis.setMapToolbarEnabled(true);

        applyMapSettings();
        updateCurrentPosition();
        populateMap();
    }

    // marker stuff
    //

    @Override
    public boolean onMarkerClick(final Marker marker) {
        marker.showInfoWindow();
        button_car.setVisibility(View.VISIBLE);
        button_car.setOnClickListener(v -> {
            Snackbar.make(v, R.string.msg_button_car, Snackbar.LENGTH_SHORT);
            if (currentPosition != null) {
                navigate(currentPosition, marker.getPosition());
            }
        });
        return false;
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent intent = new Intent(this, SiteActivity.class);
        intent.putExtra(SiteActivity.INTENT_SITE, (Site) marker.getTag());
        startActivity(intent);
    }


    // methods dealing with the map
    //

    private void applyMapSettings() {
        if (gMap != null) {
            Log.d(TAG, "applying map settings");
            gMap.setMapType(SettingsActivity.getMapStyle(this));
        }
    }

    private void navigate(@NonNull LatLng from, @NonNull LatLng to) {
        Intent navigation = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr=" + from.latitude + "," + from.longitude + "&daddr=" + to.latitude + "," + to.longitude + ""));
        navigation.setPackage("com.google.android.apps.maps");
        Log.i(TAG, String.format("starting navigation from %s to %s", from, to));
        startActivity(navigation);
    }

    private void gpsCheck() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(MapsActivity.this).addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        @SuppressWarnings("deprecation")
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(result1 -> {
            final Status status = result1.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    Log.i(TAG, "All location settings are satisfied.");
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
                    try {
                        // Show the dialog by calling startResolutionForResult(), and check the result
                        // in onActivityResult().
                        status.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException e) {
                        Log.i(TAG, "PendingIntent unable to execute request.");
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                    break;
            }
        });
    }

    private void updateCurrentPosition() {
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "requiring permission");
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_BOTH_LOCATION);
        } else {
            Log.d(TAG, "permission granted");
            fusedLocationClient.getLastLocation().addOnSuccessListener(MapsActivity.this,
                    loc -> {
                        if (loc != null) {
                            currentPosition = new LatLng(loc.getLatitude(), loc.getLongitude());
                            Log.d(TAG, String.format("current position updated: %s", currentPosition));
                        }
                    });
        }
    }

    private void populateMap() {
        MapManager mm = new MapManager() {
            @NonNull
            @Override
            public GoogleMap getGoogleMap() {
                return Objects.requireNonNull(gMap);
            }
        };
        markers = mm.putMarkersFromCsv(getCsvRows(), Site::new, BitmapDescriptorFactory.HUE_GREEN);
        goToInitialPosition();
    }

    @NonNull
    private List<CsvParser.Row> getCsvRows() {
        //noinspection unchecked
        return (List<CsvParser.Row>) getIntent().getSerializableExtra(HomeActivity.KEY_ROWS);
    }

    private void goToInitialPosition() {
        assert gMap != null;
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(getInitialPosition(), getResources().getInteger(R.integer.initial_zoom_factor)));
    }

    @NonNull
    private LatLng getInitialPosition() {
        if (markers != null && markers.size() >= 1) {
            try {
                return ((Site) Objects.requireNonNull(markers.get(0).getTag())).getPosition();
            } catch (ParserException e) {
                Log.w(TAG, "cannot get position from data, falling back to default initial position");
                e.printStackTrace();
            }
        }
        return DEFAULT_INITIAL_POSITION;
    }

}
