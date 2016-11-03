package com.fastaccess.ui.modules.about;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by Kosh on 03 Nov 2016, 9:44 PM
 */

public interface AboutMeMvp {

    interface View {}

    interface Presenter {
        void onOpenFacebook(@NonNull Context context);

        void onOpenTwitter(@NonNull Context context);

        void onOpenGooglePlus(@NonNull Context context);

        void onOpenGithub(@NonNull Context context);
    }
}
