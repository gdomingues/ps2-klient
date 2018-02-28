package com.germanno.view

import com.germanno.app.isValidIpAddress
import com.germanno.app.kodein
import com.germanno.client.EnvironmentManager
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.concurrent.Task
import javafx.scene.control.ListView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import javafx.stage.Window
import javafx.util.Duration
import tornadofx.*
import java.io.File
import java.io.IOException
import java.net.InetAddress
import java.net.UnknownHostException

/**
 * @author Germanno Domingues - germanno.domingues+github@gmail.com
 * @since 2/20/18 1:47 AM
 */
class MainController : Controller() {

    private val environmentManager: EnvironmentManager = kodein()

    private var connectivityMonitorTask: Task<Boolean>? = null
    private var startMonitoringDelayedTask: FXTimerTask? = null
    private var listener: ChangeListener<Boolean>? = null

    val connectionStatusColor = SimpleObjectProperty<Paint>()
    val connectionStatusText = SimpleObjectProperty<String>()
    val invalidIpAddress = SimpleBooleanProperty()

    val files: ObservableList<String> = FXCollections.observableArrayList<String>(
            environmentManager.listSharedFiles().map { it.canonicalPath }
    )

    fun addFolderClicked(window: Window?) {
        DirectoryChooser()
                .apply { title = "Select a folder" }
                .showDialog(window)
                .handleDialogReturn()
    }

    fun addFileClicked(window: Window?) {
        FileChooser()
                .apply { title = "Select a file" }
                .showOpenDialog(window)
                .handleDialogReturn()
    }

    private fun File?.handleDialogReturn() {
        this?.let { newFile ->
            environmentManager.shareFile(newFile)
            updateFileList()
        }
    }

    fun deleteFileClicked(it: KeyEvent) {
        when (it.code) {
            KeyCode.BACK_SPACE, KeyCode.DELETE -> {
                @Suppress("UNCHECKED_CAST")
                with(it.source as ListView<String>) {
                    environmentManager.unshareFile(File(selectedItem))
                    updateFileList()
                }
            }
            else                               -> {
            }
        }
    }

    private fun updateFileList() {
        files.apply {
            clear()
            addAll(environmentManager.listSharedFiles().map { it.canonicalPath })
        }
    }

    fun handleIpChanged(ipAddress: String) {
        val isValid = isValidIpAddress(ipAddress)

        invalidIpAddress.set(!isValid)
        startMonitoringDelayedTask?.cancel()
        stopMonitoringConnectivity()

        if (isValid) {
            startMonitoringDelayedTask = runLater(Duration.seconds(1.0)) {
                startMonitoringConnectivity(ipAddress)
            }
        }
    }

    fun stopMonitoringConnectivity() {
        connectivityMonitorTask?.run {
            valueProperty().removeListener(listener)
            cancel()
        }
    }

    private fun startMonitoringConnectivity(ipAddress: String) {
        val hostAddress = try {
            InetAddress.getByName(ipAddress)
        } catch (ex: UnknownHostException) {
            return
        }

        connectivityMonitorTask = runAsync {
            while (true) {
                val isReachable = try {
                    environmentManager.checkConnection(hostAddress)
                } catch (ex: IOException) {
                    false
                }

                value(isReachable)

                try {
                    Thread.sleep(1000)
                } catch (ex: InterruptedException) {
                    // The check for cancelled is below
                }

                if (isCancelled) {
                    stopClient()
                    break
                }
            }
            true
        }.apply {
            listener = createConnectivityListener(hostAddress)
            valueProperty().addListener(listener)
        }
    }

    private fun createConnectivityListener(hostAddress: InetAddress): ChangeListener<Boolean> {
        return ChangeListener { _, wasReachable, isReachable ->
            when {
                wasReachable != false && !isReachable -> stopClient()
                wasReachable != true && isReachable   -> startClient(hostAddress)
            }
        }
    }

    private fun stopClient() {
        runLater {
            connectionStatusColor.set(Color.RED)
            connectionStatusText.set("PlayStation 2 is not reachable")
        }

        environmentManager.killClient()
    }

    private fun startClient(hostAddress: InetAddress) {
        connectionStatusColor.set(Color.DARKGREEN)
        connectionStatusText.set("PlayStation 2 is reachable")

        runAsync {
            with(environmentManager) {
                prepare(resources)
                startClient(hostAddress)
            }
        }
    }

}