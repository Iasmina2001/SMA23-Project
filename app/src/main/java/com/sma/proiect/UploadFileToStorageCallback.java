package com.sma.proiect;
import android.net.Uri;


public interface UploadFileToStorageCallback {
    void onUploaded(Uri downloadUrl);
}
