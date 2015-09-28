package com.styleme.floating.toolbox.pro.global.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.styleme.floating.toolbox.pro.R;
import com.styleme.floating.toolbox.pro.global.helper.AppHelper;
import com.styleme.floating.toolbox.pro.global.model.AppsModel;
import com.styleme.floating.toolbox.pro.global.model.BackupModel;

import java.io.FileWriter;

/**
 * Created by Kosh on 9/6/2015. copyrights are reserved
 */
public class BackupTask extends AsyncTask<BackupModel, Boolean, Boolean> {

    private Context context;
    private ProgressDialog progressDialog;

    public BackupTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(context.getString(R.string.in_progress));
        }
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
        progressDialog = null;
        AlertDialog.Builder builder;
        if (AppHelper.isDarkTheme(context)) {
            builder = new AlertDialog.Builder(context, R.style.Alerter);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setPositiveButton(context.getString(R.string.close), null);
        if (success) {
            builder.setTitle(context.getString(R.string.success)).setMessage(context.getString(R.string.backup_success));
        } else {
            builder.setTitle(context.getString(R.string.fail)).setMessage(context.getString(R.string.backup_errror));
        }
        builder.show();
    }

    @Override
    protected Boolean doInBackground(BackupModel... params){
        try {
            BackupModel backupModel = new BackupModel();
            backupModel.setSelectedApps(new AppsModel().getAll());
            backupModel.setPrefs(AppHelper.getAllPrefs(context));
            Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
            FileWriter fw = new FileWriter(AppHelper.generateBackupFile());
            fw.write(gson.toJson(backupModel));
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
