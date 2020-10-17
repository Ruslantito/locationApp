package com.sachinvarma.easylocationsample.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.widget.Spinner;
import android.widget.Toast;

import com.sachinvarma.easylocationsample.MainActivity;
import com.sachinvarma.easylocationsample.objects.Stops;
import com.sachinvarma.easylocationsample.tools.HTTPHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
    public Spinner spinnerStops;
    public ArrayList<Stops> routeStopsArray;
    public Integer teamId;
    public MainActivity activity;

    String backendURL;
    String infoText;
    String new_id;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //routeStopsArray = new ArrayList<>();

        if (recordType == "route"){
            backendURL = myUrl + "/addRoute/" + name + "/" + transportTypeId;
            infoText = "Новый маршрут успешно добавлен!";
        }else if (recordType == "stop"){
            backendURL = myUrl + "/addStop/" + name + "/" + stop_coordX + "/" + stop_coordY;
            infoText = "Новая остановка успешно добавлена!";
        }else if (recordType == "/transportType"){
            backendURL = myUrl + "addTransportType/" + name;
            infoText = "Новый вид транспорта успешно добавлен!";
        }else if (recordType == "RouteStopNew"){
            backendURL = myUrl + "/addStopNew/" + name + "/" + stop_coordX + "/" + stop_coordY + "/" + teamId;
            infoText = "Новая остановка успешно добавлена!";
        }else { }
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        try {
            HTTPHandler handler = new HTTPHandler();
            String json = handler.makeServiceCall(backendURL);
            if (json != null) {
                JSONObject jsonObject  = new JSONObject(json);
                JSONObject jsonObject2 = new JSONObject(jsonObject.get("response").toString());
                new_id = jsonObject2.get("insertId").toString();
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

        if(recordType == "RouteStopNew"){
            if(route_id > 0 && new_id != "") {
                A_addStopInRouter addRoute;
                addRoute = new A_addStopInRouter();
                addRoute.route_id = route_id;
                addRoute.context = context;
                addRoute.stop_id = Integer.parseInt(new_id);
                addRoute.activity = activity;
                addRoute.spinnerStops = spinnerStops;
                addRoute.routeStopsArray = routeStopsArray;
                addRoute.execute();
            }
        }
    }
}
