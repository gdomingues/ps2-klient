package com.germanno.app

import javafx.geometry.Pos
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*

/**
 * @author Germanno Domingues - germanno.domingues+github@gmail.com
 * @since 2/14/18 3:34 AM
 */
class Styles : Stylesheet() {

    companion object {
        val content by cssclass()
        val topContent by cssclass()
        val centerContent by cssclass()
        val bottomContent by cssclass()
        val connectionStatusCircle by cssclass()
        val ipAddress by cssclass()
        val invalid by cssclass()

        val topLeft by cssclass()
        val topRight by cssclass()
    }

    init {
        content {
            padding = box(16.pt, 24.pt)
            minWidth = 640.pt
            minHeight = 512.pt
        }

        topContent {
            padding = box(0.pt, 0.pt, 16.pt, 0.pt)

            topLeft {
                alignment = Pos.CENTER_LEFT
                spacing = 10.pt
            }

            topRight {
                alignment = Pos.CENTER_RIGHT
                spacing = 10.pt
                padding = box(0.pt, 0.pt, 0.pt, 10.pt)
            }
        }

        bottomContent {
            alignment = Pos.CENTER_LEFT
            spacing = 10.pt
            padding = box(16.pt, 0.pt, 0.pt, 0.pt)
        }

        connectionStatusCircle {
            fill = Color.RED
            stroke = Color.BLACK
        }

        ipAddress {
            fontWeight = FontWeight.BOLD
            textFill = Color.BLACK

            and(invalid) {
                textFill = Color.RED
            }
        }
    }

}