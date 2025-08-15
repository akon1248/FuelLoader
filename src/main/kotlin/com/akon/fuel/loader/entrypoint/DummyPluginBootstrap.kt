package com.akon.fuel.loader.entrypoint

import com.akon.fuel.loader.interfaces.PaperPluginClassLoaderExtensions
import com.akon.fuel.loader.DummyPlugin
import io.papermc.paper.plugin.bootstrap.BootstrapContext
import io.papermc.paper.plugin.bootstrap.PluginBootstrap
import io.papermc.paper.plugin.bootstrap.PluginProviderContext
import io.papermc.paper.plugin.entrypoint.classloader.PaperPluginClassLoader
import org.bukkit.plugin.java.JavaPlugin
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

abstract class DummyPluginBootstrap(
    className: String,
    private val lifecycleDelegate: PluginLifecycleDelegate = object : PluginLifecycleDelegate {},
) : PluginBootstrap {

    private val qualifiedName = javaClass.packageName + '.' + className

    lateinit var classLoader: PaperPluginClassLoader

    override fun bootstrap(context: BootstrapContext) {
    }

    final override fun createPlugin(context: PluginProviderContext): JavaPlugin {
        val clazz = (classLoader as PaperPluginClassLoaderExtensions).`fuel$defineClass`(
            qualifiedName,
            generateClass()
        ) // We need to use the PaperPluginClassLoader to load a plugin class.
        val plugin = clazz
            .getDeclaredConstructor(PluginLifecycleDelegate::class.java)
            .newInstance(lifecycleDelegate) as JavaPlugin
        lifecycleDelegate.onInstantiate(plugin)
        return plugin
    }

    private fun generateClass(): ByteArray {
        val cw = ClassWriter(ClassWriter.COMPUTE_FRAMES)
        cw.visit(
            Opcodes.V1_8,
            Opcodes.ACC_PUBLIC,
            qualifiedName.replace('.', '/'),
            null,
            "org/bukkit/plugin/java/JavaPlugin",
            arrayOf(Type.getInternalName(DummyPlugin::class.java))
        )
        addFields(cw)
        addConstructor(cw)
        addMethods(cw)
        return cw.toByteArray()
    }

    private fun addFields(cw: ClassWriter) {
        cw.visitField(
            Opcodes.ACC_PRIVATE or Opcodes.ACC_FINAL,
            "lifecycleDelegate",
            Type.getDescriptor(PluginLifecycleDelegate::class.java),
            null,
            null
        )
    }

    private fun addConstructor(cw: ClassWriter) {
        val mv = cw.visitMethod(
            Opcodes.ACC_PUBLIC,
            "<init>",
            Type.getMethodDescriptor(
                Type.VOID_TYPE,
                Type.getType(PluginLifecycleDelegate::class.java),
            ),
            null,
            null
        )
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "org/bukkit/plugin/java/JavaPlugin", "<init>", "()V", false)
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitVarInsn(Opcodes.ALOAD, 1)
        mv.visitFieldInsn(
            Opcodes.PUTFIELD,
            qualifiedName.replace('.', '/'),
            "lifecycleDelegate",
            Type.getDescriptor(PluginLifecycleDelegate::class.java)
        )
        mv.visitInsn(Opcodes.RETURN)
        mv.visitMaxs(-1, -1)
    }

    private fun addMethods(cw: ClassWriter) {
        listOf("onLoad", "onEnable", "onDisable").forEach { name ->
            val mv = cw.visitMethod(Opcodes.ACC_PUBLIC, name, "()V", null, null)
            mv.visitVarInsn(Opcodes.ALOAD, 0)
            mv.visitFieldInsn(
                Opcodes.GETFIELD,
                qualifiedName.replace('.', '/'),
                "lifecycleDelegate",
                Type.getDescriptor(PluginLifecycleDelegate::class.java)
            )
            mv.visitVarInsn(Opcodes.ALOAD, 0)
            mv.visitMethodInsn(
                Opcodes.INVOKEINTERFACE,
                Type.getInternalName(PluginLifecycleDelegate::class.java),
                name,
                "(Lorg/bukkit/plugin/Plugin;)V",
                true
            )
            mv.visitInsn(Opcodes.RETURN)
            mv.visitMaxs(-1, -1)
        }
    }
}
