package com.softbankrobotics.retaildemo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy;
import com.aldebaran.qi.sdk.object.conversation.Phrase;
import com.aldebaran.qi.sdk.object.conversation.QiChatExecutor;
import com.aldebaran.qi.sdk.object.humanawareness.HumanAwareness;
import com.softbankrobotics.retaildemo.Barcode.BarcodeTracker;
import com.softbankrobotics.retaildemo.Executors.FragmentExecutor;
import com.softbankrobotics.retaildemo.Executors.StatusExecutor;
import com.softbankrobotics.retaildemo.Fragments.CollectConfirmFragment;
import com.softbankrobotics.retaildemo.Fragments.LoadingFragment;
import com.softbankrobotics.retaildemo.Fragments.MainMenuFragment;
import com.softbankrobotics.retaildemo.Fragments.ProductSelectionFragment;
import com.softbankrobotics.retaildemo.Fragments.ReturnMainFragment;
import com.softbankrobotics.retaildemo.Fragments.SplashScreenFragment;
import com.google.android.gms.vision.barcode.Barcode;
import com.softbankrobotics.retaildemo.Utils.ChatData;
import com.softbankrobotics.retaildemo.Utils.CountDownNoInteraction;
import com.softbankrobotics.retaildemo.Utils.TokenFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends RobotActivity implements RobotLifecycleCallbacks,
        BarcodeTracker.BarcodeGraphicTrackerCallback {

    private static final String TAG = "MSI_MainActivity";
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    private FragmentManager fragmentManager;
    private QiContext qiContext;
    private ChatData currentChatData, frenchChatData, englishChatData;
    private String currentFragment,currentLanguage;
    private CountDownNoInteraction countDownNoInteraction;
    private HumanAwareness humanAwareness;
    private android.content.res.Configuration config;
    private Resources res;
    private Future<Void> chatFuture;
    private String email;
    private String password;

    //topicNames needs to be updated wih the topics names of the topics in the raw resource dir
    private final List<String> topicNames = Arrays.asList("collect","collectconfirm","concepts",
            "email","feedback","mainmenu","product","productmap","productselection","productupsell",
            "returnmain","returnproduct","returnreason","collectlocker");

    public Status status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        res = getApplicationContext().getResources();
        config = res.getConfiguration();
        this.fragmentManager = getSupportFragmentManager();
        this.status = new Status(this);
        QiSDK.register(this, this);
        countDownNoInteraction = new CountDownNoInteraction(this,
                new SplashScreenFragment(), 300000,10000);
        countDownNoInteraction.start();
        if(config.locale.toString().contains("fr")){
            setUIWithLocale("fr");
        }else{
            setUIWithLocale("en");
        }
        //request the permissions to use the camera
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    RC_HANDLE_CAMERA_PERM);
        }
        setContentView(R.layout.activity_main);
        String token = TokenFile.getEmailCredentials(getApplicationContext());
        if(token.length() == 0){
            LayoutInflater inflater = this.getLayoutInflater();
            final View prompt = inflater.inflate(R.layout.dialog_email_credentials, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(prompt);
            final EditText etEmail = prompt.findViewById(R.id.username);
            final EditText etPassword = prompt.findViewById(R.id.password);
            final Button cancel = prompt.findViewById(R.id.button_cancel);
            final Button signIn = prompt.findViewById(R.id.button_sign_in);
            AlertDialog dialog = builder.create();
            cancel.setOnClickListener(v -> dialog.hide());
            signIn.setOnClickListener(v -> {
                this.email = etEmail.getText().toString();
                this.password = etPassword.getText().toString();
                Log.d(TAG,"email" + email);
                Log.d(TAG,"pws" + password);
                TokenFile.saveEmailCredentials(getApplicationContext(),
                         email+ " " + password );
                dialog.hide();
            });
            dialog.show();
            Log.d(TAG,"created credentials file");
        }else{
            this.email = token.split(" ")[0];
            this.password = token.split(" ")[1];
            Log.d(TAG, "loaded credentials file : " + email + " " + password);
        }
    }

    private void setUIWithLocale(String strLocale){
        currentLanguage = strLocale;
        Locale locale = new Locale(strLocale);
        config.setLocale(locale);
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    @Override
    public void onUserInteraction()
    {
        if(getFragment() instanceof SplashScreenFragment){
            setFragment(new MainMenuFragment());
            countDownNoInteraction.start();
        }else{
            countDownNoInteraction.reset();
        }
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        Log.d(TAG, "onRobotFocusGained");
        this.qiContext = qiContext;
        Locale tmpLocale = new Locale(currentLanguage);
        frenchChatData = new ChatData(this,qiContext,new Locale("fr"),topicNames,true);
        englishChatData = new ChatData(this,qiContext,new Locale("en"),topicNames,true);
        Map<String, QiChatExecutor> executors = new HashMap<>();
        executors.put("FragmentExecutor",new FragmentExecutor(qiContext,this));
        executors.put("StatusExecutor",new StatusExecutor(qiContext,this));
        List<String> qiVariables = new ArrayList<>(Arrays.asList("signed", "color", "size", "locker", "first"));
        List<String> dynamics = new ArrayList<>(Arrays.asList("product_0", "product_1", "product_2", "product_3", "product_4","product_type"));
        setUpChat(frenchChatData,executors,qiVariables,dynamics);
        setUpChat(englishChatData,executors,qiVariables,dynamics);
        currentChatData = englishChatData;
        config.setLocale(tmpLocale);
        res.updateConfiguration(config, res.getDisplayMetrics());
        if(currentLanguage.equals("fr")){
            currentChatData = frenchChatData;
        }else{
            currentChatData = englishChatData;
        }
        currentChatData.chat.async().addOnStartedListener(() -> {
            setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.ALWAYS);
            setFragment(new MainMenuFragment());
        });
        currentChatData.chat.async().addOnNormalReplyFoundForListener(input -> {
            countDownNoInteraction.reset();
        });
        currentChatData.variables.get("first").async().setValue("0");
        chatFuture = currentChatData.chat.async().run();
        humanAwareness = getQiContext().getHumanAwareness();
        humanAwareness.async().addOnEngagedHumanChangedListener(engagedHuman -> {
            if(getFragment() instanceof SplashScreenFragment){
                if(engagedHuman != null){
                    setFragment(new MainMenuFragment());
                }
            }else{
                countDownNoInteraction.reset();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RC_HANDLE_CAMERA_PERM: {

                if (grantResults.length == 0 || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "Permission has been denied by user");
                } else {
                    Log.i(TAG, "Permission has been granted by user");
                }
                return;
            }
        }
    }

    @Override
    public void onRobotFocusLost() {
        humanAwareness.async().removeAllOnEngagedHumanChangedListeners();
        this.qiContext = null;
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        Log.d(TAG, "onRobotFocusRefused");
    }

    @Override
    protected void onDestroy() {
        countDownNoInteraction.cancel();
        QiSDK.unregister(this, this);
        super.onDestroy();
    }

    private void setUpChat(ChatData chatData, Map<String, QiChatExecutor> executors,
                           List<String> qiVariables, List<String> dynamics){
        chatData.setupExecutors(executors);
        chatData.setupQiVariables(qiVariables);
        chatData.setupDynamics(dynamics);
    }

    //sets the fragment while updating the status productString
    public void setFragment(Fragment fragment, String productString){
        Log.d(TAG,"product String : " + productString);
        this.status.productString = productString;
        this.setFragment(fragment);

    }

    public void setFragment(Fragment fragment){
        currentFragment = fragment.getClass().getSimpleName();
        String topicName = currentFragment.toLowerCase().replace("fragment","");
        if(!(fragment instanceof  LoadingFragment || fragment instanceof ProductSelectionFragment || fragment instanceof SplashScreenFragment)) {
            this.currentChatData.goToBookmarkNewTopic("init", topicName);
        }
        Log.d(TAG, "starting fragment Transaction for fragment : " + fragment.getClass().getSimpleName());
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_fade_in_right,R.anim.exit_fade_out_left,R.anim.enter_fade_in_left,R.anim.exit_fade_out_right);
        transaction.replace(R.id.placeholder, fragment,"currentFragment");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public android.support.v4.app.Fragment getFragment() {
        return fragmentManager.findFragmentByTag("currentFragment");
    }



    public Integer getThemeId(){
        try {
            return getPackageManager().getActivityInfo(getComponentName(), 0).getThemeResource();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public HumanAwareness getHumanAwareness() {
        return humanAwareness;
    }

    public void setLocale(String lang){
        if(currentLanguage.equals(lang)){
            setFragment(new MainMenuFragment());
            return;
        }
        currentChatData.currentTopicName = null;
        currentChatData.currentTopicStatus = null;
        chatFuture.requestCancellation();
        chatFuture.thenConsume(i -> chatFuture = currentChatData.chat.async().run());
        setFragment(new LoadingFragment());
        Log.d(TAG,"setting Locale to : " + lang);
        setUIWithLocale(lang);
        runOnUiThread(() -> setContentView(R.layout.activity_main));
        currentChatData.chat.async().removeAllOnStartedListeners();
        currentChatData.chat.async().removeAllOnNormalReplyFoundForListeners();
        if(lang.equals("fr")){
            currentChatData = frenchChatData;
        }else{
            currentChatData = englishChatData;
        }
        currentChatData.chat.async().addOnStartedListener(() -> {
            setFragment(new MainMenuFragment());
        });
        currentChatData.chat.async().addOnNormalReplyFoundForListener(input -> {
            countDownNoInteraction.reset();
        });
    }

    public void setQiVariable(String variableName, String value){
        currentChatData.setQiVariable(variableName,value);
    }

    @Override
    public void onPause() {
        countDownNoInteraction.cancel();
        super.onPause();
    }

    public ChatData getCurrentChatData(){
        return currentChatData;
    }

    public String getProductString(){
        return status.productString;
    }

    public String getCurrentLanguage(){
        return currentLanguage;
    }

    protected void onResume() {
        super.onResume();
        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.OVERLAY);
        this.setFragment(new LoadingFragment());
    }

    //concept name must be "product_#"
    public void setDynamicProductName(String conceptName, String productName){
        if(conceptName.equals("product_type")){
            currentChatData.dynamics.get(5).async().addPhrases(Collections.singletonList(new Phrase(productName)));
            return;
        }
        Log.d(TAG,"Dynamic Index : " + conceptName.charAt(8));
        currentChatData.dynamics.get(Character.getNumericValue(conceptName.charAt(8))).async().addPhrases(Collections.singletonList(new Phrase(productName)));
    }

    public QiContext getQiContext() {
        return qiContext;
    }

    @Override
    public void onDetectedQrCode(Barcode barcode) {
        Log.d(TAG, "I can see the QRcode, fragment :" + getFragment().getClass().getSimpleName());
        try {
            if (getFragment() instanceof ReturnMainFragment) {
                status.currentTicket = barcode.displayValue;
                ReturnMainFragment rmf = (ReturnMainFragment) getFragment();
                rmf.updateProductList();
            } else {
                Log.d(TAG, "barcode" + barcode.displayValue);
                status.currentTicket = barcode.displayValue;
                setFragment(new CollectConfirmFragment());
            }
        } catch (Exception e) {
            Log.e(TAG, "wrong fragment needs update in Collect Fragment");
            e.printStackTrace();
        }
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}

