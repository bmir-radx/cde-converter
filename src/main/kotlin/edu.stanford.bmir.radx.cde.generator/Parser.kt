package edu.stanford.bmir.radx.cde.generator

import java.nio.file.Path

abstract class Parser {
    abstract fun fileToDataElements(fileName: Path): List<DataElement>
}
