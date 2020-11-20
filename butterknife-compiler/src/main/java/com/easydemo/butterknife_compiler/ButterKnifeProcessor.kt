package com.easydemo.butterknife_compiler

import com.easydemo.butterknife_annotations.BindView
import com.squareup.javapoet.*
import java.lang.Exception
import java.util.*
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

// @AutoService不起效
//@AutoService(Processor::class)
class ButterKnifeProcessor : AbstractProcessor() {
    private lateinit var filer: Filer
    private lateinit var messager: Messager
    private val UNBINDER = ClassName.get("com.easydemo.butterknife_runtime", "Unbinder")

    override fun init(p0: ProcessingEnvironment?) {
        super.init(p0)
        filer = p0!!.filer
        messager = p0.messager
        messager.printMessage(Diagnostic.Kind.WARNING, "==========>init")
    }

    override fun process(p0: MutableSet<out TypeElement>?, p1: RoundEnvironment?): Boolean {
        messager.printMessage(Diagnostic.Kind.WARNING, "===========>process")
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
                .addField(className, "activity", Modifier.PRIVATE)
            // 构造方法
            val constructSpecBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(className, "activity")
            var hasBinding = false
            // 遍历所有成员变量、方法等成员
            rootElement.enclosedElements.forEach { element ->
                val bindViewAnnotation = element.getAnnotation(BindView::class.java)
                if(bindViewAnnotation != null) {
                    hasBinding = true
                    // element.simpleName 返回变量名称 textView
                    constructSpecBuilder.addStatement("this.activity = activity")
                    constructSpecBuilder.addStatement("activity.\$N = activity.findViewById(\$L)", element.simpleName,bindViewAnnotation.value)
                }
            }
            // Unbind
            val unbindMethodSpecBuilder = MethodSpec.methodBuilder("unbind")
                .addAnnotation(Override::class.java)
                .addModifiers(Modifier.PUBLIC)
                .addStatement("this.activity = null")
            if(hasBinding) {
                try {
                    val typeSpec = typeSpecBuilder
                        .addMethod(constructSpecBuilder.build())
                        .addMethod(unbindMethodSpecBuilder.build())
                        .build()
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
}