package com.germanno.app

import com.germanno.client.EnvironmentManager
import com.germanno.view.MainView
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import tornadofx.*
import kotlin.reflect.KClass

/**
 * @author Germanno Domingues - germanno.domingues+github@gmail.com
 * @since 2/14/18 3:34 AM
 */
class Ps2Klient : App(MainView::class, Styles::class) {

    private val kodein = Kodein {
        bind<EnvironmentManager>() with singleton { EnvironmentManager() }
    }

    init {
        FX.dicontainer = object : DIContainer {
            override fun <T : Any> getInstance(type: KClass<T>): T {
                return if (type.java == Kodein::class.java) {
                    @Suppress("UNCHECKED_CAST")
                    kodein as T
                } else {
                    throw IllegalArgumentException("Use Kodein for dependency injection")
                }
            }
        }
    }

}

inline fun <reified T : Any> Component.kodein() = FX.dicontainer!!.getInstance<Kodein>().instance<T>()
