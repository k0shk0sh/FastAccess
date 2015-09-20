package com.styleme.floating.toolbox.pro.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.styleme.floating.toolbox.pro.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Kosh on 9/6/2015. copyrights are reserved
 */
public class TheTeam extends AppCompatActivity {
    @Bind(R.id.backdrop)
    ImageView backdrop;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.fab_detail)
    FloatingActionButton fabDetail;
    @Bind(R.id.main_content)
    CoordinatorLayout mainContent;
    @Bind(R.id.contactMe)
    FloatingActionButton contactMe;

    @OnClick(R.id.fab_detail)
    public void onPlus() {
        openGPlus();
    }

    @OnClick(R.id.contactMe)
    public void contact() {
        try {
            composeEmail();
        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(mainContent, R.string.error_email, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.the_team);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openGPlus() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.google.android.apps.plus", "com.google.android.apps.plus.phone.UrlGatewayActivity");
            intent.putExtra("customAppUri", "+KoShKoSh");
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/" + "+KoShKoSh" + "/posts")));
        }
    }

    public void composeEmail() throws Exception {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"kosh20111@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Fast Access");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, "Contact Me..."));
        }
    }
}
