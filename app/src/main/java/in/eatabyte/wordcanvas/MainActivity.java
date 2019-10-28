package in.eatabyte.wordcanvas;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class MainActivity extends AppCompatActivity {

    private CanvasView canvasView;
    private ImageButton saveButton;
    private RecyclerView listRecyclerView;
    //private RecyclerView.Adapter listRecyclerAdapter;
    private RecyclerView.LayoutManager listRecyclerLayoutManager;
    private String listContents[];
    private int count = 0;
    private ArrayList<TextView> textViews;
    int current = 0;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        //Disable Strict Mode to Allow URI Exposure While Sharing File

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViews = new ArrayList<>();
        HorizontalScrollView horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);

        LinearLayout scrollViewLayout = (LinearLayout) findViewById(R.id.scrollViewLayout);
        listContents = getResources().getStringArray(R.array.InputList);
        for (int i = 0; i < listContents.length; i++) {
            TextView textView = new TextView(this);
            textView.setText(listContents[i]);
            textView.setTextSize(35);
            textView.setHeight(100);
            textView.setWidth(100);
            textView.setGravity(Gravity.CENTER);        //17

            textView.setBackground(getDrawable(R.drawable.back));
            scrollViewLayout.addView(textView);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();
            params.height = 70;
            textView.setLayoutParams(params);
            params.height = getResources().getDimensionPixelSize(R.dimen.text_view_dimen);
            params.width = getResources().getDimensionPixelSize(R.dimen.text_view_dimen);

            textViews.add(textView);

        }
        for (int current = 0; current < textViews.size(); current++) {
            setOnClick(current);
        }




        canvasView = (CanvasView) findViewById(R.id.canvasView);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        canvasView.init(metrics);

        saveButton = (ImageButton) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDrawing();
                //Toast.makeText(MainActivity.this, "Button Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        //textViews = listRecyclerAdapter.getTextViews();
        //Toast.makeText(MainActivity.this, "" + textViews.size(), Toast.LENGTH_SHORT).show();
        isWriteStoragePermissionGranted();


    }

    public void saveDrawing() {
        try {

            isWriteStoragePermissionGranted();

            canvasView.setDrawingCacheEnabled(false);
            canvasView.setDrawingCacheEnabled(true);
            Bitmap bitmap = canvasView.getDrawingCache();
            File file, f = null;//f = null might throw a NullPointerException
            if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                file = new File(android.os.Environment.getExternalStorageDirectory(), "Dataset Canvas");
                if (!file.exists()) {
                    file.mkdirs();

                }
                f = new File(file.getAbsolutePath() + file.separator + count + ".png");
            }
            FileOutputStream ostream = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 10, ostream);
            Toast.makeText(MainActivity.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
            textViews.get(count).setTextColor(ContextCompat.getColor(MainActivity.this, R.color.scrollview_item_saved));

            ostream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void sendAppFolderToEmail(String emailId) {
        zipFileAtPath(android.os.Environment.getExternalStorageDirectory() + "/Dataset Canvas", android.os.Environment.getExternalStorageDirectory() + "/DatasetChunk.zip");
        Toast.makeText(MainActivity.this, "Compressed Folder", Toast.LENGTH_SHORT).show();
        //URI exposed to Email Intent here
        Uri URI = Uri.fromFile(new File(android.os.Environment.getExternalStorageDirectory() + "/DatasetChunk.zip"));
        try {

            final Intent emailIntent = new Intent(
                    Intent.ACTION_SEND);
            emailIntent.setType("text/plain");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                    new String[]{emailId});
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                    "Dataset Entry");
            if (URI != null) {
                //emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                emailIntent.putExtra(Intent.EXTRA_STREAM, URI);
            }
            emailIntent
                    .putExtra(android.content.Intent.EXTRA_TEXT, "Find the File Attached");

            this.startActivity(Intent.createChooser(emailIntent,
                    "Send email..."));

        } catch (Throwable t) {
            Toast.makeText(this,
                    "Request failed try again: " + t.toString(),
                    Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.normal:
                canvasView.normal();
                return true;
            /*case R.id.emboss:
                canvasView.emboss();
                return true;*/
            /*case R.id.blur:
                canvasView.blur();
                return true;*/
            case R.id.clear:
                canvasView.clear();
                return true;
            case R.id.send:
                sendAppFolderToEmail(getResources().getString(R.string.developer_email));//send code function
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void setOnClick(int i) {
        final TextView t = textViews.get(i);

        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count = textViews.indexOf(t);
                //Toast.makeText(MainActivity.this, "Count = " + count, Toast.LENGTH_SHORT).show();
                canvasView.clear();
                if (t.getCurrentTextColor() != ContextCompat.getColor(MainActivity.this, R.color.scrollview_item_saved) && t.getCurrentTextColor() != ContextCompat.getColor(MainActivity.this, R.color.scrollview_item_saved_selected)) {
                    t.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.scrollview_item_selected));
                }else{
                    t.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.scrollview_item_saved_selected));
                }
                for (int i = 0; i < textViews.size(); i++) {
                    if (i != count && textViews.get(i).getCurrentTextColor() != ContextCompat.getColor(MainActivity.this, R.color.scrollview_item_saved)) {
                        if(textViews.get(i).getCurrentTextColor() != ContextCompat.getColor(MainActivity.this, R.color.scrollview_item_saved_selected)) {
                            textViews.get(i).setTextColor(ContextCompat.getColor(MainActivity.this, R.color.scrollview_item_default));
                        }else {
                            textViews.get(i).setTextColor(ContextCompat.getColor(MainActivity.this, R.color.scrollview_item_saved));
                        }
                    }
                }
            }
        });

    }

    public boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    public String getLastPathComponent(String filePath) {
        String[] segments = filePath.split("/");
        if (segments.length == 0)
            return "";
        String lastPathComponent = segments[segments.length - 1];
        return lastPathComponent;
    }

    private void zipSubFolder(ZipOutputStream out, File folder,
                              int basePathLength) throws IOException {

        final int BUFFER = 2048;

        File[] fileList = folder.listFiles();
        BufferedInputStream origin = null;
        for (File file : fileList) {
            if (file.isDirectory()) {
                zipSubFolder(out, file, basePathLength);
            } else {
                byte data[] = new byte[BUFFER];
                String unmodifiedFilePath = file.getPath();
                String relativePath = unmodifiedFilePath
                        .substring(basePathLength);
                FileInputStream fi = new FileInputStream(unmodifiedFilePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(relativePath);
                entry.setTime(file.lastModified()); // to keep modification time after unzipping
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
        }
    }

    public boolean zipFileAtPath(String sourcePath, String toLocation) {
        final int BUFFER = 2048;

        File sourceFile = new File(sourcePath);
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(toLocation);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            if (sourceFile.isDirectory()) {
                zipSubFolder(out, sourceFile, sourceFile.getParent().length());
            } else {
                byte data[] = new byte[BUFFER];
                FileInputStream fi = new FileInputStream(sourcePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(getLastPathComponent(sourcePath));
                entry.setTime(sourceFile.lastModified()); // to keep modification time after unzipping
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


}