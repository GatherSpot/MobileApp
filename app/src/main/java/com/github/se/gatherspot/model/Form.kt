package com.github.se.gatherspot.model


import androidx.compose.runtime.Composable

interface FormListener {

}
abstract class Form {

    private val listeners = mutableListOf<Boolean>()

    @Composable
    abstract fun Display()








    class Field(val name: String, val value: String){
        var isEdited = false
        var edit = value


        fun push() : Field {
            return Field(name, edit)
        }

        fun reset() {
            edit = value
            isEdited = false
        }

        fun edit( edit : String) {
            this.edit = edit
            isEdited = true
        }

    }

}