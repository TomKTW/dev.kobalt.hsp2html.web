/*
 * dev.kobalt.hsp2html
 * Copyright (C) 2022 Tom.K
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.kobalt.hsp2html.web

import dev.kobalt.hsp2html.web.convert.ConvertRepository
import dev.kobalt.hsp2html.web.extension.ifLet
import dev.kobalt.hsp2html.web.index.indexRoute
import dev.kobalt.hsp2html.web.status.exceptionStatus
import dev.kobalt.hsp2html.web.status.notFoundStatus
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import org.slf4j.event.Level
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

fun main(args: Array<String>) {
    val parser = ArgParser("hsp2html")
    val resourcePath by parser.option(ArgType.String, "resourcePath", null, null)
    val fontPath by parser.option(ArgType.String, "fontPath", null, null)
    val jarPath by parser.option(ArgType.String, "jarPath", null, null)
    val httpServerPort by parser.option(ArgType.Int, "httpServerPort", null, null)
    val httpServerHost by parser.option(ArgType.String, "httpServerHost", null, null)
    parser.parse(args)
    ConvertRepository.apply {
        this.resourcePath = resourcePath
        this.fontPath = fontPath
        this.jarPath = jarPath
    }
    ifLet(httpServerPort, httpServerHost) { port, host ->
        val server = embeddedServer(CIO, port, host) {
            install(XForwardedHeaderSupport)
            install(DefaultHeaders) {
                header("X-Frame-Options", "SAMEORIGIN")
                header("X-Content-Type-Options", "nosniff")
                header(
                    "Permissions-Policy",
                    "geolocation=(), midi=(), sync-xhr=(), microphone=(), camera=(), magnetometer=(), gyroscope=(), fullscreen=(), payment=()"
                )
                header("Referrer-Policy", "strict-origin")
                header("Strict-Transport-Security", "max-age=2592000")
            }
            install(CachingHeaders)
            install(CallLogging) { level = Level.INFO }
            install(Compression) { gzip() }
            install(StatusPages) {
                exceptionStatus()
                notFoundStatus()
            }
            install(Routing) {
                indexRoute()
            }
        }
        Runtime.getRuntime().addShutdownHook(thread(start = false) {
            server.stop(0, 10, TimeUnit.SECONDS)
        })
        server.start(true)
    }
}
