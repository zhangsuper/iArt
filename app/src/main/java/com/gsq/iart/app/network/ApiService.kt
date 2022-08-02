package com.gsq.iart.app.network

import com.gsq.iart.data.bean.ApiResponse
import com.gsq.iart.data.bean.ConditionClassifyBean
import com.gsq.iart.data.bean.SearchResponse
import com.gsq.iart.data.bean.WorksBean
import com.gsq.iart.data.request.WorkDetailRequestParam
import com.gsq.iart.data.request.WorkPageRequestParam
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * 描述　: 网络接口API
 */
interface ApiService {

    companion object {
        const val SERVER_URL = "https://wx-dev.dslyy.com/"
    }

    /**
     * 根据分类id获取作品数据
     */
    @POST("api/v1/art/work/page")
    suspend fun getWorksDataByType(
        @Body workPageRequestParam: WorkPageRequestParam
    ): ApiResponse<ArrayList<WorksBean>>

    /**
     * 获取作品详情
     */
    @POST("api/v1/art/work/detail")
    suspend fun getWorkDetail(
        @Query("id") id: Int
    ): ApiResponse<WorksBean>

    /**
     * 过滤条件所有分类
     */
    @GET("api/v1/art/classify/prop/all")
    suspend fun getConditionAllClassify(): ApiResponse<ArrayList<ConditionClassifyBean>>

    /**
     * 过滤条件一级分类
     */
    @GET("api/v1/art/classify/prop/root")
    suspend fun getConditionRootClassify(): ApiResponse<ArrayList<ConditionClassifyBean>>

    /**
     * 过滤条件指定分类的子类
     */
    @GET("/api/v1/art/classify/prop/sub")
    suspend fun getConditionSubClassify(): ApiResponse<ArrayList<ConditionClassifyBean>>

    @GET("hotkey/json")
    suspend fun getSearchHotKey(): ApiResponse<ArrayList<SearchResponse>>


}