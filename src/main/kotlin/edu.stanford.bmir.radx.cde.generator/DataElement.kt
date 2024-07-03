package edu.stanford.bmir.radx.cde.generator

class DataElement(
        val id: String,
        val label: String,
        val prefLabel: String,
        val inputType: String,
        val values: List<Value>,
)