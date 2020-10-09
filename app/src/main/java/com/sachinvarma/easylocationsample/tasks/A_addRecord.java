package com.sachinvarma.easylocationsample.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.sachinvarma.easylocationsample.tools.HTTPHandler;

import org.json.JSONException;
import org.json.JSONObject;

import static com.sachinvarma.easylocationsample.tools.MyData.myUrl;

public class A_addRecord extends AsyncTask<Void, Void, Void> {
    public Context context;
    public String recordType;
    public Integer route_id;
    public Integer stop_id;
    public Integer transportTypeId;
    public String name;
    public String stop_coordX;
    public String stop_coordY;
    public String tempInfoText;

    String backendURL;
    String infoText;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (recordType == "route"){
            backendURL = myUrl + "/addRoute/" + name + "/" + transportTypeId;
            infoText = "Новый маршрут успешно добавлен!";
        }else if (recordType == "stop"){
            backendURL = myUrl + "/addStop/" + name + "/" + stop_coordX + "/" + stop_coordY;
            infoText = "Новая остановка успешно добавлена!";
        }else if (recordType == "/transportType"){
            backendURL = myUrl + "addTransportType/" + name;
            infoText = "Новый вид транспорта успешно добавлен!";
        }else if (recordType == "routeStop"){
            backendURL = myUrl + "/addRouteStop/" + route_id + "/" + stop_id;
            infoText = tempInfoText;
        }else {

        }
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        try {
            HTTPHandler handler = new HTTPHandler();
            String json = handler.makeServiceCall(backendURL);
            if (json != null) {
                JSONObject jsonObject  = new JSONObject(json);
                JSONObject jsonObject2 = new JSONObject(jsonObject.get("response").toString());
                String new_id = jsonObject2.get("insertId").toString();
                Log.e("list", "New record ID: " + new_id);
            } else {
                Log.e("list", "could not get JSON routes from server");
            }
        } catch (final JSONException e) {
            Log.e("list", "JSON routes parsing error: " + e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        Toast toast = Toast.makeText(context, infoText, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }
}
