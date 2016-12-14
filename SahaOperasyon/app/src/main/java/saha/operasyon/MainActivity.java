package saha.operasyon;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;
import org.jsoup.Jsoup;

public class MainActivity extends AppCompatActivity {

    EditText etUn, etPw;
    String un, pw;
    SP sp;
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = new SP(this);

        if (sp.isLoggedIn())
        {
            startActivity(new Intent(this, Home.class));
            finish();
        }
        etUn = (EditText) findViewById(R.id.etUn);
        etPw = (EditText) findViewById(R.id.etPw);
    }

    public void doLogin(View v)
    {
        un = ""+etUn.getText();
        pw = ""+etPw.getText();

        if (un.isEmpty() || pw.isEmpty())
            return;


        new AsyncTask<String,String,String>()
        {
            ProgressDialog pd;
            int res;
            int id;
            String ad;

            @Override
            protected void onPreExecute() {
                pd = new ProgressDialog(MainActivity.this);
                pd.setMessage("Giriş Yapılıyor");
                pd.setCancelable(false);
                pd.show();
            }

            protected String doInBackground(String... strings)
            {
                try
                {
                    String jsonStr = Jsoup.connect("http://192.168.68.50:81/SahaAdmin/ws.php")
                            .ignoreContentType(true)
                            .timeout(30000)
                            .data("op","login")
                            .data("un",un)
                            .data("pw",pw)
                            .data("fcm", FirebaseInstanceId.getInstance().getToken())
                            .get()
                            .text();

                    JSONObject sonuc = new JSONObject(jsonStr);
                    res = sonuc.getInt("res");
                    if (res == 1)
                    {
                        sp.setLoggedIn(true);
                        id = sonuc.getInt("id");
                        ad = sonuc.getString("ad");
                        sp.setCredentials(ad, id);
                    }
                    else
                    {
                        sp.setLoggedIn(false);
                    }
                    Log.e("x","Response : "+sonuc.toString());
                } catch (Exception e) { }
                return null;
            }

            protected void onPostExecute(String s)
            {


                pd.dismiss();

                if (res == 0)
                {
                    Toast.makeText(MainActivity.this, "Bilgiler Hatalı", Toast.LENGTH_SHORT).show();
                }
                if (res == 1)
                {
                    Toast.makeText(MainActivity.this,"Merhaba "+ad, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, Home.class));
                }
            }
        }.execute();
    }
}
