package com.gsq.iart.app.network

import com.gsq.iart.data.bean.*
import com.gsq.iart.data.request.MemberPayRequestParam
import com.gsq.iart.data.request.WorkPageRequestParam
import retrofit2.http.*

/**
 * 描述　: 网络接口API
 */
interface ApiService {

    companion object {
        const val SERVER_URL = "https://api-test.hxysg.com/"
        const val agreement_url = "${SERVER_URL}art/agreement/legal/index.html"
        const val privacy_url = "${SERVER_URL}art/agreement/legal/privacy.html"
        const val personal_url = "${SERVER_URL}art/agreement/legal/personal.html"
        const val vip_agreement_url = "${SERVER_URL}art/agreement/legal/ght-agreement.html"
        const val personal_info_url = "${SERVER_URL}art/agreement/legal/info.html"
        const val sdk_info_url = "${SERVER_URL}art/agreement/legal/share.html"
        const val write_off_remind_url = "${SERVER_URL}art/agreement/legal/remind.html"
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
        @Query("id") id: String
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
    @POST("art/hotkey/json")
    suspend fun getSearchHotKey(): ApiResponse<ArrayList<SearchResponse>>


    /**
     * 微信登录
     */
    @POST("art/user/login/weixin")
    suspend fun loginByWechat(
        @Query("code") code: String
    ): ApiResponse<UserInfo>

    /**
     * 获取用户信息
     */
    @POST("art/user/info")
    suspend fun getUserInfo(): ApiResponse<UserInfo>

    /**
     * 推出登录
     */
    @POST("art/user/logout")
    suspend fun logout(): ApiResponse<Any>

    /**
     * 注销
     */
    @POST("art/user/removeUser")
    suspend fun writeOff(): ApiResponse<Any>

    /**
     * 收藏作品
     */
    @POST("art/collect/add/{id}")
    suspend fun collectAddWork(@Path("id") id: String): ApiResponse<String>

    /**
     * 取消收藏
     */
    @POST("art/collect/remove/{id}")
    suspend fun collectRemoveWork(@Path("id") id: String): ApiResponse<String>

    /**
     * 收藏列表
     */
    @POST("art/collect/page")
    suspend fun collectWorks(
        @Body workPageRequestParam: WorkPageRequestParam
    ): ApiResponse<ArrayList<WorksBean>>

    /**
     *支付套餐列表
     */
    @POST("art/member/pay/config/listPayTerms")
    suspend fun getPayConfig(): ApiResponse<ArrayList<PayConfigBean>>

    /**
     *创建预支付订单
     */
    @POST("art/member/wx/preparePay")
    suspend fun createPreparePay(@Body memberPayRequestParam: MemberPayRequestParam): ApiResponse<PayOrderBean>

    /**
     * 支付结果回调
     */
    @POST("art/member/wx/payNotice")
    suspend fun getPayResult(): ApiResponse<String>

    /**
     * 搜索热词
     */
    @POST("art/search/helper/hotSearch")
    suspend fun getHotSearch(): ApiResponse<ArrayList<String>>

    /**
     * 下载次数加1
     */
    @POST("art/work/count/download/inc")
    suspend fun downloadInc(@Query("id") id: String): ApiResponse<String>

    /**
     * 检查版本
     */
    @POST("art/app/queryAppUpdate")
    suspend fun checkAppVersion(@Query("appVersion") version: String): ApiResponse<AppVersion>

}