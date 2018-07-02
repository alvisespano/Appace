package it.unive.dais.cevid.appace.component;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Objects;

import it.unive.dais.cevid.appace.R;
import it.unive.dais.cevid.datadroid.lib.parser.CsvParser;

public abstract class BaseActivity extends AppCompatActivity {

    public static final String BUNDLE_KEY_ROWS = "ROWS";
    public static final String BUNDLE_KEY_SITES = "SITES";
    private static final String TAG = "BaseActivity";

    @NonNull
    public List<CsvParser.Row> getCsvRowsFromIntent() {
        //noinspection unchecked
        return (List<CsvParser.Row>) getIntent().getSerializableExtra(BUNDLE_KEY_ROWS);
    }

    protected void setCollapsingToolbarFont() {
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        Typeface mantinia = Typeface.create(ResourcesCompat.getFont(this, R.font.mantinia), Typeface.NORMAL);
        collapsingToolbar.setExpandedTitleTypeface(mantinia);
        collapsingToolbar.setCollapsedTitleTypeface(mantinia);
    }

    @NonNull
    protected String getCallingActivityShortName() {
        @Nullable ComponentName cname = getCallingActivity();
        return cname == null ? TAG : cname.getShortClassName();
    }

    protected void navigate(@NonNull final LatLng to, @Nullable final String where) {
        String tag = getCallingActivityShortName();
//                Uri.parse("http://maps.google.com/maps?saddr=" + from.latitude + "," + from.longitude + "&daddr=" + to.latitude + "," + to.longitude + ""));
        String uris = String.format("https://www.google.com/maps/dir/?api=1&query=%s,%s", to.latitude, to.latitude);
        if (where != null) uris += "&destination=" + where;

        try {
            URL url = new URL(uris);
            try {
                URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Objects.requireNonNull(url).toString()));
                intent.setPackage("com.google.android.apps.maps");
                Log.d(tag, String.format("navigation URI: %s", uri));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    Log.i(tag, String.format("starting navigation to: %s", where));
                    startActivity(intent);
                } else
                    Log.e(tag, String.format("cannot start navigation to: %s", where));
            } catch (URISyntaxException e) {
                Log.e(tag, String.format("URI syntax error: %s", url));
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            Log.e(tag, String.format("URL is malformed: %s", uris));
            e.printStackTrace();
        }
    }
}
