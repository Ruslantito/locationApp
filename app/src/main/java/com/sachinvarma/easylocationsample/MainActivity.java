package com.sachinvarma.easylocationsample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sachinvarma.easylocation.EasyLocationInit;
import com.sachinvarma.easylocation.event.Event;
import com.sachinvarma.easylocation.event.LocationEvent;
import com.sachinvarma.easylocationsample.objects.Route;
import com.sachinvarma.easylocationsample.objects.Stops;
import com.sachinvarma.easylocationsample.objects.Team;
import com.sachinvarma.easylocationsample.tasks.A_addRecord;
import com.sachinvarma.easylocationsample.tasks.LoadAdmLogs;
import com.sachinvarma.easylocationsample.tasks.LoadAdmRouteStops;
import com.sachinvarma.easylocationsample.tasks.LoadAdmRoutes;
import com.sachinvarma.easylocationsample.tasks.LoadAdmTeams;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import android.provider.Settings.Secure;
import java.util.ArrayList;

import static com.sachinvarma.easylocationsample.tools.MyData.myUrl;
import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements OnClickListener, AdapterView.OnItemSelectedListener {
  private int timeInterval = 3000;
  private int fastestTimeInterval = 3000;
  private boolean runAsBackgroundService = false;
  public ArrayList<Route> routesArray;
  public ArrayList<Stops> routeStopsArray;
  public ArrayList<Team> teamsArray;

  public String android_id;
  public int routeID = 0;
  public ListView lvMain2;
  //public TextView labelRoutesListFRS, labelStopsListFRS;

  ArrayList<Stops> routeStopsArrayChoosed;
  ListView lvMain;

  TextView tvStopId, labelStopsList;
  EditText etStopName;
  EditText etCoordX, etCoordY;
  Integer stopID;


  Editable etCoordX_Temp;
  Editable etCoordY_Temp;
  Boolean coordInBufferPresent = false;


  Spinner spinnerRoutes;
  Spinner spinnerStops;
  Spinner spinnerTeams;

  Button btGetLocation;
  Button btGetLocationTmp;
  Button btAddStopNew;
  Button btHideStopNew;


  Button btGetLocCopy;
  Button btnGoToLogs;



  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    tvStopId = findViewById(R.id.tvStopId);
    etStopName = findViewById(R.id.etStopName);
    etCoordX = findViewById(R.id.etCoordX);
    etCoordY = findViewById(R.id.etCoordY);

    spinnerRoutes = findViewById(R.id.spinnerRoutes);
    spinnerStops = findViewById(R.id.spinnerStops);
    spinnerTeams = findViewById(R.id.spinnerTeam);

    btGetLocation = findViewById(R.id.btGetLocation);
    btGetLocationTmp = findViewById(R.id.btGetLocationTmp);
    btAddStopNew = findViewById(R.id.btAddStopNew);
    btHideStopNew = findViewById(R.id.btHideStopNew);


    btnGoToLogs = findViewById(R.id.btnGoToLogs);
    btnGoToLogs.setOnClickListener(this);

    btGetLocCopy = findViewById(R.id.btGetLocCopy);
    btGetLocCopy.setOnClickListener(this);


    btGetLocation.setOnClickListener(this);
    btGetLocationTmp.setOnClickListener(this);
    btAddStopNew.setOnClickListener(this);
    btHideStopNew.setOnClickListener(this);

    spinnerRoutes.setOnItemSelectedListener(this);
    spinnerStops.setOnItemSelectedListener(this);
    spinnerTeams.setOnItemSelectedListener(this);

    //скрыть ячейку для добавления остановок
    etStopName.setVisibility(View.INVISIBLE);
    etStopName.setEnabled(false);
    etStopName.setText(null);

    //скрыть ячейку для добавления новой остановки
    stopFieldVisibleOrNot(false);

    //подгружаем список всех маршрутов
    loadDataFromRoutes();

    //подгружаем список всех команд
    loadDataFromTeams();

    //подгружаем данные о координатах в которых мы сейчас находимся
    currentLocation();

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

    //if (spinnerTeams.getSelectedItemPosition() > 0){
    //  loaderRoutes.teamId_hasAccess = teamsArray.get(spinnerTeams.getSelectedItemPosition()-1).id;;
    //}

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


  //подгрузить все команды
  public void loadDataFromTeams(){
    LoadAdmTeams loaderTeams;
    loaderTeams = new LoadAdmTeams();
    //loaderTeams.teamsArray = teamsArray;
    loaderTeams.context = getApplicationContext();
    loaderTeams.teamsArray = teamsArray;
    loaderTeams.activity = this;
    loaderTeams.spinnerTeams = spinnerTeams;
    loaderTeams.execute();
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
    tvStopId.setText(null);
    etStopName.setText(null);
    //etCoordX.setText("");
    //etCoordY.setText("");
  }

  //добавить остановку в маршрут
  public void addRouteStopNew(String tempInfoText){
    A_addRecord addRouteStopNew;
    addRouteStopNew = new A_addRecord();
    addRouteStopNew.context = getApplicationContext();

    // защита от ввода специальных символов
    String stName = etStopName.getText().toString();
    stName = stName.replaceAll("\\W+","");

    addRouteStopNew.name = stName;
    addRouteStopNew.recordType = "RouteStopNew";
    addRouteStopNew.route_id = routeID;
    addRouteStopNew.tempInfoText = tempInfoText;
    addRouteStopNew.spinnerStops = spinnerStops;
    addRouteStopNew.routeStopsArray = routeStopsArray;
    addRouteStopNew.activity = this;

    addRouteStopNew.teamId = teamsArray.get(spinnerTeams.getSelectedItemPosition()-1).id;

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

  //показать или скрыть ячейку для добавления новой остановки
  public void stopFieldVisibleOrNot(boolean stopFieldVisible){
    if (stopFieldVisible) {
      spinnerStops.setVisibility(View.INVISIBLE);
      spinnerStops.setEnabled(false);

      etStopName.setVisibility(View.VISIBLE);
      etStopName.setEnabled(true);
      etStopName.setText(null);

      btAddStopNew.setVisibility(View.INVISIBLE);
      btHideStopNew.setVisibility(View.VISIBLE);
    }else {
      spinnerStops.setVisibility(View.VISIBLE);
      spinnerStops.setEnabled(true);

      etStopName.setVisibility(View.INVISIBLE);
      etStopName.setEnabled(false);
      etStopName.setText(null);

      btAddStopNew.setVisibility(View.VISIBLE);
      btHideStopNew.setVisibility(View.INVISIBLE);
    }
  }

  //show/hide button
  public void showButtonSend(Boolean showButton){
    if(showButton){
      btGetLocation.setVisibility(View.VISIBLE);
      btGetLocationTmp.setVisibility(View.INVISIBLE);
    }else{
      btGetLocation.setVisibility(View.INVISIBLE);
      btGetLocationTmp.setVisibility(View.VISIBLE);
    }
  }

  //show info window with custom text
  public void infoWindow(String infoText ){
    Toast toast = Toast.makeText(getApplicationContext(), infoText, Toast.LENGTH_LONG);
    toast.setGravity(Gravity.TOP, 0, 0);
    toast.show();
  }

  //check fields for empty
  public void checkEmptyFiels(){
    Boolean infoPresent = false;
    String infoText = "";
    if (spinnerTeams.getSelectedItemPosition() < 1) {
      infoPresent = true;
      infoText = "название команды";
    }
    if (spinnerRoutes.getSelectedItemPosition() < 1) {
      infoPresent = true;
      if (infoText.equals("")){ infoText = "название маршрута"; }
      else{ infoText = infoText + ", название маршрута"; }
    }
    String data = etStopName.getText().toString();
    if (data.equals("")) {
      infoPresent = true;
      if (infoText.equals("")){ infoText = "остановку"; }
      else{ infoText = infoText + " и остановку"; }
    }
    if (infoPresent){ infoWindow("Пожалуйста выберите " + infoText); }
  }

  @Override
  public void onClick(View arg0) {
    switch (arg0.getId()) {
      case R.id.btGetLocation:
        if (spinnerTeams.getSelectedItemPosition() > 0) {
          if (spinnerRoutes.getSelectedItemPosition() > 0) {
            String data = etStopName.getText().toString();
            if (!data.equals("")) {

              currentLocation();
              addRouteStopNew("");

              stopFieldVisibleOrNot(false);
              showButtonSend(false);
            } else { checkEmptyFiels(); }
          } else { checkEmptyFiels(); }
        } else { checkEmptyFiels(); }
        break;
      case R.id.btGetLocationTmp:
        //checkEmptyFiels();
        break;
      case R.id.btAddStopNew:
        stopFieldVisibleOrNot(true);
        break;
      case R.id.btHideStopNew:
        stopFieldVisibleOrNot(false);
        break;

      case R.id.btnGoToLogs:
        goToActivity();
        break;

      case R.id.btGetLocCopy:
        copyCoordinate();
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
      case R.id.spinnerTeam:
        if(position > 0) {
          // TODO something
        }else {
          // TODO if choosed first
        }
        break;
    };
  }
  @Override
  public void onNothingSelected(AdapterView<?> parentView) {}


//для перехода к статистике
  public void goToActivity(){
    Intent intent = new Intent(getApplicationContext(), LogsActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    getApplicationContext().startActivity(intent);
  }

  //копировать координаты в буфер (2 ячейки х у)
  public void copyCoordinate() {
    etCoordX_Temp = etCoordX.getText();
    etCoordY_Temp = etCoordY.getText();
    coordInBufferPresent = true;
    //infoWindow(etCoordX_Temp + "|||" + etCoordY_Temp);


    AlertDialog alertDialog = new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Выбор координаты для добавления")
            .setMessage("Хотите ли вы использовать сохраненные координаты?")
            .setPositiveButton("Коор-ты из буфера", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(), "Да", Toast.LENGTH_LONG).show();
              }
            })
            .setNegativeButton("Коор-ты устройства", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(), "Нет", Toast.LENGTH_LONG).show();
              }
            })
            .show();


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
        ((TextView) findViewById(R.id.tvLocation)).setText("Ваши координаты \nX: "
          + ((LocationEvent) event).location.getLatitude()
          + "  Y: " + ((LocationEvent) event).location.getLongitude());

//////////////
        etCoordX.setText(Double.toString(((LocationEvent) event).location.getLatitude()));
        etCoordY.setText(Double.toString(((LocationEvent) event).location.getLongitude()));
//////////////

      }
    }
  }

}
