package ru.zeak.zeak;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by zopyx on 25.07.2016.
 */


public class ZeakSSLConnect {


    public static class AuthTask extends AsyncTask<Void, Integer, Void> {

        //Здесь хранится будет разобранный html документ
        Document doc = null;

        //Тут храним значение заголовка сайта
        String title;


        //Локальная переменная где храним куку
        private Map<String, String> cooca = new HashMap<String, String>();

        //Локальная переменная подключенности к зеаку
        private Boolean connectedSSL;

        //Локальная Включние режима дебага
        private Boolean debug;


        //Локальная переменная логинурла
        private String loginURL;

        //Локальная переменная адреса данных (будет массив)
        private String itemURL;

        //Локальная переменная браузер-агента
        private String useragent;

        //Локальная Логин пароль пользователя
        private String login;
        private String pass;

        ArrayList<HashMap<String, String>> studentList;

         //Определение закрытой локальной переменной активитии
        private MainActivity mainActivity;

        // конструктор класса
        public AuthTask(MainActivity mainActivity,
                        Map<String, String> cooca,
                        Boolean connectedSSL,
                        Boolean debug,
                        String loginURL,
                        String itemURL,
                        String useragent,
                        String login,
                        String pass) {

            this.mainActivity = mainActivity;
            this.connectedSSL = connectedSSL;
            this.debug = debug;
            this.loginURL = "https://" + loginURL;
            this.itemURL = "https://" + itemURL;
            this.useragent = useragent;
            this.login = login;
            this.pass = pass;
            this.cooca = cooca;


        }

        public boolean isInternetAvailable() {
            try {
                InetAddress ipAddr = InetAddress.getByName("zeak.ru"); //You can replace it with your name
                return !ipAddr.equals("");

            } catch (Exception e) {
                return false;
            }

        }

// Парсим Джисон с сайта


        private ArrayList<HashMap<String, String>> ParseJSON(String json) {
            if (json != null) {
                try {
                    // Hashmap for ListView
                    ArrayList<HashMap<String, String>> retTaskList = new ArrayList<HashMap<String, String>>();

                    JSONObject jsonObj = new JSONObject(json);

                    // Getting JSON Array node
                    JSONArray todo = jsonObj.getJSONArray(mainActivity.TAG_TODO);

                    // looping through All Students
                    for (int i = 0; i < todo.length(); i++) {
                        JSONObject c = todo.getJSONObject(i);

                        String id = c.getString(mainActivity.TAG_ID);
                        String datetime = c.getString(mainActivity.TAG_DATE);
                        String dim = c.getString(mainActivity.TAG_DIM);
                        String merch = c.getString(mainActivity.TAG_MERCH);
                        String title = c.getString(mainActivity.TAG_TITLE);


                        // tmp hashmap for single student
                        HashMap<String, String> todos = new HashMap<String, String>();

                        // adding every child node to HashMap key => value
                        todos.put(mainActivity.TAG_ID, id);
                        todos.put(mainActivity.TAG_DATE, datetime);
                        todos.put(mainActivity.TAG_DIM, dim);
                        todos.put(mainActivity.TAG_MERCH, merch);
                        todos.put(mainActivity.TAG_TITLE, title);

                        // adding student to students list
                        retTaskList.add(todos);
                    }
                    return retTaskList;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                Log.e(mainActivity.TAG,"ServiceHandler; No data received from HTTP Request");
                return null;
            }
        }


        //
        //функция подключения к сайту, и возвращению куков в глобальную переменную
        //


        private void connectzeak() throws IOException {

            String token = "";
            String key;
            String val;

            try {
                Log.d(mainActivity.TAG,"connectzeak: Start");
                connectedSSL = Boolean.FALSE;
                Log.d(mainActivity.TAG,"connec:cooca.isEmpty: True");

                //получаем страницу входа


                Connection connection1 = HttpConnection.connect(loginURL)
                        .ignoreHttpErrors(true)
                        .validateTLSCertificates(false)
                        .userAgent(useragent);
                Log.d(mainActivity.TAG,"connectzeak:2: Zeak2");
                Connection.Response response1 = connection1.execute();
                Log.d(mainActivity.TAG,"connectzeak:status1:"+ String.valueOf(response1.statusCode()));
                if (debug) {
                    Log.d(mainActivity.TAG,"connectzeak:3:"+ response1.body());
                }


                doc = response1.parse();
                Elements links = doc.getElementsByTag("input");
                ;
                for (Element link : links) {
                    Log.d(mainActivity.TAG,"connectzeak.dataparse.n"+ link.attr("name"));
                    Log.d(mainActivity.TAG,"connectzeak.dataparse.v"+ link.attr("value"));
                }

                //находим csrf-токен (token) для последующиго POST запроса
                for (Map.Entry<String, String> cookie : response1.cookies().entrySet()) {
                    key = cookie.getKey();
                    val = cookie.getValue();
                    Log.d(mainActivity.TAG,"connectzeak.cookie1"+ key + " : " + val);
                    if (key.equals("csrftoken")) {
                        token = val;
                        Log.d(mainActivity.TAG,"connectzeak.token"+ key + " : " + val);
                    }
                }

                //Пробегаемся по хедерам и выводим их в дебаг
                if (debug) {
                    Log.d(mainActivity.TAG,"connectzeak.headers1 ............................ ");
                    for (Map.Entry<String, String> head : response1.headers().entrySet()) {
                        Log.d(mainActivity.TAG,"headers1"+ head.getKey() + " : " + head.getValue());
                    }
                    Log.d(mainActivity.TAG,"connectzeak.headers1 ............................ ");
                }


                //делаем пост запрос для подключения к зеаку
                Connection connection2 = connection1.url(loginURL)
                        .timeout(7000)
                        .cookies(response1.cookies())
                        .ignoreHttpErrors(true)
                        .data("username", login)
                        .data("password", pass)
                        .data("csrfmiddlewaretoken", token)
                        .data("csrftoken", token)
                        .validateTLSCertificates(false)
                        .data("next", itemURL)
                        .referrer(loginURL)
                        .method(Connection.Method.POST)
                        .followRedirects(true)
                        .ignoreContentType(true);
                Connection.Response response2 = connection2.execute();


                if (debug) {
                    doc = response2.parse();
                    Elements links2 = doc.getElementsByTag("input");
                    for (Element link : links2) {
                        Log.d(mainActivity.TAG,"connectzeak2.dparse.n "+ link.attr("name"));
                        Log.d(mainActivity.TAG,"connectzeak2.dparse.v "+ link.attr("value"));
                    }
                }

                Log.d(mainActivity.TAG,"connectzeak add cookies in variables");
                cooca.putAll(response2.cookies());
                Log.d(mainActivity.TAG,"connectzeak:status-2:"+ String.valueOf(response2.statusCode()));
                Log.d(mainActivity.TAG,"connectzeak:body:"+ response2.body());

                Log.d(mainActivity.TAG,"connectzeak done add cookies in variables");

                if (response2.statusCode() == 200 || response2.statusCode() == 302) {
                    connectedSSL = Boolean.TRUE;
                    Log.d(mainActivity.TAG,"connectzeak connectedSSL zeak true!");
                } else {
                    doc = response2.parse();
                    Log.d(mainActivity.TAG,"connectzeak connectedSSL zeak false!");
                }

                Log.d(mainActivity.TAG,"connectzeak: END");
            } catch (Exception e) {
                e.printStackTrace();
                connectedSSL = Boolean.FALSE;
            }
        }


        //
        // Получаем данные с зеака в документ (дозможно тип нужно сразу джисоновский сделать и передавать в майнактивити)
        //
        private Document getZeakdata(String getURL) {

            Document dd = null;

            try {
                //disableSSLCertCheck();
                Connection connection3 = HttpConnection.connect(getURL)
                        .cookies(cooca)
                        .ignoreHttpErrors(true)
                        .ignoreContentType(true)
                        .validateTLSCertificates(false)
                        .userAgent(useragent);
                Log.d(mainActivity.TAG,"getZeakdata:Start");
                Connection.Response response3 = connection3.execute();
                Log.d(mainActivity.TAG,"getZeakdata:statuscode:"+ String.valueOf(response3.statusCode()));
                // Получили json из сервиса
                if (debug) {
                    Log.d(mainActivity.TAG,"getZeakdata:body:"+ response3.body());
                }


                if (debug) {
                    for (Map.Entry<String, String> cookie : response3.cookies().entrySet()) {
                        Log.d(mainActivity.TAG,"cookie2"+ cookie.getKey() + " : " + cookie.getValue());

                    }
                    for (Map.Entry<String, String> head : response3.headers().entrySet()) {
                        Log.d(mainActivity.TAG,"headers2"+ head.getKey() + " : " + head.getValue());
                    }
                }


                dd = response3.parse();
                Log.d(mainActivity.TAG,"getZeakdata:END!");

            } catch (Exception e) {
                e.printStackTrace();
            }
            return dd;
        }


        //
        //Бекграунд асинхронного таска
        //
        @Override
        protected Void doInBackground(Void... params) {


            Log.d(mainActivity.TAG,"doInBackground:Start");

            //Проверка и подключение к Зеаку
            //cooca.isEmpty() ||
            if (isInternetAvailable()) {
                if (connectedSSL == Boolean.FALSE) {
                    try {
                        connectzeak();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                //Получить данные из сервиса
                if (connectedSSL == Boolean.TRUE) {
                    doc = getZeakdata(itemURL);
                }
            }
            else {
                connectedSSL=Boolean.FALSE;

            }

            Log.d(mainActivity.TAG,"doInBackground:END!");


            return null;


        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.d(mainActivity.TAG,"PostExecute:Start");

            if (connectedSSL == Boolean.TRUE) {

                mainActivity.SiteDataList = ParseJSON(doc.body().text());
                mainActivity.cooca.putAll(this.cooca);
            }

            mainActivity.connectedSSL = this.connectedSSL;
            // присваиваем переменные после выполнения действий по подключению в бекграунде
            mainActivity.RefreshMainForm();

            Log.d(mainActivity.TAG,"PostExecute:END!");
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //activity.tv.setText("i = " + values[0]);
        }


    }
}


