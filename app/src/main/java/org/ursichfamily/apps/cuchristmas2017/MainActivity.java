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
import com.google.android.gms.tasks.Task;

import com.google.gdata.client.*;
import com.google.gdata.client.photos.*;
import com.google.gdata.data.*;
import com.google.gdata.data.media.*;
import com.google.gdata.data.photos.*;
import com.google.gdata.util.AuthenticationException;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    final int RC_SIGN_IN = 0;
    TextView words;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        words = findViewById(R.id.words);

        // Configure sign-in to request the user's ID and basic profile (included in DEFAULT_SIGN_IN).
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();

        for (com.google.android.gms.common.api.Scope scope: gso.getScopeArray()
             ) {
            Log.i("no_tag", scope.toString());
        }
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
//            Log.i("no_tag", "sign-in successful");
//            TextView tv = findViewById(R.id.words);
//            String s = "";
//            s += "DisplayName: " + account.getDisplayName();
//            s += "\nEmail: " + account.getEmail();
//            s += "\nFamilyName: " + account.getFamilyName();
//            s += "\nGivenName: " + account.getGivenName();
//            s += "\nId: " + account.getId();
//            s += "\nPhotoUrl: " + account.getPhotoUrl();
//            tv.setText(s);
//            Log.i("no_tag", s);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("no_tag", "signInResult:failed code=" + e.getStatusCode());
        }
        PicasawebService picasaSvc = new PicasawebService("Ursichfamily-CUChristmas2017-1");
        new PicasaTalker().execute(picasaSvc);


    }

    private class PicasaTalker extends AsyncTask<PicasawebService, Void, Void> {
        // Input parameters are of type xxxxx
        // We will not indicate progress, so we can specify progress units of type Void.
        // The result of the computation is of type xxxxxx
        protected Void doInBackground(PicasawebService... pws) {
            PicasawebService pws0 = pws[0];
            try {
                pws0.setUserCredentials("XXXXXX", "XXXXXX");
                Log.i("no_tag", "authentication successful!? Wow!");
            } catch (AuthenticationException e) {
                Log.i("no_tag", "AuthenticationException: I don't know what to do.");
                Log.i("no_tag", "scheme: " + e.getScheme());
                Log.i("no_tag", "realm: " + e.getRealm());
                Log.i("no_tag", "params: " + e.getParameters());
                Log.i("no_tag", "authHeader: " + e.getAuthHeader());
            } catch (RuntimeException e) {
                Log.i("no_tag", "RuntimeException: I don't know what to do.");
                Log.i("no_tag", "getName: " + e.getClass().getName());
                e.printStackTrace();
            } catch (Exception e) {
                Log.i("no_tag", "Generic Exception: I don't know what to do.");
                Log.i("no_tag", "getCause: " + e.getCause());
                Log.i("no_tag", "getMessage: " + e.getMessage());
            }
            return null;
        }
    }
}
