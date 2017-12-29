package org.ursichfamily.apps.cuchristmas2017;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
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
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;

import com.google.gdata.client.*;
import com.google.gdata.client.photos.*;
import com.google.gdata.data.*;
import com.google.gdata.data.media.*;
import com.google.gdata.data.photos.*;
import com.google.gdata.util.AuthenticationException;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    final int RC_SIGN_IN = 0;
    final String PICASA_OPENID_SCOPE = "https://picasaweb.google.com/data/";
    TextView words;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        words = findViewById(R.id.words);

        Scope picasaScope = new Scope(PICASA_OPENID_SCOPE);
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(picasaScope)
                .build();
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
            // The Task returned from this call is always completed, no need to attach a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    // The GoogleSignInAccount object contains information about the signed-in user, such as the user's name.
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

//            // Signed in successfully, show authenticated UI.
//            Log.i(TAG, "sign-in successful");
//            TextView tv = findViewById(R.id.words);
//            String s = "";
//            s += "DisplayName: " + account.getDisplayName();
//            s += "\nEmail: " + account.getEmail();
//            s += "\nFamilyName: " + account.getFamilyName();
//            s += "\nGivenName: " + account.getGivenName();
//            s += "\nId: " + account.getId();
//            s += "\nPhotoUrl: " + account.getPhotoUrl();
//            tv.setText(s);
//            Log.i(TAG, s);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
        PicasawebService picasaSvc = new PicasawebService("Ursichfamily-CUChristmas2017-1");
        new PicasaTalker().execute(picasaSvc);


    }

    private static class PicasaTalker extends AsyncTask<PicasawebService, Void, Void> {
        // We will not indicate progress, so we can specify progress units of type Void.
        // The result of the computation is of type xxxxxx
        protected Void doInBackground(PicasawebService... pws) {
            Log.i(TAG,"doing In Background....");
            PicasawebService pws0 = pws[0];
            try {
                URL albumFeedURL = new URL("https://picasaweb.google.com/data/feed/api/user/USERNAME?kind=album");
                UserFeed myUserFeed = pws0.getFeed(albumFeedURL, UserFeed.class);
                Log.i(TAG, "myUserFeed.getTitle: " + myUserFeed.getTitle());
                //pws0.setUserCredentials("XXXXXXX", "XXXXXXX");
                for (AlbumEntry myAlbum : myUserFeed.getAlbumEntries()) {
                    Log.i(TAG, myAlbum.getTitle().getPlainText());
                }
                Log.i(TAG, "try'ed without exception....");
            } catch (AuthenticationException e) {
                Log.i(TAG, "AuthenticationException: I don't know what to do.");
                Log.i(TAG, "scheme: " + e.getScheme());
                Log.i(TAG, "realm: " + e.getRealm());
                Log.i(TAG, "params: " + e.getParameters());
                Log.i(TAG, "authHeader: " + e.getAuthHeader());
            } catch (RuntimeException e) {
                Log.i(TAG, "RuntimeException: I don't know what to do.");
                Log.i(TAG, "getName: " + e.getClass().getName());
                e.printStackTrace();
            } catch (Exception e) {
                Log.i(TAG, "Generic Exception: I don't know what to do.");
                Log.i(TAG, "getCause: " + e.getCause());
                Log.i(TAG, "getMessage: " + e.getMessage());
            }
            return null;
        }
    }
}