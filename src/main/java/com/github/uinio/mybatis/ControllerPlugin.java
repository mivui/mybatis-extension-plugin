package com.github.uinio.mybatis;

import com.github.uinio.mybatis.utils.PluginUtils;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.*;

import static com.github.uinio.mybatis.utils.PluginUtils.restfulMethod;

/**
 * @author Jingle-Cat
 */

public class ControllerPlugin extends PluginAdapter {

    private boolean rest = false;

    private String targetProject = null;

    private String targetPackage = null;

    private String respond = null;

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        rest = StringUtility.isTrue(properties.getProperty("rest"));
        targetProject = properties.getProperty("targetProject");
        targetPackage = properties.getProperty("targetPackage");
        respond = properties.getProperty("respond");
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
            controllerClass.addImportedType(new FullyQualifiedJavaType("java.util.Objects"));
            controllerClass.addImportedType(new FullyQualifiedJavaType("java.util.Optional"));
            //Lowercase
            String oldInitial = String.valueOf(className.toCharArray()[0]);
            String newInitial = String.valueOf(className.toCharArray()[0]).toLowerCase();
            String lowerClassName = className.replaceFirst(oldInitial, newInitial);
            controllerClass.addAnnotation("@RequestMapping(\"" + lowerClassName + "\")");

            //service inject
            String[] packageSplit = targetPackage.split("\\.");
            StringBuilder servicePackage = new StringBuilder();
            for (int i = 0; i < packageSplit.length - 1; i++) {
                servicePackage.append(packageSplit[i]).append(".");
            }
            FullyQualifiedJavaType serviceType = new FullyQualifiedJavaType(servicePackage + "service." + className + "Service");
            controllerClass.addImportedType(serviceType);
            Field serviceField = new Field(lowerClassName + "Service", serviceType);
            serviceField.addJavaDocLine("");
            serviceField.setVisibility(JavaVisibility.PRIVATE);
            controllerClass.addImportedType(new FullyQualifiedJavaType("org.springframework.beans.factory.annotation.Autowired"));
            serviceField.addAnnotation("@Autowired");
            controllerClass.addField(serviceField);

            if (!rest) {
                FullyQualifiedJavaType modelAndView = new FullyQualifiedJavaType("org.springframework.web.servlet.ModelAndView");
                controllerClass.addImportedType(modelAndView);

                Method defaultView = new Method("defaultView");
                defaultView.addAnnotation("@GetMapping");
                defaultView.setVisibility(JavaVisibility.PUBLIC);
                defaultView.setReturnType(modelAndView);
                defaultView.addBodyLine("return new ModelAndView(\"\");");
                controllerClass.addMethod(defaultView);

                Method saveView = new Method("saveView");
                saveView.addAnnotation("@GetMapping(\"saveView\")");
                saveView.setVisibility(JavaVisibility.PUBLIC);
                saveView.setReturnType(modelAndView);
                saveView.addBodyLine("return new ModelAndView(\"\");");
                controllerClass.addMethod(saveView);

                Method updateView = new Method("updateView");
                updateView.addAnnotation("@GetMapping(\"updateView\")");
                updateView.setVisibility(JavaVisibility.PUBLIC);
                updateView.setReturnType(modelAndView);
                updateView.addBodyLine("return new ModelAndView(\"\");");
                controllerClass.addMethod(updateView);

                Method detailView = new Method("detailView");
                detailView.addAnnotation("@GetMapping(\"detailView\")");
                detailView.setVisibility(JavaVisibility.PUBLIC);
                detailView.setReturnType(modelAndView);
                detailView.addBodyLine("return new ModelAndView(\"\");");
                controllerClass.addMethod(detailView);
            }

            //Add Method
            if (Objects.nonNull(respond)) {
                FullyQualifiedJavaType respondJavaType = new FullyQualifiedJavaType(respond);
                controllerClass.addImportedType(respondJavaType);
                controllerClass.addImportedType(new FullyQualifiedJavaType(recordType));
                controllerClass.addImportedType(FullyQualifiedJavaType.getNewListInstance());
                //primaryKeyType
                Optional<FullyQualifiedJavaType> optional = PluginUtils.primaryKeyType(introspectedTable);
                //primaryKeyTypeName
                Optional<String> keyNameOptional = PluginUtils.primaryKeyName(introspectedTable);
                if (optional.isPresent() && keyNameOptional.isPresent()) {
                    FullyQualifiedJavaType primaryKeyType = optional.get();
                    String primaryKeyName = keyNameOptional.get();
                    if (primaryKeyType.isExplicitlyImported()) {
                        controllerClass.addImportedType(primaryKeyType);
                    }
                    //findAll
                    Method findAllMethod = new Method("findAll");
                    findAllMethod.addAnnotation("@GetMapping(\"findAll\")");
                    restfulMethod(findAllMethod, rest);
                    findAllMethod.setReturnType(respondJavaType);
                    findAllMethod.addBodyLine("return  null;");
                    controllerClass.addMethod(findAllMethod);
                    //findById
                    Method findByIdMethod = new Method("findById");
                    findByIdMethod.setReturnType(respondJavaType);
                    findByIdMethod.addAnnotation("@GetMapping(\"findById/{" + primaryKeyName + "}\")");
                    restfulMethod(findByIdMethod, rest);
                    Parameter idParameter = new Parameter(primaryKeyType, primaryKeyName);
                    idParameter.addAnnotation("@PathVariable");
                    findByIdMethod.addParameter(idParameter);
                    findByIdMethod.addBodyLine("return  null;");
                    controllerClass.addMethod(findByIdMethod);
                    //save
                    Method saveMethod = new Method("save");
                    saveMethod.setReturnType(respondJavaType);
                    saveMethod.addAnnotation("@PostMapping(\"save\")");
                    restfulMethod(saveMethod, rest);
                    Parameter saveParameter = new Parameter(new FullyQualifiedJavaType(recordType), "record");
                    saveParameter.addAnnotation("@RequestBody");
                    saveMethod.addParameter(saveParameter);
                    saveMethod.addBodyLine("return  null;");
                    controllerClass.addMethod(saveMethod);
                    //saveBatch
                    Method saveBatchMethod = new Method("saveBatch");
                    saveBatchMethod.setReturnType(respondJavaType);
                    saveBatchMethod.addAnnotation("@PostMapping(\"saveBatch\")");
                    restfulMethod(saveBatchMethod, rest);
                    FullyQualifiedJavaType listInstance = FullyQualifiedJavaType.getNewListInstance();
                    listInstance.addTypeArgument(new FullyQualifiedJavaType(recordType));
                    Parameter saveBatchMethodParameter = new Parameter(listInstance, "records");
                    saveBatchMethodParameter.addAnnotation("@RequestBody");
                    saveBatchMethod.addParameter(saveBatchMethodParameter);
                    saveBatchMethod.addBodyLine("return  null;");
                    controllerClass.addMethod(saveBatchMethod);
                    //update
                    Method updateMethod = new Method("update");
                    updateMethod.setReturnType(respondJavaType);
                    updateMethod.addAnnotation("@PutMapping(\"update\")");
                    restfulMethod(updateMethod, rest);
                    Parameter updateParameter = new Parameter(new FullyQualifiedJavaType(recordType), "record");
                    updateParameter.addAnnotation("@RequestBody");
                    updateMethod.addParameter(saveParameter);
                    updateMethod.addBodyLine("return  null;");
                    controllerClass.addMethod(updateMethod);
                    //updateBatch
                    Method updateBatchMethod = new Method("updateBatch");
                    updateBatchMethod.setReturnType(respondJavaType);
                    updateBatchMethod.addAnnotation("@PatchMapping(\"updateBatch\")");
                    restfulMethod(updateBatchMethod, rest);
                    FullyQualifiedJavaType newListInstance = FullyQualifiedJavaType.getNewListInstance();
                    newListInstance.addTypeArgument(new FullyQualifiedJavaType(recordType));
                    Parameter updateBatchParameter = new Parameter(newListInstance, "records");
                    updateBatchParameter.addAnnotation("@RequestBody");
                    updateBatchMethod.addParameter(updateBatchParameter);
                    updateBatchMethod.addBodyLine("return  null;");
                    controllerClass.addMethod(updateBatchMethod);
                    //delete
                    Method deleteMethod = new Method("delete");
                    deleteMethod.setReturnType(respondJavaType);
                    deleteMethod.addAnnotation("@DeleteMapping(\"delete/{" + primaryKeyName + "}\")");
                    restfulMethod(deleteMethod, rest);
                    Parameter deleteIdParameter = new Parameter(primaryKeyType, primaryKeyName);
                    deleteIdParameter.addAnnotation("@PathVariable");
                    deleteMethod.addParameter(deleteIdParameter);
                    deleteMethod.addBodyLine("return  null;");
                    controllerClass.addMethod(deleteMethod);
                    //deleteBatch
                    Method deleteBatchMethod = new Method("deleteBatch");
                    deleteBatchMethod.setReturnType(respondJavaType);
                    deleteBatchMethod.addAnnotation("@DeleteMapping(\"deleteBatch/{" + primaryKeyName + "s}\")");
                    restfulMethod(deleteBatchMethod, rest);
                    Parameter deleteBatchParameter = new Parameter(primaryKeyType, "[] " + primaryKeyName + "s");
                    deleteBatchParameter.addAnnotation("@PathVariable");
                    deleteBatchMethod.addParameter(deleteBatchParameter);
                    deleteBatchMethod.addBodyLine("return  null;");
                    controllerClass.addMethod(deleteBatchMethod);
                    //page
                    Method pageMethod = new Method("page");
                    pageMethod.addAnnotation("@GetMapping(\"page\")");
                    restfulMethod(pageMethod, rest);
                    pageMethod.setReturnType(respondJavaType);
                    Parameter pageNum = new Parameter(new FullyQualifiedJavaType("int"), "pageNum");
                    pageNum.addAnnotation("@RequestParam(defaultValue = \"1\")");
                    pageMethod.addParameter(pageNum);
                    Parameter pageSize = new Parameter(new FullyQualifiedJavaType("int"), "pageSize");
                    pageSize.addAnnotation("@RequestParam(defaultValue = \"6\")");
                    pageMethod.addParameter(pageSize);
                    pageMethod.addBodyLine("return  null;");
                    controllerClass.addMethod(pageMethod);
                }
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
