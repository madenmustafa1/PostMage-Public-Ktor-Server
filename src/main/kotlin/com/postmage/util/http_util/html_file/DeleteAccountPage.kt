package com.postmage.util.http_util.html_file

import com.postmage.util.AppMessages
import com.postmage.util.Constants
import com.postmage.util.http_util.HttpRoute
import kotlinx.html.*
import kotlinx.html.stream.appendHTML

object DeleteAccountPage {
    fun formPage(): String {

        val action = HttpRoute.BASE_URL + HttpRoute.PROFILE + HttpRoute.DELETE_ACCOUNT_WEB

        val formHtml = StringBuilder().appendHTML().html {
            head {
                style {
                    unsafe {
                        raw(HtmlStyle.getDefaultStyle())
                    }
                }
            }
            body {
                div("container") {
                    h2("text-center") {
                        style = "text-align: center; color: ${HtmlColor.MAIN_COLOR}; margin-bottom: 20px;"
                        +Constants.APP_NAME
                    }
                    form(
                        action = action,
                        method = FormMethod.post
                    ) {
                        div("input-field") {
                            label {
                                htmlFor = "email"
                                +"Email"
                            }
                            input {
                                id = "email"
                                name = "email"
                                type = InputType.email
                                attributes["required"] = "true"
                            }
                        }
                        div("input-field") {
                            label {
                                htmlFor = "password"
                                +"Password"
                            }
                            input {
                                id = "password"
                                name = "password"
                                type = InputType.password
                                attributes["pattern"] = ".{5,}"
                                attributes["title"] = AppMessages().PASSWORD_NOT_BE_SHORT
                                attributes["required"] = "true"
                            }
                        }
                        button(classes = "submit-button", type = ButtonType.submit) {
                            +"Delete Account"
                        }
                    }
                }
                div("footer") {
                    style =
                        "background-color: ${HtmlColor.MAIN_COLOR}; padding: 10px; text-align: center; font-size: 12px; color: #FFFFFF;"
                    p {
                        +Constants.FOOTER
                    }
                }
            }
        }
        return formHtml.toString()
    }

}
