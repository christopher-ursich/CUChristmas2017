package org.ursichfamily.apps.cuchristmas2017;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
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
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final int PERM_REQUEST_ID_GET_ACCOUNTS = 0;
    private final int RC_SIGN_IN = 1;
    GoogleSignInClient mGoogleSignInClient;

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
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    // The GoogleSignInAccount object contains information about the signed-in user, such as the user's name.
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            Log.i("no_tag", "sign-in successful");
            TextView tv = findViewById(R.id.words);
            String s = "";
            s += "DisplayName: " + account.getDisplayName();
            s += "\nEmail: " + account.getEmail();
            s += "\nFamilyName: " + account.getFamilyName();
            s += "\nGivenName: " + account.getGivenName();
            s += "\nId: " + account.getId();
            s += "\nPhotoUrl: " + account.getPhotoUrl();
            tv.setText(s);
            Log.i("no_tag", s);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("no_tag", "signInResult:failed code=" + e.getStatusCode());
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
