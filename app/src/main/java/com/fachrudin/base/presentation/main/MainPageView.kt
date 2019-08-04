package com.fachrudin.base.presentation.main

import com.fachrudin.framework.core.BaseView
import com.fachrudin.framework.widget.LoadingView

/**
 * @author achmad.fachrudin
 * @date 21-May-19
 */
interface MainPageView : BaseView {
    var retryListener: LoadingView.OnRetryListener
}