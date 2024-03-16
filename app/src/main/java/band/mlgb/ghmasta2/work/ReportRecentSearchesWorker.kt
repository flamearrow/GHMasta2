package band.mlgb.ghmasta2.work

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import band.mlgb.ghmasta2.data.SearchQueryRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.Calendar
import java.util.concurrent.TimeUnit

@HiltWorker
class ReportRecentSearchesWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    val searchQueryRepository: SearchQueryRepository
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        val queries = searchQueryRepository.recentSearchQueries.first()
        if (queries.isNotEmpty()) {
            Log.d(TAG, "Report recent queries - $queries")
        }

        return Result.success()
    }

    companion object {
        val TAG = "ReportRecentSearchesWork"

        /**
         * If no hilt, set the data like so, but can only do primitives
         */
        fun createOnetimeWork(): WorkRequest =
            OneTimeWorkRequestBuilder<ReportRecentSearchesWorker>().setInitialDelay(
                3,
                TimeUnit.SECONDS
            )
//                .setInputData(
//                    workDataOf(
//                        QUERIES_TO_REPORT to toReport.toTypedArray()
//                    )
//                )
                .build()

        // Note: PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS(15mins) is the min interval,
        // setting a repeat interval shorter than that will automatically change to min
        // create a initialDelay to start the job at midnight
        fun createPeriodicWork(): PeriodicWorkRequest =
            PeriodicWorkRequestBuilder<ReportRecentSearchesWorker>(
                24, TimeUnit.HOURS, 15, TimeUnit.MINUTES
            ).setInitialDelay(delayTillMidnight(), TimeUnit.SECONDS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED) // if no network, will resume once network gained
                        .setRequiresCharging(false).build()
                ).build()

        private fun delayTillMidnight(): Long {
            val currentTime = Calendar.getInstance()
            // Clone the current calendar to manipulate and set to midnight
            val midnightTime = currentTime.clone() as Calendar
            midnightTime.add(Calendar.DAY_OF_YEAR, 1) // Move to the next day
            midnightTime.set(Calendar.HOUR_OF_DAY, 0) // Set hour to midnight (0)
            midnightTime.set(Calendar.MINUTE, 0) // Set minute to 0
            midnightTime.set(Calendar.SECOND, 0) // Set second to 0
            midnightTime.set(Calendar.MILLISECOND, 0) // Set millisecond to 0

            // Calculate the difference in milliseconds between the current time and midnight
            val diffInMillis = midnightTime.timeInMillis - currentTime.timeInMillis

            // Convert the difference from milliseconds to your desired unit, e.g., seconds
            return TimeUnit.MILLISECONDS.toSeconds(diffInMillis)
        }


        const val REPORT_RECENT_QURIES_WORK: String = "reportRecentQueriesWork"
    }

}



