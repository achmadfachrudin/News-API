package com.fachrudin.framework.core

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.ViewModel

/**
 * @author achmad.fachrudin
 * @date 3-Mar-19
 */
abstract class BaseViewModel : ViewModel() {
    var initialState: MutableLiveData<NetworkState> = MutableLiveData()
    var networkState: MutableLiveData<NetworkState> = MutableLiveData()

    fun getInitialState(): LiveData<NetworkState> {
        return initialState
    }

    fun getNetworkState(): LiveData<NetworkState> {
        return networkState
    }
}