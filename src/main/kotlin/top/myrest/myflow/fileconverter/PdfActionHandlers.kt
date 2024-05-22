package top.myrest.myflow.fileconverter

import java.io.File
import cn.hutool.core.img.ImgUtil
import cn.hutool.core.io.FileUtil
import cn.hutool.core.swing.DesktopUtil
import com.aspose.pdf.Document
import com.aspose.pdf.SaveFormat
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer

class Pdf2ImageActionHandler : BaseConverterActionHandler("pdf2image", "./logos/pdf2image.png", "pdf-to-image") {

    override fun isSupport(file: File): Boolean = file.exists() && file.isFile && file.name.endsWith(".pdf")

    override fun convert(file: File) {
        val name = FileUtil.mainName(file)
        PDDocument.load(file).use { doc ->
            val renderer = PDFRenderer(doc)
            for (i in 0 until doc.numberOfPages) {
                val image = renderer.renderImageWithDPI(i, 300f)
                ImgUtil.write(image, FileUtil.file(file.parentFile, name + "_" + (i + 1) + ".png"))
            }
        }
        DesktopUtil.open(file.parentFile)
    }
}

class Pdf2DocxActionHandler : BaseConverterActionHandler("pdf2docx", "logos/file/pdf.png", "pdf-to-docx") {

    override fun isSupport(file: File): Boolean = file.exists() && file.isFile && file.name.endsWith(".pdf")

    override fun convert(file: File) {
        val name = FileUtil.mainName(file)
        val document = Document(FileUtil.getInputStream(file))
        document.save(FileUtil.file(file.parentFile, "$name.docx").absolutePath, SaveFormat.DocX)
        DesktopUtil.open(file.parentFile)
    }
}
