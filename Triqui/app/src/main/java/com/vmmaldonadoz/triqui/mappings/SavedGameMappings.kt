package com.vmmaldonadoz.triqui.mappings

private const val SEPARATOR = "::"

fun CharArray.mapToString(): String {
    return this.joinToString(SEPARATOR)
}

fun String.mapToCharArray(): CharArray {
    return if (this.isBlank()) {
        CharArray(9) { ' ' }
    } else {
        this.replace(SEPARATOR, "").toCharArray()
    }
}