package com.abdul.scanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<String> myList;
    private ListView listView;
    private Button startButton;
    private Button stopButton;
    private TextView avgFileSize;
    private ProgressBar progressBar;
    private MySimpleArrayAdapter adapter;
    private static boolean actionButtonVisisble = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = (Button)findViewById(R.id.button);
        stopButton = (Button)findViewById(R.id.button2);
        avgFileSize = (TextView) findViewById(R.id.avgTextSize);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);



        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startButton.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                Intent intent = new Intent(getApplicationContext(), ScannerService.class);
                startService(intent);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startButton.setEnabled(true);
                progressBar.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(getApplicationContext(), ScannerService.class);
                stopService(intent);
                FileScanner.clear();
            }
        });

       listView = (ListView)findViewById(R.id.listView);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        startButton.setEnabled(true);
        progressBar.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(getApplicationContext(), ScannerService.class);
        stopService(intent);
        FileScanner.clear();
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(ScannerService.SCANNING_COMPLETED_ACTION));
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        if(actionButtonVisisble){
            menu.findItem(R.id.action_share).setVisible(actionButtonVisisble);
        }else{
            menu.findItem(R.id.action_share).setVisible(actionButtonVisisble);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.action_share:
                Toast.makeText(this,"Share", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                int resultCode = bundle.getInt("Result");
                if (resultCode == RESULT_OK) {
                    Toast.makeText(MainActivity.this,
                            "Scanning complete." + FileScanner.getAllFiles().size(),
                            Toast.LENGTH_LONG).show();
                    actionButtonVisisble = true;
                    invalidateOptionsMenu();
                    adapter = new MySimpleArrayAdapter(getApplicationContext(),FileScanner.getTopTenList());
                    listView.setAdapter(adapter);
                    avgFileSize.setText("Avg File Size: " +FileScanner.getAverageFileSize());


                } else {
                    Toast.makeText(MainActivity.this, "Scanning failed",
                            Toast.LENGTH_LONG).show();
                }
            }
            progressBar.setVisibility(View.INVISIBLE);
            startButton.setEnabled(true);
            FileScanner.clear();
        }
    };

    public class MySimpleArrayAdapter extends ArrayAdapter<FileScanner> {
        private final Context context;
        private final ArrayList<FileScanner> fileList;
        private TextView fileName;
        private TextView fileSize;

        public MySimpleArrayAdapter(Context context, ArrayList<FileScanner> myFiles) {
            super(context, -1, myFiles);
            this.context = context;
            this.fileList = myFiles;
        }

        @Override
        public int getCount() {
            return fileList.size();
        }

        @Override
        public FileScanner getItem(int position) {
            return fileList.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.row_layout, parent, false);

                fileName = (TextView)convertView.findViewById(R.id.fileName);
                fileSize = (TextView)convertView.findViewById(R.id.fileSize);
            }

            fileName.setText(fileList.get(position).getFileName());
            fileSize.setText("Size: "+fileList.get(position).getFileSize()/1024+" (MB)");

            return convertView;
        }
    }


}
