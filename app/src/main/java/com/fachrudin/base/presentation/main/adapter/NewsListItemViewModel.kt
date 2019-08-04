package com.fachrudin.base.presentation.main.adapter

import androidx.databinding.ObservableField
import com.fachrudin.framework.core.BaseViewModel

/**
 * @author achmad.fachrudin
 * @date 21-May-19
 */
class NewsListItemViewModel : BaseViewModel() {
    var bTextTitle = ObservableField<String>()
    var bTextTime = ObservableField<String>()
    var bTextContent = ObservableField<String>()
}