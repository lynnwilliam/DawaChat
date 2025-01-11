package com.dawaluma.desisionmaker.querybuilder

data class Query(
    val query: String,
    val level: Int,
    var answer: String? = null,
    val chain: MutableList<Query> = mutableListOf()
) {
    // Function to add an item to the list
    fun addItem(item: Query) {
        chain.add(item)
    }
}