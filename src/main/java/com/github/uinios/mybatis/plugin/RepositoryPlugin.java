package com.github.uinios.mybatis.plugin;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.*;

import static com.github.uinios.mybatis.plugin.utils.PluginUtils.*;

/**
 * @author Jingle-Cat
 */

public class RepositoryPlugin extends PluginAdapter {

    private String repository = null;

    private String uuid = null;

    private boolean mysql;

    private boolean separationPackage;

    private TopLevelClass dynamicSqlClass = null;

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        repository = properties.getProperty("repository");
        uuid = properties.getProperty("uuid");
        mysql = StringUtility.isTrue(properties.getProperty("mysql"));
        separationPackage = StringUtility.isTrue(properties.getProperty("separationPackage"));
    }

    @Override
    public boolean dynamicSqlSupportGenerated(TopLevelClass supportClass, IntrospectedTable introspectedTable) {
        removeGeneratedAnnotation(supportClass);
        if (separationPackage) {
            String sqlPackage = getDynamicSqlPackage(introspectedTable);
            //Get entity class name
            String recordType = introspectedTable.getBaseRecordType();
            String[] entityPackage = recordType.split("\\.");
            String className = entityPackage[entityPackage.length - 1];
            FullyQualifiedJavaType dynamicSql = new FullyQualifiedJavaType(sqlPackage + "." + className + "DynamicSql");
            this.dynamicSqlClass = new TopLevelClass(dynamicSql);
            dynamicSqlClass.setVisibility(JavaVisibility.PUBLIC);
            dynamicSqlClass.setFinal(true);
            //importedType
            Set<FullyQualifiedJavaType> importedTypes = supportClass.getImportedTypes();
            if (Objects.nonNull(importedTypes) && !importedTypes.isEmpty()) {
                importedTypes.forEach(importedType -> dynamicSqlClass.addImportedType(importedType));
            }
            List<Field> fields = supportClass.getFields();
            if (Objects.nonNull(fields) && !fields.isEmpty()) {
                fields.forEach(field -> dynamicSqlClass.addField(field));
            }
            List<InnerClass> innerClasses = supportClass.getInnerClasses();
            if (Objects.nonNull(innerClasses) && !innerClasses.isEmpty()) {
                innerClasses.forEach(innerClass -> dynamicSqlClass.addInnerClass(innerClass));
            }
        }
        return !separationPackage;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        List<GeneratedJavaFile> files = new ArrayList<>();
        if (separationPackage) {
            GeneratedJavaFile javaFile = new GeneratedJavaFile(dynamicSqlClass,
                    introspectedTable.getContext().getJavaClientGeneratorConfiguration().getTargetProject(),
                    context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                    context.getJavaFormatter());
            files.add(javaFile);
            return files;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean clientGenerated(Interface interface_, IntrospectedTable introspectedTable) {
        //DynamicSql config
        if (separationPackage) {
            Set<String> staticImports = interface_.getStaticImports();
            if (Objects.nonNull(staticImports) && !staticImports.isEmpty()) {
                Context context = introspectedTable.getContext();
                if (Objects.nonNull(context)) {
                    //getEntityClassName
                    String recordType = introspectedTable.getBaseRecordType();
                    String[] entityPackage = recordType.split("\\.");
                    String className = entityPackage[entityPackage.length - 1];
                    String targetPackage = context.getJavaClientGeneratorConfiguration().getTargetPackage();
                    String oldDynamicSqlClass = targetPackage + "." + className + "DynamicSqlSupport.*";
                    String dynamicSqlClass = getDynamicSqlPackage(introspectedTable) + "." + className + "DynamicSql.*";
                    staticImports.removeIf(importedType -> Objects.equals(importedType, oldDynamicSqlClass));
                    interface_.addStaticImport(dynamicSqlClass);
                }
            }
        }
        //removeGeneratedAnnotation
        removeGeneratedAnnotation(interface_);
        Optional<FullyQualifiedJavaType> optional = primaryKeyType(introspectedTable);
        if (optional.isPresent()) {
            String recordType = introspectedTable.getBaseRecordType();
            FullyQualifiedJavaType primaryKeyType = optional.get();
            if (Objects.nonNull(repository)) {
                interface_.addImportedType(new FullyQualifiedJavaType(repository));
                String[] repositorySplit = repository.split("\\.");
                String repositoryClassName = repositorySplit[repositorySplit.length - 1];
                FullyQualifiedJavaType mybatisRepository = new FullyQualifiedJavaType(repositoryClassName);
                interface_.addImportedType(new FullyQualifiedJavaType(recordType));
                mybatisRepository.addTypeArgument(new FullyQualifiedJavaType(recordType));
                if (primaryKeyType.isExplicitlyImported()) {
                    //Not the basic data type needs to guide package
                    interface_.addImportedType(primaryKeyType);
                }
                mybatisRepository.addTypeArgument(primaryKeyType);
                interface_.addSuperInterface(mybatisRepository);
            }
            //Uppercase primary key name
            Optional<String> keyName = primaryKeyName(introspectedTable);
            if (keyName.isPresent()) {
                String idName = keyName.get();
                String oldIdInitial = String.valueOf(idName.toCharArray()[0]);
                String newIdInitial = String.valueOf(idName.toCharArray()[0]).toUpperCase();
                String uppercaseIdName = idName.replaceFirst(oldIdInitial, newIdInitial);
                if (Objects.equals(uuid, "true")) {
                    interface_.addImportedType(new FullyQualifiedJavaType("java.util.*"));
                    //update insert Method
                    List<String> insertBodyLines = new ArrayList<>();
                    insertBodyLines.add("if (Objects.nonNull(record)) {");
                    if (Objects.equals(uuid, "uuid")) {
                        insertBodyLines.add("    record.set" + uppercaseIdName + "(UUID.randomUUID().toString().replaceAll(\"-\",\"\").trim());");
                    } else {
                        insertBodyLines.add("    record.set" + uppercaseIdName + "(UUID.randomUUID().toString());");
                    }
                    insertBodyLines.add("}");
                    updateMethod(interface_, "insert", new Parameter(new FullyQualifiedJavaType(recordType), "record"), insertBodyLines);

                    //update insertSelective Method
                    List<String> insertSelectiveBodyLines = new ArrayList<>();
                    insertSelectiveBodyLines.add("if (Objects.nonNull(record)) {");
                    if (Objects.equals(uuid, "uuid")) {
                        insertSelectiveBodyLines.add("    record.set" + uppercaseIdName + "(UUID.randomUUID().toString().replaceAll(\"-\",\"\").trim());");
                    } else {
                        insertSelectiveBodyLines.add("    record.set" + uppercaseIdName + "(UUID.randomUUID().toString());");
                    }
                    insertSelectiveBodyLines.add("}");
                    updateMethod(interface_, "insertSelective", new Parameter(new FullyQualifiedJavaType(recordType), "record"), insertSelectiveBodyLines);

                    //update insertMultiple Method
                    FullyQualifiedJavaType insertMultipleParam = new FullyQualifiedJavaType("java.util.Collection");
                    insertMultipleParam.addTypeArgument(new FullyQualifiedJavaType(recordType));
                    List<String> insertMultipleBodyLines = new ArrayList<>();
                    insertMultipleBodyLines.add("if (Objects.nonNull(records) && !records.isEmpty()) {");
                    if (Objects.equals(uuid, "uuid")) {
                        insertMultipleBodyLines.add("    records.forEach(record -> record.set" + uppercaseIdName + "(UUID.randomUUID().toString().replaceAll(\"-\",\"\").trim()));");
                    } else {
                        insertMultipleBodyLines.add("    records.forEach(record -> record.set" + uppercaseIdName + "(UUID.randomUUID().toString()));");
                    }
                    insertMultipleBodyLines.add("}");
                    updateMethod(interface_, "insertMultiple", new Parameter(insertMultipleParam, "records"), insertMultipleBodyLines);
                }
                //deleteMultiple Method
                Method deleteMultiple = new Method("deleteMultiple");
                deleteMultiple.setDefault(true);
                deleteMultiple.setReturnType(new FullyQualifiedJavaType("int"));
                Parameter deleteMultipleParameter = new Parameter(primaryKeyType, "[] " + idName + "s_");
                deleteMultiple.addParameter(deleteMultipleParameter);
                deleteMultiple.addBodyLine("return delete(c ->");
                deleteMultiple.addBodyLine("        c.where(" + idName + ", isInWhenPresent(" + idName + "s_)));");
                interface_.addMethod(deleteMultiple);
                //updateMultiple Method
                Method updateMultiple = new Method("updateMultiple");
                updateMultiple.setDefault(true);
                updateMultiple.setReturnType(new FullyQualifiedJavaType("int"));
                FullyQualifiedJavaType newListInstance = FullyQualifiedJavaType.getNewListInstance();
                newListInstance.addTypeArgument(new FullyQualifiedJavaType(recordType));
                Parameter updateBatchParameter = new Parameter(newListInstance, "records");
                updateMultiple.addParameter(updateBatchParameter);
                updateMultiple.addBodyLine("return records.stream().mapToInt(this::updateByPrimaryKeySelective).sum();");
                interface_.addMethod(updateMultiple);
            }

            //page Method
            Method page = new Method("page");
            page.setDefault(true);
            interface_.addImportedType(FullyQualifiedJavaType.getNewListInstance());
            FullyQualifiedJavaType listType = FullyQualifiedJavaType.getNewListInstance();
            listType.addTypeArgument(new FullyQualifiedJavaType(recordType));
            page.setReturnType(listType);
            Parameter pageNum = new Parameter(new FullyQualifiedJavaType("int"), "pageNum");
            Parameter pageSize = new Parameter(new FullyQualifiedJavaType("int"), "pageSize");
            page.addParameter(pageNum);
            page.addParameter(pageSize);
            if (mysql) {
                page.addBodyLine("return this.select(select -> select.limit(pageSize).offset((pageNum - 1) * pageSize));");
            } else {
                page.addBodyLine("return this.select(select -> select.offset((pageNum - 1) * pageSize).fetchFirst(pageSize).rowsOnly());");
            }
            interface_.addMethod(page);
        }
        if (Objects.nonNull(repository)) {
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
