package com.germanno.client

import java.io.File

/**
 * @author Germanno Domingues - germanno.domingues+github@gmail.com
 * @since 2/15/18 3:31 AM
 */
object ProcessCreator {

    fun createProcess(vararg command: String): Process = createProcess(null, *command)

    fun createProcess(workingDir: File? = null, vararg command: String): Process {
        return with(ProcessBuilder(*command)) {
            workingDir?.let { directory(it) }
            start()
        }
    }

}