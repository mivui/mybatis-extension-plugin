package com.github.data.mybatis;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static com.github.data.mybatis.utils.PluginUtils.removeGeneratedAnnotation;

/**
 * @author Jingle-Cat
 */

public class RepositoryPlugin extends PluginAdapter {

    private String repository = null;

    private boolean suppressAllComments;

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        repository = properties.getProperty("repository");
        suppressAllComments = StringUtility.isTrue(properties.getProperty("suppressAllComments"));
    }

    @Override
    public boolean dynamicSqlSupportGenerated(TopLevelClass supportClass, IntrospectedTable introspectedTable) {
        if (suppressAllComments) {
            removeGeneratedAnnotation(supportClass);
        }
        return super.dynamicSqlSupportGenerated(supportClass, introspectedTable);
    }

    @Override
    public boolean clientGenerated(Interface interface_, IntrospectedTable introspectedTable) {
        if (suppressAllComments) {
            removeGeneratedAnnotation(interface_);
        }
        if (Objects.nonNull(repository)) {
            FullyQualifiedJavaType javaType = new FullyQualifiedJavaType(repository);
            interface_.addImportedType(javaType);
            interface_.addSuperInterface(javaType);
        }
        return true;
    }

}
