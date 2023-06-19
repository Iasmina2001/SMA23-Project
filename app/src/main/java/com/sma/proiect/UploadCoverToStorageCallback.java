package com.sma.proiect;
import android.net.Uri;


public interface UploadCoverToStorageCallback {
    void onUploaded(Uri downloadUrl, String storageImagePath);
}
