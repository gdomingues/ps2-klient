package com.germanno.view

import com.germanno.app.Styles
import javafx.scene.control.TextField
import javafx.scene.layout.Priority
import tornadofx.*

/**
 * @author Germanno Domingues - germanno.domingues+github@gmail.com
 * @since 2/14/18 3:34 AM
 */
class MainView : View("PS2 Klient") {

    private val controller by inject<MainController>()

    private lateinit var ipAddressField: TextField
    private var delayedStartMonitoringTask: FXTimerTask? = null

    override val root = borderpane {
        addClass(Styles.content)

        top = anchorpane {
            addClass(Styles.topContent)

            hbox {
                hbox {
                    addClass(Styles.topLeft)

                    label("PlayStation 2 IP:")

                    ipAddressField = textfield("192.168.0.10") {
                        addClass(Styles.ipAddress)
                        toggleClass(Styles.invalid, controller.invalidIpAddress)

                        setOnKeyReleased {
                            with(it.code) {
                                if (isArrowKey || isModifierKey || isNavigationKey) return@setOnKeyReleased
                            }

                            text = text.replace(Regex("[^(0-9|.)]"), "")
                            selectRange(length, length)

                            controller.handleIpChanged(text)
                        }
                    }
                }

                hbox {
                    addClass(Styles.topRight)
                    hgrow = Priority.ALWAYS

                    button("Add Folder") {
                        setOnMouseClicked {
                            controller.addFolderClicked(currentWindow)
                        }
                    }
                    button("Add File") {
                        setOnMouseClicked {
                            controller.addFileClicked(currentWindow)
                        }
                    }
                }
            }.anchorpaneConstraints {
                leftAnchor = 0.0
                rightAnchor = 0.0
            }
        }

        center = hbox {
            addClass(Styles.centerContent)

            listview(controller.files) {
                hgrow = Priority.ALWAYS

                setOnKeyPressed {
                    controller.deleteFileClicked(it)
                }
            }
        }

        bottom = hbox {
            addClass(Styles.bottomContent)

            circle(radius = 10) {
                addClass(Styles.connectionStatusCircle)

                fillProperty().bind(controller.connectionStatusColor)
            }

            label {
                textProperty().bind(controller.connectionStatusText)
            }
        }
    }

    override fun onDock() {
        super.onDock()
        controller.handleIpChanged(ipAddressField.text)
    }

    override fun onUndock() {
        delayedStartMonitoringTask?.cancel()
        controller.stopMonitoringConnectivity()
        super.onUndock()
    }

}