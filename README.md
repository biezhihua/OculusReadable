

# oculus识别的核心代码
```kotlin
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
```

# 测试步骤

1. 直接运行工程到oculus设备上
2. 安装apk到工程的/sdcard/Android/data/com.example.myapplication/files/Download/目录下
```
cd MyApplication
adb push ./apk/base.apk /sdcard/Android/data/com.example.myapplication/files/Download
```
3. 点击工程中的请求安装按钮，给权限，进行安装
4. 检查oculus可识别到