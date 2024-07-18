package edu.stanford.bmir.radx.cde.generator

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.nio.charset.StandardCharsets
import kotlin.text.Regex
import java.nio.file.Files
import java.nio.file.Path

abstract class Parser {
//    abstract fun dataframeToDataElements(df: List<Map<String, Any>>): List<DataElement>
    abstract fun fileToDataElements(fileName: Path): List<DataElement>
}
