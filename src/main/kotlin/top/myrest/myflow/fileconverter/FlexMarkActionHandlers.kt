package top.myrest.myflow.fileconverter

import java.io.File
import cn.hutool.core.io.FileUtil
import cn.hutool.core.swing.DesktopUtil
import com.vladsch.flexmark.docx.converter.DocxRenderer
import com.vladsch.flexmark.ext.definition.DefinitionExtension
import com.vladsch.flexmark.ext.emoji.EmojiExtension
import com.vladsch.flexmark.ext.footnotes.FootnoteExtension
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughSubscriptExtension
import com.vladsch.flexmark.ext.ins.InsExtension
import com.vladsch.flexmark.ext.superscript.SuperscriptExtension
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.ext.toc.SimTocExtension
import com.vladsch.flexmark.ext.toc.TocExtension
import com.vladsch.flexmark.ext.wikilink.WikiLinkExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.data.DataHolder
import com.vladsch.flexmark.util.data.MutableDataSet

class Html2MarkdownActionHandler : BaseConverterActionHandler("html2md", "logos/file/html.png", "html-to-md") {

    override fun isSupport(file: File): Boolean = file.exists() && file.isFile && file.name.endsWith(".html")

    override fun convert(file: File) {
        val name = FileUtil.mainName(file)
        val html = FileUtil.readUtf8String(file)
        val markdown = FlexmarkHtmlConverter.builder().build().convert(html)
        FileUtil.writeUtf8String(markdown, FileUtil.file(file.parentFile, "$name.md"))
        DesktopUtil.open(file.parentFile)
    }
}

class Markdown2HtmlActionHandler : BaseConverterActionHandler("html2md", "logos/file/html.png", "html-to-md") {

    override fun isSupport(file: File): Boolean = file.exists() && file.isFile && file.name.endsWith(".md")

    override fun convert(file: File) {
        val name = FileUtil.mainName(file)
        val markdown = FileUtil.readUtf8String(file)
        val document = Parser.builder().build().parse(markdown)
        val html = HtmlRenderer.builder().build().render(document)
        FileUtil.writeUtf8String(html, FileUtil.file(file.parentFile, "$name.html"))
        DesktopUtil.open(file.parentFile)
    }
}

class Markdown2DocxActionHandler : BaseConverterActionHandler("md2docx", "logos/file/word.png", "md-to-docx") {

    override fun isSupport(file: File): Boolean = file.exists() && file.isFile && file.name.endsWith(".md")

    override fun convert(file: File) {
        val name = FileUtil.mainName(file)
        val markdown = FileUtil.readUtf8String(file)
        val options = getOptions(file.parentFile)
        val parser = Parser.builder(options).build()
        val renderer = DocxRenderer.builder(options).build()
        val document = parser.parse(markdown)
        val template = DocxRenderer.getDefaultTemplate()
        renderer.render(document, template)
        template.save(FileUtil.file(file.parentFile, "$name.docx"))
        DesktopUtil.open(file.parentFile)
    }

    private fun getOptions(dir: File): DataHolder {
        return MutableDataSet().set(
            Parser.EXTENSIONS,
            listOf(
                DefinitionExtension.create(),
                EmojiExtension.create(),
                FootnoteExtension.create(),
                StrikethroughSubscriptExtension.create(),
                InsExtension.create(),
                SuperscriptExtension.create(),
                TablesExtension.create(),
                TocExtension.create(),
                SimTocExtension.create(),
                WikiLinkExtension.create(),
            ),
        ).set(DocxRenderer.SUPPRESS_HTML, true).set(DocxRenderer.DOC_RELATIVE_URL, dir.toURI().toURL().toString()).set(DocxRenderer.DOC_ROOT_URL, dir.toURI().toURL().toString())
    }
}
