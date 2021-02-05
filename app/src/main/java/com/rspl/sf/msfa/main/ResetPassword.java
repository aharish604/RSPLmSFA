package com.rspl.sf.msfa.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebView;

import com.arteriatech.mutils.common.UtilConstants;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.ConstantsUtils;
import com.rspl.sf.msfa.interfaces.CustomDialogCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by e10526 on 26-09-2018.
 */

public class ResetPassword extends AppCompatActivity {
    private WebView webView;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;

    /*http url connection*/
    public static String getPuserIdUtilsReponse(URL url, String userName, String psw) throws IOException {
        InputStream stream = null;
        HttpsURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpsURLConnection) url.openConnection();
            // Timeout for reading InputStream arbitrarily set to 3000ms.
            connection.setReadTimeout(1000 * 30);
            // Timeout for connection.connect() arbitrarily set to 3000ms.
            connection.setConnectTimeout(1000 * 30);
            String userCredentials = userName + ":" + psw;
            String basicAuth = "Basic " + Base64.encodeToString(userCredentials.getBytes("UTF-8"), Base64.NO_WRAP);
            connection.setRequestProperty("Authorization", basicAuth);
            connection.setRequestProperty("Content-Type", "application/scim+json");
            // For this use case, set HTTP method to GET.
            connection.setRequestMethod("GET");
            // Already true by default but setting just in case; needs to be true since this request
            // is carrying an input (response) body.
            connection.setDoInput(true);
            // Open communications link (network traffic occurs here).
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            // Retrieve the response body as an InputStream.
            stream = connection.getInputStream();
            if (stream != null) {
                // Converts Stream to String with max length of 500.
                result = readResponse(stream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    public static String getPswResetUtilsReponse(URL url, String userName, String psw, String body) throws IOException {
        InputStream stream = null;
        HttpsURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpsURLConnection) url.openConnection();
            // Timeout for reading InputStream arbitrarily set to 3000ms.
            connection.setReadTimeout(1000 * 30);
            // Timeout for connection.connect() arbitrarily set to 3000ms.
            connection.setConnectTimeout(1000 * 30);
            String userCredentials = userName + ":" + psw;
            String basicAuth = "Basic " + Base64.encodeToString(userCredentials.getBytes("UTF-8"), Base64.NO_WRAP);
            connection.setRequestProperty("Authorization", basicAuth);
            connection.setRequestProperty("Content-Type", "application/scim+json");
            // For this use case, set HTTP method to GET.
            connection.setRequestMethod("PUT");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
            os.write(body.getBytes("UTF-8"));
            os.close();
            // Already true by default but setting just in case; needs to be true since this request
            // is carrying an input (response) body.

            // Open communications link (network traffic occurs here).
//            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            // Retrieve the response body as an InputStream.
            stream = connection.getInputStream();
            if (stream != null) {
                // Converts Stream to String with max length of 500.
                result = readResponse(stream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    /**
     * Converts the contents of an InputStream to a String.
     */
    private static String readResponse(InputStream stream)
            throws IOException {
        BufferedReader reader = null;
        reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        StringBuilder buffer = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            buffer.append(line).append('\n');
        }
        return buffer.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd);
        toolbar = (Toolbar) findViewById(R.id.toolbar);



       /* ConstantsUtils.initActionBarView(this, toolbar, true, getString(R.string.title_reset_pwd), 0);
        String loadUrl = "https://awfpkkad0-nonprod.accounts.ondemand.com/ui/protected/profilemanagement";
        progressDialog = ConstantsUtils.showProgressDialog(ResetPassword.this);
        if (!loadUrl.equals("")) {
            webView.loadUrl(loadUrl);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                   *//* SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(Constants.isForgetPasswordCheck, true);
                    editor.apply();*//*
                    return true;
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {

                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    try {
                        progressDialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }*/
        String url = "https://awfpkkad0-nonprod.accounts.ondemand.com";

//        String url = "https://awfpkkad0.accounts.ondemand.com";
        String tUserName = "T000000";
//        String tPsw = "ArtSCI@awfpkkad0";
        String tPsw = "RSPLSystem@NonProd";
//        String puserID = "P000240";
        String puserID = "P000017";
//        String password = "Welcome@2018";
        String password = "Rspl@123";

        onAlertDialogForPassword();
//        extendPassword2(ResetPassword.this, url, tUserName, tPsw, puserID, password);

    }

    private void extendPassword(final Context mContext, final String url, final String tUserName, final String tPsw, final String pUserID, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                String url = "https://awfpkkad0-nonprod.accounts.ondemand.com/service/scim/Users/p000017";
                String puserID = pUserID;
                try {
                    String jsonValue = getPuserIdUtilsReponse(new URL(url), tUserName, tPsw);
                    if (!TextUtils.isEmpty(jsonValue)) {
                        JSONObject jsonObject = new JSONObject(jsonValue);
                        JSONArray jsonArray = jsonObject.optJSONArray("Resources");
                        if (jsonArray != null && jsonArray.length() > 0) {
                            puserID = jsonArray.getJSONObject(0).getString("id");
                        }
                        if (!TextUtils.isEmpty(puserID)) {
                            String url1 = "https://awfpkkad0-nonprod.accounts.ondemand.com/service/scim/Users/" + puserID;
                            String validatePuser = getPuserIdUtilsReponse(new URL(url1), tUserName, tPsw);
                            if (!TextUtils.isEmpty(validatePuser)) {
                                JSONObject userObject = new JSONObject(validatePuser);
                                String userStatus = userObject.optString("passwordStatus");
                                JSONObject metaObject = userObject.getJSONObject("meta");
                                JSONArray schemasArray = userObject.optJSONArray("schemas");
                                Log.d("log", "run: " + schemasArray);
                                JSONObject bodyObject = new JSONObject();
                                bodyObject.put("id", pUserID);
                                bodyObject.put("password", password);
                                bodyObject.put("passwordStatus", "enabled");
                                bodyObject.put("meta", metaObject);
                                bodyObject.put("schemas", schemasArray);
                                String changePassword = getPswResetUtilsReponse(new URL(url1), tUserName, tPsw, bodyObject.toString());
                                if (!TextUtils.isEmpty(changePassword)) {
                                    JSONObject userPObject = new JSONObject(changePassword);
                                    String userPStatus = userPObject.optString("passwordStatus");
                                    if (!TextUtils.isEmpty(userPStatus) && userPStatus.equalsIgnoreCase("enabled")) {
                                        displayErrorMessage("Extended success", mContext);
                                    } else {
                                        displayErrorMessage("Not valid user status " + userPStatus, mContext);
                                    }
                                } else {
                                    displayErrorMessage("Not valid user", mContext);
                                }

                            } else {
                                displayErrorMessage("Not valid user", mContext);
                            }
                        } else {
                            displayErrorMessage("Not valid user", mContext);
                        }
                    } else {
//                        displayErrorMessage(mContext.getString(R.string.chei), mContext);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void displayErrorMessage(final String s, final Context mContext) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UtilConstants.displayShortToast(mContext, s);
            }
        });
    }


    private void extendPassword2(final Context mContext, final String domineUrl, final String tUserName, final String tPsw, final String pUserID, final String password) {
//        pdLoadDialog = new ProgressDialog(mContext, com.arteriatech.mutils.R.style.UtilsDialogTheme);
//        this.pdLoadDialog.setMessage(this.getString(com.arteriatech.mutils.R.string.extend_pwd_please_wait));
//        this.pdLoadDialog.setCancelable(false);
//        this.pdLoadDialog.show();
        (new Thread(new Runnable() {
            public void run() {
                String url = domineUrl + "/service/scim/Users?filter=userName%20eq%20'" + pUserID + "'";
                String puserID = pUserID;

                try {
                    String jsonValue = UtilConstants.getPuserIdUtilsReponse(new URL(url), tUserName, tPsw);
                    if (!TextUtils.isEmpty(jsonValue)) {
                        JSONObject jsonObject = new JSONObject(jsonValue);
                        JSONArray jsonArray = jsonObject.optJSONArray("Resources");
                        if (jsonArray != null && jsonArray.length() > 0) {
                            puserID = jsonArray.getJSONObject(0).getString("id");
                        }

                        if (!TextUtils.isEmpty(puserID)) {
                            String url1 = domineUrl + "/service/scim/Users/" + puserID;
                            String validatePuser = UtilConstants.getPuserIdUtilsReponse(new URL(url1), tUserName, tPsw);
                            if (!TextUtils.isEmpty(validatePuser)) {
                                JSONObject userObject = new JSONObject(validatePuser);
                                String userStatus = userObject.optString("passwordStatus");
                                JSONObject metaObject = userObject.getJSONObject("meta");
                                JSONArray schemasArray = userObject.optJSONArray("schemas");
                                JSONObject bodyObject = new JSONObject();
                                bodyObject.put("id", puserID);
                                bodyObject.put("password", password);
                                bodyObject.put("passwordStatus", "enabled");
                                bodyObject.put("meta", metaObject);
                                bodyObject.put("schemas", schemasArray);
                                String changePassword = UtilConstants.getPswResetUtilsReponse(new URL(url1), tUserName, tPsw, bodyObject.toString());
                                if (!TextUtils.isEmpty(changePassword)) {
                                    JSONObject userPObject = new JSONObject(changePassword);
                                    String userPStatus = userPObject.optString("passwordStatus");
                                    if (!TextUtils.isEmpty(userPStatus) && userPStatus.equalsIgnoreCase("enabled")) {
                                        displayErrorMessage(mContext.getString(R.string.extend_pwd_finish_success), mContext);
                                    } else {
                                        displayErrorMessage(mContext.getString(R.string.extend_pwd_error_occured) + " " + userPStatus, mContext);
                                    }
                                } else {
                                    displayErrorMessage(mContext.getString(R.string.extend_pwd_error_occured), mContext);
                                }
                            } else {
                                displayErrorMessage(mContext.getString(R.string.extend_pwd_error_occured), mContext);
                            }
                        } else {
                            displayErrorMessage(mContext.getString(R.string.extend_pwd_error_occured), mContext);
                        }
                    } else {
                        displayErrorMessage(mContext.getString(R.string.no_network_conn), mContext);
                    }
                } catch (IOException var16) {
                    var16.printStackTrace();
                    displayErrorMessage(var16.getMessage(), mContext);
                } catch (JSONException var17) {
                    var17.printStackTrace();
                    displayErrorMessage(var17.getMessage(), mContext);
                }

            }
        })).start();
    }


    String mStrNewPwd = "";
    private void onAlertDialogForPassword() {
        ConstantsUtils.showPasswordRemarksDialog(ResetPassword.this, new CustomDialogCallBack() {
            @Override
            public void cancelDialogCallBack(boolean userClicked, String ids, String description) {
                mStrNewPwd = description;
                if (userClicked) {

                }
            }
        }, getString(R.string.alert_plz_enter_password));




    }

}
