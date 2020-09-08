package dummydomain.yetanothercallblocker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dummydomain.yetanothercallblocker.data.YacbHolder;

public class CallReceiver extends BroadcastReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(CallReceiver.class);

    private final boolean monitoringService;

    @SuppressWarnings({"unused", "RedundantSuppression"}) // required for BroadcastReceivers
    public CallReceiver() {
        this(false);
    }

    public CallReceiver(boolean monitoringService) {
        this.monitoringService = monitoringService;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        LOG.debug("onReceive() invoked, monitoringService={}", monitoringService);

        if (!TelephonyManager.ACTION_PHONE_STATE_CHANGED.equals(intent.getAction())
                && !TelephonyManager.EXTRA_STATE_RINGING.equals(intent.getAction())) { // TODO: check
            LOG.warn("onReceive() unexpected action: {}", intent.getAction());
            return;
        }

        @SuppressWarnings({"deprecation", "RedundantSuppression"}) // no choice
        String extraIncomingNumber = TelephonyManager.EXTRA_INCOMING_NUMBER;

        String telephonyExtraState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        String incomingNumber = intent.getStringExtra(extraIncomingNumber);
        boolean hasNumberExtra = intent.hasExtra(extraIncomingNumber);
        LOG.info("onReceive() extraState={}, incomingNumber={}, hasNumberExtra={}",
                telephonyExtraState, incomingNumber, hasNumberExtra);

        extraLogging(intent); // TODO: make optional or remove

        if (TelephonyManager.EXTRA_STATE_RINGING.equals(intent.getAction())) {
            LOG.warn("onReceive() ignoring untested action");
            return;
        }

        if (incomingNumber == null && hasNumberExtra) {
            incomingNumber = "";
        }

        PhoneStateHandler phoneStateHandler = YacbHolder.getPhoneStateHandler();
        if (TelephonyManager.EXTRA_STATE_RINGING.equals(telephonyExtraState)) {
            phoneStateHandler.onRinging(context, incomingNumber);
        } else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(telephonyExtraState)) {
            phoneStateHandler.onOffHook(context, incomingNumber);
        } else if (TelephonyManager.EXTRA_STATE_IDLE.equals(telephonyExtraState)) {
            phoneStateHandler.onIdle(context, incomingNumber);
        }
    }

    private void extraLogging(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            LOG.trace("extraLogging() extras:");
            for (String k : extras.keySet()) {
                LOG.trace("extraLogging() key={}, value={}", k, extras.get(k));
            }

            Object subscription = extras.get("subscription"); // PhoneConstants.SUBSCRIPTION_KEY
            if (subscription != null) {
                LOG.trace("extraLogging() subscription.class={}", subscription.getClass());
                if (subscription instanceof Number) {
                    long subId = ((Number) subscription).longValue();
                    LOG.trace("extraLogging() subId={}, check={}",
                            subId, subId < Integer.MAX_VALUE);
                }
            }
        }
    }

}
