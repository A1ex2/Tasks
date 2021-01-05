package ua.org.algoritm.tasks;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.io.File;


public class DownLoadManagerIntentService extends IntentService {

    private String downloadUrl = Environment.getExternalStorageDirectory()+"/Download/";
    private final String fileName = "vayTest.apk";


    public DownLoadManagerIntentService() {
        super("DownLoadManagerIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent,startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        downloadUrl = intent.getStringExtra("downloadurl");
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        download(this,fileName,downloadUrl);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }


    private void download(Context context, String name, String apkUrl) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
        String apkDir = isFolderExist("Download");

        File f = new File(name);
        if (f.exists())
            f.delete();

        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(apkUrl));
        request.setDestinationInExternalPublicDir("download", name + ".apk");
        request.setTitle("App download");
        request.setDescription("\"" + name + "\"now is downloading");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        request.setDestinationInExternalFilesDir( this , Environment.DIRECTORY_DOWNLOADS ,  "vayTest.apk" );
//        request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, "vayTest.apk");
//        request.setDestinationUri(Uri.parse(Environment.getExternalStorageDirectory()+"/Download/vayTest.apk"));
//        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "vayTest.apk");


        try {
            long downloadId = downloadManager.enqueue(request);
        } catch (IllegalArgumentException e) {
            //open by brower
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(apkUrl);
            intent.setData(content_url);
            context.startActivity(intent);
        }
    }

    private String isFolderExist(String dir) {
        File folder = Environment.getExternalStoragePublicDirectory(dir);
        boolean rs = (folder.exists() && folder.isDirectory()) ? true : folder
                .mkdirs();
        return folder.getAbsolutePath();
    }
}
