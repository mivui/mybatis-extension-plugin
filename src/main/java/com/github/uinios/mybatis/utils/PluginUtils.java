package com.github.uinios.mybatis.utils;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * @author Jingle-Cat
 */

public class PluginUtils {

    private PluginUtils() {

    }

    public static Optional<FullyQualifiedJavaType> primaryKeyType(IntrospectedTable introspectedTable) {
        if (Objects.nonNull(introspectedTable.getGeneratedKey())) {
            String primaryKey = introspectedTable.getGeneratedKey().getColumn();
            Optional<IntrospectedColumn> optional = introspectedTable.getColumn(primaryKey);
            if (optional.isPresent()) {
                IntrospectedColumn keyColumn = optional.get();
                return Optional.ofNullable(keyColumn.getFullyQualifiedJavaType());
            }
        }
        return Optional.empty();
    }

    public static Optional<String> primaryKeyName(IntrospectedTable introspectedTable) {
        if (Objects.nonNull(introspectedTable.getGeneratedKey())) {
            String primaryKey = introspectedTable.getGeneratedKey().getColumn();
            Optional<IntrospectedColumn> optional = introspectedTable.getColumn(primaryKey);
            if (optional.isPresent()) {
                IntrospectedColumn keyColumn = optional.get();
                return Optional.ofNullable(keyColumn.getJavaProperty());
            }
        }
        return Optional.empty();
    }

    public static void restfulMethod(Method method, boolean rest) {
        if (!rest) {
            method.addAnnotation("@ResponseBody");
        }
        method.setVisibility(JavaVisibility.PUBLIC);
    }

    public static void removeGeneratedAnnotation(Object object) {
        if (Objects.nonNull(object)) {
            if (object instanceof TopLevelClass) {
                TopLevelClass topLevelClass = ( TopLevelClass ) object;
                Set<FullyQualifiedJavaType> importedTypes = topLevelClass.getImportedTypes();
                if (Objects.nonNull(importedTypes) && !importedTypes.isEmpty()) {
                    importedTypes.removeIf(importedType -> Objects.equals(importedType.getFullyQualifiedName(), "javax.annotation.Generated"));
                }
                List<Field> fields = topLevelClass.getFields();
                if (Objects.nonNull(fields) && !fields.isEmpty()) {
                    fields.get(0).addJavaDocLine("");
                    actionRemoveGeneratedAnnotation(fields);
                }
                List<Method> methods = topLevelClass.getMethods();
                if (Objects.nonNull(methods) && !methods.isEmpty()) {
                    actionRemoveGeneratedAnnotation(topLevelClass.getMethods());
                }
                List<InnerClass> innerClasses = topLevelClass.getInnerClasses();
                if (Objects.nonNull(innerClasses) && !innerClasses.isEmpty()) {
                    actionRemoveGeneratedAnnotation(innerClasses);
                }
            } else if (object instanceof Interface) {
                Interface anInterface = ( Interface ) object;
                Set<FullyQualifiedJavaType> importedTypes = anInterface.getImportedTypes();
                if (Objects.nonNull(importedTypes) && !importedTypes.isEmpty()) {
                    importedTypes.removeIf(importedType -> Objects.equals(importedType.getFullyQualifiedName(), "javax.annotation.Generated"));
                }
                List<Field> fields = anInterface.getFields();
                if (Objects.nonNull(fields) && !fields.isEmpty()) {
                    fields.get(0).addJavaDocLine("");
                    actionRemoveGeneratedAnnotation(fields);
                }
                List<Method> methods = anInterface.getMethods();
                if (Objects.nonNull(methods) && !methods.isEmpty()) {
                    actionRemoveGeneratedAnnotation(anInterface.getMethods());
                }
                List<InnerClass> innerClasses = anInterface.getInnerClasses();
                if (Objects.nonNull(innerClasses) && !innerClasses.isEmpty()) {
                    actionRemoveGeneratedAnnotation(innerClasses);
                }
            }
        }
    }

    private static void actionRemoveGeneratedAnnotation(List<?> objects) {
        String delete = "@Generated(\"org.mybatis.generator.api.MyBatisGenerator\")";
        if (Objects.nonNull(objects) && !objects.isEmpty()) {
            for (Object object : objects) {
                if (object instanceof Method) {
                    Method method = ( Method ) object;
                    List<String> annotations = method.getAnnotations();
                    if (Objects.nonNull(annotations) && !annotations.isEmpty()) {
                        for (int i = 0; i < annotations.size(); i++) {
                            if (Objects.equals(annotations.get(i), delete)) {
                                annotations.remove(i);
                                break;
                            }
                        }
                    }
                } else if (object instanceof Field) {
                    Field field = ( Field ) object;
                    List<String> annotations = field.getAnnotations();
                    if (Objects.nonNull(annotations) && !annotations.isEmpty()) {
                        for (int i = 0; i < annotations.size(); i++) {
                            if (Objects.equals(annotations.get(i), delete)) {
                                annotations.remove(i);
                                break;
                            }
                        }
                    }
                } else if (object instanceof InnerClass) {
                    InnerClass innerClass = ( InnerClass ) object;
                    List<String> annotations = innerClass.getAnnotations();
                    if (Objects.nonNull(annotations) && !annotations.isEmpty()) {
                        for (int i = 0; i < annotations.size(); i++) {
                            if (Objects.equals(annotations.get(i), delete)) {
                                annotations.remove(i);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
