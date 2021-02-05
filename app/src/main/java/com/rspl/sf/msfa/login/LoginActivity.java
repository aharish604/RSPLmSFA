package com.rspl.sf.msfa.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.arteriatech.mutils.common.UtilConstants;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.registration.UtilRegistrationActivity;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.main.MainMenu;

/**
 * Created by e10526 on 22-07-2016.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText userEdit = null;
    EditText passEdit = null;
    //    CheckBox showPass = null;
    CheckBox savePass = null;
    String appConnID="",userName="",pwd="";
    private boolean isNotification = false;
    Button bt_login = null, bt_login_clear = null;
    private TextView bt_login_forget_pass;

    private int mIntMaxLimit = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialize action bar with back button(true)
//        ActionBarView.initActionBarView(this, true);
        setContentView(R.layout.activity_login);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME,
                0);

        //getting username and password
        getUserNamePwd();
        //initialize UI
        initUI();

        //check for saved password
        checkPwdSaved(settings);

    }

    /*Checks password saved or not*/
    void checkPwdSaved(SharedPreferences settings) {
        if (settings.contains(Constants.isPasswordSaved)) {
            settings.getBoolean(Constants.isPasswordSaved, false);
        } else {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(Constants.isPasswordSaved, false);
            editor.commit();
        }

        boolean mBooleanPwdSaved =settings.getBoolean(Constants.isPasswordSaved, false);
        savePass.setChecked(mBooleanPwdSaved);
        if (mBooleanPwdSaved) {
            passEdit.setText(pwd);
        }
    }

    /*Initializes User interfaces of screen*/
    public void initUI(){
        userEdit = (EditText) findViewById(R.id.et_login_username);
        userEdit.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        passEdit = (EditText) findViewById(R.id.et_login_password);
        userEdit.setText(userName);
        userEdit.setFocusable(true);
        passEdit.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//        showPass = (CheckBox) findViewById(R.id.ch_login_show_pass);
        savePass = (CheckBox) findViewById(R.id.ch_login_save_pass);
//        showPass.setOnClickListener(this);
        savePass.setOnClickListener(this);

        bt_login = (Button) findViewById(R.id.bt_login);

        bt_login.setOnClickListener(this);
        bt_login_clear = (Button) findViewById(R.id.bt_login_clear);

        bt_login_clear.setOnClickListener(this);

//        bt_login_exit = (Button) findViewById(R.id.bt_login_exit);

//        bt_login_exit.setOnClickListener(this);

        bt_login_forget_pass = (TextView) findViewById(R.id.bt_login_forget_pass);

        bt_login_forget_pass.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bt_login:
                onValidation();
                break;
            case R.id.bt_login_clear:
                onClear();
                break;

            case R.id.bt_login_forget_pass:
                onForgetPwd();
                break;


            case R.id.ch_login_save_pass:
                SharedPreferences settings = getSharedPreferences(
                        Constants.PREFS_NAME, 0);
                if (savePass.isChecked()) {
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean(Constants.savePass, true);
                    editor.commit();
                    savePass.setChecked(true);

                } else {
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean(Constants.savePass, false);
                    editor.commit();
                    savePass.setChecked(false);
                }
                break;
        }

    }

    /*Reads username and password from Logon Context*/
    private void getUserNamePwd() {

        try {
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, 0);
            userName = sharedPreferences.getString(UtilRegistrationActivity.KEY_username,"");
            pwd = sharedPreferences.getString(UtilRegistrationActivity.KEY_password,"");
            // get Application Connection ID
           /* LogonCoreContext lgCtx = LogonCore.getInstance().getLogonContext();
            userName = lgCtx.getBackendUser();
            pwd = lgCtx.getBackendPassword();
            appConnID = LogonCore.getInstance().getLogonContext()
                    .getConnId();*/
        } catch (Exception e) {
            LogManager.writeLogError(Constants.device_reg_failed_txt, e);
        }
    }

    /*Alert for forgot password*/
    private void alertForgetPwdMsg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.MyTheme);
        builder.setMessage(R.string.alert_user_is_locked)
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                onForgetPwd();

                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        builder.show();
    }

    /*Validates username and password*/
    private void onValidation() {

        if (!passEdit.getText().toString().trim().equals("")) {
            if (userEdit.getText().toString().toUpperCase().trim()
                    .equalsIgnoreCase(userName)
                    && passEdit.getText().toString().trim()
                    .equals(pwd)) {

                SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(Constants.isPasswordSaved,savePass.isChecked());
                editor.commit();
                mIntMaxLimit = 0;
                if (UtilConstants.isNetworkAvailable(getApplicationContext())) {
                    onMainMenuLogin();
                } else {
                    Toast toast = (Toast) Toast.makeText(getApplicationContext(),
                            R.string.validation_offline_login_no_internet,
                            Toast.LENGTH_LONG);
                    toast.show();
                    //Navigating to main menu
                    onMainMenuLogin();
                }

            } else {


                if (mIntMaxLimit == 3) {
                    alertForgetPwdMsg();
                } else {
                    mIntMaxLimit++;

                    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyTheme);
                    builder.setMessage(R.string.validation_plz_enter_username_pwd)
                            .setCancelable(false)
                            .setPositiveButton(R.string.ok,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {
                                            dialog.cancel();
                                        }
                                    });
                    builder.show();
                }

            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.MyTheme);
            builder.setMessage(R.string.validation_plz_fill_all_mandatory_flds)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            });
            builder.show();
        }

    }

    /*Navigates to main menu*/
    private void onMainMenuLogin() {
        Intent intent = new Intent(this, MainMenu.class);
        intent.putExtra(Constants.SHOWNOTIFICATION,isNotification);
        startActivity(intent);
    }

    /*This method calls when back button pressed*/
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.MyTheme);
        builder.setMessage(R.string.do_u_want_exit_app)
                .setCancelable(false)
                .setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                onExit();

                            }
                        })
                .setNegativeButton(R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        builder.show();
    }
    /*This method executes exit current activity to home activity
     */
    public void onExit() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        startActivity(homeIntent);
        finish();
    }

    /*clears password field*/
    private void onClear() {
        passEdit.setText("");
        passEdit.setFocusable(true);
        passEdit.setFocusableInTouchMode(true);
    }

    /*Navigates to forget password screen*/
    private void onForgetPwd() {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    /*displays popup*/
    public void showPopup(View v) {
        UtilConstants.showPopup(getApplicationContext(), v, LoginActivity.this,
                R.menu.menu_back);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_back:
                onBackPressed();
                break;

            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

}
