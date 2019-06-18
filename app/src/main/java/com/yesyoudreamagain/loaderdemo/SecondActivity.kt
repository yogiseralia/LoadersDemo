package com.yesyoudreamagain.loaderdemo

import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {
    private val ID_LOADER: Int = 121
    var isFirstTime = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

//        findViewById<Button>(R.id.apihit_btn).apply {
//            setOnClickListener {
        apiHit()
//            }
//        }
    }

    private val loaderCallback = object : LoaderManager.LoaderCallbacks<ResponseData> {
        override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<ResponseData> {
            println("SecondActivity.onCreateLoader")
            return MyAsynctaskLoader(applicationContext)
        }

        override fun onLoadFinished(p0: Loader<ResponseData>, responseData: ResponseData?) {
            println("SecondActivity.onLoadFinished")
            val title = responseData?.title
            val userId = responseData?.userId
            println("userid = $userId with title = $title")
        }

        override fun onLoaderReset(p0: Loader<ResponseData>) {
            println("SecondActivity.onLoaderReset")
        }
    }

    private fun apiHit() {
        if (!isFirstTime) {
            supportLoaderManager.initLoader(ID_LOADER, null, loaderCallback)
            isFirstTime = true
        } else {
            supportLoaderManager.restartLoader(ID_LOADER, null, loaderCallback)
        }
    }
}
