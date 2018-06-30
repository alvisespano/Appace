package it.unive.dais.cevid.appace.component;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import it.unive.dais.cevid.appace.R;
import it.unive.dais.cevid.appace.geo.Site;

public class SiteActivity extends BaseActivity {

    private static final String TAG = "SiteActivity";
    public static final String BUNDLE_KEY_SITE = "site";

    protected FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitleFont();

        Intent intent = getIntent();
        Site site = (Site) intent.getSerializableExtra(BUNDLE_KEY_SITE);
        Log.d(TAG, String.format("got site: %s", site));

        String title = site.getTitle();
        toolbar.setTitle(title);
        toolbar.setSubtitle(title);

        this.<TextView>findViewById(R.id.site_title_textview).setText(title);
        ImageView iv = findViewById(R.id.site_imageview);
        iv.setImageDrawable(getPhoto(this, site));

        TextView tv = findViewById(R.id.site_textview);
        tv.setText(site.getDescription());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        ImageButton goGMaps = findViewById(R.id.gotomaps_site_button);
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
                        navigate(currentPosition, site.getPosition());
                    });
        });
    }

    public static Drawable getPhoto(Context ctx, Site site) {
        Resources resources = ctx.getResources();
        String name = site.getPhoto();
        final int resourceId = resources.getIdentifier(name, "drawable", ctx.getPackageName());
        try {
            return resources.getDrawable(resourceId, null);
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, String.format("SiteActivity.getDrawable(): cannot find resource by name '%s'", name));
            Toast.makeText(ctx, String.format("Questa immagine è sbagliata: '%s' non esiste. Correggere il CSV", name), Toast.LENGTH_LONG).show();
            return ctx.getDrawable(R.drawable.beolco);
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
