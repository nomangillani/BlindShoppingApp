package com.example.junaidtanoli.blindshoppingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class LoginSessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "loginsession";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_PASSWORD = "name";

    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";
    public static final String USER_KEY = "userkey";

    // Constructor
    public LoginSessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String email, String Password, String userkey){
        // Storing login value as TRUE
        editor.putString(USER_KEY,userkey);
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_PASSWORD, Password);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);

        // commit changes
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, UserRegisterorlogin.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

            // Staring Login Activity
            _context.startActivity(i);
           HomeActivity.fa.finish();
        }

    }

    public String getpassword ()
    {         pref =  _context . getSharedPreferences ( PREF_NAME , PRIVATE_MODE );
        return  pref.getString ( KEY_EMAIL ,   "" );      }
    public String getemail ()
    {         pref =  _context . getSharedPreferences ( PREF_NAME , PRIVATE_MODE );
        return  pref.getString ( KEY_PASSWORD ,   "" );      }
    public String getUserKey ()
    {         pref =  _context . getSharedPreferences ( PREF_NAME , PRIVATE_MODE );
        return  pref.getString ( USER_KEY ,   "" );      }


    /**
     * Get stored session data
     * */
   /* public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        // return user
        return user;
    }*/

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, UserRegisterorlogin.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
        HomeActivity.fa.finish();
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}