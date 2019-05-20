package com.fachrudin.base.presentation.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fachrudin.base.R
import com.fachrudin.base.core.BaseActivity
import com.fachrudin.base.core.EndlessRecyclerViewScrollListener
import com.fachrudin.base.core.NetworkState
import com.fachrudin.base.core.ViewDataBindingOwner
import com.fachrudin.base.core.widget.LoadingView
import com.fachrudin.base.databinding.ActivityMainPageBinding
import com.fachrudin.base.presentation.main.adapter.NewsListItemAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @author achmad.fachrudin
 * @date 21-May-19
 */
class MainPageActivity : BaseActivity(),
    MainPageView,
    ViewDataBindingOwner<ActivityMainPageBinding> {

    override fun getViewLayoutResId(): Int {
        return R.layout.activity_main_page
    }

    companion object {
        fun startThisActivity(context: Context) {
            val intent = Intent(context, MainPageActivity::class.java)
            context.startActivity(intent)
        }
    }

    private lateinit var viewModel: MainPageViewModel
    override lateinit var binding: ActivityMainPageBinding

    private lateinit var listAdapter: NewsListItemAdapter

    override var retryListener: LoadingView.OnRetryListener
        get() = object : LoadingView.OnRetryListener {
            override fun onRetry() {
                viewModel.getNewsFromApi(true)
            }
        }
        set(value) {}

    private var doubleBackPressed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = MainPageViewModel(this)
        viewModel = binding.vm!!

        initUI()

        viewModel.getNewsFromApi(true)
        observeNews()
        observeNetworkState()
    }

    private fun initUI() {
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(false)
            it.setHomeButtonEnabled(false)
        }

        title = getString(R.string.main_title)

        listAdapter = NewsListItemAdapter()
        binding.rvList.adapter = listAdapter
    }

    private fun observeNetworkState() {
        observeData(viewModel.getNetworkState()) { networkState ->
            networkState?.let {
                when (it) {
                    NetworkState.LOADING -> {
                        viewModel.showLoadingView.set(true)
                    }
                    NetworkState.LOADED -> {
                        viewModel.showLoadingView.set(false)
                        viewModel.showProgressBar.set(false)
                    }
                    NetworkState.EMPTY -> {
                        viewModel.showLoadingView.set(true)
                        binding.loadingView.showEmpty(
                            getString(R.string.empty_title),
                            getString(R.string.empty_msg),
                            null,
                            R.drawable.img_alert,
                            false
                        )
                    }
                    else -> it.exception?.let { e ->
                        viewModel.showProgressBar.set(false)
                        viewModel.showLoadingView.set(true)
                        binding.loadingView.showError(
                            e,
                            getString(R.string.error_title),
                            getString(R.string.error_msg)
                        )
                    }
                }
            }
        }
    }

    private fun observeNews() {
        observeData(viewModel.news) { result ->
            result?.articles?.let {
                if (viewModel.firstPage.get()!!) {
                    listAdapter.setData(it)
                    binding.rvList.clearOnScrollListeners()
                    binding.rvList.layoutManager?.let {
                        binding.rvList.addOnScrollListener(
                            createScrollListener()
                        )
                    }
                } else {
                    listAdapter.addData(it)
                }
                viewModel.showProgressBar.set(false)
                viewModel.loadMore.set(false)
            }
        }
    }

    private fun createScrollListener(): EndlessRecyclerViewScrollListener {
        return object : EndlessRecyclerViewScrollListener(binding.rvList.layoutManager as LinearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                if (!viewModel.lastPage.get()!! && !viewModel.loadMore.get()!!) {
                    viewModel.loadMore.set(true)
                    viewModel.showProgressBar.set(true)
                    viewModel.page++
                    viewModel.getNewsFromApi(false)
                }
            }

            override fun onLoadPosition(lastPosition: Int, view: RecyclerView) {
                //ignore
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        val searchItem = menu.findItem(R.id.menu_search)
        val searchView = searchItem.actionView as SearchView
        searchView.maxWidth = Int.MAX_VALUE
        searchView.inputType = InputType.TYPE_CLASS_TEXT
        searchView.queryHint = "Search News"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.page = 1
                viewModel.query = query
                viewModel.getNewsFromApi(true)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        searchView.setIconifiedByDefault(true)

        searchView.setOnCloseListener(object : SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                viewModel.page = 1
                viewModel.query = null
                viewModel.getNewsFromApi(true)
                return false
            }
        })

        return super.onCreateOptionsMenu(menu)
        // Retrieve the SearchView and plug it into SearchManager
    }

    override fun onBackPressed() {
        if (doubleBackPressed) {
            super.onBackPressed()
            return
        }
        this.doubleBackPressed = true
        Toast.makeText(this, getString(R.string.app_msg_close), Toast.LENGTH_SHORT).show()

        GlobalScope.launch(Dispatchers.Main) {
            delay(2000)
            doubleBackPressed = false
        }
    }
}