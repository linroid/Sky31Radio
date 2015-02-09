package com.linroid.sky31radio.ui.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.avos.avoscloud.PushService;
import com.avos.avoscloud.feedback.FeedbackAgent;
import com.linroid.sky31radio.BuildConfig;
import com.linroid.sky31radio.Constants;
import com.linroid.sky31radio.R;
import com.linroid.sky31radio.ui.HomeActivity;

import de.psdev.licensesdialog.LicensesDialog;

/**
 * Created by linroid on 1/17/15.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        findPreference(getString(R.string.pref_version_ame)).setSummary(BuildConfig.VERSION_NAME);
        findPreference(getString(R.string.pref_build_time)).setSummary(BuildConfig.BUILD_TIME);
        findPreference(getString(R.string.pref_feedback)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                openFeedback();
                return true;
            }
        });
        findPreference(getString(R.string.pref_license)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                openLicenseDialog();
                return true;
            }
        });
        findPreference(getString(R.string.pref_donate)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                openDonate();
                return true;
            }
        });
        findPreference(getString(R.string.pref_allow_new_program_notification)).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean enableNewProgramNotification = (Boolean)newValue;
                if(enableNewProgramNotification){
                    PushService.subscribe(getActivity(), Constants.CHANNEL_NEW_PROGRAM, HomeActivity.class);
                }else{
                    PushService.unsubscribe(getActivity(), Constants.CHANNEL_NEW_PROGRAM);
                }
                return true;
            }
        });
    }

    private void openDonate() {
        String alipayAccount = getString(R.string.alipay_account);
        ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText("AlipayAccount", alipayAccount));
        Toast.makeText(getActivity(), getString(R.string.msg_donate, alipayAccount), Toast.LENGTH_LONG).show();
    }

    private void openFeedback(){
        FeedbackAgent agent = new FeedbackAgent(getActivity());
        agent.startDefaultThreadActivity();
    }
    private void openLicenseDialog(){
        new LicensesDialog.Builder(getActivity())
                .setNotices(R.raw.notices)
                .build()
                .show();
    }
}