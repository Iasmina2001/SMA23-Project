package com.sma.proiect.reader;

import androidx.appcompat.app.AppCompatActivity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import com.github.barteksc.pdfviewer.PDFView;
import com.sma.proiect.AppState;
import com.sma.proiect.R;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;


public class PdfViewActivity extends AppCompatActivity {

    private PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view);
        pdfView = findViewById(R.id.pdfView);

        String url = getIntent().getStringExtra("url");
        Uri uri = AppState.get().getDownloadedEBookFilePathUri();
        new RetrievePDFFromUrl().execute(url);
    }

    // Create an async task class for loading pdf file from URL.
    private class RetrievePDFFromUrl extends AsyncTask<String, Void, InputStream> {
        @Override
        protected InputStream doInBackground(String... strings) {
            // we are using inputstream for getting out PDF
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                // below is the step where we are creating our connection
                HttpURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    // Response is success.
                    // We are getting input stream from url and storing it in our variable.
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            } catch (IOException e) {
                // Method to handle errors
                e.printStackTrace();
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            // After the execution of our async task we are loading our pdf in our pdf view.
            pdfView.fromStream(inputStream).load();
        }
    }
}