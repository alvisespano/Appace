package it.unive.dais.cevid.appace.component;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.unive.dais.cevid.appace.R;
import it.unive.dais.cevid.appace.geo.Site;
import it.unive.dais.cevid.datadroid.lib.parser.CsvParser;

public class MapsActivity extends BaseActivity
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
    private List<Marker> markers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // inizializza le preferenze
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // trova gli oggetti che rappresentano i bottoni e li salva come campi d'istanza
        button_here = findViewById(R.id.button_here);
        button_car = findViewById(R.id.button_car);

        // API per i servizi di localizzazione
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // inizializza la mappa asincronamente
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // quando viene premito il pulsante HERE viene eseguito questo codice
        button_here.setOnClickListener(v -> {
            gpsCheck();
            updateCurrentPosition();
            if (currentPosition != null) {
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
                navigate((Site) Objects.requireNonNull(marker.getTag()));
            }
        });
        return false;
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        startSiteActivity(this, (Site) Objects.requireNonNull(marker.getTag()));
    }


    public static void startSiteActivity(Context ctx, Site site) {
        Intent intent = new Intent(ctx, SiteActivity.class);
        intent.putExtra(SiteActivity.BUNDLE_KEY_SITE, site.getCsvRow());
        ctx.startActivity(intent);
    }

    // methods dealing with the map
    //

    private void applyMapSettings() {
        if (gMap != null) {
            Log.d(TAG, "applying map settings");
            gMap.setMapType(SettingsActivity.getMapStyle(this));
        }
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
        markers = new ArrayList<>();
        for (CsvParser.Row row : getCsvRowsFromIntent()) {
            Site site = new Site(this, row);
            String ord = site.getRomanOrdinal();
            @IdRes int mid;
            switch (site.getEra()) {
                case PreXX:
                    mid = R.drawable.marker_yellow;
                    break;
                case XX:
                    mid = R.drawable.marker_red;
                    break;
                default:
                    mid = R.drawable.marker_green;
                    break;
            }
            MarkerOptions opts = new MarkerOptions()
                    .title(String.format("%s. %s", ord, site.getTitle()))
                    .position(site.getPosition())
                    .snippet(site.getAddress())
                    .icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(this, mid, ord)));

            final Context ctx = this;
            gMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {

                    LinearLayout info = new LinearLayout(ctx);
                    info.setOrientation(LinearLayout.VERTICAL);

                    TextView title = new TextView(ctx);
                    title.setTextColor(Color.BLACK);
                    title.setGravity(Gravity.CENTER);
                    title.setTypeface(null, Typeface.BOLD);
                    title.setText(marker.getTitle());

                    TextView snippet = new TextView(ctx);
                    snippet.setTextColor(Color.GRAY);
                    snippet.setGravity(Gravity.CENTER);
                    snippet.setTypeface(null, Typeface.NORMAL);
                    snippet.setText(marker.getSnippet());

                    info.addView(title);
                    info.addView(snippet);

                    return info;
                }
            });

            Marker m = gMap.addMarker(opts);
            m.setTag(site);
            markers.add(m);
        }
        goToInitialPosition();
    }

    private void goToInitialPosition() {
        assert gMap != null;
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(getInitialPosition(), getResources().getInteger(R.integer.initial_zoom_factor)));
    }

    @NonNull
    private LatLng getInitialPosition() {
        if (markers != null && markers.size() >= 1) {
            return ((Site) Objects.requireNonNull(markers.get(0).getTag())).getPosition();
        }
        return DEFAULT_INITIAL_POSITION;
    }


    public static Bitmap writeTextOnDrawable(Context ctx, @IdRes int id, String text) {
        Bitmap bm0 = BitmapFactory.decodeResource(ctx.getResources(), id).copy(Bitmap.Config.ARGB_8888, true);
        return writeTextOnDrawable(ctx, bm0, text);
    }

    public static Bitmap writeTextOnDrawable(Context ctx, Bitmap bm0, String text) {
        final double scale = 0.45;
        Bitmap bm = Bitmap.createScaledBitmap(bm0, (int) (bm0.getWidth() * scale), (int) (bm0.getHeight() * scale), true);
        Typeface tf = Typeface.create("mantinia", Typeface.BOLD);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1);
        paint.setColor(Color.WHITE);
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(convertToPixels(ctx, 10));

        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);

        Canvas canvas = new Canvas(bm);

        //If the text is bigger than the canvas , reduce the font size
        if (textRect.width() >= (canvas.getWidth() - 4))     //the padding on either sides is considered as 4, so as to appropriately fit in the text
            paint.setTextSize(convertToPixels(ctx, 7));        //Scaling needs to be used for different dpi's

        final int adjustmentX = 0, adjustmentY = -8;

        //Calculate the positions
        int xPos = (canvas.getWidth() / 2) + adjustmentX;     //-2 is for regulating the x position offset

        //"- ((paint.descent() + paint.ascent()) / 2)" is the distance from the baseline to the center.
        int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2)) + adjustmentY;

        canvas.drawText(text, xPos, yPos, paint);

        return bm;
    }


    public static int convertToPixels(Context context, int nDP) {
        final float conversionScale = context.getResources().getDisplayMetrics().density;

        return (int) ((nDP * conversionScale) + 0.5f);

    }

}
