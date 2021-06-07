package vk.loader.api

import vk.loader.dao.BaseResponse
import vk.loader.dao.Datable
import vk.loader.dao.ResponseHolder

interface ApiCaller<T : Datable> {
    fun call(offset: Int, count: Int): ResponseHolder<BaseResponse<T>>
}