package net.typeblog.socks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.VpnService;
import android.util.Log;

import net.typeblog.socks.util.Profile;
import net.typeblog.socks.util.ProfileManager;
import net.typeblog.socks.util.Utility;

public class TaskerReceiver extends BroadcastReceiver {
    private static final String TAG = TaskerReceiver.class.getSimpleName();

    public static final String ACTION_CONNECT = "net.typeblog.socks.CONNECT";
    public static final String ACTION_DISCONNECT = "net.typeblog.socks.DISCONNECT";
    public static final String EXTRA_PROFILE = "profile";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) return;

        String action = intent.getAction();

        if (ACTION_CONNECT.equals(action)) {
            ProfileManager manager = new ProfileManager(context);
            Profile profile;

            String profileName = intent.getStringExtra(EXTRA_PROFILE);
            if (profileName != null) {
                profile = manager.getProfile(profileName);
                if (profile == null) {
                    Log.e(TAG, "Profile not found: " + profileName);
                    return;
                }
            } else {
                profile = manager.getDefault();
            }

            if (VpnService.prepare(context) != null) {
                Log.e(TAG, "VPN permission not granted. Open the app and connect manually once first.");
                return;
            }

            Log.i(TAG, "Starting VPN with profile: " + profile.getName());
            Utility.startVpn(context, profile);

        } else if (ACTION_DISCONNECT.equals(action)) {
            Log.i(TAG, "Stopping VPN service");
            context.stopService(new Intent(context, SocksVpnService.class));
        }
    }
}
