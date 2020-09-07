package com.wiryatech.gitdroid.data.repositories

import com.wiryatech.gitdroid.data.api.RetrofitBuilder
import com.wiryatech.gitdroid.data.db.UserDb
import com.wiryatech.gitdroid.data.model.Search
import com.wiryatech.gitdroid.data.model.User
import retrofit2.Response

class UserRepository(val db: UserDb) {

    suspend fun searchUser(q: String): Response<Search> {
        return RetrofitBuilder.api.searchUser(q)
    }

    suspend fun getDetailUser(username: String): Response<User> {
        return RetrofitBuilder.api.getDetailUser(username)
    }

    suspend fun getFollower(username: String): Response<List<User>> {
        return RetrofitBuilder.api.getFollower(username)
    }

    suspend fun getFollowing(username: String): Response<List<User>> {
        return RetrofitBuilder.api.getFollowing(username)
    }

}