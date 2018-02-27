package com.germanno.client.interactors

import com.germanno.client.ProcessCreator
import tornadofx.*
import java.io.File
import java.net.InetAddress

/**
 * @author Germanno Domingues - germanno.domingues+github@gmail.com
 * @since 2/15/18 3:45 AM
 */
interface ClientInteractor {

    val fileName: String

    fun setExecutableFlags(executableFile: File)

    fun copyAdditionalFiles(resources: ResourceLookup, workingDir: File)

    fun startClient(workingDir: File, binaryFile: File, hostAddress: InetAddress): Process {
        return ProcessCreator.createProcess(
                workingDir,
                binaryFile.absolutePath,
                "-h",
                hostAddress.hostAddress,
                "listen"
        )
    }

    fun addFile(workingDir: File, newSharedFile: File)

    fun removeFile(workingDir: File, fileToRemove: File)

    fun listSharedFiles(workingDir: File): List<File>

    fun checkConnection(hostAddress: InetAddress): Boolean

}