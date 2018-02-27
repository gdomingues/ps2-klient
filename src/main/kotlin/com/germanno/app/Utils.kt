package com.germanno.app

import java.net.InetAddress

/**
 * @author Germanno Domingues - germanno.domingues+github@gmail.com
 * @since 2/25/18 8:44 PM
 */

fun isValidIpAddress(ipAddress: String) =
        try {
            InetAddress.getByName(ipAddress)
            ipAddress.isNotEmpty()
        } catch (ex: Exception) {
            false
        }