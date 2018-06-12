package it.unive.dais.cevid.appace.component;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import it.unive.dais.cevid.appace.R;
import it.unive.dais.cevid.appace.geo.Site;
import it.unive.dais.cevid.datadroid.lib.parser.ParserException;

public class SitoActivity extends AppCompatActivity {

    private static final String TAG = "SitoActivity";
    static final String INTENT_SITE = "site";

    protected FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sito);

        Intent intent = getIntent();
        Site site = (Site) intent.getSerializableExtra(INTENT_SITE);
        Log.d(TAG, String.format("got site: %s", site));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            toolbar.setTitle(site.getTitle());
            ((TextView) findViewById(R.id.sito_textview)).setText(site.getDescription());
            toolbar.setLogo(getDrawable(site.getPhoto()));
        } catch (ParserException e) {
            Log.e(TAG, String.format("exception caught: %s", e));
            e.printStackTrace();
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Button goGMaps = (Button) findViewById(R.id.goGMaps);
        goGMaps.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(SitoActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SitoActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            fusedLocationClient.getLastLocation().addOnSuccessListener(SitoActivity.this,
                    (@NonNull Location loc) -> {
                        LatLng currentPosition = new LatLng(loc.getLatitude(), loc.getLongitude());
                        try {
                            navigate(currentPosition, site.getPosition());
                        } catch (ParserException e) {
                            e.printStackTrace();
                        }
                    });
        });
    }

    public Drawable getDrawable(String name) {
        Resources resources = getResources();
        final int resourceId = resources.getIdentifier(name, "drawable", getPackageName());
        return resources.getDrawable(resourceId, null);
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
