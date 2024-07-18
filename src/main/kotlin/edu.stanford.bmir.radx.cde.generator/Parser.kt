package edu.stanford.bmir.radx.cde.generator

import java.nio.file.Path

abstract class Parser {
//    abstract fun dataframeToDataElements(df: List<Map<String, Any>>): List<DataElement>
    abstract fun fileToDataElements(fileName: Path): List<DataElement>
}
