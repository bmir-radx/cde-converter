package edu.stanford.bmir.radx.cde.generator

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.nio.file.Path

class GCBParser : Parser() {

    private fun processResponseText(text: String): List<Value> {
        val choices = text.split(";")
        val values = mutableListOf<Value>()
        for (choice in choices) {
            val split = choice.indexOf(",")
            if (split > 0) {
                val index = choice.substring(0, split).trim().toInt()
                val value = choice.substring(split + 1).trim().lowercase()
                values.add(Value(index, value))
            }
        }
        return values
    }

    private fun determineInputType(inputText: String): String {
        return when (inputText) {
            "text" -> "Text"
            "integer" -> "Number"
            else -> "Value List"
        }
    }

    private fun dataframeToDataElements(df: List<Map<String, Any>>): List<DataElement> {
        val cdes = mutableListOf<DataElement>()
        for (row in df) {
            val response = row["Responses"].toString()
            val inputType = determineInputType(response.trim().lowercase())
            val values = if (inputType == "Value List") processResponseText(response) else emptyList()
            val dataElement = DataElement(
                    row["Variable"].toString(),
                    row["Concept"].toString(),
                    row["RADx Global Prompt"].toString(),
                    inputType,
                    values
            )
            cdes.add(dataElement)
        }
        return cdes
    }

    override fun fileToDataElements(fileName: Path): List<DataElement> {
        val workbook = XSSFWorkbook(fileName.toFile())
        val sheet = workbook.getSheet("2_RADx_Global_Codebook")
        val rows = sheet.map { row -> row.map { it.toString() } }
        val headers = rows.first()
        val data = rows.drop(1).map { row -> headers.zip(row).toMap() }
        return dataframeToDataElements(data)
    }
}

