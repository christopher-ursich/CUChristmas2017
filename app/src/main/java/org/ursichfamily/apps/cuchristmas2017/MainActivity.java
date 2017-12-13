package org.ursichfamily.apps.cuchristmas2017;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class MainActivity extends AppCompatActivity {

    private final int PERM_REQUEST_ID_GET_ACCOUNTS = 24601;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.sign_in_button).setOnClickListener(this);

        int permCheck_getAccounts = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
        if (permCheck_getAccounts != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.GET_ACCOUNTS}, PERM_REQUEST_ID_GET_ACCOUNTS);
        }

        TextView tv = findViewById(R.id.words);
        // Taken from https://developer.android.com/training/id-auth/identify.html
        AccountManager am = AccountManager.get(this);
        //Account[] accounts = am.getAccountsByType("com.google");
        Account[] accounts = am.getAccounts();
        String s = "num accounts: " + accounts.length;
        for (int i = 0; i < accounts.length; i++) {
            s += "\n" + i
                    + " type:" + accounts[i].type
                    + " creator: " + accounts[i].CREATOR
                    + " name:" + accounts[i].name
                    ;
        }
        //String s = "granted. num of accts = " + accounts.length + "\naccounts[0].name = " + accounts[0].name;
        tv.setText(s);

        // Configure sign-in to request the user's ID and basic profile (included in DEFAULT_SIGN_IN).
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            // ...
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERM_REQUEST_ID_GET_ACCOUNTS: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        // Taken from https://developer.android.com/training/id-auth/identify.html
                        AccountManager am = AccountManager.get(this);
                        //Account[] accounts = am.getAccountsByType("com.google");
                        Account[] accounts = am.getAccounts();
                        String s = "granted. num of accts = " + accounts.length;
                        TextView tv = findViewById(R.id.words);
                        tv.setText(s);
                    }
                }
            }
        }
    }
}
