Trialy.io Android SDK
============

![Logo](https://www.trialy.io/img/ic_launcher.png)

Trialy makes it super easy to create a free trial for your in-app-purchases. Trialy takes care of the heavy lifting for you (using server-side checks with customizable grace periods to allow offline use, for example) so you can focus on developing an amazing app.

 * Add the library to your app using gradle
 * Grab a free API key from [here][0]

For documentation and additional information see [the website][0].


Download
--------

```groovy
dependencies {
  compile 'io.trialy.library:trialy:1.0.9'
}
```

Usage
--------------------

Initialize the library in your main activity's `onCreate()` method:

```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize the library and check the current trial status on every launch
        Trialy mTrialy = new Trialy(mContext, "YOUR_TRIALY_APP_KEY");
        mTrialy.checkTrial("YOUR_TRIAL_SKU", mTrialyCallback);
    }
```

Add a callback handler:

```java
   private TrialyCallback mTrialyCallback = new TrialyCallback() {
        @Override
        public void onResult(int status, long timeRemaining, String sku) {
            switch (status){
                case STATUS_TRIAL_JUST_STARTED:
                    //The trial has just started - enable the premium features for the user
                     break;
                case STATUS_TRIAL_RUNNING:
                    //The trial is currently running - enable the premium features for the user
                    break;
                case STATUS_TRIAL_JUST_ENDED:
                    //The trial has just ended - block access to the premium features
                    break;
                case STATUS_TRIAL_NOT_YET_STARTED:
                    //The user hasn't requested a trial yet - no need to do anything
                    break;
                case STATUS_TRIAL_OVER:
                    //The trial is over
                    break;
                default:
                    Log.e(TAG, "Trialy response: " + Trialy.getStatusMessage(status));
                    break;
            }
        }

    };
```

To start a trial, call `mTrialy.startTrial("YOUR_TRIAL_SKU", mTrialyCallback);`
Your app key and trial SKU can be found in your Trialy developer dashboard.

If you're using a "per Google account" trial, also add the `GET_ACCOUNTS` permission to your `AndroidManifest.xml` (and request it within your app on Android 6.0+):

```java
    <uses-permission android:name="android.permission.GET_ACCOUNTS" />
```


 [0]: https://www.trialy.io