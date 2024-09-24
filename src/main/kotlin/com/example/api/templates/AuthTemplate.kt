package com.example.api.templates

import kotlinx.css.*
import kotlinx.html.*

fun HTML.authTemplate(clientId: String, redirectUrl: String, state: String) {
    head {
        title("Авторизация")
        link(rel = "stylesheet", href = "/styles.css", type = "text/css")
    }
    body {
        div("column max_width flex_center") {
            h1 {
                +"Авторизация"
            }
            authForm(clientId = clientId, redirectUri = redirectUrl, state = state)
            a(href = "/registration?client_id=$clientId&redirect_uri=$redirectUrl&state=$state") {
                +"Зарегистрироваться"
            }
        }
    }
}

fun FlowContent.authForm(clientId: String, redirectUri: String, state: String) {
    postForm(action = "/auth?client_id=$clientId&redirect_uri=$redirectUri&state=$state", classes = "column") {
        input(type = InputType.text, name = "login")
        input(type = InputType.password, name = "password")
        submitInput { this.value = "Войти" }
    }
}

fun CSSBuilder.authCss() {
    body {
        backgroundColor = Color.white
    }

    rule(".max_width") {
        width = 100.vw
    }

    rule(".flex_center") {
        display = Display.flex
        alignItems = Align.center
    }

    rule(".column") {
        display = Display.flex
        flexDirection = FlexDirection.column
    }

    rule(".row") {
        display = Display.flex
        flexDirection = FlexDirection.row
    }
}