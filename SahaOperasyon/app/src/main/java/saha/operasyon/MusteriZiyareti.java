package saha.operasyon;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;

public class MusteriZiyareti extends AppCompatActivity
{
    JSONArray dS = new JSONArray();
    LayoutInflater li;
    BaseAdapter baUrunler = null;
    BaseAdapter baSepet = null;

    ListView lvUrunler = null, lvSepet = null;

    ArrayList<ContentValues> sepet = new ArrayList<>();

    Dialog miktarDialog = null;
    JSONObject secilenMusteri = null;
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musteri_ziyareti);

        String data = getIntent().getExtras().getString("json_data");
        try
        {
            secilenMusteri = new JSONObject(data);
            String ad = secilenMusteri.getString("ad");
            getSupportActionBar().setTitle("Müşteri Ziyareti");
            getSupportActionBar().setSubtitle(ad);
        } catch (Exception e) { Log.e("x","JSON EX : "+e.toString()); }


        li = LayoutInflater.from(this);

        lvSepet = (ListView) findViewById(R.id.lv_sepet);
        baSepet = new BaseAdapter() {
            @Override
            public int getCount()
            {
                return sepet.size();
            }

            @Override
            public Object getItem(int i) {
                return null;
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            public View getView(int i, View view, ViewGroup viewGroup)
            {
                if (view == null)
                    view = li.inflate(R.layout.urun_item, null);

                ImageView iv = (ImageView) view.findViewById(R.id.iv_urun);
                TextView tvAd = (TextView) view.findViewById(R.id.tv_urun_adi);
                TextView tvFiyat = (TextView) view.findViewById(R.id.tv_urun_fiyati);

                ContentValues cv = sepet.get(i);
                tvAd.setText(cv.getAsString("ad"));
                double fiyat = cv.getAsDouble("fiyat");

                double toplam = cv.getAsInteger("miktar") * fiyat;

                tvFiyat.setText(cv.getAsString("miktar")+" Adet - "+fiyat+" ₺ ["+toplam+" ₺]");
                String resimURL = cv.getAsString("resim");

                Picasso.with(MusteriZiyareti.this)
                        .load(resimURL)
                        .placeholder(R.drawable.box)
                        .error(R.drawable.error)
                        .into(iv);
                return view;
            }
        };

        lvSepet.setAdapter(baSepet);

        lvSepet.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                ContentValues cv = sepet.get(i);
                sepet.remove(cv);
                baSepet.notifyDataSetChanged();
                return false;
            }
        });


        lvUrunler = (ListView) findViewById(R.id.lv_urunler);
        baUrunler = new BaseAdapter()
        {
            public int getCount()
            {
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
                    view = li.inflate(R.layout.urun_item, null);

                ImageView iv = (ImageView) view.findViewById(R.id.iv_urun);
                TextView tvAd = (TextView) view.findViewById(R.id.tv_urun_adi);
                TextView tvFiyat = (TextView) view.findViewById(R.id.tv_urun_fiyati);

                try
                {
                    JSONObject urun = dS.getJSONObject(i);
                    tvAd.setText(urun.getString("ad"));
                    tvFiyat.setText(urun.getString("fiyat")+" ₺");
                    String resimURL = urun.getString("resim");
                    Picasso.with(MusteriZiyareti.this)
                            .load(resimURL)
                            .placeholder(R.drawable.box)
                            .error(R.drawable.error)
                            .into(iv);
                } catch (Exception e){}


                return view;
            }
        };



        lvUrunler.setAdapter(baUrunler);

        lvUrunler.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                try
                {
                    final JSONObject jo = dS.getJSONObject(i);
                    View v = li.inflate(R.layout.miktar_secimi,null);

                    AlertDialog.Builder adb = new AlertDialog.Builder(MusteriZiyareti.this);
                    adb.setView(v);

                    final EditText etMiktar = (EditText) v.findViewById(R.id.et_miktar);
                    Button btnOk = (Button) v.findViewById(R.id.btn_miktar_ok);

                    btnOk.setOnClickListener(new View.OnClickListener()
                    {
                        public void onClick(View view)
                        {
                            String str = ""+etMiktar.getText();
                            if (str.isEmpty())
                            {
                                Toast.makeText(MusteriZiyareti.this,"Ürün Eklemek İçin, Miktar Girmek Zorundasınız", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                int miktar = new Integer(str);
                                ContentValues cv = new ContentValues();
                                try
                                {
                                    cv.put("miktar", miktar);
                                    cv.put("fiyat", jo.getString("fiyat"));
                                    cv.put("id", jo.getString("id"));
                                    cv.put("ad", jo.getString("ad"));
                                    cv.put("resim", jo.getString("resim"));

                                    sepet.add(cv);

                                    baSepet.notifyDataSetChanged();
                                } catch (Exception e) { }
                            }
                            miktarDialog.dismiss();
                        }
                    });

                    miktarDialog = adb.create();
                    miktarDialog.show();

                } catch (Exception e) {}
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUrunler();
    }

    public void getUrunler()
    {
        //dS = null;

        new AsyncTask<String, String, String>()
        {
            ProgressDialog pd;

            protected void onPreExecute()
            {
                pd = new ProgressDialog(MusteriZiyareti.this);
                pd.setMessage("Ürün Listesi Alınıyor");
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
                            .data("op", "get_urunler")
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
                    AlertDialog.Builder adb = new AlertDialog.Builder(MusteriZiyareti.this);
                    adb.setTitle("Ürün Listesi Hatası");
                    adb.setMessage("Ürün Listesi Alınamadı. Detay : "+s);
                    adb.setPositiveButton("Tamam", null);
                    adb.show();
                }
                else
                {
                    baUrunler.notifyDataSetChanged();
                }
            }
        }.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add("Tamamla").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        String ad = ""+item.getTitle();
        if (ad.equals("Tamamla"))
        {
            siparisiTamamla();
        }
        return super.onOptionsItemSelected(item);
    }


    void siparisiTamamla()
    {
        try
        {
            SP sp = new SP(this);
            final JSONObject siparis = new JSONObject();
            siparis.put("user_id", sp.getId());
            siparis.put("musteri_id", secilenMusteri.getString("musteri_id"));

            double toplamFiyat = 0;
            JSONArray urunler = new JSONArray();
            int cnt = 0;
            for (ContentValues urun : sepet)
            {
                int miktar = urun.getAsInteger("miktar");
                double fiyat = urun.getAsDouble("fiyat");
                double araToplam = fiyat * miktar;
                toplamFiyat += araToplam;

                JSONObject urunObject = new JSONObject();
                urunObject.put("urun_id", urun.getAsString("id"));
                urunObject.put("miktar", miktar);
                urunObject.put("ara_toplam", araToplam);

                urunler.put(cnt, urunObject);
                cnt++;
            }
            siparis.put("toplam_fiyat", toplamFiyat);
            siparis.put("urunler", urunler);

            Log.e("x","SİPARİS DATA : ");
            Log.e("x",siparis.toString());

            new AsyncTask<String,String, String>()
            {
                ProgressDialog pd = new ProgressDialog(MusteriZiyareti.this);

                @Override
                protected void onPreExecute() {
                    pd.setMessage("Sipariş Gönderiliyor");
                    pd.setCancelable(false);
                    pd.show();
                }

                @Override
                protected String doInBackground(String... strings) {
                    try
                    {
                        String responseStr = Jsoup.connect("http://192.168.68.50:81/SahaAdmin/ws.php")
                                .ignoreContentType(true)
                                .timeout(30000)
                                .data("op", "add_siparis")
                                .data("payload", siparis.toString())
                                .get()
                                .text();

                        Log.e("x","Response : "+responseStr);
                        return "ok";
                    } catch (Exception e) { return e.toString(); }
                }

                @Override
                protected void onPostExecute(String s) {

                    pd.dismiss();
                    AlertDialog.Builder adb = new AlertDialog.Builder(MusteriZiyareti.this);
                    String mesaj = "";
                    if (s.equals("ok"))     mesaj = "Sipariş Gönderildi";
                    else mesaj = "Hata. Detay : "+s;

                    adb.setTitle("Sonuç");
                    adb.setMessage(mesaj);
                    adb.setPositiveButton("Tamam", null);
                    adb.show();
                }
            }.execute();


        } catch (Exception e){ Log.e("x","SİPARİS EX : "+e.toString());}
    }
}
