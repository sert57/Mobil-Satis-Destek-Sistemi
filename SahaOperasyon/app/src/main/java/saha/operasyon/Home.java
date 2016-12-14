package saha.operasyon;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;

public class Home extends AppCompatActivity {

    SP sp;
    JSONArray dS=  new JSONArray();
    ListView lv;
    BaseAdapter ba;
    LayoutInflater li;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sp = new SP(this);


        li = LayoutInflater.from(this);
        lv = (ListView) findViewById(R.id.lv);

        ba = new BaseAdapter() {
            @Override
            public int getCount() {
                return dS.length();
            }

            @Override
            public Object getItem(int i) {
                return null;
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup)
            {
                if (view == null)
                {
                    view = li.inflate(android.R.layout.simple_list_item_1, null);
                }
                TextView tv = (TextView) view.findViewById(android.R.id.text1);
                try
                {
                    String ad = dS.getJSONObject(i).getString("ad");
                    tv.setText(ad);
                } catch (Exception e) { }
                return view;
            }
        };

        lv.setAdapter(ba);

        // Müşteri Ziyareti Başlatma
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                try
                {
                    String json = dS.getJSONObject(i).toString();
                    Intent intent = new Intent(Home.this, MusteriZiyareti.class);
                    intent.putExtra("json_data", json);
                    startActivity(intent);

                } catch (Exception e) { }
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                try
                {
                    JSONObject secilenMusteri = dS.getJSONObject(i);
                    double mustEnlem = secilenMusteri.getDouble("enlem");
                    double mustBoylam = secilenMusteri.getDouble("boylam");

                    Intent intent =
                            new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("google.navigation:q="+mustEnlem+","+mustBoylam));

                    startActivity(intent);
                } catch (Exception e) { }
                return false;
            }
        });

        ActionBar ab = getSupportActionBar();
        ab.setTitle(sp.getAd());
        ab.setSubtitle("Personel Id : "+sp.getId());
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        getRotalar();
    }

    public void getRotalar()
    {
        //dS = null;

        new AsyncTask<String, String, String>()
        {
            ProgressDialog pd;

            protected void onPreExecute()
            {
                pd = new ProgressDialog(Home.this);
                pd.setMessage("Rota Bilgisi Alınıyor");
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
                            .data("op", "get_gunluk_rota")
                            .data("id", sp.getId()+"")
                            .get()
                            .text();

                    dS = new JSONArray(jsonStr);
                    return "ok";
                } catch (Exception e) { return e.toString(); }

            }

            protected void onPostExecute(String s)
            {
                pd.dismiss();

                if (!s.equals("ok"))
                {
                    AlertDialog.Builder adb = new AlertDialog.Builder(Home.this);
                    adb.setTitle("Rota Hatası");
                    adb.setMessage("Rota Bilgisi Alınamadı. Detay : "+s);
                    adb.setPositiveButton("Tamam", null);
                    adb.show();
                }
                else
                {
                    ba.notifyDataSetChanged();
                }
            }
        }.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Exit").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add("Yenile").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        String ad = ""+item.getTitle();

        if (ad.equals("Yenile"))
        {
            getRotalar();
        }
        if (ad.equals("Exit"))
        {
            sp.logout();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
