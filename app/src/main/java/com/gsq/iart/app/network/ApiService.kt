package com.gsq.iart.app.network

import com.gsq.iart.data.bean.*
import com.gsq.iart.data.request.CompareAddItemsRequestParam
import com.gsq.iart.data.request.CompareAddRequestParam
import com.gsq.iart.data.request.DictionaryWoksRequestParam
import com.gsq.iart.data.request.MemberPayRequestParam
import com.gsq.iart.data.request.WorkPageRequestParam
import retrofit2.http.*

/**
 * 描述　: 网络接口API
 */
interface ApiService {

    companion object {
        const val SERVER_URL = "https://api.hxysg.com/"

        const val H5_SERVER_URL = "https://www.hxysg.com/"
        const val agreement_url = "${H5_SERVER_URL}agreement/legal/index.html"//服务协议
        const val privacy_url = "${H5_SERVER_URL}agreement/legal/privacy.html"//隐私政策
        const val personal_url = "${H5_SERVER_URL}agreement/legal/personal.html"//用户信息保护指引
        const val vip_agreement_url =
            "${H5_SERVER_URL}agreement/legal/ght-agreement.html"//国画通会员服务协议
        const val personal_info_url = "${H5_SERVER_URL}agreement/legal/info.html"//个人信息收集清单
        const val sdk_info_url = "${H5_SERVER_URL}agreement/legal/share.html"//信息共享清单
        const val write_off_remind_url = "${H5_SERVER_URL}agreement/legal/remind.html"//重要提醒
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
    @GET("art/app/queryAppUpdate")
    suspend fun checkAppVersion(@Query("appVersion") version: String): ApiResponse<AppVersion>


    /**
     * 图典菜单列表
     */
    @GET("art/classify/atlas/all")
    suspend fun getDictionaryClassify(): ApiResponse<ArrayList<DictionaryMenuBean>>

    /**
     * 图典一级分类
     */
    @GET("art/classify/atlas/root")
    suspend fun getDictionaryRootClassify(): ApiResponse<ArrayList<DictionaryMenuBean>>

    /**
     * 图典指定分类列表
     */
    @GET("art/classify/atlas/sub")
    suspend fun getDictionaryClassifyById(@Query("pid") id: Int): ApiResponse<ArrayList<DictionaryMenuBean>>

    /**
     * 图典列表作品数据
     */
    @POST("art/work/atlas/page")
    suspend fun getDictionaryWorks(
        @Body requestParam: DictionaryWoksRequestParam
    ): ApiResponse<ArrayList<DictionaryWorksBean>>


    /**
     * 图单列表
     */
    @GET("art/work/atlas/compare/findPage")
    suspend fun findComparePage(
        @Query("pageNum") pageNum: Int, @Query("pageSize") pageSize: Int
    ): ApiResponse<ArrayList<DictionarySetsBean>>


    /**
     * 图单作品列表
     */
    @GET("art/work/atlas/compare/findItemPage")
    suspend fun findCompareItemPage(
        @Query("id") id: Long, @Query("pageNum") pageNum: Int, @Query("pageSize") pageSize: Int
    ): ApiResponse<ArrayList<DictionaryWorksBean>>

    /**
     * 添加图单
     */
    @POST("art/work/atlas/compare/add")
    suspend fun addCompare(
        @Body requestParam: CompareAddRequestParam
    ): ApiResponse<Any>

    @POST("art/work/atlas/compare/addItems")
    suspend fun addCompareItems(
        @Body requestParam: CompareAddItemsRequestParam
    ): ApiResponse<Any>

    /**
     * 图单重命名
     */
    @PUT("art/work/atlas/compare/rename")
    suspend fun compareRename(
        @Query("id") id: Long,
        @Query("name") name: String
    ): ApiResponse<Any>

    /**
     * 批量删除
     */
    @HTTP(method = "DELETE", path = "art/work/atlas/compare/deleteItems", hasBody = true)
    suspend fun deleteCompareItems(
        @Body requestParam: CompareAddItemsRequestParam
    ): ApiResponse<Any>

    @DELETE("art/work/atlas/compare/delete")
    suspend fun deleteCompare(
        @Query("id") id: Long
    ): ApiResponse<Any>

}