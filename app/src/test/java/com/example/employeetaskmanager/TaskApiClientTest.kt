package com.example.employeetaskmanager

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.SocketTimeoutException

class TaskApiClientTest {

    @Test
    fun fetchTasks_successfulResponse_returnsParsedTasksAndSetsGetHeaders() = runBlocking {
        val expectedTask = ApiTaskDto(
            userId = 7,
            id = 31L,
            title = "Prepare report",
            completed = false
        )

        var fakeConnection: FakeHttpURLConnection? = null
        var parsedBody: String? = null

        val client = TaskApiClient(
            endpoint = "https://example.test/todos",
            connectionFactory = { url ->
                FakeHttpURLConnection(
                    url = url,
                    statusCode = HttpURLConnection.HTTP_OK,
                    responseBody = "test-response"
                ).also { fakeConnection = it }
            },
            responseParser = { body ->
                parsedBody = body
                listOf(expectedTask)
            }
        )

        val result = client.fetchTasks()

        assertTrue(result.isSuccess)
        assertEquals(listOf(expectedTask), result.getOrThrow())
        assertEquals("test-response", parsedBody)

        val connection = fakeConnection
        assertNotNull(connection)
        assertEquals("GET", connection!!.requestMethod)
        assertEquals("application/json", connection.getRequestProperty("Accept"))
        assertTrue(connection.wasDisconnected)
    }

    @Test
    fun fetchTasks_noContent_returnsEmptyList() = runBlocking {
        var parserCalled = false
        var fakeConnection: FakeHttpURLConnection? = null

        val client = TaskApiClient(
            endpoint = "https://example.test/todos",
            connectionFactory = { url ->
                FakeHttpURLConnection(
                    url = url,
                    statusCode = HttpURLConnection.HTTP_NO_CONTENT
                ).also { fakeConnection = it }
            },
            responseParser = {
                parserCalled = true
                emptyList()
            }
        )

        val result = client.fetchTasks()

        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().isEmpty())
        assertTrue(!parserCalled)
        assertTrue(fakeConnection?.wasDisconnected == true)
    }

    @Test
    fun fetchTasks_serverError_returnsHttpException() = runBlocking {
        var fakeConnection: FakeHttpURLConnection? = null

        val client = TaskApiClient(
            endpoint = "https://example.test/todos",
            connectionFactory = { url ->
                FakeHttpURLConnection(
                    url = url,
                    statusCode = HttpURLConnection.HTTP_UNAVAILABLE,
                    errorBody = "Service unavailable"
                ).also { fakeConnection = it }
            }
        )

        val result = client.fetchTasks()
        val exception = result.exceptionOrNull()

        assertTrue(exception is ApiHttpException)
        assertEquals(503, (exception as ApiHttpException).statusCode)
        assertTrue(exception.message!!.contains("Service unavailable"))
        assertTrue(fakeConnection?.wasDisconnected == true)
    }

    @Test
    fun fetchTasks_internalServerError_returnsHttpExceptionAndDisconnects() = runBlocking {
        var fakeConnection: FakeHttpURLConnection? = null

        val client = TaskApiClient(
            endpoint = "https://example.test/todos",
            connectionFactory = { url ->
                FakeHttpURLConnection(
                    url = url,
                    statusCode = HttpURLConnection.HTTP_INTERNAL_ERROR,
                    errorBody = "Internal Server Error"
                ).also { fakeConnection = it }
            }
        )

        val result = client.fetchTasks()
        val exception = result.exceptionOrNull()

        assertTrue(exception is ApiHttpException)
        assertEquals(500, (exception as ApiHttpException).statusCode)
        assertTrue(exception.message!!.contains("Internal Server Error"))
        assertTrue(fakeConnection?.wasDisconnected == true)
    }

    @Test
    fun fetchTasks_readTimeout_returnsFailureAndDisconnects() = runBlocking {
        var fakeConnection: FakeHttpURLConnection? = null

        val timeoutException = SocketTimeoutException("Read timed out")

        val client = TaskApiClient(
            endpoint = "https://example.test/todos",
            connectTimeoutMs = 1000,
            readTimeoutMs = 1250,
            connectionFactory = { url ->
                FakeHttpURLConnection(
                    url = url,
                    statusCode = HttpURLConnection.HTTP_OK,
                    responseCodeException = timeoutException
                ).also { fakeConnection = it }
            }
        )

        val result = client.fetchTasks()
        val exception = result.exceptionOrNull()

        assertTrue(result.isFailure)
        assertTrue(exception is SocketTimeoutException)
        assertEquals("Read timed out", exception?.message)
        assertEquals(1250, fakeConnection?.readTimeout)
        assertTrue(fakeConnection?.wasDisconnected == true)
    }

    @Test
    fun fetchTasks_malformedJson_returnsFailureAndDisconnects() = runBlocking {
        var fakeConnection: FakeHttpURLConnection? = null

        val client = TaskApiClient(
            endpoint = "https://example.test/todos",
            connectionFactory = { url ->
                FakeHttpURLConnection(
                    url = url,
                    statusCode = HttpURLConnection.HTTP_OK,
                    responseBody = """[{"userId":1,"id":1,"title":"""
                ).also { fakeConnection = it }
            }
        )

        val result = client.fetchTasks()

        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
        assertTrue(fakeConnection?.wasDisconnected == true)
    }

    @Test
    fun fetchTasks_missingRequiredField_returnsFailureAndDisconnects() = runBlocking {
        var fakeConnection: FakeHttpURLConnection? = null

        val client = TaskApiClient(
            endpoint = "https://example.test/todos",
            connectionFactory = { url ->
                FakeHttpURLConnection(
                    url = url,
                    statusCode = HttpURLConnection.HTTP_OK,
                    responseBody = """[{"userId":1,"id":1,"title":"Prepare report"}]"""
                ).also { fakeConnection = it }
            }
        )

        val result = client.fetchTasks()

        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
        assertTrue(fakeConnection?.wasDisconnected == true)
    }

    @Test
    fun fetchTasks_blankResponseBody_returnsFailure() = runBlocking {
        var fakeConnection: FakeHttpURLConnection? = null

        val client = TaskApiClient(
            endpoint = "https://example.test/todos",
            connectionFactory = { url ->
                FakeHttpURLConnection(
                    url = url,
                    statusCode = HttpURLConnection.HTTP_OK,
                    responseBody = ""
                ).also { fakeConnection = it }
            }
        )

        val result = client.fetchTasks()

        assertTrue(result.isFailure)
        assertTrue(
            result.exceptionOrNull() is IllegalStateException
        )
        assertTrue(fakeConnection?.wasDisconnected == true)
    }

    @Test
    fun fetchTasks_networkFailure_returnsFailure() = runBlocking {
        val client = TaskApiClient(
            endpoint = "https://example.test/todos",
            connectionFactory = {
                throw IOException("Network unavailable")
            }
        )

        val result = client.fetchTasks()

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IOException)
        assertEquals(
            "Network unavailable",
            result.exceptionOrNull()?.message
        )
    }
}

private class FakeHttpURLConnection(
    url: URL,
    private val statusCode: Int,
    private val responseBody: String = "",
    private val errorBody: String = "",
    private val responseCodeException: IOException? = null
) : HttpURLConnection(url) {

    var wasDisconnected = false
        private set

    override fun connect() {
        connected = true
    }

    override fun disconnect() {
        wasDisconnected = true
        connected = false
    }

    override fun usingProxy(): Boolean = false

    override fun getResponseCode(): Int {
        responseCodeException?.let { throw it }
        return statusCode
    }

    override fun getInputStream(): InputStream {
        return ByteArrayInputStream(responseBody.toByteArray(Charsets.UTF_8))
    }

    override fun getErrorStream(): InputStream? {
        return if (errorBody.isEmpty()) {
            null
        } else {
            ByteArrayInputStream(errorBody.toByteArray(Charsets.UTF_8))
        }
    }
}
