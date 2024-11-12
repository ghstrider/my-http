package com.iankush.httpcatstest

final case class User(
    id: Long,
    email: String,
    password: String,
    isLoggedIn: Boolean
)

final case class LoginRequest(email: String, password: String)
final case class RegisterRequest(email: String, password: String)
final case class ResetPasswordRequest(email: String, newPassword: String)
