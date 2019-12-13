package com.mtools.android.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.mtools.android.R;
import com.mtools.android.activity.crack_music;

import java.io.File;
import java.util.Objects;

public class DownloadService extends Service {

    private DownloadTask downloadTask;

    private String downloadUrl;

    private String downloadName;

    private DownloadListener listener = new DownloadListener() {

        @Override
        public void onProgress(int progress) {
            getNotificationManager().notify(1, getNotification("Downloading...", progress));
        }

        @Override
        public void onSuccess() {
            downloadTask = null;
            // 下载成功时将前台服务通知关闭 并创建一个下载成功的通知
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("Download Success", -1));
            Toast.makeText(DownloadService.this, "Download Success", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailed() {
            downloadTask = null;
            // 下载失败时将前台服务通知关闭 并创建一个下载失败的通知
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("Download Failed", -1));
            Toast.makeText(DownloadService.this, "Download Failed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPaused() {
            downloadTask = null;
            Toast.makeText(DownloadService.this, "Paused", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCanceled() {
            downloadTask = null;
            stopForeground(true);
            Toast.makeText(DownloadService.this, "Failed", Toast.LENGTH_SHORT).show();
        }
    };


    private DownloadBinder mBinder = new DownloadBinder();


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    public class DownloadBinder extends Binder {

        public void startDownload(String url, String fileName) {
            if (downloadTask == null) {
                Log.d("GOOD", "startDownload: task not null");
                downloadUrl = url;
                downloadName = fileName;
                downloadTask = new DownloadTask(listener);
                downloadTask.execute(downloadUrl, fileName);
                startForeground(1, getNotification("Downloading...", 0));
                Toast.makeText(DownloadService.this, "Downloading...", Toast.LENGTH_SHORT).show();
            }
        }

        public void pauseDownload() {
            if (downloadTask != null) {
                downloadTask.pauseDownload();
            }
        }

        public void cancelDownload() {
            if (downloadTask != null) {
                Log.d("GOOD", "cancelDownload: ");
                downloadTask.cancelDownload();
            }
            if (downloadUrl != null) {
                Log.d("GOOD", "cancel: delete");
                // 取消下载时需将文件删除，并将通知关闭
//                String directory = Environment.DIRECTORY_DOWNLOADS;
//                File file = new File(directory, downloadName);
                String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                File file = new File(directory + "/" + downloadName);
                Log.d("GOOD", "cancelDownload: dir:" + directory);
                if (file.exists()) {
                    Log.d("GOOD", "cancel: file exist");
                    // delete() will return a boolean result
                    // the warning will be removed unless we catch the result.
                    boolean isDelete = file.delete();
                    if (isDelete) {
                        Log.d("GOOD", "File delete success.");
                    } else {
                        Log.d("GOOD", "File delete failed.");
                    }
                }
                getNotificationManager().cancel(1);
                stopForeground(true);
                Toast.makeText(DownloadService.this, "Canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }


    private Notification getNotification(String title, int progress) {
        Intent intent = new Intent(this, crack_music.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);

        /*
         * 从Android 8.0(API 26)起 所有 Notification 都要指定 Channel
         * 对于每一个 Channel 可以单独设置
         * 比如：通知开关、提示音、是否震动等
         * 这样每一个通知在用户面前都是透明的
         */
        String CHANNEL_ID = "1";
        String CHANNEL_NAME = "Download_Channel";
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Objects.requireNonNull(manager).createNotificationChannel(channel);

        Notification.Builder builder = new Notification.Builder(this, CHANNEL_ID);
        builder.setContentTitle(title)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setChannelId(CHANNEL_ID)
                .setContentIntent(pi);
        Notification notification = builder.build();
        startForeground(1, notification);

        if (progress >= 0) {
            // 当progress大于或等于0时才显示下载进度
            builder.setContentText(progress + "%");
            builder.setProgress(100, progress, false);
        }
        return builder.build();
    }

}
