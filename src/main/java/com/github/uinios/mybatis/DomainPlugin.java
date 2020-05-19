package com.github.uinios.mybatis;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.internal.util.StringUtility;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * @author Jingle-Cat
 */

public class DomainPlugin extends PluginAdapter {

    private boolean serializable;

    private boolean dateSerialize;

    private Json json = null;

    enum Json {
        fastjon,
        jackson
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        serializable = StringUtility.isTrue(properties.getProperty("serializable"));
        dateSerialize = StringUtility.isTrue(properties.getProperty("dateSerialize"));
        String json = properties.getProperty("json");
        if (Objects.equals(json, Json.fastjon.name())) {
            this.json = Json.fastjon;
        } else if (Objects.equals(json, Json.jackson.name())) {
            this.json = Json.jackson;
        }
    }

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if (serializable) {
            topLevelClass.addImportedType("java.io.Serializable");
            topLevelClass.addSuperInterface(new FullyQualifiedJavaType("Serializable"));
        }
        if (dateSerialize) {
            final List<IntrospectedColumn> columns = introspectedTable.getAllColumns();
            boolean flag = false;
            if (Objects.nonNull(columns) && !columns.isEmpty()) {
                for (IntrospectedColumn column : columns) {
                    if (Objects.nonNull(column)) {
                        final FullyQualifiedJavaType type = column.getFullyQualifiedJavaType();
                        final String javaType = type.getShortName();
                        if (javaType.equals(LocalDate.class.getSimpleName()) ||
                                javaType.equals(LocalDateTime.class.getSimpleName())) {
                            flag = true;
                            break;
                        }
                    }
                }
            }
            if (flag) {
                if (Objects.nonNull(json)) {
                    switch (json) {
                        case fastjon:
                            final String jSONField = "com.alibaba.fastjson.annotation.JSONField";
                            topLevelClass.addImportedType(jSONField);
                            break;
                        case jackson:
                            final String jsonFormat = "com.fasterxml.jackson.annotation.JsonFormat";
                            topLevelClass.addImportedType(jsonFormat);
                            break;
                    }
                }
                final String dateTimeFormat = "org.springframework.format.annotation.DateTimeFormat";
                topLevelClass.addImportedType(dateTimeFormat);
            }
        }
        return true;
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        if (dateSerialize) {
            final FullyQualifiedJavaType type = field.getType();
            final String name = type.getShortName();
            if (Objects.equals(name, LocalDate.class.getSimpleName())) {
                if (Objects.nonNull(json)) {
                    switch (json) {
                        case fastjon:
                            field.addAnnotation("@JSONField(pattern=\"yyyy-MM-dd\")");
                            break;
                        case jackson:
                            field.addAnnotation("@JsonFormat(pattern=\"yyyy-MM-dd\")");
                            break;
                    }
                }
                field.addAnnotation("@DateTimeFormat(pattern = \"yyyy-MM-dd\")");
            } else if (Objects.equals(name, LocalDateTime.class.getSimpleName())) {
                if (Objects.nonNull(json)) {
                    switch (json) {
                        case fastjon:
                            field.addAnnotation("@JsonFormat(pattern=\"yyyy-MM-dd HH:mm:ss\")");
                            break;
                        case jackson:
                            field.addAnnotation("@JSONField(pattern=\"yyyy-MM-dd HH:mm:ss\")");
                            break;
                    }
                }
                field.addAnnotation(" @DateTimeFormat(pattern = \"yyyy-MM-dd HH:mm:ss\")");
            }
        }
        return true;
    }
}
