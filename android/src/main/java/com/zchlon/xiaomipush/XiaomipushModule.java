package com.zchlon.xiaomipush;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class XiaomipushModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

    private static String TAG = "XiaomipushModule";
    private Context mContext;
    private static String mEvent;
    private static WritableMap mParams = null;

    private static ReactApplicationContext reactContext = null;

    private final static String RECEIVE_REGISTRATION_ID = "getRegistrationId";
    private final static String RECEIVE_NOTIFICATION = "receiveNotification";
    private final static String OPEN_NOTIFICATION = "openNotification";


    public XiaomipushModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;
    }

    @Override
    public String getName() {
        return "XiaomiPush";
    }


    /**
     *  注册推送服务
     */
    @ReactMethod
    public void registerPush(Promise promise) {
        if (shouldInit()) {
            String appId = ExampleUtil.getAppId(mContext.getApplicationContext());
            String appKey = ExampleUtil.getAppKey(mContext.getApplicationContext());

            if (appId == null || appId.isEmpty() || appKey == null || appKey.isEmpty()) {
                Log.i(TAG, "AppId or AppKey can't be null");
                promise.resolve("AppId or AppKey can't be null");
                return;
            }
            MiPushClient.registerPush(mContext.getApplicationContext(), appId, appKey);
            reactContext = getReactApplicationContext();
        }
    }

    /**
     *  关闭推送服务
     */
    @ReactMethod
    public void unregisterPush(Promise promise) {
        MiPushClient.unregisterPush(mContext.getApplicationContext());
    }

    /**
     *  设置别名
     */
    @ReactMethod
    public void setAlias(String alias) {
        Boolean valid = ExampleUtil.isValidTagAndAlias(alias);
        if (valid) {
            MiPushClient.setAlias(mContext.getApplicationContext(), alias, null);
        } else {
            Log.i(TAG, "alias is invalid");
        }
    }

    /**
     *  删除别名
     */
    @ReactMethod
    public void unsetAlias(String alias) {
        MiPushClient.unsetAlias(mContext.getApplicationContext(), alias, null);
    }


    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = Process.myPid();
        for (RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }
    private static void sendEvent() {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(mEvent, mParams);
        XiaomipushModule.mParams = null;
    }

    @Override
    public void onHostResume() {
        Log.d(TAG, "onHostResume");
    }

    @Override
    public void onHostPause() {
        Log.d(TAG, "onHostPause");
    }

    @Override
    public void onHostDestroy() {
        Log.d(TAG, "onHostDestroy");
    }

    public static class XiaomiPushReceiver extends PushMessageReceiver {

        private String mRegId;
        private long mResultCode = -1;
        private String mReason;
        private String mCommand;
        private String mMessage;
        private String mTopic;
        private String mAlias;
        private String mUserAccount;
        private String mStartTime;
        private String mEndTime;

        @Override
        public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
            mMessage = message.getContent();
            if(!TextUtils.isEmpty(message.getTopic())) {
                mTopic=message.getTopic();
            } else if(!TextUtils.isEmpty(message.getAlias())) {
                mAlias=message.getAlias();
            } else if(!TextUtils.isEmpty(message.getUserAccount())) {
                mUserAccount=message.getUserAccount();
            }
        }
        @Override
        public void onNotificationMessageClicked(Context context, MiPushMessage message) {
            mMessage = message.getContent();
            if(!TextUtils.isEmpty(message.getTopic())) {
                mTopic=message.getTopic();
            } else if(!TextUtils.isEmpty(message.getAlias())) {
                mAlias=message.getAlias();
            } else if(!TextUtils.isEmpty(message.getUserAccount())) {
                mUserAccount=message.getUserAccount();
            }
            XiaomipushModule.mEvent = OPEN_NOTIFICATION;
            XiaomipushModule.mParams = Arguments.createMap();
            XiaomipushModule.sendEvent();
        }
        @Override
        public void onNotificationMessageArrived(Context context, MiPushMessage message) {
            mMessage = message.getContent();
            if(!TextUtils.isEmpty(message.getTopic())) {
                mTopic=message.getTopic();
            } else if(!TextUtils.isEmpty(message.getAlias())) {
                mAlias=message.getAlias();
            } else if(!TextUtils.isEmpty(message.getUserAccount())) {
                mUserAccount=message.getUserAccount();
            }

            XiaomipushModule.mEvent = RECEIVE_NOTIFICATION;
            XiaomipushModule.mParams = Arguments.createMap();
            XiaomipushModule.sendEvent();
        }
        @Override
        public void onCommandResult(Context context, MiPushCommandMessage message) {
            String command = message.getCommand();
            List<String> arguments = message.getCommandArguments();
            String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
            String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
            if (MiPushClient.COMMAND_REGISTER.equals(command)) {
                if (message.getResultCode() == ErrorCode.SUCCESS) {
                    mRegId = cmdArg1;

                    XiaomipushModule.mEvent = RECEIVE_REGISTRATION_ID;
                    XiaomipushModule.mParams = Arguments.createMap();
                    XiaomipushModule.mParams.putString("RegId", mRegId);
                    XiaomipushModule.sendEvent();

                    Log.d("XiaoMiPush", "RegId:" + mRegId);
                }
            } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
                if (message.getResultCode() == ErrorCode.SUCCESS) {
                    mAlias = cmdArg1;
                }
            } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
                if (message.getResultCode() == ErrorCode.SUCCESS) {
                    mAlias = cmdArg1;
                }
            } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
                if (message.getResultCode() == ErrorCode.SUCCESS) {
                    mTopic = cmdArg1;
                }
            } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {
                if (message.getResultCode() == ErrorCode.SUCCESS) {
                    mTopic = cmdArg1;
                }
            } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {
                if (message.getResultCode() == ErrorCode.SUCCESS) {
                    mStartTime = cmdArg1;
                    mEndTime = cmdArg2;
                }
            }
        }
        @Override
        public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
            String command = message.getCommand();
            List<String> arguments = message.getCommandArguments();
            String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
            String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
            if (MiPushClient.COMMAND_REGISTER.equals(command)) {
                if (message.getResultCode() == ErrorCode.SUCCESS) {
                    mRegId = cmdArg1;
                }
            }
        }
    }
}


