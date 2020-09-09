package dummydomain.yetanothercallblocker;

import android.content.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dummydomain.yetanothercallblocker.data.NumberInfo;
import dummydomain.yetanothercallblocker.data.NumberInfoService;
import dummydomain.yetanothercallblocker.event.CallEndedEvent;
import dummydomain.yetanothercallblocker.event.CallOngoingEvent;
import dummydomain.yetanothercallblocker.utils.PhoneUtils;

import static dummydomain.yetanothercallblocker.EventUtils.postEvent;

public class PhoneStateHandler {

    private static final Logger LOG = LoggerFactory.getLogger(PhoneStateHandler.class);

    private final Settings settings;
    private final NumberInfoService numberInfoService;
    private final NotificationService notificationService;

    private boolean isOffHook;

    public PhoneStateHandler(Settings settings, NumberInfoService numberInfoService,
                             NotificationService notificationService) {
        this.settings = settings;
        this.numberInfoService = numberInfoService;
        this.notificationService = notificationService;
    }

    public void onRinging(Context context, String phoneNumber) {
        LOG.debug("onRinging({})", phoneNumber);

        if (phoneNumber == null) {
            if (!PermissionHelper.hasNumberInfoPermissions(context)) {
                LOG.warn("No info permissions");
                return;
            }
            return; // TODO: check
        }

        boolean blockingEnabled = settings.getCallBlockingEnabled();
        boolean showNotifications = settings.getIncomingCallNotifications();

        if (!blockingEnabled && !showNotifications) {
            return;
        }

        NumberInfo numberInfo = numberInfoService.getNumberInfo(phoneNumber,
                settings.getCachedAutoDetectedCountryCode(), false);

        boolean blocked = false;
        if (blockingEnabled && !isOffHook && numberInfoService.shouldBlock(numberInfo)) {
            blocked = PhoneUtils.rejectCall(context);

            if (blocked) {
                notificationService.notifyCallBlocked(numberInfo);

                numberInfoService.blockedCall(numberInfo);

                postEvent(new CallEndedEvent());
            }
        }

        if (!blocked && showNotifications) {
            notificationService.startCallIndication(numberInfo);
        }
    }

    public void onOffHook(Context context, String phoneNumber) {
        LOG.debug("onOffHook({})", phoneNumber);

        isOffHook = true;

        postEvent(new CallOngoingEvent());
    }

    public void onIdle(Context context, String phoneNumber) {
        LOG.debug("onIdle({})", phoneNumber);

        isOffHook = false;

        notificationService.stopAllCallsIndication();

        postEvent(new CallEndedEvent());
    }

}
