package com.example.twostepauthui

data class OtpState(
    val code : List<Int?> = (1..4).map { null }, //This will store the digit till we had typed
    val focussedIndex : Int? = null, //Index of the currently focused text field
    val isValid : Boolean? = null,

)
