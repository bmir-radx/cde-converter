package edu.stanford.bmir.radx.cde.generator

import edu.stanford.bmir.radx.datadictionary.lib.Csv
import edu.stanford.bmir.radx.datadictionary.lib.CsvParser
import org.springframework.stereotype.Component
import picocli.CommandLine
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.Callable


@Component
@CommandLine.Command(name = "convert", mixinStandardHelpOptions = true, version = ["0.0.1"],
        description = ["Convert CDE to JSON format."])
class ConverterCLI(
        private val converter: Converter,
        private val jsonWriter: JsonWriter
): Callable<Int> {

    @CommandLine.Option(names = ["-i", "--input"], required = true,
            description = ["Path to the input file containing CDEs to convert."])
    lateinit var inputFile: Path

    @CommandLine.Option(names = ["-o", "--output"], required = true,
            description = ["Path to the output file to write after conversion."])
    lateinit var outputFile: Path

    @CommandLine.Option(names = ["-f", "--format"],
            description = ["The format to convert to. Valid values: \${COMPLETION-CANDIDATES}"], required = true)
    lateinit var format: InputFormat

    @CommandLine.Option(names = ["-c", "--cedar"],
            description = ["The operation to perform against CEDAR. Valid values: \${COMPLETION-CANDIDATES}"],
            required = false)
    var cedarOperation: CedarOperation? = null

    @CommandLine.Option(names = ["-a", "--apiKey"], required = false,
            description = ["Validate against CEDAR."])
    lateinit var apiKey: String

    @CommandLine.Option(names = ["-t", "--target"], required = false,
            description = ["FolderId to which CEDAR artifacts should be uploaded."])
    lateinit var target: String

    @Throws(IOException::class)
    override fun call(): Int {
        val parser = pickParser(format)
        val dataElements = parser.fileToDataElements(inputFile)
        val artifacts = converter.convert(dataElements)
        jsonWriter.writeJsonFile(artifacts, outputFile)
        if (cedarOperation != null) {
            val requester = CedarRequester(apiKey)
            when (cedarOperation) {
                CedarOperation.VALIDATE -> requester.validate(artifacts)
                CedarOperation.UPLOAD -> requester.upload(artifacts, target)
                null -> TODO()
            }
        }
        return 0
    }

    private fun pickParser(inputFormat: InputFormat): Parser {
        return when (inputFormat) {
            InputFormat.GCB -> GCBParser()
            InputFormat.DATADICTIONARY -> DataDictParser()
        }
    }

    @Throws(IOException::class)
    private fun parseCsv(pth: Path): Csv {
        Files.newInputStream(pth).use { inputStream -> return CsvParser().parseCsv(inputStream) }
    }

    @Throws(IOException::class)
    fun convertDataDictionary(path: Path) {
        val csv = parseCsv(path)
        val dataElements = DataDictionaryConverter().convert(csv)
    }
}