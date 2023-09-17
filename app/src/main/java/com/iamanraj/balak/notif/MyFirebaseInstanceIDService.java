package com.iamanraj.balak.notif;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.iamanraj.balak.MainActivity;
import com.iamanraj.balak.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {

    String CHANNEL_ID = "MyNewNotification";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        if (message.getData().size()>0){
            Map<String,String> dataMap = message.getData();

            String title = dataMap.get("title");
            String body = dataMap.get("body");
            String image = dataMap.get("image");

            createNot(body);
            addNot(title,body,image);
        }

    }

    private void addNot(String title, String body, String image) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID);
        builder.setSmallIcon(R.drawable.notif);
        builder.setColor(getResources().getColor(R.color.purple));
        builder.setContentTitle(title);
        builder.setContentText(body);

        if (image != null){
            if (image.length() > 10 && image.startsWith("http")){
                builder.setLargeIcon(getBitmapFromUrl(image));
                builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(getBitmapFromUrl(image)));

            }
        }

        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setAutoCancel(true);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("title",title);
        intent.putExtra("body",body);
        intent.putExtra("image",image);
        PendingIntent pendingIntent;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            pendingIntent = PendingIntent.getActivity(this, 1, intent,PendingIntent.FLAG_IMMUTABLE);

        }else {
            pendingIntent = PendingIntent.getActivity(this, 1, intent,PendingIntent.FLAG_UPDATE_CURRENT);

        }
        builder.setContentIntent(pendingIntent);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getApplicationContext());
        int NOTIFICATION_ID = 1;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED){
                managerCompat.notify(NOTIFICATION_ID,builder.build());
            }
        }else {
            managerCompat.notify(NOTIFICATION_ID,builder.build());
        }
    }

    private Bitmap getBitmapFromUrl(String image) {

        try {

            URL url = new URL(image);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            return bitmap;

        }catch (Exception e){

            return null;
        }

    }

    private void createNot(String body) {

        CharSequence name = "Balak";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,name, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(body);

            NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }

    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }
}
