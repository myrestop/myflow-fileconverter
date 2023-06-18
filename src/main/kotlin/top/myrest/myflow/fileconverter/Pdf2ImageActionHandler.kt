package top.myrest.myflow.fileconverter

import java.io.File
import cn.hutool.core.img.ImgUtil
import cn.hutool.core.io.FileUtil
import cn.hutool.core.swing.DesktopUtil
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer
import top.myrest.myflow.AppInfo
import top.myrest.myflow.action.ActionKeywordHandler
import top.myrest.myflow.action.ActionParam
import top.myrest.myflow.action.ActionResult
import top.myrest.myflow.action.highlight
import top.myrest.myflow.action.singleCallback

class Pdf2ImageActionHandler : ActionKeywordHandler {

    private val actionId = "pdf2image"

    override fun queryAction(param: ActionParam): List<ActionResult> {
        if (param.args.isEmpty()) {
            return listOf(
                ActionResult(
                    actionId = actionId,
                    score = 100,
                    logo = "./logos/pdf2image.png",
                    title = listOf(LanguageBundle.INSTANCE.pdfToImage.highlight),
                    callbacks = singleCallback {
                        val list = AppInfo.actionWindow.showFileChooser(filenameFilter = { _, name -> name?.isPdf() == true })
                        convert2Image(list.firstOrNull())
                    },
                ),
            )
        }

        val firstArg = param.args.first()
        if (firstArg.type.isFile() && firstArg.value is File && (firstArg.value as File).name.isPdf()) {
            return listOf(
                ActionResult(
                    actionId = actionId,
                    score = 98,
                    logo = "./logos/pdf2image.png",
                    title = listOf(LanguageBundle.INSTANCE.pdfToImage.highlight),
                    callbacks = singleCallback(result = firstArg.value) { if (it is File) convert2Image(it) },
                ),
            )
        }

        return emptyList()
    }

    private fun convert2Image(pdf: File?) {
        if (pdf == null || !pdf.name.isPdf()) {
            return
        }

        val dir = pdf.parent
        val name = FileUtil.mainName(pdf)
        val doc = PDDocument.load(pdf)
        val renderer = PDFRenderer(doc)

        for (i in 0 until doc.numberOfPages) {
            val image = renderer.renderImageWithDPI(i, 300f)
            ImgUtil.write(image, File(dir + File.separator + name + "_" + (i + 1) + ".png"))
        }

        DesktopUtil.open(pdf.parentFile)
    }

    private fun String.isPdf(): Boolean = this.endsWith(".pdf")
}
