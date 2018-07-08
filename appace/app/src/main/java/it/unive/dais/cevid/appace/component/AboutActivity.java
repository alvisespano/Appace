package it.unive.dais.cevid.appace.component;

import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.LeadingMarginSpan;
import android.widget.TextView;

import it.unive.dais.cevid.appace.BuildConfig;
import it.unive.dais.cevid.appace.R;


/**
 * Activity per la schermata di crediti e about.
 *
 * @author Alvise Spanò, Università Ca' Foscari
 */
public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_about);
        // TODO: migliorare l'indentazione della prima linea di ogni paragrafo, ma stare attenti ai tag html che vengono persi nella conversione a Spannable
//        indentTextView(R.id.about_textview, R.string.credits);
//        indentTextView(R.id.about2_textview, R.string.credits2);
    }

    private void indentTextView(@IdRes int rid, @StringRes int sid) {
        TextView tv = findViewById(rid);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Spanned s = Html.fromHtml(getString(sid), Html.FROM_HTML_MODE_COMPACT);
            tv.setText(s);
        }
    }

    public static SpannableString indentText(Spanned text, int marginFirstLine, int marginNextLines) {
        SpannableString result=new SpannableString(text);
        result.setSpan(new LeadingMarginSpan.Standard(marginFirstLine, marginNextLines),0,text.length(),0);
        return result;
    }
}
