package com.easydemo.butterknife_compiler

import com.easydemo.butterknife_annotations.BindView
import com.easydemo.butterknife_annotations.OnClick
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import java.util.*
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror
import javax.tools.Diagnostic

// @AutoService不起效
//@AutoService(Processor::class)
class ButterKnifeProcessor : AbstractProcessor() {
    private lateinit var filer: Filer
    private lateinit var messager: Messager
    private val UNBINDER = ClassName.get("com.easydemo.butterknife_runtime", "Unbinder")
    // 因为是java module，所以引用Android View需要手动ClassName.get
    private val VIEW = ClassName.get("android.view", "View")
    private val ON_CLICK_LISTENER = ClassName.get("android.view.View", "OnClickListener")
    private val VIEW_TYPE = "android.view.View"

    override fun init(p0: ProcessingEnvironment?) {
        super.init(p0)
        filer = p0!!.filer
        messager = p0.messager
        messager.printMessage(Diagnostic.Kind.WARNING, "==========>init\n")
    }

    override fun process(p0: MutableSet<out TypeElement>?, p1: RoundEnvironment?): Boolean {
        messager.printMessage(Diagnostic.Kind.WARNING, "===========>process\n")
        // 遍历所有的class文件
        p1!!.rootElements.forEach { rootElement ->
            // enclosingElement 如果是类，则返回包名
            val packageStr = rootElement.enclosingElement.toString()
            // simpleName 如果是类，则返回类名 如果是成员变量，则返回成员变量名
            val classStr = rootElement.simpleName.toString()
            val className = ClassName.get(packageStr, classStr)
            val bindingClassName = ClassName.get(packageStr, classStr + "Binding")
            // 类
            val typeSpecBuilder = TypeSpec.classBuilder(bindingClassName)
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.FINAL)
                .addSuperinterface(UNBINDER)
                .addField(className, "target", Modifier.PRIVATE)
                .addField(VIEW, "source", Modifier.PRIVATE)
            // 构造方法
            val constructSpecBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(className, "target", Modifier.FINAL)
                .addParameter(VIEW, "source", Modifier.FINAL)
                .addStatement("this.target = target")
                .addStatement("this.source = source")
            // Unbind
            val unbindMethodSpecBuilder = MethodSpec.methodBuilder("unbind")
                .addAnnotation(Override::class.java)
                .addModifiers(Modifier.PUBLIC)
            var hasBinding = false
            // 遍历所有成员变量、方法等成员
            rootElement.enclosedElements.forEach { element ->
                // @BindView
                val bindViewAnnotation = element.getAnnotation(BindView::class.java)
                if(bindViewAnnotation != null) {
                    hasBinding = true
                    // element.simpleName 返回变量名称 textView
                    // 在构造方法中声明
                    constructSpecBuilder.addStatement("target.\$N = source.findViewById(\$L)", element.simpleName, bindViewAnnotation.value)
                    unbindMethodSpecBuilder.addStatement("target.\$N = null", element.simpleName)
                }
                // @OnClick
                val onClickAnnotation = element.getAnnotation(OnClick::class.java)
                if(onClickAnnotation != null) {
                    hasBinding = true
                    val executableElement = element as ExecutableElement
                    // 注解Ids
                    val viewIds = onClickAnnotation.values
                    // 方法名称
                    val functionName = executableElement.simpleName.toString()
                    // 被注解的方法参数，必须满足没有参数或者只有View参数
                    val parameters = executableElement.parameters
                    // 较检
                    if(parameters.size > 1) {
                        messager.printMessage(Diagnostic.Kind.ERROR, "@OnClick方法参数大于1")
                    }
                    if(parameters.size == 1 && !isSubtypeOfType(parameters[0].asType(), VIEW_TYPE)) {
                        messager.printMessage(Diagnostic.Kind.ERROR, "@OnClick方法参数不是View")
                    }
                    constructSpecBuilder.addStatement("\$T view", VIEW)
                    viewIds.forEach {id ->
                        // 根据Ids发现View
                        constructSpecBuilder.addStatement("view = source.findViewById(\$L)", id)
                        constructSpecBuilder.beginControlFlow("if(view != null)")
                        // 创建匿名内部类
                        val onClickBuilder = MethodSpec.methodBuilder("onClick")
                            .addAnnotation(Override::class.java)
                            .addModifiers(Modifier.PUBLIC)
                            .addParameter(VIEW, "v")
                        if(parameters.size <= 0) {
                            onClickBuilder.addStatement("target.\$L()", functionName)
                        } else {
                            onClickBuilder.addStatement("target.\$L(v)", functionName)
                        }
                        val callbackBuilder = TypeSpec.anonymousClassBuilder("")
                            .superclass(ON_CLICK_LISTENER)
                            .addMethod(onClickBuilder.build())
                        constructSpecBuilder.addStatement("view.setOnClickListener(\$L)", callbackBuilder.build())
                        constructSpecBuilder.endControlFlow()
                    }
                }
            }
            unbindMethodSpecBuilder
                .addStatement("this.target = null")
                .addStatement("this.source = null")
            val typeSpec = typeSpecBuilder
                .addMethod(constructSpecBuilder.build())
                .addMethod(unbindMethodSpecBuilder.build())
                .build()
            if(hasBinding) {
                try {
                    JavaFile.builder(packageStr, typeSpec).build().writeTo(filer)
                } catch(e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return false
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return Collections.singleton(BindView::class.java.canonicalName)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    // 从ButterKnife源码拷贝的
    fun isSubtypeOfType(typeMirror: TypeMirror, otherType: String): Boolean {
        if (isTypeEqual(typeMirror, otherType)) {
            return true
        }
        if (typeMirror.kind != TypeKind.DECLARED) {
            return false
        }
        val declaredType = typeMirror as DeclaredType
        val typeArguments = declaredType.typeArguments
        if (typeArguments.size > 0) {
            val typeString =
                StringBuilder(declaredType.asElement().toString())
            typeString.append('<')
            for (i in typeArguments.indices) {
                if (i > 0) {
                    typeString.append(',')
                }
                typeString.append('?')
            }
            typeString.append('>')
            if (typeString.toString() == otherType) {
                return true
            }
        }
        val element = declaredType.asElement() as? TypeElement ?: return false
        val typeElement = element
        val superType = typeElement.superclass
        if (isSubtypeOfType(superType, otherType)) {
            return true
        }
        for (interfaceType in typeElement.interfaces) {
            if (isSubtypeOfType(interfaceType, otherType)) {
                return true
            }
        }
        return false
    }

    // 从ButterKnife源码拷贝的
    private fun isTypeEqual(typeMirror: TypeMirror, otherType: String): Boolean {
        return otherType == typeMirror.toString()
    }
}