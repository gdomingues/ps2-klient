package com.germanno.client.interactors

import com.germanno.client.ProcessCreator
import tornadofx.*
import java.io.File
import java.net.InetAddress
import java.util.concurrent.TimeUnit

/**
 * @author Germanno Domingues - germanno.domingues+github@gmail.com
 * @since 2/15/18 3:47 AM
 */
abstract class UnixInteractor : ClientInteractor {

    override fun setExecutableFlags(executableFile: File) {
        ProcessCreator.createProcess(
                "chmod",
                "+x",
                executableFile.path
        ).waitFor()
    }

    override fun copyAdditionalFiles(resources: ResourceLookup, workingDir: File) {
    }

    override fun addFile(workingDir: File, newSharedFile: File) {
        ProcessCreator.createProcess(
                workingDir,
                "ln",
                "-s",
                newSharedFile.absolutePath,
                newSharedFile.name
        ).waitFor()
    }

    override fun removeFile(workingDir: File, fileToRemove: File) {
        ProcessCreator.createProcess(
                workingDir,
                "rm",
                fileToRemove.name
        ).waitFor()
    }

    override fun listSharedFiles(workingDir: File): List<File> {
        return workingDir.listFiles().toList()
    }

    override fun checkConnection(hostAddress: InetAddress): Boolean {
        return with(
                ProcessCreator
                        .createProcess(
                                "ping",
                                "-c",
                                "1",
                                hostAddress.hostAddress
                        )
        ) {
            waitFor(1, TimeUnit.SECONDS)
            !isAlive && exitValue() == 0
        }
    }

}