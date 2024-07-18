package edu.stanford.bmir.radx.cde.generator

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact
import org.metadatacenter.artifacts.model.core.NumericType
import org.metadatacenter.artifacts.model.core.builders.FieldSchemaArtifactBuilder
import org.metadatacenter.artifacts.model.core.builders.ListFieldBuilder
import org.metadatacenter.artifacts.model.renderer.JsonSchemaArtifactRenderer
import org.springframework.stereotype.Component
import java.io.FileInputStream
import java.io.InputStream
import java.nio.file.Path

@Component
class Converter {

    private val objectMapper = ObjectMapper()
    private val renderer = JsonSchemaArtifactRenderer()

//    fun convert(path: Path): List<ObjectNode> {
//        val inputStream: InputStream = FileInputStream(path.toFile())
//        val jsonString = String(inputStream.readAllBytes())
//        val tree: JsonNode = objectMapper.readTree(jsonString)
//        return tree.map { convertNode(it) }
//    }

    fun convert(cdes: List<DataElement>): List<ObjectNode> {
        return cdes.map { convertNode(it) }
    }

    private fun convertNode(dataElement: DataElement): ObjectNode {
//    private fun convertNode(node: JsonNode): ObjectNode {
//        val dataElement: DataElement = readNode(node)
        val artifact: FieldSchemaArtifact = makeCedarArtifact(dataElement)
        return renderer.renderFieldSchemaArtifact(artifact)
    }

    private fun makeCedarArtifact(dataElement: DataElement): FieldSchemaArtifact {
        val builder = chooseBuilder(dataElement.inputType, dataElement.values)
        builder.withIdentifier(dataElement.id)
        builder.withName(dataElement.label)
        builder.withPreferredLabel(dataElement.prefLabel)
        return builder.build()
    }

    private fun chooseBuilder(inputType: String, values: List<Value>): FieldSchemaArtifactBuilder {
        val builder = when (inputType) {
            "Text" -> FieldSchemaArtifact.textFieldBuilder()
            "Number" -> FieldSchemaArtifact.numericFieldBuilder()
                    .withNumericType(NumericType.INTEGER)
            "edu.stanford.bmir.radx.cde.generator.Value List" -> getListFieldBuilder(values)
            else -> FieldSchemaArtifact.listFieldBuilder() // Throw an exception
        }
        return builder
    }

    private fun getListFieldBuilder(values: List<Value>): ListFieldBuilder {
        val builder = FieldSchemaArtifact.listFieldBuilder()
        for (value in values) {
            builder.withOption(value.toString())
        }
        builder.withMultipleChoice(false)
        return builder
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