package com.iankush.httpcatstest

import cats.data.Kleisli
import cats.effect.IO

trait Authentication {
  def login: Kleisli[IO, LoginRequest, Either[String, User]]
  def logout(user: User): IO[Either[String, User]]
}

trait Registration {
  def register: Kleisli[IO, RegisterRequest, Either[String, User]]
}

trait PasswordManagement {
  def resetPassword: Kleisli[IO, ResetPasswordRequest, Either[String, User]]
  def changePassword(user: User, newPassword: String): IO[Either[String, User]]
}
