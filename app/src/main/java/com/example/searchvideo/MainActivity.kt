package com.example.searchvideo

import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil.setContentView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.searchvideo.Base.BaseActivity_ko
import com.example.searchvideo.ViewModel.MainViewModel
import com.example.searchvideo.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.android.ext.android.inject
import java.io.ByteArrayOutputStream
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



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
