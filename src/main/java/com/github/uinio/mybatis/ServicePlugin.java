package com.github.uinio.mybatis;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.PropertyRegistry;

import java.util.*;

import static com.github.uinio.mybatis.utils.PluginUtils.primaryKeyType;

/**
 * @author uinio
 */

public class ServicePlugin extends PluginAdapter {

    private String targetProject = null;

    private String targetPackage = null;

    private String basicService = null;

    private String basicServiceImpl = null;

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        targetProject = properties.getProperty("targetProject");
        targetPackage = properties.getProperty("targetPackage");
        basicService = properties.getProperty("basicService");
        basicServiceImpl = properties.getProperty("basicServiceImpl");
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        if (Objects.nonNull(targetPackage) && Objects.nonNull(targetProject)) {
            List<GeneratedJavaFile> files = new ArrayList<>();
            //Get entity class name
            String recordType = introspectedTable.getBaseRecordType();
            String[] entityPackage = recordType.split("\\.");
            String className = entityPackage[entityPackage.length - 1];
            //Create interface
            String servicePackage = targetPackage + "." + className + "Service";
            FullyQualifiedJavaType service = new FullyQualifiedJavaType(servicePackage);
            Interface serviceInterface = new Interface(service);
            serviceInterface.setVisibility(JavaVisibility.PUBLIC);
            if (Objects.nonNull(basicService)) {
                //Import package
                serviceInterface.addImportedType(new FullyQualifiedJavaType(basicService));
                //Interface name
                String[] basicServicePackage = basicService.split("\\.");
                FullyQualifiedJavaType interfacePackage = new FullyQualifiedJavaType(basicServicePackage[basicServicePackage.length - 1]);
                //接口添加泛型格式BaseService<实体,主键类型>
                serviceInterface.addImportedType(new FullyQualifiedJavaType(recordType));
                interfacePackage.addTypeArgument(new FullyQualifiedJavaType(recordType));
                Optional<FullyQualifiedJavaType> optional = primaryKeyType(introspectedTable);
                if (optional.isPresent()) {
                    FullyQualifiedJavaType javaType = optional.get();
                    if (javaType.isExplicitlyImported()) {
                        serviceInterface.addImportedType(javaType);
                    }
                    interfacePackage.addTypeArgument(javaType);
                }
                serviceInterface.addSuperInterface(interfacePackage);
                GeneratedJavaFile javaFile = new GeneratedJavaFile(serviceInterface, targetProject,
                        context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                        context.getJavaFormatter());
                files.add(javaFile);
            }
            if (Objects.nonNull(basicServiceImpl) && Objects.nonNull(basicService)) {
                String[] serviceImplPackage = basicServiceImpl.split("\\.");
                //Create an implementation class interface
                FullyQualifiedJavaType serviceImpl = new FullyQualifiedJavaType(targetPackage + "." + "impl." + className + "ServiceImpl");
                TopLevelClass serviceImplClass = new TopLevelClass(serviceImpl);
                serviceImplClass.setVisibility(JavaVisibility.PUBLIC);
                serviceImplClass.addImportedType("org.springframework.stereotype.Service");
                serviceImplClass.addAnnotation("@Service");
                //Inherit Base Service Impl
                serviceImplClass.addImportedType(basicServiceImpl);
                FullyQualifiedJavaType javaType = new FullyQualifiedJavaType(serviceImplPackage[serviceImplPackage.length - 1]);
                serviceImplClass.addImportedType(recordType);
                javaType.addTypeArgument(new FullyQualifiedJavaType(recordType));
                Optional<FullyQualifiedJavaType> optional = primaryKeyType(introspectedTable);
                if (optional.isPresent()) {
                    FullyQualifiedJavaType qualifiedJavaType = optional.get();
                    if (qualifiedJavaType.isExplicitlyImported()) {
                        //Not the basic data type needs to guide package
                        serviceImplClass.addImportedType(qualifiedJavaType);
                    }
                    javaType.addTypeArgument(qualifiedJavaType);
                }
                serviceImplClass.setSuperClass(javaType);
                //Add implementation class
                serviceImplClass.addImportedType(servicePackage);
                serviceImplClass.addSuperInterface(new FullyQualifiedJavaType(className + "Service"));
                GeneratedJavaFile javaFile = new GeneratedJavaFile(serviceImplClass, targetProject,
                        context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                        context.getJavaFormatter());
                files.add(javaFile);
            }
            return files;
        }
        return Collections.emptyList();
    }


}
