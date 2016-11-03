package com.fastaccess.ui.modules.about;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.fastaccess.R;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

/**
 * Created by Kosh on 03 Nov 2016, 9:45 PM
 */

public class AboutMePresenter extends BasePresenter<AboutMeMvp.View> implements AboutMeMvp.Presenter {
    protected AboutMePresenter(@NonNull AboutMeMvp.View view) {
        super(view);
    }

    public static AboutMePresenter with(@NonNull AboutMeMvp.View view) {
        return new AboutMePresenter(view);
    }

    @Override public void onOpenFacebook(@NonNull Context context) {
        try {
            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/100001283501777")));
        } catch (Exception e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/k0shk0sh")));
        }
    }

    @Override public void onOpenTwitter(@NonNull Context context) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=im_kosh")));
        } catch (Exception e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/im_kosh")));
        }

    }

    @Override public void onOpenGooglePlus(@NonNull Context context) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/102911990180102979096")));
        } catch (Exception ignored) {
            Toast.makeText(context, R.string.failed_to_open_gplus, Toast.LENGTH_SHORT).show();
        }
    }

    @Override public void onOpenGithub(@NonNull Context context) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/k0shk0sh")));
        } catch (Exception ignored) {
            Toast.makeText(context, R.string.failed_to_open_github, Toast.LENGTH_SHORT).show();
        }
    }
}
