package ru.zeak.zeak;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener ,  SwipeRefreshLayout.OnRefreshListener{


    public SwipeRefreshLayout mySwipeRefreshLayout;


    ListView listView;
    private static final List<Indicator> indicators = new ArrayList<Indicator>();

    public static final String TAG = "ZeakLogs";

    //Логин пароль пользователя
    String login = "";
    String pass = "";

    // Глобальная переменная места хранение кукки
    Map<String, String> cooca = new HashMap<String, String>();

    //Глобальная переменная подключенности к зеаку
    Boolean connected = Boolean.FALSE;
    Boolean connectedSSL = Boolean.FALSE;

    //Включние режима дебага
    Boolean debug = Boolean.FALSE;

    //Глобальная переменная логинурла
    String loginURL = "zeak.ru/accounts/login/?next=/";

    //Глобальная переменная адреса данных (будет массив)
    String itemURLfirst = "zeak.ru/t3pio_indicatejson/";
    String itemURL;

    //Глобальная переменная браузер-агента
    String useragent = "ZEAK-ANDROID; Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.101 Safari/537.36";






    //Переменная для хранения распарсенного джисона с сайта для функции обновления
    ArrayList<HashMap<String, String>> SiteDataList;

    //Статичные переменные для разбора джисона с сайта
    public static final String TAG_TODO = "todo";
    public static final String TAG_ID = "id";
    public static final String TAG_DATE = "doitdate";
    public static final String TAG_DIM = "todo_dim";
    public static final String TAG_MERCH = "idmerch_id";
    public static final String TAG_TITLE = "title";


    //Для сохранения настроек
    SharedPreferences sPref;
    public static final String LOGIN_SAVED_TEXT = "login_saved_text";
    public static final String PASSWORD_SAVED_TEXT = "password_saved_text";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // найдем View-элементы
        Log.d(TAG, "найдем View-элементы");


        Log.d(TAG, "присваиваем обработчик кнопкам");



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        Log.d(TAG, "присваиваем mySwipeRefreshLayout");

        mySwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mySwipeRefreshLayout.setOnRefreshListener(this);

        // Находим листвью главной формы и связываем
        listView = (ListView) findViewById(R.id.list);

        // грузим настройки
        loadSettings();


    }



    //Грузим настройки
    public void loadSettings() {
        Log.d(TAG, "Загрузка настроек из файла");
        sPref = getSharedPreferences("ZeakPref",MODE_PRIVATE);
        login = sPref.getString(LOGIN_SAVED_TEXT, "");
        pass = sPref.getString(PASSWORD_SAVED_TEXT, "");
        itemURL = itemURLfirst+login+"/";
        Log.d(TAG, "Загрузка настроек из файла завершена");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_refresh) {
//            mySwipeRefreshLayout.setRefreshing(true);
            Log.d(TAG, "Refresh menu item selected");
            GetInfoFromSite();
//            mySwipeRefreshLayout.setRefreshing(false);
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.d(TAG, "SetActivity.class");
            Intent intent = new Intent(this, SetActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_about) {
            return true;
        }
        if (id == R.id.action_exit) {
            System.exit(0);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        Log.d(TAG, "по id определяем кнопку, вызвавшую этот обработчик");

//        switch (v.getId()) {
//            case R.id.btnOk:
//                // кнопка ОК
//                Log.d(TAG, "кнопка ОК");
//
//                //new ZeakPlainConnect.AuthTask(this,cooca, connected,debug, loginURL,itemURL,useragent, login, pass).execute();
//                new ZeakSSLConnect.AuthTask(this, cooca, connectedSSL, debug, loginURL, itemURL, useragent, login, pass).execute();
//
//                Toast.makeText(this, "Подключение к сайту", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.btnCancel:
//                // кнопка Cancel
//                Log.d(TAG, "кнопка Cancel");
//                connectedSSL = Boolean.FALSE;
//
//                // конструктор класса с AsyncTask
//
//
//                //tvOut.setText("Нажата кнопка Cancel");
//                Toast.makeText(this, "Соединение сброшено", Toast.LENGTH_SHORT).show();
//                break;
//
//        }
    }




// Класс для элементов листвью главной формы
    //Определяет поля котоые будут в элементе листвью

    private static class Indicator {
        public final String name;
        public final String value;
        public final String date;

        public Indicator(String name, String value, String date) {
            this.name = name;
            this.value = value;
            this.date = date;
        }
    }

    //Адаптер для класса листвью
    //здесь соединяем листвью и дочернии элементы на каждой записи листа
    private class IndicatorAdapter extends ArrayAdapter<Indicator> {

        public IndicatorAdapter(Context context) {
            super(context, R.layout.row, indicators);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Indicator indicator = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.row, null);
            }

            //Полосую листы через even_row и odd
            if ( position % 2 == 0)
                convertView.setBackgroundResource(R.drawable.odd_row);
            else
                convertView.setBackgroundResource(R.drawable.even_row);

            ((TextView) convertView.findViewById(R.id.ColValue))
                    .setText(indicator.name);
            ((TextView) convertView.findViewById(R.id.ColName))
                    .setText(indicator.value);
            ((TextView) convertView.findViewById(R.id.ColDate))
                    .setText(indicator.date);
            return convertView;
        }
    }

// Функция обновления элементов на главной форме асинхрон отработал
    public void RefreshMainForm() {
        Log.d(TAG, "RefreshMainForm: start");
        if (connectedSSL == Boolean.TRUE) {
            mySwipeRefreshLayout.setRefreshing(true);
            Log.d(TAG, "RefreshMainForm: start1");
            ArrayAdapter<Indicator> adapter = new IndicatorAdapter(this);
//            Log.d(TAG, "RefreshMainForm: start2");
            adapter.clear();
//            Log.d(TAG, "RefreshMainForm: start2-1"+SiteDataList.size());
           // adapter.notifyDataSetChanged();

            for (int i = 0; i < SiteDataList.size(); i++) {
//                Log.d(TAG, "RefreshMainForm: start3");
                Indicator ginger = new Indicator(SiteDataList.get(i).get(TAG_DIM).toString(), SiteDataList.get(i).get(TAG_TITLE).toString(), SiteDataList.get(i).get(TAG_DATE).toString());
                adapter.add(ginger);
//                Log.d(TAG, "RefreshMainForm: start4");
            }
//        Indicator ginger2 = new Indicator("zopa","zopyx","zzz");
//        adapter.add(ginger2);
            Log.d(TAG, "RefreshMainForm: start list adapter" + SiteDataList.size());
            listView.setAdapter(adapter);


            Toast.makeText(this, "Обновлено", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Ошибка подключения к сайту", Toast.LENGTH_SHORT).show();
        }
        mySwipeRefreshLayout.setRefreshing(false);
    }


    // Функция обновления элементов на главной форме вызываю асинхрон
    public void GetInfoFromSite() {
        Log.d(TAG, "кнопка ОК");
        mySwipeRefreshLayout.setRefreshing(true);
        //new ZeakPlainConnect.AuthTask(this,cooca, connected,debug, loginURL,itemURL,useragent, login, pass).execute();
        //Toast.makeText(this, "Подключение к сайту", Toast.LENGTH_SHORT).show();
        new ZeakSSLConnect.AuthTask(this, cooca, connectedSSL, debug, loginURL, itemURL, useragent, login, pass).execute();


    }

    // переопределяю рефреш от свайпа
    @Override
    public void onRefresh() {
        Log.d(TAG, "кнопка Refresh");
        mySwipeRefreshLayout.setRefreshing(true);
//        вызываю рефреш формы ментодом свайпа
        GetInfoFromSite();
    }


}

