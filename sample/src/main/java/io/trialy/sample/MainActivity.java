package io.trialy.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

import io.trialy.library.Trialy;
import io.trialy.library.TrialyCallback;
import static io.trialy.library.Constants.*;


public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";
    private Context mContext = this;

    private final static String TRIALY_APP_KEY = "N1PG28SX7DMMTIXHJ6G"; //TODO: Replace with your app key, which can be found on your Trialy developer dashboard
    private final static String TRIALY_SKU = "default"; //TODO: Replace with a trial SKU, which can be found on your Trialy developer dashboard. Each app can have multiple trials

    //An instance of the library
    Trialy mTrialy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);

        //Initialize the library and check the current trial status on every launch
        mTrialy = new Trialy(mContext, TRIALY_APP_KEY);
        mTrialy.checkTrial(TRIALY_SKU, mTrialyCallback);

        //A button to start the trial
        Button btnStartTrial = (Button)findViewById(R.id.btnStartTrial);
        btnStartTrial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTrialy.startTrial(TRIALY_SKU, mTrialyCallback);
            }
        });

        //An optional button to check the trial status (just for demo purposes)
        Button btnCheckTrialStatus = (Button)findViewById(R.id.btnCheckTrialStatus);
        btnCheckTrialStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTrialy.checkTrial(TRIALY_SKU, mTrialyCallback);
            }
        });

        Button btnBuyIap = (Button)findViewById(R.id.btnBuyIap);
        btnBuyIap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //When the user purchases the IAP, record the conversion from trial
                //to paid user (optional, just for analytics purposes)
                mTrialy.recordConversion(TRIALY_SKU, mTrialyCallback);
            }
        });
    }


    private TrialyCallback mTrialyCallback = new TrialyCallback() {
        @Override
        public void onResult(int status, long timeRemaining, String sku) {
            Log.i(TAG, "onResult status: " + status + "; time remaining: " + timeRemaining);

            switch (status){
                case STATUS_TRIAL_JUST_STARTED:
                    //The trial has just started - enable the premium features for the user
                    //TODO: Activate the premium features for the user (depends on your app)
                    activatePremiumFeatures();
                    //Disable the "Start Trial" button
                    disableStartTrialButton();
                    //Update the "Time remaining"-label
                    updateTimeRemainingLabel(timeRemaining);
                    //Optional: Show an informational dialog
                    int daysRemaining = Math.round(timeRemaining / (60 * 60 * 24));
                    showDialog("Trial started", String.format(Locale.ENGLISH, "You can now try the premium features for %d days",  daysRemaining), "OK");
                    break;
                case STATUS_TRIAL_RUNNING:
                    //The trial is currently running
                    //TODO: Enable the premium features for the user (depends on your app)
                    activatePremiumFeatures();
                    //Update the "Time remaining"-label
                    updateTimeRemainingLabel(timeRemaining);
                    //Disable the "Start Trial" button
                    disableStartTrialButton();
                    break;
                case STATUS_TRIAL_JUST_ENDED:
                    //The trial has just ended - block access to the premium features (if the user hasn't paid for them in the meantime)
                    //TODO: Deactivate the premium features for the user (depends on your app)
                    deactivatePremiumFeatures();
                    //Disable the "Start Trial" button
                    disableStartTrialButton();
                    //Hide the "Time remaining"-label
                    updateTimeRemainingLabel(-1);
                    break;
                case STATUS_TRIAL_NOT_YET_STARTED:
                    //The user hasn't requested a trial yet - no need to do anything
                    break;
                case STATUS_TRIAL_OVER:
                    //The trial is over - disable the "Start trial"-button
                    disableStartTrialButton();
                    break;
                default:
                    Log.e(TAG, "Trialy response: " + Trialy.getStatusMessage(status));
                    break;
            }

            Snackbar.make(findViewById(android.R.id.content), "onCheckResult: " + Trialy.getStatusMessage(status), Snackbar.LENGTH_LONG)
                    .setAction("OK", null).show();
        }

    };

    private void activatePremiumFeatures() {
        Button btnUseRocketLauncher = (Button)findViewById(R.id.btnUseRocketLauncher);
        btnUseRocketLauncher.setEnabled(true);
    }

    private void deactivatePremiumFeatures() {
        Button btnUseRocketLauncher = (Button)findViewById(R.id.btnUseRocketLauncher);
        btnUseRocketLauncher.setEnabled(false);
    }

    private void updateTimeRemainingLabel(long timeRemaining){
        if(timeRemaining == -1){
            //Hide the llTimeRemaining-LinearLayout
            LinearLayout llTimeRemaining = (LinearLayout)findViewById(R.id.llTimeRemaining);
            llTimeRemaining.setVisibility(View.GONE);
            return;
        }
        //Convert the "timeRemaining"-value (in seconds) to days
        int daysRemaining = (int) timeRemaining / (60 * 60 * 24);
        //Update the tvTimeRemaining-TextView
        TextView tvTimeRemaining = (TextView)findViewById(R.id.tvTimeRemaining);
        tvTimeRemaining.setText(String.format(Locale.ENGLISH, "Your trial ends in %d days",  daysRemaining));
        //Show the llTimeRemaining-LinearLayout
        LinearLayout llTimeRemaining = (LinearLayout)findViewById(R.id.llTimeRemaining);
        llTimeRemaining.setVisibility(View.VISIBLE);
    }

    private void disableStartTrialButton(){
        Button btnStartTrial = (Button)findViewById(R.id.btnStartTrial);
        btnStartTrial.setEnabled(false);
    }

    private void showDialog(String title, String message, String buttonLabel){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(message)
                .setTitle(title)
                .setPositiveButton(buttonLabel, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_clear_cache) {
            mTrialy.clearLocalCache();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
