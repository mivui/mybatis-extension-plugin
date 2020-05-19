package com.github.uinios.mybatis;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;

import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * @author Jingle-Cat
 */

public class RepositoryPlugin extends PluginAdapter {

    private String repository = null;

    private final String mysql = "MySQL";

    private String database = mysql;

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        repository = properties.getProperty("repository");
        this.database = properties.getProperty("database");
    }

    @Override
    public boolean clientGenerated(Interface interface_, IntrospectedTable introspectedTable) {
        if (Objects.nonNull(repository)) {
            //Add parent class
            interface_.addImportedType(new FullyQualifiedJavaType(repository));
            String[] split = repository.split("\\.");
            String className = split[split.length - 1];
            FullyQualifiedJavaType mybatisRepository = new FullyQualifiedJavaType(className);
            String recordType = introspectedTable.getBaseRecordType();
            interface_.addImportedType(new FullyQualifiedJavaType(recordType));
            mybatisRepository.addTypeArgument(new FullyQualifiedJavaType(recordType));
            FullyQualifiedJavaType primaryKey = PluginUtils.getPrimaryKey(introspectedTable);
            if (primaryKey.isExplicitlyImported()) {
                //Not the basic data type needs to guide package
                interface_.addImportedType(primaryKey);
            }
            mybatisRepository.addTypeArgument(primaryKey);
            interface_.addSuperInterface(mybatisRepository);
            //Add method
            if (Objects.nonNull(database)) {
                Method page = new Method("page");
                page.setDefault(true);
                //Add return type
                interface_.addImportedType(FullyQualifiedJavaType.getNewListInstance());
                FullyQualifiedJavaType listType = FullyQualifiedJavaType.getNewListInstance();
                listType.addTypeArgument(new FullyQualifiedJavaType(recordType));
                page.setReturnType(listType);
                //(int pageNum, int pageSize)
                Parameter pageNum = new Parameter(new FullyQualifiedJavaType("int"), "pageNum");
                Parameter pageSize = new Parameter(new FullyQualifiedJavaType("int"), "pageSize");
                page.addParameter(pageNum);
                page.addParameter(pageSize);
                interface_.addImportedType(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.select.SelectDSL"));
                String[] entityPackage = recordType.split("\\.");
                String entityName = entityPackage[entityPackage.length - 1];
                String oldInitial = String.valueOf(entityName.toCharArray()[0]);
                String newInitial = String.valueOf(entityName.toCharArray()[0]).toLowerCase();
                String lowerRecord = entityName.replaceFirst(oldInitial, newInitial);
                page.addBodyLine("SelectStatementProvider provider = SelectDSL.select(selectList).from(" + lowerRecord + ")");
                if (database.equalsIgnoreCase(mysql)) {
                    page.addBodyLine("        .limit(pageSize).offset((pageNum - 1) * pageSize)");
                } else {
                    page.addBodyLine("        .offset((pageNum - 1) * pageSize).fetchFirst(pageSize).rowsOnly()");
                }
                interface_.addImportedType(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.render.RenderingStrategies"));
                page.addBodyLine("        .build().render(RenderingStrategies.MYBATIS3);");
                page.addBodyLine("    return this.selectMany(provider);");
                interface_.addMethod(page);
            }
            //addRewriteAnnotation
            interface_.addImportedType(new FullyQualifiedJavaType("java.lang.Override"));
            interface_.getMethods().forEach(method -> {
                if (!method.isStatic()) {
                    method.addJavaDocLine("@Override");
                }
            });
        }
        return true;
    }

}
