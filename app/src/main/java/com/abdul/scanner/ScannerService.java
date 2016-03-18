package com.abdul.scanner;

import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.io.File;

public class ScannerService extends IntentService {

    private final int NOTIFICATION_ID = 12345;
    private int result = Activity.RESULT_CANCELED;
    public static String SCANNING_COMPLETED_ACTION = "com.abdul.scanner.COMPLETED";

    public ScannerService(){
        super("ScannerService");
    }

    @Override
    public void onDestroy() {
        cancelNotification();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(rootPath);
        showNotification();
        getFiles(file);
        result = Activity.RESULT_OK;
        publishResult();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void getFiles(File dir){

        File list[] = dir.listFiles();

        for(int i=0;i < list.length; i++){
            if(!list[i].isDirectory()){
                // myList.add(list[i].getName()+"******** Size: " +list[i].length()/1024);
                FileScanner.add(new FileScanner(list[i].getName(),list[i].length()/1024,"format"));
            }else{
                getFiles(list[i]);
            }
        }
    }

    public void showNotification(){
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.sym_def_app_icon)
                        .setContentTitle("Scannig.......");

        Intent targetIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(NOTIFICATION_ID, builder.build());
    }

    public void cancelNotification(){
        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.cancel(NOTIFICATION_ID);

    }

    private void publishResult() {
        Intent intent = new Intent(SCANNING_COMPLETED_ACTION);
        intent.putExtra("Result", result);
        sendBroadcast(intent);
    }
}
