package com.fastaccess.ui.modules.whats_new;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by Kosh on 05 Nov 2016, 3:29 AM
 */

public interface WhatsNewMvp {

    interface View {}//op-out

    interface Presenter {
        void openRateApp(@NonNull Context context);
    }

}
