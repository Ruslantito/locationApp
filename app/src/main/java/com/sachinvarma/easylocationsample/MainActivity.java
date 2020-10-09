package com.sachinvarma.easylocationsample;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import com.sachinvarma.easylocation.EasyLocationInit;
import com.sachinvarma.easylocation.event.Event;
import com.sachinvarma.easylocation.event.LocationEvent;
import com.sachinvarma.easylocationsample.objects.Route;
import com.sachinvarma.easylocationsample.objects.Stops;
import com.sachinvarma.easylocationsample.tasks.LoadAdmRouteStops;
import com.sachinvarma.easylocationsample.tasks.LoadAdmRoutes;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

  private int timeInterval = 3000;
  private int fastestTimeInterval = 3000;
  private boolean runAsBackgroundService = false;


////////////
  public ListView lvMain2;
  public ArrayList<Route> routesArray;
  public ArrayList<Stops> routeStopsArray;
  //public TextView labelRoutesListFRS, labelStopsListFRS;


Spinner spinnerRoutes;
Spinner spinnerStops;


  ArrayList<Stops> routeStopsArrayChoosed;
  ListView lvMain;

  TextView tvStopId, labelStopsList;
  EditText etStopName;
  EditText etCoordX, etCoordY;

////////////


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    findViewById(R.id.btGetLocation).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        new EasyLocationInit(MainActivity.this, timeInterval, fastestTimeInterval, runAsBackgroundService);
      }
    });


///////////
    lvMain = findViewById(R.id.lvMain);
    lvMain2 = findViewById(R.id.lvMain2);
    //labelRoutesListFRS = findViewById(R.id.labelRoutesListFRS);
    //labelStopsListFRS = findViewById(R.id.labelStopsListFRS);



    tvStopId = findViewById(R.id.tvStopId);
    etStopName = findViewById(R.id.etStopName);
    etCoordX = findViewById(R.id.etCoordX);
    etCoordY = findViewById(R.id.etCoordY);


    spinnerRoutes = findViewById(R.id.spinnerRoutes);
    spinnerStops = findViewById(R.id.spinnerStops);


    //подгружаем список всех маршрутов
    loadDataFromRoutes();

    /*
    //подгружаем остановки для выбранного маршрута
    lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        loadDataFromRouteStops(routesArray.get(lvMain.getCheckedItemPosition()).id, "none");
        //btnEditFRS.setVisibility(View.VISIBLE);
      }});

    //подгружаем данные выбранной остановки в ячейки
    lvMain2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        loadSelectedFromStops();
      }});
     */



    spinnerRoutes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
        int routeID;
        if(position > 0) {
          routeID = routesArray.get(position-1).id;
          loadDataFromRouteStops(routeID);
        }else {
          loadDataFromRoutes();
          cleanFields();
        }
      }
      @Override
      public void onNothingSelected(AdapterView<?> parentView) {}
    });



    spinnerStops.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
        if(position > 0) {
          loadSelectedFromStopsNew(position-1);
        }else {
          cleanFields();
        }
      }
      @Override
      public void onNothingSelected(AdapterView<?> parentView) {}
    });




///////////


  }



//////////////

  //подгрузить все маршруты
  public void loadDataFromRoutes(){
    LoadAdmRoutes loaderRoutes;
    loaderRoutes = new LoadAdmRoutes();
    loaderRoutes.routesArray = routesArray;
    loaderRoutes.lvMain = lvMain;

    loaderRoutes.context = getApplicationContext();
    loaderRoutes.activity = this;

    loaderRoutes.spinnerRoutes = spinnerRoutes;
    loaderRoutes.spinnerStops = spinnerStops;
    //loaderRoutes.labelRoutesListFRS = labelRoutesListFRS;

    loaderRoutes.execute();
  }

  //подгрузить остановки для выбранного маршрута
  public void loadDataFromRouteStops(int selId){
    LoadAdmRouteStops loadRouteStops;
    loadRouteStops = new LoadAdmRouteStops();
    loadRouteStops.lvMain = lvMain2;
    loadRouteStops.routeStopsArray = routeStopsArray;
    loadRouteStops.routeId = selId;
    loadRouteStops.context = getApplicationContext();
    loadRouteStops.activity = this;

    loadRouteStops.spinnerStops = spinnerStops;

    //loadRouteStops.labelStopsListFRS = labelStopsListFRS;

    loadRouteStops.execute();
  }


  //подгрузить информацию выбранной остановки в ячейки
  public void loadSelectedFromStops(){
    Integer tempID = routeStopsArray.get(lvMain2.getCheckedItemPosition()).id;
    tvStopId.setText(tempID.toString());

    etStopName.setText(routeStopsArray.get(lvMain2.getCheckedItemPosition()).name);
    double tempX = routeStopsArray.get(lvMain2.getCheckedItemPosition()).x;
    double tempY = routeStopsArray.get(lvMain2.getCheckedItemPosition()).y;
    etCoordX.setText(Double.toString(tempX));
    etCoordY.setText(Double.toString(tempY));
  }




  //NEW подгрузить информацию выбранной остановки в ячейки
  public void loadSelectedFromStopsNew(int selId){
    Integer tempID = routeStopsArray.get(selId).id;
    tvStopId.setText(tempID.toString());

    etStopName.setText(routeStopsArray.get(selId).name);
    double tempX = routeStopsArray.get(selId).x;
    double tempY = routeStopsArray.get(selId).y;
    etCoordX.setText(Double.toString(tempX));
    etCoordY.setText(Double.toString(tempY));
  }


  //зачистить данные об остановке в ячейках
  public void cleanFields(){
    tvStopId.setText("");

    etStopName.setText("");
    etCoordX.setText("");
    etCoordY.setText("");
  }

//////////////




  @Override
  protected void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);
  }

  @Override
  protected void onStop() {
    super.onStop();
    EventBus.getDefault().unregister(this);
  }

  @SuppressLint("SetTextI18n")
  @Subscribe
  public void getEvent(final Event event) {
    if (event instanceof LocationEvent) {
      if (((LocationEvent) event).location != null) {
        ((TextView) findViewById(R.id.tvLocation)).setText("The Latitude is "
          + ((LocationEvent) event).location.getLatitude()
          + "\n and the Longitude is "
          + ((LocationEvent) event).location.getLongitude());
      }
    }
  }


}
