package saha.operasyon;


import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

// Firebase Üzerinden Mesaj Geldiğinde
// Calistirilacak Sinif
public class FBM_Message extends FirebaseMessagingService
{
    // Gelen Mesaj Bu Methoda
    // remoteMessage Olarak İletiliyor
    public void onMessageReceived(RemoteMessage gelenMesaj)
    {
        Map<String, String> payload = gelenMesaj.getData();
        String msg = payload.get("msg");


        NotificationCompat.Builder n = new NotificationCompat.Builder(getApplicationContext());

        n.setSmallIcon(R.mipmap.ic_launcher);
        n.setContentText(msg);
        n.setContentTitle("Sipariş Bildirimi");

        NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nManager.notify((int)(Math.random() * 10000), n.build());


    }
}
