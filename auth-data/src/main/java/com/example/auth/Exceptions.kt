package com.example.auth

import java.lang.Exception

class NoSuchUserException : Exception("No such user found")
class WrongPasswordException : Exception("Wrong password")
class UserAlreadyExitsException : Exception("User already exits")
class InvalidPhoneNumberException : Exception("Invalid mobile number")
class EmptyPasswordException : Exception("Password is empty")
class EmptyUserNameException : Exception("Username is empty")
class UnknownUserException : Exception("Unknown user exception")

//user local datasource errors
class UserCreationException : Exception("Error creating user")
