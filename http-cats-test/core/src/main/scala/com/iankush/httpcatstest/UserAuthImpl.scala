package com.iankush.httpcatstest

// UserAuthImpl.scala
import cats.data.Kleisli
import cats.effect.IO
import cats.syntax.all._

object UserDatabase {
  private var users: Map[String, User] = Map.empty
  private var idCounter: Long = 1L

  def findUserByEmail(email: String): Option[User] = users.get(email)
  def saveUser(user: User): User = {
    users += (user.email -> user)
    user
  }
  def updateUser(user: User): User = {
    users += (user.email -> user)
    user
  }
}

class UserAuthImpl
    extends Authentication
    with Registration
    with PasswordManagement {

  override def login: Kleisli[IO, LoginRequest, Either[String, User]] =
    Kleisli { request =>
      IO {
        UserDatabase.findUserByEmail(request.email) match {
          case Some(user) if user.password == request.password =>
            val loggedInUser = user.copy(isLoggedIn = true)
            UserDatabase.updateUser(loggedInUser)
            Right(loggedInUser)
          case Some(_) => Left("Incorrect password.")
          case None    => Left("User not found.")
        }
      }
    }

  override def logout(user: User): IO[Either[String, User]] = IO {
    if (user.isLoggedIn) {
      val loggedOutUser = user.copy(isLoggedIn = false)
      UserDatabase.updateUser(loggedOutUser)
      Right(loggedOutUser)
    } else {
      Left("User is not logged in.")
    }
  }

  override def register: Kleisli[IO, RegisterRequest, Either[String, User]] =
    Kleisli { request =>
      IO {
        UserDatabase.findUserByEmail(request.email) match {
          case Some(_) => Left("User already exists.")
          case None =>
            val newUser = User(
              UserDatabase.idCounter,
              request.email,
              request.password,
              isLoggedIn = false
            )
            UserDatabase.idCounter += 1
            Right(UserDatabase.saveUser(newUser))
        }
      }
    }

  override def resetPassword
      : Kleisli[IO, ResetPasswordRequest, Either[String, User]] = Kleisli {
    request =>
      IO {
        UserDatabase.findUserByEmail(request.email) match {
          case Some(user) =>
            val updatedUser = user.copy(password = request.newPassword)
            Right(UserDatabase.updateUser(updatedUser))
          case None => Left("User not found.")
        }
      }
  }

  override def changePassword(
      user: User,
      newPassword: String
  ): IO[Either[String, User]] = IO {
    if (user.isLoggedIn) {
      val updatedUser = user.copy(password = newPassword)
      Right(UserDatabase.updateUser(updatedUser))
    } else {
      Left("User is not logged in.")
    }
  }
}
