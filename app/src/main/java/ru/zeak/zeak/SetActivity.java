package ru.zeak.zeak;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by zopyx on 18.09.2016.
 */
public class SetActivity extends Activity implements OnClickListener {
    SharedPreferences sPref;
    TextView tvLogin;
    TextView tvPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(MainActivity.TAG, "onCreate SetActivity");
        setContentView(R.layout.settings);

        Log.d(MainActivity.TAG, "onCreate SetActivity1");
        tvLogin = (TextView) findViewById(R.id.etLoginSettings);
        tvPassword = (TextView) findViewById(R.id.etPasswordSettings);


        sPref = getSharedPreferences("ZeakPref", MODE_PRIVATE);


        tvLogin.setText(sPref.getString(MainActivity.LOGIN_SAVED_TEXT, ""));
        tvPassword.setText(sPref.getString(MainActivity.PASSWORD_SAVED_TEXT, ""));

        Button btnLoad = (Button) findViewById(R.id.btnLoadSettings);
        Button btnSave = (Button) findViewById(R.id.btnSaveSettings);
        btnLoad.setOnClickListener(this);
        btnSave.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {


        Log.d(MainActivity.TAG, "по id определяем кнопку, вызвавшую этот обработчик");

        switch (v.getId()) {
            case R.id.btnLoadSettings:
                // кнопка Load
                Log.d(MainActivity.TAG, "кнопка Load");
                sPref = getSharedPreferences("ZeakPref", MODE_PRIVATE);
                tvLogin.setText(sPref.getString(MainActivity.LOGIN_SAVED_TEXT, ""));
                tvPassword.setText(sPref.getString(MainActivity.PASSWORD_SAVED_TEXT, ""));
                Toast.makeText(this, "Подключение к сайту", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnSaveSettings:
                // кнопка Save
                Log.d(MainActivity.TAG, "кнопка Save");
                sPref = getSharedPreferences("ZeakPref",MODE_PRIVATE);
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString(MainActivity.LOGIN_SAVED_TEXT, tvLogin.getText().toString());
                ed.putString(MainActivity.PASSWORD_SAVED_TEXT, tvPassword.getText().toString());
                ed.commit();
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();

                break;

        }

    }
}