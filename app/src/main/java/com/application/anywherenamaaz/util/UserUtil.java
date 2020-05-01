package com.application.anywherenamaaz.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.application.anywherenamaaz.model.User;

import org.json.JSONException;
import org.json.JSONObject;

public class UserUtil {

    private static final String TAG = "UserUtil" ;

    //Get Name from User database if exists
    public String getNameFromDb(SQLiteDatabase myDb) {
        String name = "";
        try {
            Cursor resultSet = myDb.rawQuery("Select * from User",null);
            if(resultSet.getCount()>0) {
                resultSet.moveToFirst();
                name = resultSet.getString(0);
                resultSet.close();
                myDb.close();
            }
        } catch(Exception e) {
            Log.e(TAG,"Error in getName() - cursor error");
        }
        return name;
}

    public void sendNameAndToken(String name, String token) {
        if(name!=null && !name.isEmpty()
            && token!=null && !token.isEmpty()) {
            try {
                post(name, token);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void post(String name, String token) throws JSONException {
        AndroidNetworking.post("http://192.168.1.7:3000/user")
                .addHeaders("Content-Type","application/json")
                .addBodyParameter(new User(name,token))
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.d("Error", error.toString());
                    }
                });
    }
}
