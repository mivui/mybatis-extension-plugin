package com.github.uinios.mybatis.plugin;

import com.github.uinios.mybatis.plugin.utils.PluginUtils;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.*;

import static com.github.uinios.mybatis.plugin.utils.PluginUtils.restfulMethod;

/**
 * @author Jingle-Cat
 */

public class ControllerPlugin extends PluginAdapter {

    private boolean rest = false;

    private String targetProject = null;

    private String targetPackage = null;

    private String respond = null;

    private boolean zh_cn = false;

    //not property

    private String findAllErrorJson;

    private String findByIdErrorJson;

    private String saveSuccessJson;

    private String saveErrorJson;

    private String saveBatchSuccessJson;

    private String saveBatchErrorJson;

    private String updateSuccessJson;

    private String updateErrorJson;

    private String updateBatchSuccessJson;

    private String updateBatchErrorJson;

    private String deleteSuccessJson;

    private String deleteErrorJson;

    private String deleteBatchSuccessJson;

    private String deleteBatchErrorJson;

    private String pageErrorJson;


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
        zh_cn = StringUtility.isTrue(properties.getProperty("zh_cn"));
        if (zh_cn) {
            findAllErrorJson = "{}查询全部失败!";
            findByIdErrorJson = "根据主键查询{}失败!";
            saveSuccessJson = "添加{}成功!";
            saveErrorJson = "添加{}失败!";
            saveBatchSuccessJson = "{}批量新增成功!";
            saveBatchErrorJson = "{}批量新增失败!";
            updateSuccessJson = "更新{}成功!";
            updateErrorJson = "更新{}失败!";
            updateBatchSuccessJson = "{}批量更新成功!";
            updateBatchErrorJson = "{}批量更新失败!";
            deleteSuccessJson = "删除{}成功!";
            deleteErrorJson = "删除{}成功!";
            deleteBatchSuccessJson = "{}批量删除失败!";
            deleteBatchErrorJson = "{}批量删除失败!";
            pageErrorJson = "{}分页查询失败!";
        }
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

            //addTitleFieldForLogOutputPlaceholder
            if (zh_cn) {
                controllerClass.addImportedType(FullyQualifiedJavaType.getStringInstance());
                Field contentField = new Field("content", FullyQualifiedJavaType.getStringInstance());
                contentField.setVisibility(JavaVisibility.PRIVATE);
                contentField.setFinal(true);
                contentField.setStatic(true);
                contentField.setInitializationString("\"content\"");
                controllerClass.addField(contentField);
            }

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
                String[] respondSplit = respond.split("\\.");
                String respondClassName = respondSplit[respondSplit.length - 1];
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
                    findAllMethod.addBodyLine("try {");
                    findAllMethod.addBodyLine("return " + respondClassName + ".success(" + lowerClassName + "Service.findAll());");
                    findAllMethod.addBodyLine("} catch (Exception e) {");
                    if (zh_cn) {
                        findAllMethod.addBodyLine("log.error(\"" + findAllErrorJson + " {}\", content, e.getMessage());");
                    } else {
                        findAllMethod.addBodyLine("log.error(\"{}\",e.getMessage());");
                    }
                    findAllMethod.addBodyLine("}");
                    if (zh_cn) {
                        findAllMethod.addBodyLine("return " + respondClassName + ".failure(\"" + findAllErrorJson + "\", content);");
                    } else {
                        findAllMethod.addBodyLine("return " + respondClassName + ".failure(\"\");");
                    }
                    controllerClass.addMethod(findAllMethod);
                    //findById
                    Method findByIdMethod = new Method("findById");
                    findByIdMethod.setReturnType(respondJavaType);
                    findByIdMethod.addAnnotation("@GetMapping(\"findById/{" + primaryKeyName + "}\")");
                    restfulMethod(findByIdMethod, rest);
                    Parameter idParameter = new Parameter(primaryKeyType, primaryKeyName);
                    idParameter.addAnnotation("@PathVariable");
                    findByIdMethod.addParameter(idParameter);
                    findByIdMethod.addBodyLine("try {");
                    findByIdMethod.addBodyLine("final Optional<" + className + "> optional = " + lowerClassName + "Service.findById(" + primaryKeyName + ");");
                    findByIdMethod.addBodyLine("if (optional.isPresent()) {");
                    findByIdMethod.addBodyLine("return " + respondClassName + ".success(optional.get());");
                    findByIdMethod.addBodyLine("}");
                    findByIdMethod.addBodyLine("} catch (Exception e) {");
                    if (zh_cn) {
                        findByIdMethod.addBodyLine("log.error(\"" + findByIdErrorJson + " {}\", content, e.getMessage());");
                    } else {
                        findByIdMethod.addBodyLine("log.error(\"{}\", e.getMessage());");
                    }
                    findByIdMethod.addBodyLine("}");
                    if (zh_cn) {
                        findByIdMethod.addBodyLine("return " + respondClassName + ".failure(\"" + findByIdErrorJson + "\", content);");
                    } else {
                        findByIdMethod.addBodyLine("return " + respondClassName + ".failure(\"\");");
                    }
                    controllerClass.addMethod(findByIdMethod);
                    //save
                    Method saveMethod = new Method("save");
                    saveMethod.setReturnType(respondJavaType);
                    saveMethod.addAnnotation("@PostMapping(\"save\")");
                    restfulMethod(saveMethod, rest);
                    Parameter saveParameter = new Parameter(new FullyQualifiedJavaType(recordType), "record");
                    saveParameter.addAnnotation("@RequestBody");
                    saveMethod.addParameter(saveParameter);
                    saveMethod.addBodyLine("try {");
                    saveMethod.addBodyLine("int count = " + lowerClassName + "Service.insert(record);");
                    saveMethod.addBodyLine("if (count > 0) {");
                    if (zh_cn) {
                        saveMethod.addBodyLine("return " + respondClassName + ".success(\"" + saveSuccessJson + "\", content);");
                    } else {
                        saveMethod.addBodyLine("return " + respondClassName + ".success(\"\");");
                    }
                    saveMethod.addBodyLine("}");
                    saveMethod.addBodyLine("} catch (Exception e) {");
                    if (zh_cn) {
                        saveMethod.addBodyLine("log.error(\"" + saveErrorJson + " {}\", content, e.getMessage());");
                    } else {
                        saveMethod.addBodyLine("log.error(\"{}\", e.getMessage());");
                    }
                    saveMethod.addBodyLine("}");
                    if (zh_cn) {
                        saveMethod.addBodyLine("return " + respondClassName + ".failure(\"" + saveErrorJson + "\", content);");
                    } else {
                        saveMethod.addBodyLine("return " + respondClassName + ".failure(\"\");");
                    }
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
                    saveBatchMethod.addBodyLine("try {");
                    saveBatchMethod.addBodyLine("int count = " + lowerClassName + "Service.insertMultiple(records);");
                    saveBatchMethod.addBodyLine("if (count > 0) {");
                    if (zh_cn) {
                        saveBatchMethod.addBodyLine("return " + respondClassName + ".success(\"" + saveBatchSuccessJson + "\", content);");
                    } else {
                        saveBatchMethod.addBodyLine("return " + respondClassName + ".success(\"\");");
                    }
                    saveBatchMethod.addBodyLine("}");
                    saveBatchMethod.addBodyLine("} catch (Exception e) {");
                    if (zh_cn) {
                        saveBatchMethod.addBodyLine("log.error(\"" + saveBatchErrorJson + " {}\", content, e.getMessage());");
                    } else {
                        saveBatchMethod.addBodyLine("log.error(\"{}\", e.getMessage());");
                    }
                    saveBatchMethod.addBodyLine("}");
                    if (zh_cn) {
                        saveBatchMethod.addBodyLine("return " + respondClassName + ".failure(\"" + saveBatchErrorJson + "\", content);");
                    } else {
                        saveBatchMethod.addBodyLine("return " + respondClassName + ".failure(\"\");");
                    }
                    controllerClass.addMethod(saveBatchMethod);
                    //update
                    Method updateMethod = new Method("update");
                    updateMethod.setReturnType(respondJavaType);
                    updateMethod.addAnnotation("@PutMapping(\"update\")");
                    restfulMethod(updateMethod, rest);
                    Parameter updateParameter = new Parameter(new FullyQualifiedJavaType(recordType), "record");
                    updateParameter.addAnnotation("@RequestBody");
                    updateMethod.addParameter(saveParameter);
                    updateMethod.addBodyLine("try {");
                    updateMethod.addBodyLine("int count = " + lowerClassName + "Service.update(record);");
                    updateMethod.addBodyLine("if (count > 0) {");
                    if (zh_cn) {
                        updateMethod.addBodyLine("return " + respondClassName + ".success(\"" + updateSuccessJson + "\", content);");
                    } else {
                        updateMethod.addBodyLine("return " + respondClassName + ".success(\"\");");
                    }
                    updateMethod.addBodyLine("}");
                    updateMethod.addBodyLine("} catch (Exception e) {");
                    if (zh_cn) {
                        updateMethod.addBodyLine("log.error(\"" + updateErrorJson + " {}\", content, e.getMessage());");
                    } else {
                        updateMethod.addBodyLine("log.error(\"{}\", e.getMessage());");
                    }
                    updateMethod.addBodyLine("}");
                    if (zh_cn) {
                        updateMethod.addBodyLine("return " + respondClassName + ".failure(\"" + updateErrorJson + "\", content);");
                    } else {
                        updateMethod.addBodyLine("return " + respondClassName + ".failure(\"\");");
                    }
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
                    updateBatchMethod.addBodyLine("try {");
                    updateBatchMethod.addBodyLine("int count = " + lowerClassName + "Service.updateMultiple(records);");
                    updateBatchMethod.addBodyLine("if (count > 0) {");
                    if (zh_cn) {
                        updateBatchMethod.addBodyLine("return " + respondClassName + ".success(\"" + updateBatchSuccessJson + "\", content);");
                    } else {
                        updateBatchMethod.addBodyLine("return " + respondClassName + ".success(\"\");");
                    }
                    updateBatchMethod.addBodyLine("}");
                    updateBatchMethod.addBodyLine("} catch (Exception e) {");
                    if (zh_cn) {
                        updateBatchMethod.addBodyLine("log.error(\"" + updateBatchErrorJson + " {}\", content, e.getMessage());");
                    } else {
                        updateBatchMethod.addBodyLine("log.error(\"{}\", e.getMessage());");
                    }
                    updateBatchMethod.addBodyLine("}");
                    if (zh_cn) {
                        updateBatchMethod.addBodyLine("return " + respondClassName + ".failure(\"" + updateBatchErrorJson + "\", content);");
                    } else {
                        updateBatchMethod.addBodyLine("return " + respondClassName + ".failure(\"\");");
                    }
                    controllerClass.addMethod(updateBatchMethod);
                    //delete
                    Method deleteMethod = new Method("delete");
                    deleteMethod.setReturnType(respondJavaType);
                    deleteMethod.addAnnotation("@DeleteMapping(\"delete/{" + primaryKeyName + "}\")");
                    restfulMethod(deleteMethod, rest);
                    Parameter deleteIdParameter = new Parameter(primaryKeyType, primaryKeyName);
                    deleteIdParameter.addAnnotation("@PathVariable");
                    deleteMethod.addParameter(deleteIdParameter);
                    deleteMethod.addBodyLine("try {");
                    deleteMethod.addBodyLine("if (Objects.nonNull(" + primaryKeyName + ")) {");
                    deleteMethod.addBodyLine("int count = " + lowerClassName + "Service.deleteById(" + primaryKeyName + ");");
                    deleteMethod.addBodyLine("if (count > 0) {");
                    if (zh_cn) {
                        deleteMethod.addBodyLine("return " + respondClassName + ".success(\"" + deleteSuccessJson + "\", content);");
                    } else {
                        deleteMethod.addBodyLine("return " + respondClassName + ".success(\"\");");
                    }
                    deleteMethod.addBodyLine("}");
                    deleteMethod.addBodyLine("}");
                    deleteMethod.addBodyLine("} catch (Exception e) {");
                    if (zh_cn) {
                        deleteMethod.addBodyLine("log.error(\"" + deleteErrorJson + " {}\", content, e.getMessage());");
                    } else {
                        deleteMethod.addBodyLine("log.error(\"{}\", e.getMessage());");
                    }
                    deleteMethod.addBodyLine("}");
                    if (zh_cn) {
                        deleteMethod.addBodyLine("return " + respondClassName + ".failure(\"" + deleteErrorJson + "\", content);");
                    } else {
                        deleteMethod.addBodyLine("return " + respondClassName + ".failure(\"\");");
                    }
                    controllerClass.addMethod(deleteMethod);
                    //deleteBatch
                    Method deleteBatchMethod = new Method("deleteBatch");
                    deleteBatchMethod.setReturnType(respondJavaType);
                    deleteBatchMethod.addAnnotation("@DeleteMapping(\"deleteBatch/{" + primaryKeyName + "s}\")");
                    restfulMethod(deleteBatchMethod, rest);
                    Parameter deleteBatchParameter = new Parameter(primaryKeyType, "[] " + primaryKeyName + "s");
                    deleteBatchParameter.addAnnotation("@PathVariable");
                    deleteBatchMethod.addParameter(deleteBatchParameter);
                    deleteBatchMethod.addBodyLine("try {");
                    deleteBatchMethod.addBodyLine("if (Objects.nonNull(" + primaryKeyName + "s)) {");
                    deleteBatchMethod.addBodyLine("int count = " + lowerClassName + "Service.deleteMultiple(" + primaryKeyName + "s);");
                    deleteBatchMethod.addBodyLine("if (count > 0) {");
                    if (zh_cn) {
                        deleteBatchMethod.addBodyLine("return " + respondClassName + ".success(\"" + deleteBatchSuccessJson + "\", content);");
                    } else {
                        deleteBatchMethod.addBodyLine("return " + respondClassName + ".success(\"\");");
                    }
                    deleteBatchMethod.addBodyLine("}");
                    deleteBatchMethod.addBodyLine("}");
                    deleteBatchMethod.addBodyLine("} catch (Exception e) {");
                    if (zh_cn) {
                        deleteBatchMethod.addBodyLine("log.error(\"" + deleteBatchErrorJson + " {}\", content, e.getMessage());");
                    } else {
                        deleteBatchMethod.addBodyLine("log.error(\"{}\", e.getMessage());");
                    }
                    deleteBatchMethod.addBodyLine("}");
                    if (zh_cn) {
                        deleteBatchMethod.addBodyLine("return " + respondClassName + ".failure(\"" + deleteBatchErrorJson + "\", content);");
                    } else {
                        deleteBatchMethod.addBodyLine("return " + respondClassName + ".failure(\"\");");
                    }
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
                    pageMethod.addBodyLine("try {");
                    pageMethod.addBodyLine("return " + respondClassName + ".success(" + lowerClassName + "Service.page(pageNum, pageSize));");
                    pageMethod.addBodyLine("} catch (Exception e) {");
                    if (zh_cn) {
                        pageMethod.addBodyLine("log.error(\"" + pageErrorJson + " {}\", content, e.getMessage());");
                    } else {
                        pageMethod.addBodyLine("log.error(\"{}\", e.getMessage());");
                    }
                    pageMethod.addBodyLine("}");
                    if (zh_cn) {
                        pageMethod.addBodyLine("return " + respondClassName + ".failure(\"" + pageErrorJson + "\", content);");
                    } else {
                        pageMethod.addBodyLine("return " + respondClassName + ".failure(\"\");");
                    }
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
