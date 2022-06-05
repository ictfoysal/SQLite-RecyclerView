package com.foysaltech.sqliterecyclerview;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class FetchData extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Model> dataholder;
    DBManager dbManager;

    private String[] storagepermission;
    private static final int STORAGE_REQUEST_CODE_EXPORT = 1;
    private static final int STORAGE_REQUEST_CODE_IMPORT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetchdata);

        dbManager = new DBManager(this);
        storagepermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        recyclerView = (RecyclerView) findViewById(R.id.recview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Cursor cursor = new DBManager(this).readalldata();
        dataholder = new ArrayList<>();

        while (cursor.moveToNext()) {
            Model obj = new Model(cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3));
            dataholder.add(obj);
        }

        CustomAdapter adapter = new CustomAdapter(dataholder);
        recyclerView.setAdapter(adapter);

    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragepermissionimport() {
        ActivityCompat.requestPermissions(this, storagepermission, STORAGE_REQUEST_CODE_IMPORT);
    }

    private void requestStoragepermissionEXPORT() {
        ActivityCompat.requestPermissions(this, storagepermission, STORAGE_REQUEST_CODE_EXPORT);
    }

    private void importCSV() {
        String filePathAndName = Environment.getExternalStorageDirectory() + "/" + "SQLiteBackup/" + "SQLite_Backup.csv";

        File csvFile = new File(filePathAndName);

        if (csvFile.exists()) {

            try {

                CSVReader csvReader = new CSVReader(new FileReader(csvFile.getAbsoluteFile()));

                String[] nextLine;
                while ((nextLine = csvReader.readNext()) != null) {
                    String name = nextLine[0];
                    String phone = nextLine[1];
                    String email = nextLine[3];

                    long timestamp = System.currentTimeMillis();
                    String id = dbManager.addrecord(
                            "" + name,
                            "" + phone,
                            "" + email);

                }
                Toast.makeText(this, "Backup Restored", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "No Backup Found", Toast.LENGTH_SHORT).show();
        }

    }

    private void exportCSV() {
        File folder = new File(Environment.getExternalStorageDirectory() + "/" + "SQLiteBackup");

        boolean isFolderCreated = false;
        if (!folder.exists()) {
            isFolderCreated = folder.mkdir();
        }

        String csvFileName = "SQLite_Backup.csv";
        String PathAndName = folder.toString() + "/" + csvFileName;

        ArrayList<Model> recordsList = new ArrayList<>();
        recordsList.clear();
        recordsList = dataholder;

        try {
            FileWriter fileWriter = new FileWriter(PathAndName);
            for (int i = 0; i < recordsList.size(); i++) {
                fileWriter.append("" + recordsList.get(i).getName());
                fileWriter.append(",");
                fileWriter.append("" + recordsList.get(i).getContact());
                fileWriter.append(",");
                fileWriter.append("" + recordsList.get(i).getEmail());
                fileWriter.append(",");
                /*fileWriter.append("" + recordsList.get(i).getCity());
                fileWriter.append(",");
                fileWriter.append("" + recordsList.get(i).getIntro());
                fileWriter.append(",");
                fileWriter.append("" + recordsList.get(i).getPhone());
                fileWriter.append(",");
                fileWriter.append("" + recordsList.get(i).getStreet());
                fileWriter.append(",");*/
            }
            fileWriter.flush();
            fileWriter.close();
            Toast.makeText(this, "backup exported to" + PathAndName, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.backupId:
                if (checkStoragePermission()) {
                    exportCSV();
                } else {
                    requestStoragepermissionEXPORT();
                }
                break;
            case R.id.RestoreId:
                if (checkStoragePermission()) {
                    importCSV();
                    onResume();
                } else {
                    requestStoragepermissionimport();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case STORAGE_REQUEST_CODE_EXPORT:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    exportCSV();
                } else {
                    Toast.makeText(this, "storage permission required", Toast.LENGTH_SHORT).show();
                }
                break;
            case STORAGE_REQUEST_CODE_IMPORT:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    importCSV();
                } else {
                    Toast.makeText(this, "storage permission required", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}