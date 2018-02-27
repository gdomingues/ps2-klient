package com.germanno.client

import com.germanno.client.interactors.ClientInteractor
import com.germanno.client.interactors.LinuxInteractor
import com.germanno.client.interactors.MacOsInteractor
import com.germanno.client.interactors.WindowsInteractor
import com.sun.javafx.PlatformUtil
import net.harawata.appdirs.AppDirsFactory
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

    private val appDataDir by lazy {
        File(AppDirsFactory.getInstance().getUserDataDir("PS2 Klient", null, "GermannoDomingues"))
                .apply { if (!exists()) mkdirs() }
    }

    private val sharedFilesDir by lazy {
        appDataDir.resolve("sharedFiles")
                .apply { if (!exists()) mkdirs() }
    }

    private val binaryFile by lazy {
        appDataDir.resolve(interactor.fileName)
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
        interactor.addFile(sharedFilesDir, newSharedFile)
    }

    fun unshareFile(fileToRemove: File) {
        interactor.removeFile(sharedFilesDir, fileToRemove)
    }

    fun listSharedFiles(): List<File> {
        return interactor.listSharedFiles(sharedFilesDir)
    }

    fun checkConnection(hostAddress: InetAddress): Boolean {
        return interactor.checkConnection(hostAddress)
    }

}