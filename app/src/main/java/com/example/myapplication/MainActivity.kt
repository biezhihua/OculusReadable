package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File


class MainActivity : AppCompatActivity() {


    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        externalFilesDir?.mkdirs()
    }

    private fun fixCustomHome() {
        val packageManager = packageManager
        val installedApplications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        for (applicationInfo in installedApplications) {
            try {
                val metaData = applicationInfo.metaData
                if (metaData != null) {
                    val installerPackageName = packageManager.getInstallerPackageName(applicationInfo.packageName)
                    val int = metaData.getInt("com.oculus.environmentVersion", 0)
                    if (int >= 1) {
                        if (installerPackageName != null && installerPackageName == packageName) {
                            // 清空安装包的installer，这样能让oculus识别出来
                            packageManager.setInstallerPackageName(applicationInfo.packageName, null)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private var installLocal = registerForActivityResult(
        StartActivityForResult()
    ) { result ->
        Log.d("TAG", "onActivityResult() called with: result = $result")
        fixCustomHome()
    }

    private fun requestPermission() {
//        val canRequestPackageInstalls = packageManager.canRequestPackageInstalls()
//        if (!canRequestPackageInstalls) {
//            startInstallPermissionSettingActivity()
//        }
        installApk()
    }

    @SuppressLint("WrongConstant")
    private fun installApk() {
        try {
            // 只能放在内部路径，否则会造成包解析失败
            // /sdcard/Android/data/com.example.myapplication/files/Download/base.apk
            val externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val apk = File(
                externalFilesDir,
                "base.apk"
            )
            val uriForFile = FileProvider.getUriForFile(this, "test.provider", apk)
            val intent = Intent("android.intent.action.INSTALL_PACKAGE")
            intent.setDataAndType(uriForFile, "application/vnd.android.package-archive")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME, packageName)
            intent.putExtra(Intent.EXTRA_RETURN_RESULT, true)
            installLocal.launch(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onRequestPermission(view: View) {
        requestPermission()
    }

    private fun startInstallPermissionSettingActivity() {
        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        this.startActivity(intent)
    }

}