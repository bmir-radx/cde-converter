package edu.stanford.bmir.radx.cde.generator

import com.fasterxml.jackson.databind.node.ObjectNode
import org.metadatacenter.artifacts.model.core.FieldSchemaArtifact
import org.metadatacenter.artifacts.model.core.fields.XsdNumericDatatype
import org.metadatacenter.artifacts.model.core.NumericField.NumericFieldBuilder
import org.metadatacenter.artifacts.model.core.TextField.TextFieldBuilder
import org.metadatacenter.artifacts.model.core.ListField.ListFieldBuilder
import org.metadatacenter.artifacts.model.renderer.JsonSchemaArtifactRenderer
import org.springframework.stereotype.Component

@Component
class Converter {

    private val renderer = JsonSchemaArtifactRenderer()

    fun convert(cdes: List<DataElement>): List<ObjectNode> {
        return cdes.map { convertNode(it) }
    }

    private fun convertNode(dataElement: DataElement): ObjectNode {
        val artifact: FieldSchemaArtifact = makeCedarArtifact(dataElement)
        return renderer.renderFieldSchemaArtifact(artifact)
    }

    private fun makeCedarArtifact(dataElement: DataElement): FieldSchemaArtifact {
        val builder = when (dataElement.inputType) {
            "Text" -> TextFieldBuilder()
                .withIdentifier(dataElement.id)
                .withName(dataElement.label)
                .withPreferredLabel(dataElement.prefLabel)
            "Number" -> NumericFieldBuilder()
                .withNumericType(XsdNumericDatatype.INTEGER)
                .withIdentifier(dataElement.id)
                .withName(dataElement.label)
                .withPreferredLabel(dataElement.prefLabel)
            "Value List" -> getListFieldBuilder(dataElement.values)
                .withIdentifier(dataElement.id)
                .withName(dataElement.label)
                .withPreferredLabel(dataElement.prefLabel)
            else -> ListFieldBuilder() // Throw an exception
        }
        return builder.build()
    }

    private fun getListFieldBuilder(values: List<Value>): ListFieldBuilder {
        val builder = ListFieldBuilder()
        for (value in values) {
            builder.withOption(value.toString())
        }
        builder.withIsMultiple(false)
        return builder
    }
}