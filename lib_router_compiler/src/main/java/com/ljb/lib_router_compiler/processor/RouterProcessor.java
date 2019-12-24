package com.ljb.lib_router_compiler.processor;

import com.google.auto.service.AutoService;
import com.ljb.lib_router_annotation.Router;
import com.ljb.lib_router_annotation.entity.IRouterGroup;
import com.ljb.lib_router_annotation.entity.IRouterRoot;
import com.ljb.lib_router_annotation.entity.RouterInfo;
import com.ljb.lib_router_annotation.entity.Constant;
import com.ljb.lib_router_compiler.utils.Log;
import com.ljb.lib_router_compiler.utils.Utils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

@AutoService(Processor.class)
@SupportedOptions(Constant.MODULE_NAME)
@SupportedAnnotationTypes(Constant.ANNOTATION_TYPE)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class RouterProcessor extends AbstractProcessor {

    private Elements mElementUtils;
    private Filer mFiler;
    private Types mTypeUtils;
    private Log mLog;
    private String moduleName;

    private List<RouterInfo> routers = new ArrayList<>();

    private Map<String, String> roots = new TreeMap<>();
    private Map<String, List<RouterInfo>> groups = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mLog = Log.newLog(processingEnvironment.getMessager());
        mElementUtils = processingEnvironment.getElementUtils();
        mFiler = processingEnvironment.getFiler();
        mTypeUtils = processingEnvironment.getTypeUtils();

        Map<String, String> options = processingEnvironment.getOptions();
        if (!Utils.isEmpty(options)) {
            moduleName = options.get(Constant.MODULE_NAME);
        }
        System.out.println("-------------------------------------------");
        mLog.i("moduleName:" + moduleName);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (!Utils.isEmpty(set)) {
            Set<? extends Element> elementsAnnotateds = roundEnvironment.getElementsAnnotatedWith(Router.class);
            if (!Utils.isEmpty(elementsAnnotateds)) {
                processor(elementsAnnotateds);
            }
            return true;
        }
        return false;
    }

    private void processor(Set<? extends Element> elements) {
        TypeElement activity = mElementUtils.getTypeElement(Constant.ACTIVITY);
        mLog.i(activity.toString());

        for (Element element : elements) {
            TypeMirror typeMirror = element.asType();
            mLog.i(typeMirror.toString());

            Router annotation = element.getAnnotation(Router.class);
            if (mTypeUtils.isSubtype(typeMirror, activity.asType())) {
                mLog.i("------------1111111111111111111111111111111111111111111111111111111111111111111111------------");
                routers.add(new RouterInfo(element, annotation.path()));
            }
            mLog.i("-----9999999999999999-----" + routers.toString());
        }
        handleRouterInfo();

        generateGroupClass();
        generateRootClass();
    }

    private void handleRouterInfo() {
        if (!Utils.isEmpty(routers)) {
            for (RouterInfo info : routers) {
                if (checkRouterPath(info)) {
                    List<RouterInfo> routerInfos = groups.get(info.group);
                    if (Utils.isEmpty(routerInfos)) {
                        List<RouterInfo> infos = new ArrayList<>();
                        infos.add(info);
                        groups.put(info.group, infos);
                    } else {
                        routerInfos.add(info);
                    }
                }
            }
        }
    }

    private boolean checkRouterPath(RouterInfo info) {
        String group = info.getGroup();
        String path = info.getPath();
        if (!path.startsWith("/")) {
            return false;
        }
        if (Utils.isEmpty(group)) {
            String defaulteGroup = path.substring(1, path.indexOf("/", 1));
            if (Utils.isEmpty(defaulteGroup)) {
                return false;
            }
            info.setGroup(defaulteGroup);
        }
        return true;
    }

    private void generateGroupClass() {
        if (!Utils.isEmpty(groups)) {

            ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName
                    .get(ClassName.get(Map.class),
                            ClassName.get(String.class),
                            ClassName.get(RouterInfo.class));
            ParameterSpec parameter = ParameterSpec.builder(parameterizedTypeName, "routers").build();

            for (Map.Entry<String, List<RouterInfo>> entry : groups.entrySet()) {
                String group = entry.getKey();
                List<RouterInfo> paths = entry.getValue();

                MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Constant.METHOD_NAME)
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Override.class)
                        .returns(void.class)
                        .addParameter(parameter);

                for (RouterInfo info : paths) {
                    methodBuilder.addStatement("routers.put($S,$T.build($S,$S,$T.class))",
                            info.getPath(),
                            ClassName.get(RouterInfo.class),
                            info.getGroup(),
                            info.getPath(),
                            ClassName.get(((TypeElement) info.getElement())));
                }

                String className = Constant.NAME_OF_GROUP + group;
                TypeSpec typeSpec = TypeSpec.classBuilder(className)
                        .addModifiers(Modifier.PUBLIC)
                        .addSuperinterface(ClassName.get(IRouterGroup.class))
                        .addMethod(methodBuilder.build())
                        .build();

                JavaFile javaFile = JavaFile.builder(Constant.PACKAGE_OF_GENERATE_FILE, typeSpec).build();

                try {
                    javaFile.writeTo(mFiler);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                roots.put(group, className);
            }
        }
    }


    private void generateRootClass() {
        if (!Utils.isEmpty(roots)) {

            ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(
                    ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ParameterizedTypeName.get(ClassName.get(Class.class),
                            WildcardTypeName.subtypeOf(ClassName.get(IRouterGroup.class))
                    ));

            ParameterSpec parameter = ParameterSpec.builder(parameterizedTypeName, "roots").build();

            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Constant.METHOD_NAME)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .returns(void.class)
                    .addParameter(parameter);

            for (Map.Entry<String, String> entry : roots.entrySet()) {
                methodBuilder.addStatement("roots.put($S,$T.class)", entry.getKey(), ClassName.get(Constant.PACKAGE_OF_GENERATE_FILE, entry.getValue()));
            }

            TypeSpec typeSpec = TypeSpec.classBuilder(Constant.NAME_OF_ROOT + moduleName)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(IRouterRoot.class)
                    .addMethod(methodBuilder.build())
                    .build();

            JavaFile javaFile = JavaFile.builder(Constant.PACKAGE_OF_GENERATE_FILE, typeSpec).build();

            try {
                javaFile.writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
