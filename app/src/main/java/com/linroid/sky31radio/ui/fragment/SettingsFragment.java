package com.linroid.sky31radio.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.avos.avoscloud.feedback.FeedbackAgent;
import com.linroid.sky31radio.BuildConfig;
import com.linroid.sky31radio.R;
import com.linroid.sky31radio.data.FirService;
import com.linroid.sky31radio.model.FirVersion;
import com.linroid.sky31radio.module.Injector;

import javax.inject.Inject;

import de.psdev.licensesdialog.LicensesDialog;
import rx.Subscriber;
import rx.android.app.AppObservable;

/**
 * Created by linroid on 1/17/15.
 */
public class SettingsFragment extends PreferenceFragment {
    @Inject
    FirService firService;
    private boolean injected = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity();
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        findPreference(getString(R.string.pref_build_time)).setSummary(BuildConfig.BUILD_TIME);

        Preference checkVersionPref = findPreference(getString(R.string.pref_check_version));
        checkVersionPref.setSummary(getString(R.string.tpl_version, BuildConfig.VERSION_NAME));
        checkVersionPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                checkVersion();
                return true;
            }
        });

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
    }


    private void checkVersion() {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getString(R.string.msg_checking_version));
        dialog.show();
        AppObservable.bindFragment(this, firService.checkVersion(BuildConfig.APPLICATION_ID))
        .subscribe(new Subscriber<FirVersion>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }

            @Override
            public void onNext(FirVersion firVersion) {
                if(firVersion.getVersion()> BuildConfig.VERSION_CODE){
                    dialog.dismiss();
                    showNewVersionFoundDialog(firVersion);
                }else{
                    Toast.makeText(getActivity(), R.string.msg_no_new_version, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showNewVersionFoundDialog(final FirVersion newFirVersion) {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.title_new_version_found)
                .setMessage(getString(R.string.msg_new_version_found, newFirVersion.getVersionShort(), newFirVersion.getVersion(), newFirVersion.getChangeLog()))
                .setPositiveButton(R.string.btn_dialog_update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent downloadPageIntent = new Intent(Intent.ACTION_VIEW);
                        downloadPageIntent.setData(Uri.parse(newFirVersion.getUpdateUrl()));
                        getActivity().startActivity(downloadPageIntent);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create()
             .show();
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        if(!injected){
            injected = true;
            Injector injector = (Injector)getActivity();
            injector.inject(this);
        }
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