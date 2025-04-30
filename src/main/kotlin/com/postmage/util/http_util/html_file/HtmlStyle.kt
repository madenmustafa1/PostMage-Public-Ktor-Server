package com.postmage.util.http_util.html_file

object HtmlStyle {

    fun getDefaultStyle(): String {
        return """
                        body {
                            display: flex;
                            flex-direction: column;
                            justify-content: center;
                            align-items: center;
                            min-height: 100vh;
                            font-family: 'Roboto', sans-serif;
                            margin: 0;
                            padding: 0;
                            background-color: ${HtmlColor.MAIN_COLOR};
                            color: ${HtmlColor.MAIN_COLOR};
                        }

                        .container {
                            width: 90%;
                            max-width: 400px;
                            padding: 20px;
                            background-color: #FFFFFF;
                            box-shadow: 0 0 5px rgba(0, 0, 0, 0.3);
                            color: ${HtmlColor.MAIN_COLOR};
                        }

                        .input-field {
                            margin-bottom: 20px;
                            background-color: #FFFFFF;
                        }

                        .input-field label {
                            display: block;
                            font-size: 14px;
                            font-weight: 500;
                            margin-bottom: 5px;
                            color: ${HtmlColor.MAIN_COLOR};
                        }

                        .input-field input {
                            width: 100%;
                            padding: 10px;
                            border: 1px solid #ccc;
                            border-radius: 4px;
                            font-size: 16px;
                            background-color: #FFFFFF;
                            color: ${HtmlColor.MAIN_COLOR};
                        }

                        .submit-button {
                            width: 100%;
                            padding: 10px;
                            background-color: ${HtmlColor.MAIN_COLOR};
                            color: #FFFFFF;
                            border: none;
                            border-radius: 4px;
                            font-size: 16px;
                            font-weight: 500;
                            cursor: pointer;
                        }

                        .submit-button:hover {
                            background-color: #1976d2;
                        }

                        .footer {
                            position: fixed;
                            bottom: 0;
                            left: 0;
                            right: 0;
                            background-color: ${HtmlColor.MAIN_COLOR};
                            padding: 10px;
                            text-align: center;
                            font-size: 12px;
                            color: #FFFFFF;
                        }

                        .footer p {
                            margin: 0;
                        }

                        @media (max-width: 768px) {
                            .container {
                                width: 90%;
                                max-width: 100%;
                            }
                        }
                    """.trimIndent()
    }

    fun resultPageStyle(): String {
        return """
                    body {
                        display: flex;
                        flex-direction: column;
                        justify-content: center;
                        align-items: center;
                        min-height: 100vh;
                        font-family: 'Roboto', sans-serif;
                        margin: 0;
                        padding: 0;
                        background-color: ${HtmlColor.MAIN_COLOR};
                        color: #FFFFFF;
                    }

                    .container {
                        width: 400px;
                        padding: 20px;
                        background-color: #FFFFFF;
                        box-shadow: 0 0 5px rgba(0, 0, 0, 0.3);
                        color: ${HtmlColor.MAIN_COLOR};
                        text-align: center;
                    }

                    .success-message {
                        font-size: 24px;
                        font-weight: bold;
                        color: ${HtmlColor.MAIN_COLOR};
                        margin-bottom: 20px;
                    }

                    .footer {
                        background-color: ${HtmlColor.MAIN_COLOR};
                        padding: 10px;
                        text-align: center;
                        font-size: 12px;
                        color: #FFFFFF;
                        width: 100%;
                        position: fixed;
                        bottom: 0;
                    }

                    .footer p {
                        margin: 0;
                    }
                """.trimIndent()
    }

}
