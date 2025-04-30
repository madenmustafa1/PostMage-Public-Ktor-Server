package com.postmage.util.http_util.html_file

import com.postmage.util.Constants
import kotlinx.html.*
import kotlinx.html.stream.appendHTML

object ResultPage {

    fun get(message: String, result: Boolean, titleStr: String? = null): String {
        var title = titleStr ?: ""

        if (titleStr == null) {
            title = "Successful"
            if (!result) title = "Failed"
        }

        return StringBuilder().appendHTML().html {
            head {
                style {
                    unsafe {
                        raw(HtmlStyle.resultPageStyle())
                    }
                }
            }
            body {
                div("container") {
                    style = "margin: 0 auto;"
                    h2("success-message") {
                        style = "margin-top: 0;"
                        +title
                    }
                    p {
                        +message
                    }
                }
                div("footer") {
                    p {
                        +Constants.FOOTER
                    }
                }
            }
        }.toString()
    }

}