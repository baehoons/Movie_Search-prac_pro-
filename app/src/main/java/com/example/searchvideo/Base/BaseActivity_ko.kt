package com.example.searchvideo.Base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.getViewModel

abstract class BaseActivity_ko <T : ViewDataBinding, R : BaseViewModel> : AppCompatActivity() {

    lateinit var viewDataBinding: T

    /**
     * setContentView로 호출할 Layout의 리소스 Id.
     * ex) R.layout.activity_sbs_main
     */
    abstract fun getLayoutId(): Int

    /**
     * viewModel 로 쓰일 변수.
     */
    abstract fun getViewModel(): R

    abstract fun getBindingVariable(): Int
    /**
     * 레이아웃을 띄운 직후 호출.
     * 뷰나 액티비티의 속성 등을 초기화.
     * ex) 리사이클러뷰, 툴바, 드로어뷰..
     */
//    abstract fun initStartView()
//
//    /**
//     * 두번째로 호출.
//     * 데이터 바인딩 및 rxjava 설정.
//     * ex) rxjava observe, databinding observe..
//     */
//    abstract fun initDataBinding()
//
//    /**
//     * 바인딩 이후에 할 일을 여기에 구현.
//     * 그 외에 설정할 것이 있으면 이곳에서 설정.
//     * 클릭 리스너도 이곳에서 설정.
//     */
//    abstract fun initAfterBinding()

    abstract fun setUp()

    private var isSetBackButtonValid = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewDataBinding = DataBindingUtil.setContentView(this, getLayoutId())
        viewDataBinding.lifecycleOwner = this
        viewDataBinding.setVariable(getBindingVariable(),getViewModel())
        viewDataBinding.executePendingBindings()
        setUp()
        //snackbarObserving()

    }

    private fun snackbarObserving() {
        getViewModel().observeSnackbarMessage(this) {
            Snackbar.make(findViewById(android.R.id.content), it, Snackbar.LENGTH_LONG).show()
        }
        getViewModel().observeSnackbarMessageStr(this){
            Snackbar.make(findViewById(android.R.id.content), it, Snackbar.LENGTH_LONG).show()
        }
    }


}