package it.unive.dais.cevid.appace.component;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Objects;

import it.unive.dais.cevid.appace.R;


public class SiteDetailsActivity extends AppCompatActivity {

    private static final String TAG = "SiteDetailsActivity";
    private LatLng currentPosition;
    private LatLng markerPosition;

    protected FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sitedetails);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        // TODO: use title for looking up the site

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Button goGMaps = (Button) findViewById(R.id.goGMaps);
        goGMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(SiteDetailsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SiteDetailsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(SiteDetailsActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location loc) {
                                if (loc != null) {
                                    SiteDetailsActivity.this.currentPosition = new LatLng(loc.getLatitude(), loc.getLongitude());
                                    Log.i(TAG, "current position updated");
                                    if (currentPosition != null)
                                        SiteDetailsActivity.this.markerPosition = (LatLng) Objects.requireNonNull(intent.getExtras()).get("MarkerPosition");
                                    navigate(currentPosition, markerPosition);
                                }
                            }
                        });


            }
        });
    }

    protected void backToMap() {
        startActivity(new Intent(this, MapsActivity.class));
    }

    protected void navigate(LatLng from, LatLng to) {
        Intent navigation = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr=" + from.latitude + "," + from.longitude + "&daddr=" + to.latitude + "," + to.longitude + ""));
        navigation.setPackage("com.google.android.apps.maps");
        Log.i(TAG, String.format("starting navigation from %s to %s", from, to));
        startActivity(navigation);
    }


}
