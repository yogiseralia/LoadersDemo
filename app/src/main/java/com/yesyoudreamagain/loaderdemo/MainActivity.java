package com.yesyoudreamagain.loaderdemo;

import android.Manifest;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Created by Yogesh Seralia on 6/16/2019.
 */
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int RC_READ_CONTACT = 212;
    private static final int ID_READ_CONTACTS_LOADER = 21;
    private static final String TAG = "MainActivity";
    private String readContactsPermission;
    private String[] mColumnProjection = {ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts._ID};
    private TextView context_text;
    private boolean isFirstTimeLoaded;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context_text = findViewById(R.id.content_text);
//        Button button = findViewById(R.id.button);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readContacts();
            }
        });
        readContactsPermission = Manifest.permission.READ_CONTACTS;

        if (PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, readContactsPermission)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, readContactsPermission)) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Need Permission!")
                        .setMessage("Permission to access the microphone is required for this app to read contacts.")
                        .setTitle("Permission required")
                        .setPositiveButton(
                                "OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestPermission();
                                    }
                                })
                        .show();
            } else {
                requestPermission();
            }
        } else {
            readContacts();
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{readContactsPermission}, RC_READ_CONTACT);
    }

    private void readContacts() {
        if (!isFirstTimeLoaded) {
            getSupportLoaderManager().initLoader(ID_READ_CONTACTS_LOADER, null, this);
            isFirstTimeLoaded = true;
        } else {
            getSupportLoaderManager().restartLoader(ID_READ_CONTACTS_LOADER, null, this);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int i = 0; i < permissions.length; i++) {
            if (permissions[i].equals(readContactsPermission) && grantResults[i] == PERMISSION_GRANTED) {
                readContacts();
                break;
            }
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle bundle) {
        if (id == ID_READ_CONTACTS_LOADER) {
            return new CursorLoader(this, ContactsContract.Contacts.CONTENT_URI, mColumnProjection, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            StringBuilder stringBuilder = new StringBuilder();
            while (!cursor.isAfterLast()) {
                stringBuilder.append(cursor.getString(0)).append(" ").append(cursor.getString(1))
                        .append("\n");
                Log.d(TAG, "[" + cursor.getString(0) + "]");
                cursor.moveToNext();
            }
            context_text.setText(stringBuilder.toString());
        } else {
            Toast.makeText(this, "No Contacts on device", Toast.LENGTH_SHORT).show();
            context_text.setText("No Contacts on device");
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset() called with: loader = [" + loader + "]");
    }
}
