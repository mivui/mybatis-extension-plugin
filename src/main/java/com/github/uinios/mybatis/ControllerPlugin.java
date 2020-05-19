package com.github.uinios.mybatis;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.*;

/**
 * @author Jingle-Cat
 */

public class ControllerPlugin extends PluginAdapter {

    private boolean disable = false;

    private boolean rest = false;

    private String targetProject = null;

    private String targetPackage = null;

    private String basicController = null;


    @Override
    public boolean validate(List<String> warnings) {
        return disable;
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        disable = StringUtility.isTrue(properties.getProperty("disable"));
        rest = StringUtility.isTrue(properties.getProperty("rest"));
        targetProject = properties.getProperty("targetProject");
        targetPackage = properties.getProperty("targetPackage");
        basicController = properties.getProperty("basicController");
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        if (Objects.nonNull(targetPackage) && Objects.nonNull(targetProject)) {
            List<GeneratedJavaFile> files = new ArrayList<>();
            //getEntityClassName
            String recordType = introspectedTable.getBaseRecordType();
            String[] entityPackage = recordType.split("\\.");
            String className = entityPackage[entityPackage.length - 1];
            FullyQualifiedJavaType controller = new FullyQualifiedJavaType(targetPackage + "." + className + "Controller");
            TopLevelClass controllerClass = new TopLevelClass(controller);
            controllerClass.addImportedType("org.springframework.web.bind.annotation.*");
            controllerClass.setVisibility(JavaVisibility.PUBLIC);
            controllerClass.addImportedType("lombok.extern.slf4j.Slf4j");
            controllerClass.addAnnotation("@Slf4j");
            if (rest) {
                controllerClass.addAnnotation("@RestController");
            } else {
                controllerClass.addImportedType("org.springframework.stereotype.Controller");
                controllerClass.addAnnotation("@Controller");
            }
            //Lowercase
            String oldInitial = String.valueOf(className.toCharArray()[0]);
            String newInitial = String.valueOf(className.toCharArray()[0]).toLowerCase();
            String lowerClassName = className.replaceFirst(oldInitial, newInitial);
            controllerClass.addAnnotation("@RequestMapping(\"" + lowerClassName + "\")");
            //addTitleFieldForLogOutputPlaceholder
            controllerClass.addJavaDocLine("");
            controllerClass.addImportedType(FullyQualifiedJavaType.getStringInstance());
            Field contentField = new Field("content", FullyQualifiedJavaType.getStringInstance());
            contentField.setVisibility(JavaVisibility.PRIVATE);
            contentField.setFinal(true);
            contentField.setStatic(true);
            contentField.setInitializationString("\"\"");
            controllerClass.addField(contentField);

            if (Objects.nonNull(basicController)) {
                //inherit
                controllerClass.addImportedType(basicController);
                FullyQualifiedJavaType parentClass = new FullyQualifiedJavaType(basicController);
                controllerClass.addImportedType(recordType);
                parentClass.addTypeArgument(new FullyQualifiedJavaType(recordType));
                FullyQualifiedJavaType primaryKey = PluginUtils.getPrimaryKey(introspectedTable);
                if (primaryKey.isExplicitlyImported()) {
                    controllerClass.addImportedType(primaryKey);
                }
                parentClass.addTypeArgument(primaryKey);
                controllerClass.setSuperClass(parentClass);
                //constructor
                Method constructor = new Method(className + "Controller");
                constructor.setReturnType(new FullyQualifiedJavaType(""));
                constructor.setVisibility(JavaVisibility.PROTECTED);
                constructor.addBodyLine("super(content);");
                controllerClass.addMethod(constructor);
            }

            GeneratedJavaFile javaFile = new GeneratedJavaFile(controllerClass, targetProject,
                    context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                    context.getJavaFormatter());
            files.add(javaFile);
            return files;
        }
        return Collections.emptyList();
    }
}
