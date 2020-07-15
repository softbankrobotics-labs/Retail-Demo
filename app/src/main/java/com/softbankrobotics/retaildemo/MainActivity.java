package com.softbankrobotics.retaildemo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
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
import com.google.android.gms.vision.barcode.Barcode;
import com.softbankrobotics.retaildemo.Barcode.BarcodeTracker;
import com.softbankrobotics.retaildemo.Executors.FragmentExecutor;
import com.softbankrobotics.retaildemo.Executors.LanguageExecutor;
import com.softbankrobotics.retaildemo.Executors.StatusExecutor;
import com.softbankrobotics.retaildemo.Fragments.CollectConfirmFragment;
import com.softbankrobotics.retaildemo.Fragments.LoadingFragment;
import com.softbankrobotics.retaildemo.Fragments.MainMenuFragment;
import com.softbankrobotics.retaildemo.Fragments.ProductSelectionFragment;
import com.softbankrobotics.retaildemo.Fragments.ReturnMainFragment;
import com.softbankrobotics.retaildemo.Fragments.SplashScreenFragment;
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
    //topicNames needs to be updated wih the topics names of the topics in the raw resource dir
    public Status status;
    public Map<String, ChatData> chatDataList = new HashMap<>();
    private FragmentManager fragmentManager;
    private QiContext qiContext;
    private ChatData currentChatData, chatData;
    private String currentFragment, currentLanguage;
    private CountDownNoInteraction countDownNoInteraction;
    private HumanAwareness humanAwareness;
    private android.content.res.Configuration config;
    private Resources res;
    private Future<Void> chatRunFuture;
    private String email;
    private String password;
    private Boolean objects_built = false;
    private final List<String> topicNames = Arrays.asList("collect", "collectconfirm", "concepts",
            "email", "feedback", "mainmenu", "product", "productmap", "productselection", "productupsell",
            "returnmain", "returnproduct", "returnreason", "collectlocker");
    private List<String> locales = Arrays.asList("fr", "en");
    private List<String> qiVariables = new ArrayList<>(Arrays.asList("signed", "color", "size", "locker", "first"));
    private List<String> dynamics = new ArrayList<>(Arrays.asList("product_0", "product_1", "product_2", "product_3", "product_4", "product_type"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //res = getApplicationContext().getResources();
        res = getResources();
        config = res.getConfiguration();
        this.fragmentManager = getSupportFragmentManager();
        this.status = new Status(this);
        QiSDK.register(this, this);
        countDownNoInteraction = new CountDownNoInteraction(this,
                new SplashScreenFragment(), 120000, 10000);
        countDownNoInteraction.start();
        if (config.locale.toString().contains("fr")) {
            setUIWithLocale("fr");
        } else {
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
        if (token.length() == 0) {
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
                if (!etEmail.getText().toString().equalsIgnoreCase("") && !etPassword.getText().toString().equalsIgnoreCase("")) {
                    this.email = etEmail.getText().toString();
                    this.password = etPassword.getText().toString();
                    TokenFile.saveEmailCredentials(getApplicationContext(),
                            email + " " + password);
                    dialog.hide();
                }
            });
            dialog.show();
            Log.i(TAG, "created credentials file");
        } else {
            this.email = token.split(" ")[0];
            this.password = token.split(" ")[1];
            Log.d(TAG, "loaded credentials file : " + email + " " + password);
            Log.i(TAG, "loaded credentials file");
        }
    }

    private void setUIWithLocale(String strLocale) {
        currentLanguage = strLocale;
        Locale locale = new Locale(strLocale);
        config.setLocale(locale);
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    @Override
    public void onUserInteraction() {
        if (getFragment() instanceof SplashScreenFragment) {
            setFragment(new MainMenuFragment());
            countDownNoInteraction.start();
        } else {
            countDownNoInteraction.reset();
        }
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        Log.i(TAG, "onRobotFocusGained: locale : " + config.locale.toString());

        this.qiContext = qiContext;

        initChat();

        if (currentLanguage.equals("fr")) {
            currentChatData = chatDataList.get("fr");
        } else {
            currentChatData = chatDataList.get("en");
        }

        runChat();
        startHumanAwareness();
    }

    private void initChat() {
        Log.i(TAG, "Objects_built : " + objects_built);
        if (!objects_built) {
            Log.d(TAG, "initChat: started");

            Map<String, QiChatExecutor> executors = new HashMap<>();
            executors.put("FragmentExecutor", new FragmentExecutor(qiContext, this));
            executors.put("StatusExecutor", new StatusExecutor(qiContext, this));
            executors.put("LanguageExecutor", new LanguageExecutor(qiContext, this));

            for (String locale : locales) {
                try {
                    chatData = new ChatData(this, qiContext, new Locale(locale), topicNames, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                chatDataList.put(locale, chatData);
                if (chatData.languageIsInstalled)
                    chatData.setUpChatContent(executors, qiVariables, dynamics);
            }
            objects_built = true;
            Log.d(TAG, "initChat: objects_built: "+objects_built);
        } else {
            Log.d(TAG, "initChat: Chat already initialized");
            for (String locale : locales) {
                chatDataList.get(locale).setupChat(this.qiContext);
            }
        }
    }

    private void runChat() {
        currentChatData.chat.async().addOnStartedListener(() -> {
            runOnUiThread(() -> {
                setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.ALWAYS);
                setFragment(new MainMenuFragment());
            });
        });
        currentChatData.chat.async().addOnNormalReplyFoundForListener(input -> {
            countDownNoInteraction.reset();
        });
        currentChatData.variables.get("first").async().setValue("0");
        chatRunFuture = currentChatData.chat.async().run();
    }

    private void startHumanAwareness() {
        humanAwareness = getQiContext().getHumanAwareness();
        humanAwareness.async().addOnEngagedHumanChangedListener(engagedHuman -> {
            if (engagedHuman != null) {
                if (getFragment() instanceof SplashScreenFragment) {
                    setFragment(new MainMenuFragment());
                    countDownNoInteraction.start();
                } else {
                    countDownNoInteraction.reset();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
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
        Log.i(TAG, "onRobotFocusLost");
        if (humanAwareness != null) {
            try {
                humanAwareness.removeAllOnEngagedHumanChangedListeners();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (currentChatData.chat != null) {
            try {
                currentChatData.chat.removeAllOnStartedListeners();
                currentChatData.chat.removeAllOnNormalReplyFoundForListeners();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.qiContext = null;
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        Log.i(TAG, "onRobotFocusRefused");
    }

    @Override
    protected void onDestroy() {
        countDownNoInteraction.cancel();
        QiSDK.unregister(this, this);
        super.onDestroy();
    }

    //sets the fragment while updating the status productString
    public void setFragment(Fragment fragment, String productString) {
        Log.d(TAG, "product String : " + productString);
        this.status.productString = productString;
        this.setFragment(fragment);
    }

    public Fragment getFragment() {
        return fragmentManager.findFragmentByTag("currentFragment");
    }

    public void setFragment(Fragment fragment) {
        currentFragment = fragment.getClass().getSimpleName();
        String topicName = currentFragment.toLowerCase().replace("fragment", "");
        if (!(fragment instanceof LoadingFragment || fragment instanceof ProductSelectionFragment || fragment instanceof SplashScreenFragment)) {
            this.currentChatData.goToBookmarkNewTopic("init", topicName);
        }
        if (this.currentChatData != null) {
            if (fragment instanceof LoadingFragment || fragment instanceof SplashScreenFragment) {
                this.currentChatData.enableListeningAnimation(false);
            } else {
                this.currentChatData.enableListeningAnimation(true);
            }
        }
        Log.i(TAG, "starting fragment Transaction for fragment : " + fragment.getClass().getSimpleName());
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_fade_in_right, R.anim.exit_fade_out_left, R.anim.enter_fade_in_left, R.anim.exit_fade_out_right);
        transaction.replace(R.id.placeholder, fragment, "currentFragment");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public Integer getThemeId() {
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

    public void setLocale(String lang) {
        if (currentLanguage.equals(lang)) {
            setFragment(new MainMenuFragment());
            return;
        }
        currentChatData.currentTopicName = null;
        currentChatData.currentTopicStatus = null;

        runOnUiThread(() -> {
            this.setFragment(new LoadingFragment());
        });
        Log.i(TAG, "setting Locale to : " + lang);
        setUIWithLocale(lang);

        currentChatData.chat.async().removeAllOnStartedListeners();
        currentChatData.chat.async().removeAllOnNormalReplyFoundForListeners();

        if (lang.equals("fr")) {
            currentChatData = chatDataList.get("fr");
        } else {
            currentChatData = chatDataList.get("en");
        }

        if (chatRunFuture != null) {
            chatRunFuture.requestCancellation();
            chatRunFuture.thenConsume(ignored -> runChat());
        } else {
            runChat();
        }
    }

    public void setQiVariable(String variableName, String value) {
        currentChatData.setQiVariable(variableName, value);
    }

    @Override
    public void onPause() {
        countDownNoInteraction.cancel();
        super.onPause();
    }

    public ChatData getCurrentChatData() {
        return currentChatData;
    }

    public String getProductString() {
        return status.productString;
    }

    public String getCurrentLanguage() {
        return currentLanguage;
    }

    protected void onResume() {
        super.onResume();
        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.OVERLAY);
        this.setFragment(new LoadingFragment());
    }

    //concept name must be "product_#"
    public void setDynamicProductName(String conceptName, String productName) {
        if (conceptName.equals("product_type")) {
            currentChatData.dynamics.get(5).async().addPhrases(Collections.singletonList(new Phrase(productName)));
            return;
        }
        Log.d(TAG, "Dynamic Index : " + conceptName.charAt(8));
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

