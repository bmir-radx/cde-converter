package edu.stanford.bmir.radx.cde.generator

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.FileInputStream
import java.io.InputStream
import java.nio.file.Path

class JsonParser : Parser() {

    private val objectMapper = ObjectMapper()

    override fun fileToDataElements(path: Path): List<DataElement> {
        val inputStream: InputStream = FileInputStream(path.toFile())
        val jsonString = String(inputStream.readAllBytes())
        val tree: JsonNode = objectMapper.readTree(jsonString)
        return tree.map { readNode(it) }
    }

    private fun readNode(node: JsonNode): DataElement {
        val id = node.get("id").textValue()
        val label = node.get("label").textValue()
        val prefLabel = node.get("pref_label").textValue()
        val inputType = node.get("input_type").textValue()
        val values: List<Value> = node.get("values").map { extractValue(it) }
        return DataElement(id, label, prefLabel, inputType, values)
    }

    private fun extractValue(node: JsonNode): Value {
        val index = node.get("index").intValue()
        val text = node.get("text").textValue()
        return Value(index, text)
    }
}