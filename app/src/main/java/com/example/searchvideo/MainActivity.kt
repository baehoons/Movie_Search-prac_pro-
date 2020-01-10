package com.example.searchvideo

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.searchvideo.Base.BaseActivity_ko
import com.example.searchvideo.ViewModel.MainViewModel
import com.example.searchvideo.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.android.ext.android.inject
import android.util.Log
import com.gmail.ayteneve93.api.kakao.ApiKakaoSearchService
import com.gmail.ayteneve93.api.kakao.ApiKakaoVideoSearchDataModels
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*


class MainActivity :BaseActivity_ko<ActivityMainBinding, MainViewModel>() {

    override val layoutResourceId: Int
        get() = R.layout.activity_main
    override val viewModel: MainViewModel by viewModel()

    private val mainAdapter:MainAdapter by inject()

    override fun initStartView() {

        recycler_view.run {
            adapter = mainAdapter
            layoutManager = StaggeredGridLayoutManager(6,1).apply{
                gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
                orientation = StaggeredGridLayoutManager.VERTICAL
            }
            setHasFixedSize(true)

        }

    }

    override fun initDataBinding() {
        viewModel.videoSearchPersonLiveData.observe(this, Observer {
            it.documents.forEach { document ->
                //val url2:String = document.thumbnail+".jpg"
                val ss:String = document.thumbnail
                mainAdapter.addVideoItem(ss,document.url)
            }
            mainAdapter.notifyDataSetChanged()
        })
    }

    override fun initAfterBinding() {
        main_activity_search_button.setOnClickListener{
            viewModel.getVideoSearch(main_activity_search_text_view.text.toString(),1,80)
        }
    }
}
