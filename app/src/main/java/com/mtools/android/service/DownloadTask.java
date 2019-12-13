package com.mtools.android.service;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * String - 执行AsyncTask时需要传入一个字符串参数给后台任务
 * Int    - 表示用整型数据作为进度显示单位
 * Int    - 表示用整型数据来反馈执行结果
 */
public class DownloadTask extends AsyncTask<String, Integer, Integer> {

    // 下载状态
    private static final int TYPE_SUCCESS = 0;
    private static final int TYPE_FAILED = 1;
    private static final int TYPE_PAUSED = 2;
    private static final int TYPE_CANCELED = 3;

    private DownloadListener listener;

    private boolean isCanceled = false;

    private boolean isPaused = false;

    private int lastProgress;

    DownloadTask(DownloadListener listener) {
        this.listener = listener;
    }


    /**
     * 在后台执行具体的下载逻辑
     */
    @Override
    protected Integer doInBackground(String... params) {
        InputStream is = null;
        RandomAccessFile savedFile = null;
        File file = null;
        try {
            /*
             * 获取下载的URL地址 根据URL解析出下载的文件名
             * 将文件下载到SD卡的Download目录
             * 注：Android高版本需在Manifest中添加requestLegacyExternalStorage获取权限
             */
            long downloadedLength = 0; // 记录已下载的文件长度
            String downloadUrl = params[0];
//            String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
            String fileName = params[1];
            String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            file = new File(directory + "/" + fileName);

            // 若文件存在 则读取已下载字节数
            if (file.exists()) {
                downloadedLength = file.length();
            }

            // 获取文件总长度 并进行判断
            long contentLength = getContentLength(downloadUrl);
            if (contentLength == 0) {
                return TYPE_FAILED;
            } else if (contentLength == downloadedLength) {
                // 已下载字节和文件总字节相等，说明下载完成
                return TYPE_SUCCESS;
            }

            // 断点下载 指定从哪个字节开始下载
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .addHeader("RANGE", "bytes=" + downloadedLength + "-")
                    .url(downloadUrl)
                    .build();
            Response response = client.newCall(request).execute();
            if (response != null) {
                is = Objects.requireNonNull(response.body()).byteStream();
                savedFile = new RandomAccessFile(file, "rw");
                savedFile.seek(downloadedLength); // 跳过已下载的字节
                byte[] b = new byte[1024];
                int total = 0;
                int len;
                // 不断写入本地 并判断是否触发暂停或取消操作
                while ((len = is.read(b)) != -1) {
                    if (isCanceled) {
                        return TYPE_CANCELED;
                    } else if (isPaused) {
                        return TYPE_PAUSED;
                    } else {
                        total += len;
                        savedFile.write(b, 0, len);
                        // 计算已下载的百分比
                        int progress = (int) ((total + downloadedLength) * 100 / contentLength);
                        // 更新百分比进度
                        publishProgress(progress);
                    }
                }
                Objects.requireNonNull(response.body()).close();
                // 如中途无异常 表示下载成功
                return TYPE_SUCCESS;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (savedFile != null) {
                    savedFile.close();
                }
                if (isCanceled && file != null) {
                    boolean isDelete = file.delete();
                    if (isDelete) {
                        Log.d("GOOD", "File delete success");
                    } else {
                        Log.d("GOOD", "File delete failed");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return TYPE_FAILED;
    }


    /**
     * 在界面上更新当前的下载进度
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];
        // 若进度有变化 则更新进度显示
        if (progress > lastProgress) {
            listener.onProgress(progress);
            lastProgress = progress;
        }
    }


    /**
     * 通知最终的下载结果
     */
    @Override
    protected void onPostExecute(Integer status) {
        // 根据参数中传入的下载状态来进行回调
        switch (status) {
            case TYPE_SUCCESS: {
                listener.onSuccess();
                break;
            }
            case TYPE_FAILED: {
                listener.onFailed();
                break;
            }
            case TYPE_PAUSED: {
                listener.onPaused();
                break;
            }
            case TYPE_CANCELED: {
                listener.onCanceled();
                break;
            }
            default:
                break;
        }
    }


    void pauseDownload() {
        isPaused = true;
    }


    void cancelDownload() {
        isCanceled = true;
    }

    /**
     * 根据下载的URL地址获取文件总长度
     */
    private long getContentLength(String downloadUrl) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(downloadUrl)
                .build();
        Response response = client.newCall(request).execute();
        if (response != null && response.isSuccessful()) {
            long contentLength = Objects.requireNonNull(response.body()).contentLength();
            Objects.requireNonNull(response.body()).close();
            return contentLength;
        }
        return 0;
    }
}
