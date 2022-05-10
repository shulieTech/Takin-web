package io.shulie.takin.web.biz.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;


/**
 * @Author: 南风
 * @Date: 2022/5/6 4:18 下午
 *
 */
@Component
public class PDFUtil {

    @Value("${pdf.path}")
    private  String PDF_DOWN_PATH ;

    public String exportPDF(String html,String pdfName) throws IOException {
        String font = this.getClass().getResource("/fonts/msyh.ttf").toString();
//        String font = "/fonts/msyh.ttf";
        String pdf = PDF_DOWN_PATH + File.separator + pdfName;
        if(FileUtil.exist(pdf)){
            //如果同名文件存在,直接返回路径
            return pdf;
        }
        return this.exportPDF(pdf,html,font);
    }

    // 导出到指定 PDF 路径
    public  String exportPDF( String pdf,String html,  String... fonts) throws DocumentException, IOException {
        ITextRenderer renderer = new ITextRenderer();
        addFont(renderer, fonts);
        FileUtil.mkParentDirs(pdf);
        try (OutputStream os = new FileOutputStream(pdf)) {
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(os);
        }
        return pdf;
    }

    // 加载准备好的字体
    private  void addFont(ITextRenderer renderer, String... fonts) throws DocumentException, IOException {
        ITextFontResolver fontResolver = renderer.getFontResolver();
        for (String font : fonts) {
            fontResolver.addFont(font, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        }
    }

    // 解析 FreeMarker 模板
    public  String parseFreemarker(String freemarker, Map<String, Object> dataModel) {
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("templates", TemplateConfig.ResourceMode.CLASSPATH));
        Template template = engine.getTemplate(freemarker);
        return template.render(dataModel);
    }


}
