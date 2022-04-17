package me.fan87.placeholderplugin.tasks

import me.fan87.placeholderplugin.PlaceHolderExtension
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.jvm.tasks.Jar
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.LdcInsnNode
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

open class PlaceHolderTask: Jar() {

    @InputFile
    val input: RegularFileProperty

    init {
        archiveClassifier.set("placeholder")
        this.input = project.objects.fileProperty()
        this.input.set(File(project.buildDir, "libs/" + project.name + "-" + project.version + ".jar"))
    }

    @TaskAction
    fun action() {
        val placeHolders = project.extensions.getByType(PlaceHolderExtension::class.java)


        var inputFile = input.get().asFile
        assert(inputFile.name.endsWith(".jar")) { "The input must be a jar file!" }
        val outputFile = archiveFile.get().asFile
        assert(outputFile.name.endsWith(".jar")) { "The output must be a jar file!" }

        println("Input File: " + inputFile.name)
        println("Output File: " + outputFile.name)

        val inputStream = ZipInputStream(FileInputStream(inputFile))
        val outputStream = ZipOutputStream(FileOutputStream(outputFile))

        var entry = inputStream.nextEntry

        while(entry != null) {
            var out: ByteArray = inputStream.readBytes()

            if (entry.name.endsWith(".class")) {
                try {
                    val node = ClassNode()
                    val reader = ClassReader(out)
                    reader.accept(node, 0)

                    for (method in node.methods) {
                        for (instruction in method.instructions) {
                            if (instruction is LdcInsnNode) {
                                if (instruction.cst is String) {
                                    for (placeHolder in placeHolders.placeHolders) {
                                        if ("%${placeHolder.key}%" in (instruction.cst as String)) {
                                            instruction.cst = (instruction.cst as String)
                                                .replace("%${placeHolder.key}%", placeHolder.value.get(), false)
                                        }
                                    }
                                }
                            }
                        }
                    }


                    val writer = ClassWriter(ClassWriter.COMPUTE_FRAMES)
                    node.accept(writer)
                    out = writer.toByteArray()
                } catch (e: Exception) {
                    project.logger.warn("Something went wrong while transforming " + entry.name + " , using original class file as input...", e)
                }
            }

            outputStream.putNextEntry(ZipEntry(entry.name))
            outputStream.write(out)

            outputStream.closeEntry()
            inputStream.closeEntry()

            entry = inputStream.nextEntry
        }

        inputStream.close()
        outputStream.close()
    }


}