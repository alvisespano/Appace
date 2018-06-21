package it.unive.dais.cevid.appace.component;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import it.unive.dais.cevid.appace.R;
import it.unive.dais.cevid.appace.geo.Site;
import it.unive.dais.cevid.datadroid.lib.parser.ParserException;
import it.unive.dais.cevid.datadroid.lib.util.UnexpectedException;

public class SiteActivity extends BaseActivity {

    private static final String TAG = "SiteActivity";
    static final String INTENT_SITE = "site";

    protected FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitleFont();

        Intent intent = getIntent();
        Site site = (Site) intent.getSerializableExtra(INTENT_SITE);
        Log.d(TAG, String.format("got site: %s", site));

        try {
            CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
            collapsingToolbar.setTitle(site.getTitle());
            Drawable d = getDrawable(site.getPhoto());
            toolbar.setTitle(site.getTitle());
            toolbar.setSubtitle(site.getTitle());
            ImageView v = findViewById(R.id.site_imageview);
            v.setImageDrawable(d);
            ((TextView) findViewById(R.id.site_textview)).setText(site.getDescription());
        } catch (ParserException e) {
            Log.e(TAG, String.format("exception caught: %s", e));
            e.printStackTrace();
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Button goGMaps = findViewById(R.id.goGMaps);
        goGMaps.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(SiteActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SiteActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            fusedLocationClient.getLastLocation().addOnSuccessListener(SiteActivity.this,
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

    private Drawable getDrawable(String name) {
        Resources resources = getResources();
        final int resourceId = resources.getIdentifier(name, "drawable", getPackageName());
        try {
            return resources.getDrawable(resourceId, null);
        } catch (Resources.NotFoundException e) {
            // TODO: una volta testato il CSV questo errore non può più accadere
//            throw new UnexpectedException(String.format("SiteActivity.getDrawable(): cannot find resource by name '%s'", name));
            Log.e(TAG, String.format("SiteActivity.getDrawable(): cannot find resource by name '%s'", name));
            Toast.makeText(this, String.format("Questa immagine è sbagliata: '%s' non esiste. Correggere il CSV", name), Toast.LENGTH_LONG).show();
            return getDrawable(R.drawable.beolco);
        }
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
