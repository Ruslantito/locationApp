package com.sachinvarma.easylocationsample.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import com.sachinvarma.easylocationsample.MainActivity;
import com.sachinvarma.easylocationsample.R;
import com.sachinvarma.easylocationsample.objects.Route;
import com.sachinvarma.easylocationsample.objects.Stops;
import com.sachinvarma.easylocationsample.tools.HTTPHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import static com.sachinvarma.easylocationsample.tools.MyData.myUrl;

public class LoadAdmRoutes extends AsyncTask<Void, Void, Void> {
    public Context context;
    public ArrayList<Route> routesArray;
    public MainActivity activity;
    public Spinner spinnerRoutes;
    //public ListView lvMain;

    List<String> routesList;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        routesArray = new ArrayList<>();
        routesList = new ArrayList<>();
        routesList.add("Выберите маршрут");
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        try {
            HTTPHandler handler = new HTTPHandler();
            String json = handler.makeServiceCall(myUrl + "/routes");
            if (json != null) {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject newJSONObject = jsonArray.getJSONObject(i);
                    Route route = new Route();
                    route.id = newJSONObject.getInt("id");
                    route.name = newJSONObject.getString("name");
                    route.transportType = newJSONObject.getInt("transport_type_id");
                    routesArray.add(route);
                    routesList.add(route.name);
                }
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

        //подгружаем список в переменную находящуюся в Activity
        activity.routesArray = routesArray;

        /*
        // устанавливаем режим выбора пунктов списка
        lvMain.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_single_choice, routesList);
        lvMain.setAdapter(adapter);
        */

        // выпадающий список для маршрутов
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(context, R.layout.spinner_layout, routesList);
        //ArrayAdapter<String> adapter2 = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, routesList);
        spinnerRoutes.setAdapter(adapter2);
    }

}
