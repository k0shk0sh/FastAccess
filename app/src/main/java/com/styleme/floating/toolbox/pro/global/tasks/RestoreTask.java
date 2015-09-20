package com.styleme.floating.toolbox.pro.global.tasks;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;

import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.styleme.floating.toolbox.pro.AppController;
import com.styleme.floating.toolbox.pro.R;
import com.styleme.floating.toolbox.pro.fragments.SettingsFragment;
import com.styleme.floating.toolbox.pro.global.helper.AppHelper;
import com.styleme.floating.toolbox.pro.global.model.AppsModel;
import com.styleme.floating.toolbox.pro.global.model.BackupModel;

import java.io.FileReader;
import java.util.Map;

/**
 * Created by Kosh on 9/6/2015. copyrights are reserved
 */
public class RestoreTask extends AsyncTask<Boolean, Void, BackupModel> {
    private SettingsFragment context;
    private ProgressDialog progressDialog;

    public RestoreTask(SettingsFragment context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context.getActivity());
            progressDialog.setCancelable(false);
            progressDialog.setMessage(context.getString(R.string.in_progress));
        }
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(BackupModel success) {
        super.onPostExecute(success);
        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
        progressDialog = null;
        AlertDialog.Builder builder;
        if (AppHelper.isDarkTheme(context.getActivity())) {
            builder = new AlertDialog.Builder(context.getActivity(), R.style.Alerter);
        } else {
            builder = new AlertDialog.Builder(context.getActivity());
        }
        if (success != null) {
            builder.setPositiveButton(context.getString(R.string.close), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    context.post();
                    context.postToService();
                    context.getActivity().recreate();
                }
            });
            builder.setTitle(context.getString(R.string.success)).setMessage(context.getString(R.string.restore_success));
            builder.setCancelable(false);
            builder.setNeutralButton("Delete Backup File", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AppHelper.getBackupFile().delete();
                    context.post();
                    context.postToService();
                    context.getActivity().recreate();
                }
            });
        } else {
            builder.setPositiveButton(context.getString(R.string.close), null);
            builder.setTitle(context.getString(R.string.fail)).setMessage(context.getString(R.string.restore_fail));
        }
        builder.show();
    }

    @Override
    protected BackupModel doInBackground(Boolean... params) {
        try {
            JsonReader reader = new JsonReader(new FileReader(AppHelper.getBackupFile()));
            BackupModel backupModel = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(reader, BackupModel.class);
            reader.close();
            if (backupModel != null) {
                if (backupModel.getSelectedApps() != null && backupModel.getSelectedApps().size() != 0) {
                    new AppsModel().add(backupModel.getSelectedApps());
                }
                if (backupModel.getPrefs() != null) {
                    for (Map.Entry<String, ?> pref : backupModel.getPrefs().entrySet()) {
                        if (pref != null) {
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AppController.getController());
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            if (pref.getValue() instanceof Boolean) {
                                editor.putBoolean(pref.getKey(), (Boolean) pref.getValue());
                            } else if (pref.getValue() instanceof Integer) {
                                editor.putInt(pref.getKey(), (Integer) pref.getValue());
                            } else if (pref.getValue() instanceof String) {
                                editor.putString(pref.getKey(), pref.getValue().toString());
                            } else if (pref.getValue() instanceof Float) {
                                editor.putFloat(pref.getKey(), (Float) pref.getValue());
                            } else if (pref.getValue() instanceof Long) {
                                editor.putLong(pref.getKey(), (Long) pref.getValue());
                            }
                            editor.apply();
                        }
                    }
                }
                return backupModel;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
