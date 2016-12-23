package com.github.imishx.activityrouter.compiler;

import com.github.imishx.activityrouter.annotation.ActivityInterceptor;
import com.github.imishx.activityrouter.annotation.ActivityMapping;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class RouterProcessor extends AbstractProcessor {
    private static final boolean DEBUG = false;
    private static final String CLS_PREFIX = "ActivityMappingFactory";
    private static final String GEN_PKG_NAME = "com.github.imishx.activityrouter.router";

    private static final String OPT_MAIN_MODULE = "mainModule";
    private static final String OPT_MODULE_NAME = "moduleName";
    private static final String OPT_INCLUDE_MODULES = "includeModules";

    private Map<String, String> options;
    private Messager messager;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
        options = processingEnv.getOptions();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> ret = new HashSet<>();
        ret.add(ActivityMapping.class.getCanonicalName());
        ret.add(ActivityInterceptor.class.getCanonicalName());
        return ret;
    }

    @Override
    public Set<String> getSupportedOptions() {
        HashSet<String> set = new HashSet<>();
        set.add(OPT_MAIN_MODULE);
        set.add(OPT_MODULE_NAME);
        set.add(OPT_INCLUDE_MODULES);
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return false;
        }

        boolean isMainModule = Boolean.parseBoolean(options.get(OPT_MAIN_MODULE));
        if (isMainModule) {
            String includeModules = options.get(OPT_INCLUDE_MODULES);
            if (null != includeModules) {
                generateModulesRouterInit(includeModules.split(","));
            } else {
                generateDefaultRouterInit();
            }
        }
        return handleRouter(CLS_PREFIX.concat("_").concat(options.get(OPT_MODULE_NAME)), roundEnv);
    }

    /**
     * 创建默认com.github.imishx.activityrouter.RouterInit.java文件
     *
     * public class RouterInit{
     *     public static final init(){
     *     }
     * }
     *
     */
    private void generateDefaultRouterInit() {
        MethodSpec.Builder initMethod = MethodSpec.methodBuilder("init")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC);

        TypeSpec routerInit = TypeSpec.classBuilder("RouterInit")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(initMethod.build())
                .build();
        try {
            JavaFile.builder(GEN_PKG_NAME, routerInit)
                    .build()
                    .writeTo(filer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建具体com.github.imishx.activityrouter.RouterInit.java文件
     *
     * public class RouterInit{
     *     public static final init(){
     *         ActivityMappingFactory_app.gen();
     *         ActivityMappingFactory_app_module.gen();
     *         ...
     *     }
     * }
     *
     */
    private void generateModulesRouterInit(String[] moduleNames) {
        MethodSpec.Builder initMethod = MethodSpec.methodBuilder("init")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC);

        for (String module : moduleNames) {
            initMethod.addStatement(CLS_PREFIX.concat("_") + module + ".gen()");
        }

        TypeSpec routerInit = TypeSpec.classBuilder("RouterInit")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(initMethod.build())
                .build();
        try {
            JavaFile.builder(GEN_PKG_NAME, routerInit)
                    .build()
                    .writeTo(filer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private boolean handleRouter(String genClassName, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(ActivityMapping.class);
        MethodSpec.Builder mapMethod = MethodSpec.methodBuilder("gen")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                .addStatement("java.util.HashMap<String,Class> paramTypes")
                .addStatement("java.util.List<com.github.imishx.activityrouter.router.Interceptor> interceptors;");

        for (Element element : elements) {
            ActivityMapping activityMapping = element.getAnnotation(ActivityMapping.class);

            mapMethod.addStatement("paramTypes = new java.util.HashMap<>()");
            addStatement(mapMethod, activityMapping.intParams(), int.class);
            addStatement(mapMethod, activityMapping.longParams(), long.class);
            addStatement(mapMethod, activityMapping.booleanParams(), boolean.class);
            addStatement(mapMethod, activityMapping.shortParams(), short.class);
            addStatement(mapMethod, activityMapping.floatParams(), float.class);
            addStatement(mapMethod, activityMapping.doubleParams(), double.class);
            addStatement(mapMethod, activityMapping.byteParams(), byte.class);
            addStatement(mapMethod, activityMapping.charParams(), char.class);
            addStatement(mapMethod, activityMapping.stringParams(), String.class);

            for (String format : activityMapping.value()) {
                ClassName className;
                if (element.getKind() == ElementKind.CLASS) {
                    className = ClassName.get((TypeElement) element);

                    if (format.startsWith("/") || format.endsWith("/")) {
                        messager.printMessage(Diagnostic.Kind.ERROR, "url of " + className + " is not well formed");
                        return false;
                    }

                    TypeElement typeElement = (TypeElement) element;
                    AnnotationMirror annotationMirror = getAnnotationMirror(typeElement, ActivityInterceptor.class.getName());

                    if (null == annotationMirror) {
                        mapMethod.addStatement("interceptors = null");
                        mapMethod.addStatement("com.github.imishx.activityrouter.router.Routers.map($S, $T.class, paramTypes,null)", format, className);
                    } else {
                        mapMethod.addStatement("interceptors = new java.util.ArrayList<>()");
                        AnnotationValue annotationValue = getAnnotationValue(annotationMirror, "value");
                        List<AnnotationValue> values = (List<AnnotationValue>) annotationValue.getValue();

                        for (AnnotationValue value : values) {
                            mapMethod.addStatement("interceptors.add(new $T())", ClassName.get(asTypeElement((TypeMirror) value.getValue())));
                        }
                        mapMethod.addStatement("com.github.imishx.activityrouter.router.Routers.map($S, $T.class, paramTypes,interceptors)", format, className);

                    }

                } else {
                    throw new IllegalArgumentException("unknow type");
                }

            }
            mapMethod.addCode("\n");

        }
        TypeSpec routerMapping = TypeSpec.classBuilder(genClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(mapMethod.build())
                .build();
        try {
            JavaFile.builder(GEN_PKG_NAME, routerMapping)
                    .build()
                    .writeTo(filer);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return true;
    }


    private static AnnotationMirror getAnnotationMirror(TypeElement typeElement, String className) {
        for (AnnotationMirror m : typeElement.getAnnotationMirrors()) {
            if (m.getAnnotationType().toString().equals(className)) {
                return m;
            }
        }
        return null;
    }

    private static AnnotationValue getAnnotationValue(AnnotationMirror annotationMirror, String key) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet()) {
            if (entry.getKey().getSimpleName().toString().equals(key)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private TypeElement asTypeElement(TypeMirror typeMirror) {
        Types TypeUtils = this.processingEnv.getTypeUtils();
        return (TypeElement) TypeUtils.asElement(typeMirror);
    }

    private void addStatement(MethodSpec.Builder mapMethod, String[] args, Class typeClz) {
        for (String name : args) {
            mapMethod.addStatement("paramTypes.put($S,$T.class)", name, typeClz);
        }
    }

}
