package edu.stanford.bmir.radx.cde.generator

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import org.metadatacenter.nih.ingestor.poster.HttpRequestConstants
import org.metadatacenter.nih.ingestor.poster.RequestURLPrefixes
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class Validator(private val apiKey: String) {
//    private val httpRequester = HttpRequester(apiKey)
//    private val retryableRequester = RetryableRequester(httpRequester, "validate")
//
//    fun validate(cdes: List<ObjectNode>) {
//        for (cde in cdes) {
//            retryableRequester.validate(cde)
//        }
//    }
    private val mapper = ObjectMapper()
    private val writer = mapper.writer()
    @Throws(IOException::class)
    private fun createAndOpenConnection(urlForRequest: URL, requestMethod: String): HttpURLConnection {
        val connection = urlForRequest.openConnection() as HttpURLConnection
        connection.setRequestMethod(requestMethod)
        if (requestMethod == HttpRequestConstants.POST) {
            connection.setDoOutput(true)
            connection.setRequestProperty("Content-Type", "application/json")
        }
        connection.setRequestProperty("Accept", "application/json")
        connection.setRequestProperty("Authorization", "apiKey $apiKey")
        return connection
    }

    @Throws(IOException::class)
    private fun validateAgainstCedar(cde: ObjectNode?) {
        val urlForValidateRequest = URL(RequestURLPrefixes.validateURL)
        val connection = createAndOpenConnection(urlForValidateRequest, HttpRequestConstants.POST)
        val os = connection.outputStream
        val cdeBytes: ByteArray = writer.writeValueAsBytes(cde)
        os.write(cdeBytes)
        os.flush()
        os.close()
        val responseCode = connection.getResponseCode()
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // System.out.println(getResponseBody(connection.getInputStream()));
            connection.disconnect()
        } else {
            throw IOException()
        }
    }

    @Throws(IOException::class)
    fun validate(cdes: List<ObjectNode>) {
        for (cde in cdes) {
            validateAgainstCedar(cde)
        }
    }
}