package com.germanno.client

import com.germanno.client.interactors.ClientInteractor
import com.germanno.client.interactors.LinuxInteractor
import com.germanno.client.interactors.MacOsInteractor
import com.germanno.client.interactors.WindowsInteractor
import com.sun.javafx.PlatformUtil
import tornadofx.*
import java.io.File
import java.io.FileOutputStream
import java.lang.IllegalStateException
import java.net.InetAddress
import java.util.concurrent.TimeUnit

/**
 * @author Germanno Domingues - germanno.domingues+github@gmail.com
 * @since 2/15/18 3:16 AM
 */
class EnvironmentManager {

    private val interactor: ClientInteractor =
            when {
                PlatformUtil.isMac()     -> MacOsInteractor()
                PlatformUtil.isLinux()   -> LinuxInteractor()
                PlatformUtil.isWindows() -> WindowsInteractor()
                else                     -> throw IllegalStateException("Operating System is not supported")
            }

    private val workingDir by lazy {
        File(System.getProperty("java.io.tmpdir"), "PS2Klient")
                .apply { if (!exists()) mkdirs() }
    }

    private val preferencesFile by lazy {
        File("ps2-klient.ini")
                .apply { if (!exists()) createNewFile() }
    }

    private val sharedFilesDir by lazy {
        workingDir.resolve("sharedFiles")
                .apply { if (!exists()) mkdirs() }
    }

    private val binaryFile by lazy {
        workingDir.resolve(interactor.fileName)
    }

    private val runningProcesses = mutableSetOf<Process>()

    fun prepare(resources: ResourceLookup) {
        //createTempDir()
        createDestinationFile(resources)
    }

    private fun createDestinationFile(resources: ResourceLookup) {
        with(binaryFile) {
            if (!exists()) {
                createNewFile()
                copyPs2Client(resources)
                interactor.copyAdditionalFiles(resources, binaryFile.parentFile)
                interactor.setExecutableFlags(binaryFile)

                println(resources.stream("/${interactor.fileName}"))
            }
        }
    }

    private fun copyPs2Client(resources: ResourceLookup) {
        resources
                .stream("/${interactor.fileName}")
                .use { origin ->
                    println("Copying bytes")
                    FileOutputStream(binaryFile).buffered().use { destination ->
                        origin.copyTo(destination)
                    }
                }
    }

    fun startClient(hostAddress: InetAddress) {
        interactor.startClient(sharedFilesDir, binaryFile, hostAddress)
                .let { runningProcesses.add(it) }
    }

    fun killClient() {
        runningProcesses
                .forEach {
                    with(it) {
                        destroyForcibly()
                        waitFor(10, TimeUnit.MILLISECONDS)
                    }
                }
    }

    fun shareFile(newSharedFile: File) {
        getPreferences()
                .apply { add(newSharedFile.absolutePath) }
                .saveToPreferences()

        interactor.addFile(sharedFilesDir, newSharedFile)
    }

    fun unshareFile(fileToRemove: File) {
        getPreferences()
                .apply { remove(fileToRemove.absolutePath) }
                .saveToPreferences()

        interactor.removeFile(sharedFilesDir, fileToRemove)
    }

    fun listSharedFiles(): List<File> {
        getPreferences()
                .forEach { interactor.addFile(sharedFilesDir, File(it)) }

        return interactor.listSharedFiles(sharedFilesDir)
    }

    private fun getPreferences(): MutableList<String> {
        return preferencesFile
                .readLines()
                .toMutableList()
    }

    private fun MutableList<String>.saveToPreferences() {
        preferencesFile
                .writeText(
                        toMutableSet()
                                .joinToString(System.lineSeparator())
                )
    }

    fun checkConnection(hostAddress: InetAddress): Boolean {
        return interactor.checkConnection(hostAddress)
    }

}