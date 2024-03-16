package band.mlgb.ghmasta2.network

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri

@SuppressLint("QueryPermissionsNeeded")
fun Context.openUrl(url: String) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(url)
    }
    // Check if there's an app that can handle this intent
    // best practise to check it before starting, but if check fails on pixel 5
//    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
//    }
}
