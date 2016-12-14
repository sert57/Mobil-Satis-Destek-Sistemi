package saha.operasyon;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SP
{
    SharedPreferences sp;
    SharedPreferences.Editor edit;

    public SP(Context c)
    {
        sp = PreferenceManager.getDefaultSharedPreferences(c);
        edit = sp.edit();
    }

    public void setFCM(String t) { edit.putString("fcm", t).commit();}

    public String getFCM() { return sp.getString("fcm","");}

    public void logout()
    {
        edit.clear().commit();
    }

    public void setLoggedIn(boolean b)
    {
        edit.putBoolean("logged_in",b).commit();
    }

    public boolean isLoggedIn()
    {
        return sp.getBoolean("logged_in", false);
    }

    public void setCredentials(String ad, int id)
    {
        edit.putInt("id",id);
        edit.putString("ad",ad);
        edit.commit();
    }

    public int getId() { return sp.getInt("id",0);}
    public String getAd() { return sp.getString("ad","");}

}
