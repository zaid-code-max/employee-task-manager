package com.example.employeetaskmanager

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class TaskApiClient(
    private val endpoint: String = DEFAULT_ENDPOINT,
    private val connectTimeoutMs: Int = 10_000,
    private val readTimeoutMs: Int = 10_000,
    private val connectionFactory: (URL) -> HttpURLConnection = { url ->
        url.openConnection() as HttpURLConnection
    },
    private val responseParser: (String) -> List<ApiTaskDto> = ::parseApiTasks
) {

    suspend fun fetchTasks(): Result<List<ApiTaskDto>> = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null

        try {
            connection = connectionFactory(URL(endpoint)).apply {
                requestMethod = "GET"
                connectTimeout = connectTimeoutMs
                readTimeout = readTimeoutMs
                instanceFollowRedirects = true
                setRequestProperty("Accept", "application/json")
            }

            val responseCode = connection.responseCode

            if (responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                return@withContext Result.success(emptyList())
            }

            if (responseCode !in 200..299) {
                val errorBody = connection.errorStream?.bufferedReader(Charsets.UTF_8)?.use {
                    it.readText()
                }.orEmpty()

                return@withContext Result.failure(
                    ApiHttpException(responseCode, errorBody.take(500))
                )
            }

            val responseBody = connection.inputStream.bufferedReader(Charsets.UTF_8).use {
                it.readText()
            }

            if (responseBody.isBlank()) {
                throw IllegalStateException("API returned an empty response body.")
            }

            Result.success(responseParser(responseBody))
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (exception: Exception) {
            Result.failure(exception)
        } finally {
            connection?.disconnect()
        }
    }

    companion object {
        const val DEFAULT_ENDPOINT = "https://jsonplaceholder.typicode.com/todos"
    }
}

private fun parseApiTasks(responseBody: String): List<ApiTaskDto> {
    val jsonArray = JSONArray(responseBody)
    val tasks = mutableListOf<ApiTaskDto>()

    for (index in 0 until jsonArray.length()) {
        val item = jsonArray.getJSONObject(index)
        val title = item.getString("title")

        if (title.isBlank()) {
            throw IllegalArgumentException("API task title cannot be blank.")
        }

        tasks.add(
            ApiTaskDto(
                userId = item.getInt("userId"),
                id = item.getLong("id"),
                title = title,
                completed = item.getBoolean("completed")
            )
        )
    }

    return tasks
}

class ApiHttpException(
    val statusCode: Int,
    responseBody: String
) : Exception(
    "API request failed with HTTP $statusCode" +
        if (responseBody.isNotBlank()) ": $responseBody" else ""
)
