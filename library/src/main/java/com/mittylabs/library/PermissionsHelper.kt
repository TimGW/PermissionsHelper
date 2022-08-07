package com.mittylabs.library

import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

class PermissionsHelper(
    activity: Context?,
    private val listener: PermissionListener,
    private val permission: String
) : ContextWrapper(activity), DefaultLifecycleObserver {

    private val requestPermissionLauncher = getActivity().registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            listener.onPermissionGranted()
        } else {
            listener.onPermissionDenied()
        }
    }

    init {
        getActivity().lifecycle.addObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        getActivity().lifecycle.removeObserver(this)
    }

    fun check() {
        if (getActivity().lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            when {
                isPermissionGranted(permission) -> listener.onPermissionGranted()
                shouldShowRequestPermissionRationale(getActivity(), permission) -> {
                    listener.onPermissionRationale {
                        requestPermissionLauncher.launch(permission)
                    }
                }
                else -> requestPermissionLauncher.launch(permission)
            }
        } else {
            throw IllegalStateException("Invalid lifecycle")
        }
    }

    private fun isPermissionGranted(permission: String) = ContextCompat.checkSelfPermission(
        this@PermissionsHelper,
        permission
    ) == PackageManager.PERMISSION_GRANTED

    private fun Context.getActivity(): FragmentActivity {
        return when (this) {
            is FragmentActivity -> this
            is AppCompatActivity -> this
            is ContextWrapper -> this.baseContext.getActivity()
            else -> throw IllegalStateException("Invalid context")
        }
    }

    interface PermissionListener {
        fun onPermissionGranted()
        fun onPermissionRationale(permissionRequestAction: () -> Unit)
        fun onPermissionDenied()
    }
}