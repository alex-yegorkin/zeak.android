package ru.zeak.zeak;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zopyx on 25.07.2016.
 */


public class ZeakPlainConnect {



    public static class AuthTask extends AsyncTask<Void, Integer, Void> {

        //Здесь хранится будет разобранный html документ
        Document doc = null;

        //Тут храним значение заголовка сайта
        String title;

        //Локальная переменная где храним куку
        private Map<String, String> cooca = new HashMap<String, String>();

        //Локальная переменная подключенности к зеаку
        private Boolean connected;

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


        //Определение закрытой локальной переменной активитии
        private MainActivity mainActivity;

        // конструктор класса
        public AuthTask(MainActivity mainActivity,
                        Map<String, String> cooca,
                        Boolean connected,
                        Boolean debug,
                        String loginURL,
                        String itemURL,
                        String useragent,
                        String login,
                        String pass) {

            this.mainActivity = mainActivity;
            this.connected = connected;
            this.debug = debug;
            this.loginURL = "http://" + loginURL;
            this.itemURL = "http://" + itemURL;
            this.useragent = useragent;
            this.login = login;
            this.pass = pass;
            this.cooca = cooca;

        }

        //
        //функция подключения к сайту, и возвращению куков в глобальную переменную
        //
        private void connectzeak() {

            String token = "";
            String key;
            String val;

            try {
                Log.d("connectzeak:", "Start");
                connected = Boolean.FALSE;
                Log.d("connec:cooca.isEmpty:", "True");

                //получаем страницу входа
                Connection connection1 = HttpConnection.connect(loginURL)
                        .ignoreHttpErrors(true)
                        .userAgent(useragent);
                Log.d("connectzeak:2:", "Zeak2");
                Connection.Response response1 = connection1.execute();

                if (debug) {
                    Log.d("connectzeak:3:", response1.body());
                }


                //находим csrf-токен (token) для последующиго POST запроса
                for (Map.Entry<String, String> cookie : response1.cookies().entrySet()) {
                    key = cookie.getKey();
                    val = cookie.getValue();
                    Log.d("connectzeak.cookie1", key + " : " + val);
                    if (key.equals("csrftoken")) {
                        token = val;
                        Log.d("connectzeak.token", key + " : " + val);
                    }
                }

                //Пробегаемся по хедерам и выводим их в дебаг
                if (debug) {
                    for (Map.Entry<String, String> head : response1.headers().entrySet()) {
                        Log.d("headers1", head.getKey() + " : " + head.getValue());
                    }
                    Log.d("connectzeak.headers1", " .... ");
                }


                //делаем пост запрос для подключения к зеаку
                Connection connection2 = connection1.url(loginURL)
                        // .timeout(7000)
                        .cookies(response1.cookies())
                        .ignoreHttpErrors(true)
                        .data("username", login)
                        .data("password", pass)
                        .data("csrfmiddlewaretoken", token)
                        .data("next", itemURL)
                        .method(Connection.Method.POST)
                        .followRedirects(true);
                Connection.Response response2 = connection2.ignoreContentType(true).execute();
                Log.d("connectzeak", "add cookies in variables");
                cooca.putAll(response2.cookies());
                Log.d("connectzeak", "done add cookies in variables");
                connected = Boolean.TRUE;
                Log.d("connectzeak", "connected zeak true!");
                Log.d("connectzeak:", "END");
            } catch (Exception e) {
                e.printStackTrace();
                connected = Boolean.FALSE;
            }

        }



        //
        // Получаем данные с зеака в документ (дозможно тип нужно сразу джисоновский сделать и передавать в майнактивити)
        //
        private Document getZeakdata(String getURL) {

            Document dd = null;

            try {
                Connection connection3 = HttpConnection.connect(getURL)
                        .cookies(cooca)
                        .ignoreHttpErrors(true)
                        .ignoreContentType(true)
                        .userAgent(useragent);
                Log.d("getZeakdata:", "Start");
                Connection.Response response3 = connection3.execute();

                // Получили json из сервиса
                if (debug) {
                    Log.d("getZeakdata:body:", response3.body());
                }


                if (debug) {
                    for (Map.Entry<String, String> cookie : response3.cookies().entrySet()) {
                        Log.d("cookie2", cookie.getKey() + " : " + cookie.getValue());

                    }
                    for (Map.Entry<String, String> head : response3.headers().entrySet()) {
                        Log.d("headers2", head.getKey() + " : " + head.getValue());
                    }
                }


                dd = response3.parse();
                Log.d("getZeakdata:", "END!");

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



            Log.d("doInBackground:", "Start");

            //Проверка и подключение к Зеаку
            //cooca.isEmpty() ||
            if (connected == Boolean.FALSE ) {
                connectzeak();
            }

            //Получить данные из сервиса
            if (connected == Boolean.TRUE ) {
                doc = getZeakdata(itemURL);
            }

            Log.d("doInBackground:", "END!");


            return null;


        }




    @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.d("PostExecute", "Start");


            // присваиваем переменные после выполнения действий по подключению в бекграунде


            mainActivity.cooca.putAll(this.cooca);
            mainActivity.connected=this.connected;

            Log.d("PostExecute", "END!");
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //activity.tv.setText("i = " + values[0]);
        }


    }
}


