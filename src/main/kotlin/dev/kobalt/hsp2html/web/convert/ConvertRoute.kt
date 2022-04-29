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

package dev.kobalt.hsp2html.web.convert

import dev.kobalt.hsp2html.web.extension.*
import dev.kobalt.hsp2html.web.html.HtmlStringContent
import dev.kobalt.hsp2html.web.html.LimitedSizeInputStream
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.html.*

fun Route.convertRoute() {
    route("convert/") {
        get {
            call.respondHtmlContent(
                title = ConvertRepository.pageTitle,
                description = ConvertRepository.pageSubtitle
            ) {
                pageArticle(
                    ConvertRepository.pageTitle,
                    ConvertRepository.pageSubtitle
                ) {
                    h3 { text("Form") }
                    form {
                        method = FormMethod.post
                        encType = FormEncType.multipartFormData
                        div {
                            pageInputFile("Page file", "input")
                            br { }
                            pageInputSubmit("Begin conversion", "submit")
                        }
                    }
                    pageMarkdown(ConvertRepository.pageContent)
                }
            }
        }
        post {
            runCatching {
                val parts = call.receiveMultipart().readAllParts()
                val part = parts.find { it.name == "input" } as? PartData.FileItem ?: throw Exception()
                val data = LimitedSizeInputStream(part.streamProvider(), 500 * 1024).readBytes().decodeToString()
                call.respond(HtmlStringContent(HttpStatusCode.OK, ConvertRepository.submit(data)))
            }.getOrElse {
                call.respondHtmlContent(
                    title = ConvertRepository.pageTitle,
                    description = ConvertRepository.pageSubtitle
                ) {
                    pageArticle(
                        ConvertRepository.pageTitle,
                        ConvertRepository.pageSubtitle
                    ) {
                        h3 { text("Failure") }
                        p { text("Conversion process was not successful.") }
                        if (it is TimeoutCancellationException) {
                            p { text("The process took too long to convert as the server is most likely overloaded. You could try again later or download converter locally, which might be preferred if this is not functional.") }
                        }
                    }
                }
            }
        }
    }
}

