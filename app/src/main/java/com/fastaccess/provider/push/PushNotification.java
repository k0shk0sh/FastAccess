package com.fastaccess.provider.push;

import com.fastaccess.R;
import com.fastaccess.helper.NotificationHelper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Kosh on 24 May 2016, 6:56 PM
 */
public class PushNotification extends FirebaseMessagingService {
    @Override public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getNotification() != null) {
            NotificationHelper.notifyShort(this, remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody(), R.drawable.ic_fa);
        }
    }
}
