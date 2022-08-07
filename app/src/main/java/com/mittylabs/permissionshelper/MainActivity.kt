package com.mittylabs.permissionshelper

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.mittylabs.library.PermissionsHelper

class MainActivity : AppCompatActivity(), PermissionsHelper.PermissionListener {

    private lateinit var view: View
    private val helper = PermissionsHelper(this, this, Manifest.permission.ACCESS_COARSE_LOCATION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        view = findViewById<LinearLayout>(R.id.root)
        helper.check()
    }

    override fun onPermissionGranted() {
        view.snackbar(message = "Permission is granted!")
    }

    override fun onPermissionRationale(permissionRequestAction: () -> Unit) {
        view.snackbar(
            message = "Please allow the permissions",
            actionMessage = "Allow",
            action = { permissionRequestAction.invoke() })
    }

    override fun onPermissionDenied() {
        view.snackbar(
            message = "Go to settings to enable permissions",
            actionMessage = "Settings",
            action = {
                startActivity(Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    addCategory(Intent.CATEGORY_DEFAULT)
                    data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                })
            })
    }
}