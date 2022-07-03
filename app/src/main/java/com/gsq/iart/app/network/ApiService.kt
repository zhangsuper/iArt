package com.gsq.iart.app.network

import com.gsq.iart.data.bean.ApiPagerResponse
import com.gsq.iart.data.bean.ApiResponse
import com.gsq.iart.data.bean.WorksBean
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 描述　: 网络接口API
 */
interface ApiService {

    companion object {
        const val SERVER_URL = "https://wanandroid.com/"
    }

    /**
     * 根据分类id获取作品数据
     */
    @GET("project/list/{page}/json")
    suspend fun getWorksDataByType(
        @Path("page") pageNo: Int,
        @Query("cid") cid: Int
    ): ApiResponse<ApiPagerResponse<ArrayList<WorksBean>>>


}