package edu.stanford.bmir.radx.cde.generator

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.node.ObjectNode
import org.springframework.stereotype.Component
import java.io.File

@Component
class JsonWriter {

    private val objectMapper = ObjectMapper()

    fun writeJsonFile(dataElements: List<ObjectNode>, fileName: String) {
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.writeValue(File(fileName), dataElements)
    }
}