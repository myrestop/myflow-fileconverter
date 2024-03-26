package top.myrest.myflow.fileconverter

import java.io.File
import cn.hutool.core.io.FileUtil
import cn.hutool.core.swing.DesktopUtil
import cn.hutool.core.text.csv.CsvReadConfig
import cn.hutool.core.text.csv.CsvUtil
import top.myrest.myflow.AppInfo
import top.myrest.myflow.action.ActionKeywordHandler
import top.myrest.myflow.action.ActionParam
import top.myrest.myflow.action.ActionResult
import top.myrest.myflow.action.highlight
import top.myrest.myflow.action.singleCallback
import top.myrest.myflow.language.LanguageBundle
import top.myrest.myflow.util.Jackson.toJsonString

class Csv2JsonActionHandler : ActionKeywordHandler {

    private val actionId = "csv2json"

    override fun queryAction(param: ActionParam): List<ActionResult> {
        if (param.args.isEmpty()) {
            return listOf(
                ActionResult(
                    actionId = actionId,
                    score = 100,
                    logo = "logos/file/csv.png",
                    title = listOf(LanguageBundle.getBy(Constants.PLUGIN_ID, "csv-to-json").highlight),
                    callbacks = singleCallback {
                        val list = AppInfo.actionWindow.showFileChooser(filenameFilter = { _, name -> name?.isCsv() == true })
                        convert2Json(list.firstOrNull())
                    },
                ),
            )
        }

        val firstArg = param.args.first()
        if (firstArg.type.isFile() && firstArg.value is File && (firstArg.value as File).name.isCsv()) {
            return listOf(
                ActionResult(
                    actionId = actionId,
                    score = 98,
                    logo = "logos/file/csv.png",
                    title = listOf(LanguageBundle.getBy(Constants.PLUGIN_ID, "csv-to-json").highlight),
                    callbacks = singleCallback(result = firstArg.value) { if (it is File) convert2Json(it) },
                ),
            )
        }

        return emptyList()
    }

    private fun convert2Json(csv: File?) {
        if (csv == null || !csv.name.isCsv()) {
            return
        }

        val name = FileUtil.mainName(csv)
        val reader = CsvUtil.getReader(CsvReadConfig().setHeaderLineNo(0))
        val csvData = reader.read(csv)
        val json = csvData.rows.map { it.fieldMap }.toJsonString(true)
        FileUtil.writeUtf8String(json, FileUtil.file(csv.parentFile, "$name.json"))

        DesktopUtil.open(csv.parentFile)
    }

    private fun String.isCsv(): Boolean = this.endsWith(".csv")
}