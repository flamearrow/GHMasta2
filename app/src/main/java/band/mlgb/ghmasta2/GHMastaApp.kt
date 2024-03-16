package band.mlgb.ghmasta2

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import band.mlgb.ghmasta2.work.ReportRecentSearchesWorker
import band.mlgb.ghmasta2.work.ReportRecentSearchesWorker.Companion.REPORT_RECENT_QURIES_WORK
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
class GHMastaApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        scheduleReportingWork()
    }

    private fun scheduleReportingWork() {
        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                REPORT_RECENT_QURIES_WORK,
                ExistingPeriodicWorkPolicy.UPDATE,
                ReportRecentSearchesWorker.createPeriodicWork(),
            )
    }
}