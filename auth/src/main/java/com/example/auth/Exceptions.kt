package com.example.auth

import java.lang.Exception

class NoSuchUserException : Exception("No such user found")
class WrongPasswordException : Exception("Wrong password")
class UserAlreadyExitsException : Exception("User already exits")
class InvalidPhoneNumberException : Exception("Invalid mobile number")
class EmptyPasswordException : Exception("Username is empty")
class EmptyUserNameException : Exception("Password is empty")
