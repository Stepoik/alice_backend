package com.example.api.templates

import kotlinx.html.*

fun HTML.registrationTemplate(clientId: String, redirectUri: String, state: String) {
    head {
        title("Регистрация")
        link(rel = "stylesheet", href = "/styles.css", type = "text/css")
    }
    body {
        div("column max_width flex_center") {
            h1 {
                +"Регистрация"
            }
            registerForm(clientId = clientId, redirectUri = redirectUri, state = state)
        }
    }
}

fun FlowContent.registerForm(clientId: String, redirectUri: String, state: String) {
    postForm(action = "/registration?client_id=$clientId&redirect_uri=$redirectUri&state=$state", classes = "column") {
        input(type = InputType.text, name = "name")
        input(type = InputType.text, name = "login")
        input(type = InputType.password, name = "password")
        submitInput { this.value = "Зарегистрироваться" }
    }
}