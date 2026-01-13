package com.fish.fishingplanner.ror.presentation.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Message
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.webkit.CookieManager
import android.webkit.PermissionRequest
import android.webkit.URLUtil
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.Toast
import com.fish.fishingplanner.ror.presentation.app.FishingPlannerApp

class SpinChoiceTimeVi(
    private val chickenContext: Context,
    private val spinChoiceTimeCallback: SpinChoiceTimeCallBack,
    private val chickenWindow: Window
) : WebView(chickenContext) {
    private var fileChooserHandler: ((ValueCallback<Array<Uri>>?) -> Unit)? = null

    fun setFileChooserHandler(handler: (ValueCallback<Array<Uri>>?) -> Unit) {
        this.fileChooserHandler = handler
    }
    init {
        val webSettings = settings
        webSettings.apply {
            setSupportMultipleWindows(true)
            allowFileAccess = true
            allowContentAccess = true
            domStorageEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            userAgentString = WebSettings.getDefaultUserAgent(chickenContext).replace("; wv)", "").replace("Version/4.0 ", "")
            @SuppressLint("SetJavaScriptEnabled")
            javaScriptEnabled = true
            cacheMode = WebSettings.LOAD_NO_CACHE
        }
        isNestedScrollingEnabled = true



        layoutParams = FrameLayout.LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )

        super.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?,
            ): Boolean {
                val link = request?.url?.toString() ?: ""

                return if (request?.isRedirect == true) {
                    view?.loadUrl(request?.url.toString())
                    true
                }
                else if (URLUtil.isNetworkUrl(link)) {
                    false
                } else {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                    try {
                        chickenContext.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(chickenContext, "This application not found", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
//                else if(link.startsWith("intent")){
//                    todoSphereIntentStart(link)
//                    true
//                } else {
//                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
//                    try {
//                        todoSphereContext.startActivity(intent)
//                    } catch (e: Exception) {
//                        Toast.makeText(todoSphereContext, "This application not found", Toast.LENGTH_SHORT).show()
//                    }
//                    true
//                }
            }


            override fun onPageFinished(view: WebView?, url: String?) {
                CookieManager.getInstance().flush()
                spinChoiceTimeCallback.chickenOnFirstPageFinished()
                if (url?.contains("ninecasino") == true) {
                    FishingPlannerApp.Companion.chickenInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                    Log.d(FishingPlannerApp.Companion.CHICKEN_MAIN_TAG, "onPageFinished : ${FishingPlannerApp.Companion.chickenInputMode}")
                    chickenWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                } else {
                   FishingPlannerApp.Companion.chickenInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                    Log.d(FishingPlannerApp.Companion.CHICKEN_MAIN_TAG, "onPageFinished : ${FishingPlannerApp.Companion.chickenInputMode}")
                    chickenWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                }
            }


        })

        super.setWebChromeClient(object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest?) {
                spinChoiceTimeCallback.chickenOnPermissionRequest(request)
            }

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?,
            ): Boolean {
                fileChooserHandler?.invoke(filePathCallback)
                return true
            }
            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message?
            ): Boolean {
                chickenHandleCreateWebWindowRequest(resultMsg)
                return true
            }
        })
    }


    fun chickenFLoad(link: String) {
        super.loadUrl(link)
    }

    private fun chickenHandleCreateWebWindowRequest(resultMsg: Message?) {
        if (resultMsg == null) return
        if (resultMsg.obj != null && resultMsg.obj is WebViewTransport) {
            val transport = resultMsg.obj as WebViewTransport
            val windowWebView = SpinChoiceTimeVi(chickenContext, spinChoiceTimeCallback, chickenWindow)
            transport.webView = windowWebView
            resultMsg.sendToTarget()
            spinChoiceTimeCallback.chickenHandleCreateWebWindowRequest(windowWebView)
        }
    }

//    private fun todoSphereIntentStart(link: String) {
//        var scheme = ""
//        var token = ""
//        val part1 = link.split("#").first()
//        val part2 = link.split("#").last()
//        token = part1.split("?").last()
//        part2.split(";").forEach {
//            if (it.startsWith("scheme")) {
//                scheme = it.split("=").last()
//            }
//        }
//        val finalUriString = "$scheme://receiveetransfer?$token"
//        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(finalUriString))
//        try {
//            todoSphereContext.startActivity(intent)
//        } catch (e: Exception) {
//            Toast.makeText(todoSphereContext, "This application not found", Toast.LENGTH_SHORT).show()
//        }
//    }

}