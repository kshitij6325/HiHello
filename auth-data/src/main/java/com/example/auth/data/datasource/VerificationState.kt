package com.example.auth.data.datasource

import com.example.auth.data.User

sealed class State(val user: User) {
    class Otp(mUser: User) : State(mUser)
    class Complete(mUser: User) : State(mUser)
}
