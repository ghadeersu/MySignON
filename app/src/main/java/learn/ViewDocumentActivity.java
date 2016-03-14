package learn;

import android.support.v7.app.AppCompatActivity;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.content.Context;
import android.provider.MediaStore;
import android.database.Cursor;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URISyntaxException;
import java.net.URI;
import java.io.File;


public class ViewDocumentActivity extends AppCompatActivity {
/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_document);

        WebView mWebView = (WebView) findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        //mWebView.loadUrl("https://docs.google.com/viewer?url=" + "https://docs.google.com/file/d/0B7pKTkDz8c3gWGNRTWJidTBTVmc/edit?usp=sharing");

        //path (change)
        String path = "https://drive.google.com/drive/my-drive";//"https://docs.google.com/document/d/1nWzG1VdKfEP73BodbO8HBQQxdeqDBOqXyysqQderGE8/edit?usp=sharing";
       ////////////////////////////////////////////////////////////

        WebView urlWebView = (WebView) findViewById(R.id.webView);
        urlWebView.setWebViewClient(new AppWebViewClients());
        urlWebView.getSettings().setJavaScriptEnabled(true);
        urlWebView.getSettings().setUseWideViewPort(true);
        urlWebView.loadUrl(path);//"https://drive.google.com/viewerng/viewer?embedded=true&url=" + path);
    }

    public class AppWebViewClients extends WebViewClient {



        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);

        }
    }*/
}
 /*private void getDocument()
        {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/msword,application/pdf");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            // Only the system receives the ACTION_OPEN_DOCUMENT, so no need to test.
            startActivityForResult(intent, REQUEST_CODE_DOC);
        }


        @Override
        protected void onActivityResult(int req, int result, Intent data)
        {
            // TODO Auto-generated method stub
            super.onActivityResult(req, result, data);
            if (result == RESULT_OK)
            {
                Uri fileuri = data.getData();
                String docFilePath = getFileNameByUri(this, fileuri);
            }
        }

// get file path

        private String getFileNameByUri(Context context, Uri uri)
        {
            String filepath = "";//default fileName
            Uri filePathUri = uri;
            File file;
            if (uri.getScheme().toString().compareTo("content") == 0)
            {
                Cursor cursor = context.getContentResolver().query(uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA, MediaStore.Images.Media.ORIENTATION }, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                cursor.moveToFirst();

                String mImagePath = cursor.getString(column_index);
                cursor.close();
                filepath = mImagePath;

            }
            else
            if (uri.getScheme().compareTo("file") == 0)
            {
                try
                {
                    file = new File(new URI(uri.toString()));
                    if (file.exists())
                        filepath = file.getAbsolutePath();

                }
                catch (URISyntaxException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            else
            {
                filepath = uri.getPath();
            }
            return filepath;
        }
    }*/



