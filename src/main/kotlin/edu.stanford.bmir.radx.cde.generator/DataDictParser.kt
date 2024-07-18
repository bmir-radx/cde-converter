package edu.stanford.bmir.radx.cde.generator

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

class DataDictParser : Parser() {

    private fun detectEncoding(filePath: Path): String {
        val bytes = Files.readAllBytes(filePath)
        val charset = java.nio.charset.Charset.forName(StandardCharsets.UTF_8.name())
        return charset.name()
    }

    private fun extractNumber(text: String): Int {
        val match = Regex("-?\\d+").find(text)
        return match?.value?.toInt() ?: 0
    }

    private fun processResponseText(text: String): List<Value> {
        val choices = text.split("|")
        val values = mutableListOf<Value>()
        for (choice in choices) {
            val split = choice.indexOf("=")
            if (split > 0) {
                val index = extractNumber(choice.substring(0, split).trim())
                val value = choice.substring(split + 1).trim().lowercase().substring(1, choice.length - 1)
                values.add(Value(index, value))
            }
        }
        return values
    }

    private fun determineInputType(response: String, hasEnumerations: Boolean): String {
        return when (response) {
            "string" -> "Text"
            "decimal" -> "Number"
            "dateTime" -> "Date"
            "integer", "int" -> if (hasEnumerations) "Value List" else "Number"
            "boolean" -> "Value List"
            else -> "Value List"
        }
    }

    private fun dataframeToDataElements(df: List<Map<String, Any>>): List<DataElement> {
        val cdes = mutableListOf<DataElement>()
        for (row in df) {
            val identifier = row["Id"].toString()
            val label = row["Label"].toString()
            val datatypeText = row["Datatype"].toString()
            val valueText = row["Enumeration"].toString()
            val values = if (valueText.isNotEmpty()) processResponseText(valueText) else emptyList()
            val missingValueText = row["Additional Missing Value Codes"].toString()
            val missingValues = if (missingValueText.isNotEmpty()) processResponseText(missingValueText) else emptyList()
            val datatype = determineInputType(datatypeText, values.isNotEmpty())
            val description = row["Description"]?.toString() ?: label
            val dataElement = DataElement(
                    identifier,
                    label,
                    description,
                    datatype,
                    values
            )
            cdes.add(dataElement)
        }
        return cdes
    }

    override fun fileToDataElements(fileName: Path): List<DataElement> {
        val encoding = detectEncoding(fileName)
        val df = Files.readAllLines(fileName, java.nio.charset.Charset.forName(encoding))
                .drop(1)
                .map { it.split(",") }
                .map { it.map { value -> value.trim() } }
        val headers = df.first()
        val data = df.drop(1).map { row -> headers.zip(row).toMap() }
        return dataframeToDataElements(data)
    }
}
