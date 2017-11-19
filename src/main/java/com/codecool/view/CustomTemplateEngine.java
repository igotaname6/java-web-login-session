package com.codecool.view;

import com.codecool.model.User;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Template;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.context.Context;

import java.io.StringWriter;

public class CustomTemplateEngine{

    private TemplateEngine templateEngine;

    public CustomTemplateEngine(){
        //Setup templates properties
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML5");

        //initialize
        this.templateEngine= new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

    }

    public String createHtml(User user) {

        StringWriter writer = new StringWriter();
        Context context = new Context();
        context.setVariable("user", user);

        templateEngine.process("main", context, writer);
        return writer.toString();
    }
}
