package band.mlgb.ghmasta2.network

/**
 * Wrap Data and errors
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Loading<T> : Resource<T>(data = null)
    class Result<T>(data: T) : Resource<T>(data = data) {
        fun requireData(): T = requireNotNull(data)
    }

    class Error<T>(message: String) : Resource<T>(message = message) {
        fun requireErrorMessage(): String = requireNotNull(message)
    }
}