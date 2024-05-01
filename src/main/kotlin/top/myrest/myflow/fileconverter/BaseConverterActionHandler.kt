package top.myrest.myflow.fileconverter

import java.io.File
import top.myrest.myflow.AppInfo
import top.myrest.myflow.action.ActionKeywordHandler
import top.myrest.myflow.action.ActionParam
import top.myrest.myflow.action.ActionResult
import top.myrest.myflow.action.highlight
import top.myrest.myflow.action.singleCallback
import top.myrest.myflow.language.LanguageBundle

abstract class BaseConverterActionHandler(
    val actionId: String,
    val logo: String,
    val nameBundleId: String,
) : ActionKeywordHandler {

    abstract fun isSupport(file: File): Boolean

    abstract fun convert(file: File)

    override fun queryAction(param: ActionParam): List<ActionResult> {
        if (param.args.isEmpty()) {
            return listOf(
                ActionResult(
                    actionId = actionId,
                    score = 100,
                    logo = logo,
                    title = listOf(LanguageBundle.getBy(Constants.PLUGIN_ID, nameBundleId).highlight),
                    callbacks = singleCallback(
                        label = AppInfo.currLanguageBundle.shared.execute,
                    ) {
                        val file = AppInfo.actionWindow.showFileChooser(filenameFilter = { file, _ -> isSupport(file) }).firstOrNull()
                        if (file != null && isSupport(file)) {
                            convert(file)
                        }
                    },
                ),
            )
        }

        val firstArg = param.args.first()
        if (firstArg.type.isFile() && firstArg.value is File && isSupport(firstArg.value as File)) {
            return listOf(
                ActionResult(
                    actionId = actionId,
                    score = 98,
                    logo = logo,
                    title = listOf(LanguageBundle.getBy(Constants.PLUGIN_ID, nameBundleId).highlight),
                    callbacks = singleCallback(
                        result = firstArg.value,
                        label = AppInfo.currLanguageBundle.shared.execute,
                    ) {
                        if (it is File && isSupport(it)) convert(it)
                    },
                ),
            )
        }

        return emptyList()
    }
}