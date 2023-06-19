package com.sma.proiect.helpers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sma.proiect.AppState;
import com.sma.proiect.R;
import com.sma.proiect.UploadCoverToStorageCallback;
import com.sma.proiect.UploadFileToStorageCallback;
import com.sma.proiect.reader.PdfViewActivity;
import java.io.ByteArrayOutputStream;


public class StorageOperationHelper {

    public void uploadEBookFile(String ISBN10, UploadFileToStorageCallback uploadFileToStorageCallback) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("eBooks").child(ISBN10);
        Uri browsedEBookFilePathUri = AppState.get().getBrowsedEBookFilePathUri();
        if (browsedEBookFilePathUri != null) {
            storageReference.putFile(browsedEBookFilePathUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            uploadFileToStorageCallback.onUploaded(uri);
                        }
                    });
                }
            });
        }
    }

    public void uploadBookCover(String ISBN10, String storageImagePath, UploadCoverToStorageCallback uploadCoverToStorageCallback) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(storageImagePath).child(ISBN10);
        Uri browsedEBookCoverPathUri = AppState.get().getBrowsedEBookCoverPathUri();
        if (browsedEBookCoverPathUri != null) {
            storageReference.putFile(browsedEBookCoverPathUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            uploadCoverToStorageCallback.onUploaded(uri, storageImagePath);
                        }
                    });
                }
            });
        }
    }

    public void setBookCoverReader(ImageView imageView, String ISBN10) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference photoReference = storageReference.child("eBookCovers").child(ISBN10);

        final long ONE_MEGABYTE = 1024 * 1024;
        photoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if (bmp != null) {
                    imageView.setImageBitmap(bmp);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                imageView.setImageResource(R.drawable.book_cover);
            }
        });
    }

    public void deleteBookCoverFromStorage(Context context, String ISBN10, String storageImagePath) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        storageRef.child(storageImagePath).child(ISBN10).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Toast.makeText(context.getApplicationContext(), "Successfully deleted eBookCover", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(context.getApplicationContext(), "Failed to delete eBookCover", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteEBookFileFromStorage(Context context, String ISBN10) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        storageRef.child("eBooks").child(ISBN10).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Toast.makeText(context.getApplicationContext(), "Successfully deleted eBookFile", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Toast.makeText(context.getApplicationContext(), "Failed to delete eBookFile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void openEBook(Context context, String ISBN10) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        storageRef.child("eBooks").child(ISBN10).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // File found
                AppState.get().setDownloadedEBookFilePathUri(uri);
                if (uri != null) {
                    Intent intent = new Intent(context, PdfViewActivity.class);
                    intent.putExtra("url", uri.toString());
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context.getApplicationContext(), "File not found", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // File not found
                AppState.get().setDownloadedEBookCoverPathUri(null);
            }
        });
    }
}
