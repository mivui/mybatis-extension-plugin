package com.github.uinios.mybatis;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;

import java.util.List;

/**
 * @author Jingle-Cat
 */

public class PluginUtils {

    private PluginUtils() {

    }

    static FullyQualifiedJavaType getPrimaryKey(IntrospectedTable introspectedTable) {
        List<IntrospectedColumn> primaryKey = introspectedTable.getPrimaryKeyColumns();
        FullyQualifiedJavaType keyType = null;
        for (IntrospectedColumn col : primaryKey) {
            keyType = col.getFullyQualifiedJavaType();
            break;
        }
        return keyType;
    }
}
