package com.dawaluma.desisionmaker.database

interface IncrementalNumbersInterface {
    fun getNewID(): Long
    fun getNewID(type:String): Long
}