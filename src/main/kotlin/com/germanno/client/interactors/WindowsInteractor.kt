package com.germanno.client.interactors

import com.germanno.client.ProcessCreator
import tornadofx.*
import java.io.File
import java.io.FileOutputStream
import java.net.InetAddress
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

/**
 * @author Germanno Domingues - germanno.domingues+github@gmail.com
 * @since 2/15/18 3:49 AM
 */
class WindowsInteractor : ClientInteractor {

    private val charset = Charset.forName("UTF-8")

    override val fileName: String = "ps2client.exe"

    private fun getFileList(workingDir: File): File {
        return File(workingDir, "elflist.txt")
                .apply { if (!exists()) createNewFile() }
    }

    override fun setExecutableFlags(executableFile: File) {
    }

    override fun copyAdditionalFiles(resources: ResourceLookup, workingDir: File) {
        val destination =
                workingDir
                        .resolve("pthreadGC2.dll")
                        .apply { if (!exists()) createNewFile() }

        resources
                .stream("/pthreadGC2.dll")
                .use { origin ->
                    FileOutputStream(destination).buffered().use { destination ->
                        origin.copyTo(destination)
                    }
                }
    }

    override fun addFile(workingDir: File, newSharedFile: File) {
        getFileList(workingDir)
                .appendText(
                        "${newSharedFile.absolutePath}${System.lineSeparator()}",
                        charset
                )
    }

    override fun removeFile(workingDir: File, fileToRemove: File) {
        getFileList(workingDir)
                .run {
                    writeText(
                            readLines(charset)
                                    .filterNot { it == fileToRemove.absolutePath }
                                    .joinToString(System.lineSeparator()),
                            charset
                    )
                }
    }

    override fun listSharedFiles(workingDir: File): List<File> {
        return getFileList(workingDir)
                .readLines(charset)
                .map { File(it) }
                .filter { file ->
                    file.exists()
                            .also { exists -> if (!exists) file.delete() }
                }
    }

    override fun checkConnection(hostAddress: InetAddress): Boolean {
        return with(
                ProcessCreator
                        .createProcess(
                                "ping",
                                "-n",
                                "1",
                                hostAddress.hostAddress
                        )
        ) {
            waitFor(1, TimeUnit.SECONDS)
            !isAlive && exitValue() == 0
        }
    }

}
