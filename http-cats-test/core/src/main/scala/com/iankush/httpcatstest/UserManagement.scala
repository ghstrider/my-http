package com.iankush.httpcatstest

// UserManagementApp.scala
import cats.effect.{ExitCode, IO, IOApp}

object UserManagementApp extends IOApp {

  val userAuth = new UserAuthImpl()

  def run(args: List[String]): IO[ExitCode] = {
    val registerFlow =
      userAuth.register.run(RegisterRequest("test@example.com", "password123"))
    val loginFlow =
      userAuth.login.run(LoginRequest("test@example.com", "password123"))
    val resetPasswordFlow = userAuth.resetPassword.run(
      ResetPasswordRequest("test@example.com", "newPassword123")
    )

    for {
      _ <- registerFlow.flatMap {
        case Right(user) => IO(println(s"User registered: $user"))
        case Left(error) => IO(println(s"Registration failed: $error"))
      }
      _ <- loginFlow.flatMap {
        case Right(user) => IO(println(s"User logged in: $user"))
        case Left(error) => IO(println(s"Login failed: $error"))
      }
      _ <- resetPasswordFlow.flatMap {
        case Right(user) => IO(println(s"Password reset for user: $user"))
        case Left(error) => IO(println(s"Password reset failed: $error"))
      }
    } yield ExitCode.Success
  }
}
