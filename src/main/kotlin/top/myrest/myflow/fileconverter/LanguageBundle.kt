package top.myrest.myflow.fileconverter

import top.myrest.myflow.enumeration.LanguageType

class LanguageBundle {

    var languageType = LanguageType.EN_US

    var pdfToImage = "Pdf To Image"

    companion object {

        @JvmStatic
        lateinit var INSTANCE: LanguageBundle
    }
}