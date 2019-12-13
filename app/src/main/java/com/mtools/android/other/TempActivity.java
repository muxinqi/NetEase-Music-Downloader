//package com.mtools.android;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import android.Manifest;
//import android.content.ComponentName;
//import android.content.Intent;
//import android.content.ServiceConnection;
//import android.content.pm.PackageManager;
//import android.os.Bundle;
//import android.os.IBinder;
//import android.view.GestureDetector;
//import android.view.View;
//import android.widget.Button;
//import android.widget.Toast;
//
//import com.mtools.android.service.DownloadService;
//
//import org.jetbrains.annotations.NotNull;
//
//public class TempActivity extends AppCompatActivity implements View.OnClickListener {
//
//    private DownloadService.DownloadBinder downloadBinder;
//
//    private ServiceConnection connection = new ServiceConnection() {
//
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            // 获取实例
//            downloadBinder = (DownloadService.DownloadBinder)service;
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//        }
//    };
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_temp);
//
//        // 组件初始化
//        Button startDownload = findViewById(R.id.start_download_button);
//        Button pauseDownload = findViewById(R.id.pause_download_button);
//        Button cancelDownload = findViewById(R.id.cancel_download_button);
//
//        // 监听点击操作
//        startDownload.setOnClickListener(this);
//        pauseDownload.setOnClickListener(this);
//        cancelDownload.setOnClickListener(this);
//
//        // 启动服务
//        Intent intent = new Intent(this, DownloadService.class);
//        startService(intent);
//
//        // 绑定服务
//        bindService(intent, connection, BIND_AUTO_CREATE);
//        if (ContextCompat.checkSelfPermission(TempActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(TempActivity.this, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1);
//        }
//    }
//
//    @Override
//    public void onClick(View v) {
//        if (downloadBinder == null) {
//            return;
//        }
//        switch (v.getId()) {
//            case R.id.start_download_button: {
//                String url = "https://domain.ltd/1G.bin";
//                downloadBinder.startDownload(url,"1G.bin");
//                break;
//            }
//            case R.id.pause_download_button: {
//                downloadBinder.pauseDownload();
//                break;
//            }
//            case R.id.cancel_download_button: {
//                downloadBinder.cancelDownload();
//                break;
//            }
//            default:
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
//        if (requestCode == 1) {
//            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "拒绝权限将无法使用程序", Toast.LENGTH_SHORT).show();
//                finish();
//            }
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        unbindService(connection);
//    }
//}
