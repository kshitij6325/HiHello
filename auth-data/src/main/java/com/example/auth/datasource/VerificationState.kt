package com.example.auth.datasource

import com.example.auth.User

sealed class State(val user: User) {
    class Otp(mUser: User) : State(mUser)
    class Complete(mUser: User) : State(mUser)
}
