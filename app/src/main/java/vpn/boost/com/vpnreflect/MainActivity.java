package vpn.boost.com.vpnreflect;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Object keyStore = new Object();
        //VpnSetter.listProfiles(this, keyStore);
        Map<String, Object> fields = new HashMap<>();
        fields.put("name", "MySecond");        // 0
        fields.put("type", 1);   // 1
        fields.put("server", "162.32.62.10");        // 2
        fields.put("username", "test1");
        fields.put("password", "qwerty");
        fields.put("dnsServers", " ");
        fields.put("searchDomains", " ");
        fields.put("routes", " ");
        fields.put("mppe", false);
        fields.put("l2tpSecret", " ");
        fields.put("ipsecIdentifier", " ");
        fields.put("ipsecSecret", "vpn");
        fields.put("ipsecUserCert", " ");
        fields.put("ipsecCaCert", " ");
        fields.put("saveLogin", true);

        try {
            VpnSetter.addVpnProfile(this, "qwerty", fields, keyStore);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }
}