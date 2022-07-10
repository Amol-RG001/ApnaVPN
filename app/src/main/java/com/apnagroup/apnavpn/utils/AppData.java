package com.apnagroup.apnavpn.utils;

/**
 * Created by appteve on 30/01/2017.
 */

public interface AppData {


    String GOOGLE_BILLING_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgWbP23FERESA0iVoH/Ws8T/tDtWrsXGu/mCAcsuJRfNblrmquOILyQaN3RtS9xLvez4lo6JbOqU4QJD65H+jw4Kn51am3uDAZh+mRAKls+UEjbvVtz5v9T4OQsK/L+Ht1qgepgTqtbOzIJOG2TcYgjZRtZKSpJFETCErKm+P5+c1F/mCY1NdLuaKh5qSYt2Rp0A95HjKGjUQIYGv/n+RRmpUBj2WvngjCkSVyj+V1VUwappNnV1Wn0s3ld4fUfP5TBkrhZTXefHlFdgVeU/nQ2naEnHE99+ewP9O5buC9TjVij2EWbv4s2xYq+2jbi76NZI0leKzQuB2Nc10WK64xwIDAQAB";

    String TEST_PURCHASE_SKU = "android.test.purchased";

    String SUBS_LIFETIME = "com.tigadev.vpnx.ad"; // lifetime
    String SUBS_1_MONTH = "com.tigadev.vpnx.monthly"; // monthly
    String SUBS_12_MONTH = "com.tigadev.vpnx.yearly"; // yearly

    String MERCHANT_ID = null;

    String APP_PREFERENCES = "UserDataApp";

    String IAP_LIFETIME = "iaplifetime";
    String IAP_MONTHLY = "iapmonthly";
    String IAP_YEARLY = "iapyearly";

    String IS_VIP = "isvip";
    String IS_SUBS = "issub";
    String DATE_PURCHASE = "datepurchase";
    String DATE_END = "dateend";
    String DATE_LIFETIME = "datelifetime";
}
