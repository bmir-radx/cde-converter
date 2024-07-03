package edu.stanford.bmir.radx.cde.generator

import org.springframework.stereotype.Component
import picocli.CommandLine
import java.io.IOException
import java.util.concurrent.Callable

@Component
@CommandLine.Command(name = "convert", mixinStandardHelpOptions = true, version = ["0.0.1"],
        description = ["Convert CDE to JSON format."])
class ConvertCommand(
        private val converter: Converter,
        private val jsonWriter: JsonWriter
): Callable<Int> {
//    private val converter = Converter()
//    private val jsonWriter = JsonWriter()

    @CommandLine.Option(names = ["-i", "--input"], required = true,
            description = ["Path to the input file containing CDEs to convert."])
    lateinit var inputFile: String

    @CommandLine.Option(names = ["-o", "--output"], required = true,
            description = ["Path to the output file to write after conversion."])
    lateinit var outputFile: String

    @Throws(IOException::class)
    override fun call(): Int {
        val dataElements = converter.convert(inputFile)
        jsonWriter.writeJsonFile(dataElements, outputFile)
        return 0
    }

}