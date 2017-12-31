package org.ursichfamily.apps.cuchristmas2017;

import android.content.Intent;
import android.net.Uri;
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
import com.google.common.base.Charsets;

import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String albumMapJSONtext = BuildConfig.albumMapJSON;
    private static final String TAG = "MainActivity";
    final int RC_SIGN_IN = 0;
    TextView words;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.sign_in_button).setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void openAlbumInBrowser(String albumURL) {
        Uri webpage = Uri.parse(albumURL);
        Intent openInBrowserIntent = new Intent(Intent.ACTION_VIEW, webpage);
        if (openInBrowserIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(openInBrowserIntent);
        }
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
            String googleEmail = account.getEmail();
            Log.i(TAG, "googleEmail: " + googleEmail);

//            String googleEmailHash = sha256hash(googleEmail);
//            Log.i(TAG, "googleEmailHash: " + googleEmailHash);
            String googleEmailHashN = sha256hash(googleEmail + "\n");
            Log.i(TAG, "googleEmailHashN: " + googleEmailHashN);

            // Load album map
            JSONArray reader = new JSONArray(albumMapJSONtext);

            String albumURL = "";
            for (int i = 0; i < reader.length(); i++) {
                //Log.i(TAG, "working on id:" + reader.getJSONObject(i).getString("id"));
                if (reader.getJSONObject(i).getString("id").equals(googleEmailHashN)) {
                    albumURL = reader.getJSONObject(i).getString("albumURL");
                    Log.i(TAG, "Found!: " + reader.getJSONObject(i).getString("id") + "has albumURL: " + albumURL);
                    break;  // early exit once we've found the match
                }
            }

            if (!albumURL.equals("")) {
                openAlbumInBrowser(albumURL);
            }

            // Signed in successfully, show authenticated UI.
//            Log.i(TAG, "sign-in successful");
            TextView tv = findViewById(R.id.words);
            String s = "account info: ";
//            s += "DisplayName: " + account.getDisplayName();
            s += "\nEmail: " + account.getEmail();
//            s += "\nFamilyName: " + account.getFamilyName();
//            s += "\nGivenName: " + account.getGivenName();
//            s += "\nId: " + account.getId();
//            s += "\nPhotoUrl: " + account.getPhotoUrl();
            tv.setText(s);
            Log.i(TAG, s);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String sha256hash(String s) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(s.getBytes(Charsets.US_ASCII));
            return hexStringOfByteArray(hashedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String hexStringOfByteArray(byte[] byteArray) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < byteArray.length; i++) {
            stringBuilder.append(Integer.toString((byteArray[i] & 0xff) + 0x100, 16).substring(1));
        }
        return stringBuilder.toString();
    }
}