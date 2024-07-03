package edu.stanford.bmir.radx.cde.generator

class Value(
        val index: Int,
        val text: String,
) {

    override fun toString(): String {
        return "$index: $text"
    }
}