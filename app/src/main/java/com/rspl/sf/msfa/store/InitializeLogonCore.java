/*
package com.rspl.sf.msfa.store;

import android.content.Context;
import android.content.SharedPreferences;
import com.arteriatech.mutils.log.LogManager;
import com.arteriatech.mutils.registration.RegistrationModel;
import com.rspl.sf.msfa.BuildConfig;
import com.rspl.sf.msfa.R;
import com.rspl.sf.msfa.common.Constants;
import com.rspl.sf.msfa.registration.Configuration;
import com.rspl.sf.msfa.registration.RegistrationActivity;
import com.sap.maf.tools.logon.core.LogonCore;
import com.sap.maf.tools.logon.core.LogonCoreException;
import com.sap.maf.tools.logon.core.LogonCoreListener;
import com.sap.maf.tools.logon.logonui.api.LogonListener;
import com.sap.maf.tools.logon.logonui.api.LogonUIFacade;
import com.sap.maf.tools.logon.manager.LogonContext;
import com.sybase.persistence.DataVault;

public class InitializeLogonCore implements  LogonCoreListener, LogonListener {
    @Override
    public void registrationFinished(boolean b, String s, int i, DataVault.DVPasswordPolicy dvPasswordPolicy) {

    }

    @Override
    public void deregistrationFinished(boolean b) {

    }

    @Override
    public void backendPasswordChanged(boolean b) {

    }

    @Override
    public void applicationSettingsUpdated() {

    }

    @Override
    public void traceUploaded() {

    }

    @Override
    public void onLogonFinished(String s, boolean b, LogonContext logonContext) {

    }

    @Override
    public void onSecureStorePasswordChanged(boolean b, String s) {

    }

    @Override
    public void onBackendPasswordChanged(boolean b) {

    }

    @Override
    public void onUserDeleted() {

    }

    @Override
    public void onApplicationSettingsUpdated() {

    }

    @Override
    public void registrationInfo() {

    }

    @Override
    public void objectFromSecureStoreForKey() {

    }

    @Override
    public void onRefreshCertificate(boolean b, String s) {

    }

    public InitializeLogonCore(Context context) {
        this.context = context;
    }

    LogonCore logonCore = null;
    Context context=null;
    private LogonUIFacade mLogonUIFacade;
    RegistrationModel registrationModel = new RegistrationModel();
    public void  getinitLogonCore(){
        intializeRegistrationModel();
        initLogonCore(registrationModel);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME,0);
            String password = sharedPreferences.getString("username", "");
            logonCore.unlockStore(password);
        } catch (LogonCoreException var6) {
            var6.printStackTrace();
        }
    }
    private void initLogonCore( RegistrationModel registrationModel) {
        try {
            this.logonCore = LogonCore.getInstance();
            this.mLogonUIFacade = LogonUIFacade.getInstance();
            this.mLogonUIFacade.init(this, this.context, registrationModel.getAppID());
            this.logonCore.setLogonCoreListener(this);
            this.logonCore.init(this.context, registrationModel.getAppID());

            try {
                if (!this.logonCore.isStoreAvailable()) {
                    this.logonCore.createStore((String)null, false);
                }
            } catch (LogonCoreException var4) {
                var4.printStackTrace();
            }
        } catch (Exception var5) {
            LogManager.writeLogError(this.getClass().getSimpleName() + ".initLogonCore: " + var5.getMessage());
        }

    }

    private void intializeRegistrationModel() {
        registrationModel.setAppID(Configuration.APP_ID);
        registrationModel.setHttps(Configuration.IS_HTTPS);
        registrationModel.setPassword(Configuration.pwd_text);
        registrationModel.setPort(Configuration.port_Text);
        registrationModel.setSecConfig(Configuration.secConfig_Text);
        registrationModel.setServerText(Configuration.server_Text);
        registrationModel.setShredPrefKey(Constants.PREFS_NAME);
        registrationModel.setFormID(Configuration.farm_ID);
        registrationModel.setSuffix(Configuration.suffix);

        registrationModel.setDataVaultFileName(Constants.DataVaultFileName);
        registrationModel.setOfflineDBPath(Constants.offlineDBPath);
        registrationModel.setOfflineReqDBPath(Constants.offlineReqDBPath);
        registrationModel.setIcurrentUDBPath(Constants.icurrentUDBPath);
        registrationModel.setIbackupUDBPath(Constants.ibackupUDBPath);
        registrationModel.setIcurrentRqDBPath(Constants.icurrentRqDBPath);
        registrationModel.setIbackupRqDBPath(Constants.ibackupRqDBPath);
        //noPasscodeClasses.add(MainMenu.class.getName());
        // registrationModel.setNoPasscodeActivity(noPasscodeClasses);
        registrationModel.setAppActionBarIcon(R.mipmap.ic_action_bar_logo);
        registrationModel.setAppLogo(R.drawable.arteria_new_logo_transparent);
        registrationModel.setAppVersionName(BuildConfig.VERSION_NAME);
        registrationModel.setEmainId(this.context.getString(R.string.register_support_email));
        registrationModel.setPhoneNo(this.context.getString(R.string.register_support_phone));
        registrationModel.setEmailSubject("");//getString(R.string.email_subject)
        registrationModel.setRegisterActivity(RegistrationActivity.class);

//        registrationModel.setMainMenuActivity(LoginActivity.class);
    }
}
*/
