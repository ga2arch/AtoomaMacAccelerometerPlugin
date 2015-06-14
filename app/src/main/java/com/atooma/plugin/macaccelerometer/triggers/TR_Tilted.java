package com.atooma.plugin.macaccelerometer.triggers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.atooma.plugin.AlarmBasedTrigger;
import com.atooma.plugin.IntentBasedTrigger;
import com.atooma.plugin.ParameterBundle;
import com.atooma.plugin.Schedule;
import com.atooma.plugin.Trigger;
import com.atooma.plugin.macaccelerometer.Constants;
import com.atooma.plugin.macaccelerometer.R;
import com.atooma.sdk.IAtoomaService;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Gabriele on 13/06/15.
 */
public class TR_Tilted extends AlarmBasedTrigger {

    BroadcastReceiver receiver;
    Schedule schedule;
    private IAtoomaService service = null;
    private String ruleId;
    private ParameterBundle params;
    private String mId;

    @Override
    public void onRevoke(String s) {

    }

    private static final String TAG = "TR_Tilted";

    public TR_Tilted(Context context, final String id, int version) {
        super(context, id, version);

        final IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.CHANGE_INTENT);

        final TR_Tilted ths = this;

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "Received broadcast");

                if (service != null) {
                    Log.i(TAG, "Here");

                    try {
                        Field f = AlarmBasedTrigger.class.getDeclaredField("moduleId");
                        f.setAccessible(true);
                        mId = (String) f.get((AlarmBasedTrigger) ths);

                        Log.i(TAG, "ModuleId: " + mId);

                    } catch (NoSuchFieldException e) {
                        Log.e(TAG, "No Such Field");
                    } catch (IllegalAccessException e) {
                        Log.e(TAG, "Illegal Access");
                    }

                    try {
                        service.trigger(mId, id, ruleId, params);
                    } catch (RemoteException var4) {
                        var4.printStackTrace();
                    }

                }
            }
        };

        context.registerReceiver(receiver, filter);
    }

    @Override
    public void defineUI() {
        setIcon(R.drawable.plugin_icon_normal);
        setTitle(R.string.tilted);
    }

    @Override
    public void declareParameters() {
        addParameter(R.string.authentication,
                R.string.authentication, "ACCESS", "PLUGIN", true,
                "com.atooma.plugin.macaccelerometer.AccessServer");

        addParameter(R.string.change_from_normal_to_tilted,
                R.string.change_from_normal_to_tilted_null, "NORMAL_TILTED", "BOOLEAN", false, null);
    }

   @Override
    public void declareVariables() {
        //addVariable(R.string.current_status, "STATUS", "STRING");
    }

    @Override
    public void timeout(IAtoomaService atoomaService, String ruleId, ParameterBundle parameters) throws RemoteException {
        service = atoomaService;
        this.ruleId = ruleId;
        params = parameters;

        onTimeout(ruleId, parameters);
    }

    @Override
    public void onTimeout(String ruleId, ParameterBundle parameters) {
        Log.i(TAG, "Ruleid: "+ ruleId);

        //trigger(ruleId, parameters);
    }

    @Override
    public Schedule getScheduleInfo() throws RemoteException {
        return new Schedule.Builder().exact(false).triggerAtTime(System.currentTimeMillis()).build();
    }
}
