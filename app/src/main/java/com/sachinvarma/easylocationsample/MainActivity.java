package com.sachinvarma.easylocationsample;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import com.sachinvarma.easylocation.EasyLocationInit;
import com.sachinvarma.easylocation.event.Event;
import com.sachinvarma.easylocation.event.LocationEvent;
import com.sachinvarma.easylocationsample.objects.Route;
import com.sachinvarma.easylocationsample.objects.Stops;
import com.sachinvarma.easylocationsample.tasks.A_addRecord;
import com.sachinvarma.easylocationsample.tasks.LoadAdmRouteStops;
import com.sachinvarma.easylocationsample.tasks.LoadAdmRoutes;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnClickListener, AdapterView.OnItemSelectedListener {

  private int timeInterval = 3000;
  private int fastestTimeInterval = 3000;
  private boolean runAsBackgroundService = false;

  public ListView lvMain2;
  public ArrayList<Route> routesArray;
  public ArrayList<Stops> routeStopsArray;
  //public TextView labelRoutesListFRS, labelStopsListFRS;

  ArrayList<Stops> routeStopsArrayChoosed;
  ListView lvMain;

  TextView tvStopId, labelStopsList;
  EditText etStopName;
  EditText etCoordX, etCoordY;
  public int routeID = 0;
  Integer stopID;

  Spinner spinnerRoutes;
  Spinner spinnerStops;
  Button btGetLocation;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    //lvMain = findViewById(R.id.lvMain);
    //lvMain2 = findViewById(R.id.lvMain2);
    //labelRoutesListFRS = findViewById(R.id.labelRoutesListFRS);
    //labelStopsListFRS = findViewById(R.id.labelStopsListFRS);

    tvStopId = findViewById(R.id.tvStopId);
    etStopName = findViewById(R.id.etStopName);
    etCoordX = findViewById(R.id.etCoordX);
    etCoordY = findViewById(R.id.etCoordY);

    spinnerRoutes = findViewById(R.id.spinnerRoutes);
    spinnerStops = findViewById(R.id.spinnerStops);
    btGetLocation = findViewById(R.id.btGetLocation);

    btGetLocation.setOnClickListener(this);
    spinnerRoutes.setOnItemSelectedListener(this);
    spinnerStops.setOnItemSelectedListener(this);

    //подгружаем данные о координатах в которых мы сейчас находимся
    currentLocation();

    //подгружаем список всех маршрутов
    loadDataFromRoutes();


    /*
    //подгружаем остановки для выбранного маршрута
    lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        loadDataFromRouteStops(routesArray.get(lvMain.getCheckedItemPosition()).id, "none");
      }});

    //подгружаем данные выбранной остановки в ячейки
    lvMain2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        loadSelectedFromStops();
      }});
     */


  }



//////////////
  //подгрузить все маршруты
  public void loadDataFromRoutes(){
    LoadAdmRoutes loaderRoutes;
    loaderRoutes = new LoadAdmRoutes();
    loaderRoutes.routesArray = routesArray;
    //loaderRoutes.lvMain = lvMain;
    loaderRoutes.context = getApplicationContext();
    loaderRoutes.activity = this;
    loaderRoutes.spinnerRoutes = spinnerRoutes;

    //loaderRoutes.labelRoutesListFRS = labelRoutesListFRS;

    loaderRoutes.execute();
  }

  //подгрузить остановки для выбранного маршрута
  public void loadDataFromRouteStops(int selId){
    LoadAdmRouteStops loadRouteStops;
    loadRouteStops = new LoadAdmRouteStops();
    //loadRouteStops.lvMain = lvMain2;
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
  public void loadSelectedFromStopsNew(int selPosition, int selId){
    Integer tempID = selId;
    tvStopId.setText(tempID.toString());

    etStopName.setText(routeStopsArray.get(selPosition).name);

    //double tempX = routeStopsArray.get(selPosition).x;
    //double tempY = routeStopsArray.get(selPosition).y;
    //etCoordX.setText(Double.toString(tempX));
    //etCoordY.setText(Double.toString(tempY));
  }


  //зачистить данные об остановке в ячейках
  public void cleanFields(){
    //spinnerStops.setAdapter(null);

    tvStopId.setText("");
    etStopName.setText("");
    //etCoordX.setText("");
    //etCoordY.setText("");
  }


  //добавить остановку в маршрут
  public void addRouteStopNew(String tempInfoText){
    A_addRecord addRouteStopNew;
    addRouteStopNew = new A_addRecord();
    addRouteStopNew.context = getApplicationContext();
    addRouteStopNew.route_id = routeID;
    addRouteStopNew.recordType = "RouteStopNew";
    addRouteStopNew.tempInfoText = tempInfoText;

    addRouteStopNew.name = etStopName.getText().toString();
    String tempX = etCoordX.getText().toString();
    String tempY = etCoordY.getText().toString();
    Integer tempValue = 0;
    if (tempX.equals("")){ tempX = tempValue.toString(); }
    if (tempY.equals("")){ tempY = tempValue.toString(); }
    addRouteStopNew.stop_coordX = tempX;
    addRouteStopNew.stop_coordY = tempY;

    addRouteStopNew.execute();
  }


  //получить координату девайса
  public void currentLocation(){
    new EasyLocationInit(MainActivity.this, timeInterval, fastestTimeInterval, runAsBackgroundService);
  }

  @Override
  public void onClick(View arg0) {
    switch (arg0.getId()) {
      case R.id.btGetLocation:
        currentLocation();
        addRouteStopNew("");
        break;
    };
  }


  @Override
  public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
    switch (parentView.getId()) {
      case R.id.spinnerRoutes:
        if(position > 0) {
          routeID = routesArray.get(position-1).id;
          loadDataFromRouteStops(routeID);
        }else {
          spinnerStops.setAdapter(null);
        }
        break;
      case R.id.spinnerStops:
        if(position > 0) {
          stopID = routeStopsArray.get(position-1).id;
          loadSelectedFromStopsNew(position-1, stopID);
        }else {
          cleanFields();
        }
        break;
    };
  }
  @Override
  public void onNothingSelected(AdapterView<?> parentView) {}

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
        ((TextView) findViewById(R.id.tvLocation)).setText("Ваши координаты \nX: "
          + ((LocationEvent) event).location.getLatitude()
          + "  Y: "
          + ((LocationEvent) event).location.getLongitude());


//////////////
        etCoordX.setText(Double.toString(((LocationEvent) event).location.getLatitude()));
        etCoordY.setText(Double.toString(((LocationEvent) event).location.getLongitude()));
//////////////


      }
    }
  }

}
