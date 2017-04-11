package vpn.boost.com.vpnreflect;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by root on 09.04.17.
 */

public class VpnSetter {
    private static final String TAG = "VpnSetter";
    private static final String VPN = "VPN_";
    public static final Set<String> VPN_PROFILE_KEYS = getMappedFields().keySet(); // contains keys for quicker generation of key-value map for each


    private static Map<String, Class<?>> getMappedFields() {
        Map<String, Class<?>> fieldsAndTypes = new HashMap<String, Class<?>>();
        fieldsAndTypes.put("name", String.class);        // 0
        fieldsAndTypes.put("type", int.class);   // 1
        fieldsAndTypes.put("server", String.class);        // 2
        fieldsAndTypes.put("username", String.class);
        fieldsAndTypes.put("password", String.class);
        fieldsAndTypes.put("dnsServers", String.class);
        fieldsAndTypes.put("searchDomains", String.class);
        fieldsAndTypes.put("routes", String.class);
        fieldsAndTypes.put("mppe", boolean.class);
        fieldsAndTypes.put("l2tpSecret", String.class);
        fieldsAndTypes.put("ipsecIdentifier", String.class);
        fieldsAndTypes.put("ipsecSecret", String.class);
        fieldsAndTypes.put("ipsecUserCert", String.class);
        fieldsAndTypes.put("ipsecCaCert", String.class);
        fieldsAndTypes.put("saveLogin", boolean.class);
        return fieldsAndTypes;
    }

    public static void listProfiles(Context context, Object keyStore) {
        Class<?> keyStoreClass = null;
        try {
            keyStoreClass = Class.forName("android.security.KeyStore");

            Method getInstanceMethod = keyStoreClass.getDeclaredMethod("getInstance");
            getInstanceMethod.setAccessible(true);
            keyStore = getInstanceMethod.invoke(null);
            Method getIsUnlockedMethod = keyStore.getClass().getDeclaredMethod("isUnlocked");
            boolean isUnlocked = (boolean) getIsUnlockedMethod.invoke(keyStore);


            if (!isUnlocked) return;
            Log.d(TAG, "listProfiles: class " + keyStore.getClass());
            Class[] decodeparameterTypes = new Class[]{String.class, byte[].class};
            List<?> profiles = new ArrayList<>();
            try {
                Method methodList = keyStore.getClass().getDeclaredMethod("saw", String.class);
                methodList.setAccessible(true);
                String[] keys = (String[]) methodList.invoke(keyStore, VPN);

                Method getMethod = keyStore.getClass().getDeclaredMethod("get", String.class);
                getMethod.setAccessible(true);

                Class<?> vpnProfileClass = Class.forName("com.android.internal.net.VpnProfile");
                Method decodeMethod = vpnProfileClass.getDeclaredMethod("decode", decodeparameterTypes);
                decodeMethod.setAccessible(true);

                if (keys != null) {
                    for (String key : keys) {
                        Object vpnProfileObject = decodeMethod.invoke(new Object[]{
                                VPN,
                                getMethod.invoke(keyStore, VPN + key)
                        });
                        Log.d(TAG, "listProfiles: " + vpnProfileObject);
                    }
                }

//            Context vpnset = context.createPackageContext("com.android.settings", Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
//            Class<?> vpnSettings = Class.forName("com.android.settings.vpn2.VpnSettings", true, vpnset.getClassLoader());
//
//            Method method = vpnSettings.getDeclaredMethod("loadVpnProfiles", profileparameterTypes);
//            method.setAccessible(true);
//            profiles = (List<?>) method.invoke(null, keyStore, new int[]{});
//            Log.d(TAG, "listProfiles: " + profiles);

            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }  catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "listProfiles: ");
    }

    public static void addVpnProfile(Context context, String vpnProfileKey, Map<String, Object> values, Object keyStore) throws ClassNotFoundException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException, PackageManager.NameNotFoundException {
        Context applicationContext = context.getApplicationContext();
        String profileKey = Long.toHexString(System.currentTimeMillis());
        Class<?> keyStoreClass = Class.forName("android.security.KeyStore");

        Method getInstanceMethod = keyStoreClass.getDeclaredMethod("getInstance");
        getInstanceMethod.setAccessible(true);
        keyStore = getInstanceMethod.invoke(null);
        Method getIsUnlockedMethod = keyStore.getClass().getDeclaredMethod("isUnlocked");
        boolean isUnlocked = (boolean) getIsUnlockedMethod.invoke(keyStore);

        if (!isUnlocked) return;

        Class<?> vpnProfileClass = Class.forName("com.android.internal.net.VpnProfile");
        Constructor constructor = vpnProfileClass.getConstructor(String.class);
        Object vpnProfile = constructor.newInstance(profileKey);

        Method encodeMethod = vpnProfileClass.getDeclaredMethod("encode");
        encodeMethod.setAccessible(true);
        byte[] profileInBytes = (byte[]) encodeMethod.invoke(vpnProfile);

        Method methodPut = keyStoreClass.getDeclaredMethod("put", String.class, byte[].class, int.class, int.class);
        methodPut.setAccessible(true);



        Map<String, Class<?>> vpnProfileMap = getMappedFields();
        Iterator<String> profileKeysIterator = vpnProfileMap.keySet().iterator();
        while (profileKeysIterator.hasNext()) {
            String key = profileKeysIterator.next();
            Log.d(TAG, "addVpnProfile: key " + key + " val " + values.get(key));
            Field field = vpnProfile.getClass().getDeclaredField(key);
            field.setAccessible(true);
            if (vpnProfileMap.get(key).equals(String.class) && !TextUtils.isEmpty((CharSequence) values.get(key))) {
                field.set(vpnProfile, values.get(key));//change this
            } else if (vpnProfileMap.get(key).equals(int.class) && values.get(key) != null) {
                field.setInt(vpnProfile, (int) values.get(key));// change this
            } else if (vpnProfileMap.get(key).equals(boolean.class) && values.get(key) != null) {
                field.setBoolean(vpnProfile, (boolean) values.get(key));// change this
            }

        }

        methodPut.invoke(keyStore, VPN + profileKey, profileInBytes, -1, 1);

        Context vpnset = applicationContext.createPackageContext("com.android.settings", Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
        Class<?> vpnSettings = Class.forName("com.android.settings.vpn2.VpnSettings", true, vpnset.getClassLoader());


        Class<?>[] privateVpnSettingsClasses = vpnSettings.getDeclaredClasses();
        Class<?> vpnPreference = null;
        for (Class<?> priv : privateVpnSettingsClasses) {
            if (priv.getSimpleName().equals("VpnPreference")) {
                vpnPreference = priv;
                break;
            }
        }

//        Constructor preferenceConstructor = vpnPreference.getConstructor(Context.class, vpnProfileClass);
//        Object vpnPreferences = preferenceConstructor.newInstance(context, vpnProfile);


    }
}
