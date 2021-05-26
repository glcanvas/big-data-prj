package vk.api

import vk.dao.BaseResponse
import vk.dao.Datable
import vk.dao.ResponseHolder

interface ApiCaller<T : Datable> {
    fun call(offset: Int, count: Int): ResponseHolder<BaseResponse<T>>
}