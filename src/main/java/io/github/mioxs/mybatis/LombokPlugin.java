package io.github.mioxs.mybatis;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.List;
import java.util.Properties;

public class LombokPlugin extends PluginAdapter {

    private boolean data;

    private boolean getter;

    private boolean setter;

    private boolean toString;

    private boolean equalsAndHashCode;

    private boolean builder;

    private boolean noArgsConstructor;

    private boolean allArgsConstructor;

    private boolean requiredArgsConstructor;

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        data = StringUtility.isTrue(properties.getProperty("data"));
        getter = StringUtility.isTrue(properties.getProperty("getter"));
        setter = StringUtility.isTrue(properties.getProperty("setter"));
        toString = StringUtility.isTrue(properties.getProperty("toString"));
        equalsAndHashCode = StringUtility.isTrue(properties.getProperty("equalsAndHashCode"));
        builder = StringUtility.isTrue(properties.getProperty("builder"));
        noArgsConstructor = StringUtility.isTrue(properties.getProperty("noArgsConstructor"));
        allArgsConstructor = StringUtility.isTrue(properties.getProperty("allArgsConstructor"));
        requiredArgsConstructor = StringUtility.isTrue(properties.getProperty("requiredArgsConstructor"));
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if (data) {
            topLevelClass.addImportedType("lombok.Data");
            topLevelClass.addAnnotation("@Data");
        }
        if (!data && getter) {
            topLevelClass.addImportedType("lombok.Getter");
            topLevelClass.addAnnotation("@Getter");
        }
        if (!data && setter) {
            topLevelClass.addImportedType("lombok.Setter");
            topLevelClass.addAnnotation("@Setter");
        }
        if (!data && toString) {
            topLevelClass.addImportedType("lombok.ToString");
            topLevelClass.addAnnotation("@ToString");
        }
        if (!data && equalsAndHashCode) {
            topLevelClass.addImportedType("lombok.EqualsAndHashCode");
            topLevelClass.addAnnotation("@EqualsAndHashCode");
        }
        if (builder) {
            topLevelClass.addImportedType("lombok.Builder");
            topLevelClass.addAnnotation("@Builder");
        }
        if (noArgsConstructor) {
            topLevelClass.addImportedType("lombok.NoArgsConstructor");
            topLevelClass.addAnnotation("@NoArgsConstructor");
        }

        if (allArgsConstructor) {
            topLevelClass.addImportedType("lombok.AllArgsConstructor");
            topLevelClass.addAnnotation("@AllArgsConstructor");
        }
        if (!data && requiredArgsConstructor) {
            topLevelClass.addImportedType("lombok.RequiredArgsConstructor");
            topLevelClass.addAnnotation("@RequiredArgsConstructor");
        }

        return true;
    }

    @Override
    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        if (setter) {
            return false;
        } else return !data;
    }

    @Override
    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        if (getter) {
            return false;
        } else return !data;
    }

}
