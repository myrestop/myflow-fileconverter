package top.myrest.myflow.fileconverter

import java.io.File
import cn.hutool.core.io.FileUtil
import cn.hutool.core.swing.DesktopUtil
import cn.hutool.core.text.csv.CsvReadConfig
import cn.hutool.core.text.csv.CsvUtil
import top.myrest.myflow.util.Jackson.toJsonString

class Csv2JsonActionHandler : BaseConverterActionHandler("csv2json", "logos/file/csv.png", "csv-to-json") {

    override fun isSupport(file: File): Boolean = file.exists() && file.name.endsWith(".csv")

    override fun convert(file: File) {
        val name = FileUtil.mainName(file)
        val reader = CsvUtil.getReader(CsvReadConfig().setHeaderLineNo(0))
        val csvData = reader.read(file)
        val json = csvData.rows.map { it.fieldMap }.toJsonString(true)
        FileUtil.writeUtf8String(json, FileUtil.file(file.parentFile, "$name.json"))
        DesktopUtil.open(file.parentFile)
    }
}