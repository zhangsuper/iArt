package com.gsq.iart.app.network

import com.gsq.iart.data.bean.*
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
        const val SERVER_URL = "http://api-test.hxysg.com/"
        const val agreement_url = "${SERVER_URL}art/agreement/legal/index.html"
        const val privacy_url = "${SERVER_URL}art/agreement/legal/privacy.html"
    }

    /**
     * 首页分类列表
     */
    @POST("art/classify/list")
    suspend fun getClassifyList(): ApiResponse<ArrayList<HomeClassifyBean>>

    /**
     * 根据分类id获取作品数据
     */
    @POST("art/work/page")
    suspend fun getWorksDataByType(
        @Body workPageRequestParam: WorkPageRequestParam
    ): ApiResponse<ArrayList<WorksBean>>

    /**
     * 获取作品详情
     */
    @POST("art/work/detail")
    suspend fun getWorkDetail(
        @Query("id") id: Int
    ): ApiResponse<WorksBean>

    /**
     * 过滤条件所有分类
     */
    @GET("art/classify/prop/all")
    suspend fun getConditionAllClassify(): ApiResponse<ArrayList<ConditionClassifyBean>>

    /**
     * 过滤条件一级分类
     */
    @GET("art/classify/prop/root")
    suspend fun getConditionRootClassify(): ApiResponse<ArrayList<ConditionClassifyBean>>

    /**
     * 过滤条件指定分类的子类
     */
    @GET("art/classify/prop/all")
    suspend fun getConditionSubClassify(): ApiResponse<ArrayList<ConditionClassifyBean>>

//    @GET("hotkey/json")
    @GET("/api/v1/art/hotkey/json")
    suspend fun getSearchHotKey(): ApiResponse<ArrayList<SearchResponse>>


}