package com.example.filrouge.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.filrouge.MainActivity;
import com.example.filrouge.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "frallo " + "FirebaseMsgService";
    private static final String CHANNEL_ID   = "filrouge_channel";
    private static final String CHANNEL_NAME = "Incidents FilRouge";
    private static int notificationId = 0; // incrémenté à chaque notif pour les empiler

    // ------------------------------------------------------------------ token

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        // En production : envoyer ce token au serveur backend
        Log.d(TAG, "Nouveau token Firebase : " + token);
    }

    // ------------------------------------------------------------------ messages

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "Message reçu de : " + remoteMessage.getFrom());

        String title = null;
        String body  = null;

        // Payload "Data" : clés personnalisées titre/corps
        Map<String, String> data = remoteMessage.getData();
        if (!data.isEmpty()) {
            title = data.get("titre");
            body  = data.get("corps");
            Log.d(TAG, "Payload Data → titre=" + title + " corps=" + body);
        }

        // Payload "Notification" : titre/body standard Firebase
        if (remoteMessage.getNotification() != null) {
            if (title == null) title = remoteMessage.getNotification().getTitle();
            if (body  == null) body  = remoteMessage.getNotification().getBody();
            Log.d(TAG, "Payload Notification → titre=" + title + " corps=" + body);
        }

        // Valeurs par défaut si rien n'a été fourni
        if (title == null) title = "Nouvel incident";
        if (body  == null) body  = "Un nouvel incident a été signalé.";

        showNotification(title, body);
    }

    // ------------------------------------------------------------------ affichage

    private void showNotification(String title, String body) {
        // Crée le canal (obligatoire Android 8+, ignoré sur les versions antérieures)
        createNotificationChannel();

        // PendingIntent : ouvre MainActivity au clic
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                notificationId,
                intent,
                PendingIntent.FLAG_IMMUTABLE // obligatoire sur Android 12+
        );

        // Construction de la notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        // Empiler les notifications (ID différent à chaque fois)
        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(notificationId++, builder.build());
        }
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
        );
        channel.setDescription("Notifications pour les incidents FilRouge");
        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
    }
}
