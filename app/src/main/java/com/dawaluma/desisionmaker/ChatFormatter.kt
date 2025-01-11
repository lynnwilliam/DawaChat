package com.dawaluma.desisionmaker

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

fun applyStyleGuide(unformattedText: String): String{

    var builder = java.lang.StringBuilder()
    var arrayData = unformattedText.split("\n").toMutableList()
    if ( arrayData.size == 1){
        return unformattedText
    }

    for (i in arrayData.indices) {
        val numberedString = startsWithNumber(arrayData[i])
        numberedString?.let{
            val replaceMeWith =  Formating.startBold + numberedString + Formating.endBold
            arrayData[i] = arrayData[i].replaceFirst(numberedString,replaceMeWith )
        }
        if (arrayData[i].endsWith(":")) {
            arrayData[i] = Formating.startBold + arrayData[i] + Formating.endBold
        }

        if (arrayData[i].contains(". ")){
            arrayData[i] = arrayData[i].replace(". ", ".\n")
        }

        if (arrayData[i].startsWith("- ")) {
            arrayData[i] = Formating.startBold + arrayData[i] + Formating.endBold
        }

        builder.append(arrayData[i]+"\n")
    }
    return builder.toString()
}

fun startsWithNumber(textline: String): String? {

    val result = textline.substringBefore('.')

    if ( result.isNullOrEmpty() || !result[0].isDigit()){
        return null
    }
    return result
}

fun parseStringWithTags(unformattedAnswer: String): AnnotatedString {

    var appliedStyletext = applyStyleGuide(unformattedAnswer)


    return buildAnnotatedString {
        val regex = Regex("@<b>(.*?)@</b>", RegexOption.DOT_MATCHES_ALL)
        var lastIndex = 0

        regex.findAll(appliedStyletext).forEach { matchResult ->

            val startIndex = matchResult.range.first
            val endIndex = matchResult.range.last + 1
            val boldText = matchResult.groups[1]?.value.orEmpty() // This captures only the text inside the tags.

            // Append non-bold text before the bold segment
            if (startIndex > lastIndex) {
                append(appliedStyletext.substring(lastIndex, startIndex))
            }

            // Append the bold text with styling and exclude the tags themselves
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(boldText) // Append only the inner bold text
            }

            lastIndex = endIndex
        }

        // Append any remaining text after the last match
        if (lastIndex < appliedStyletext.length) {
            append(appliedStyletext.substring(lastIndex))
        }
    }

}

object Formating{
    val startBold = "@<b>"
    val endBold = "@</b>"
}
