package com.easydemo.easybutterknife

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.easydemo.butterknife.ButterKnife
import com.easydemo.butterknife_annotations.BindView
import com.easydemo.butterknife_annotations.OnClick
import com.easydemo.butterknife_runtime.Unbinder

/**
 * @author lujunnan
 * @date 2020/11/20 5:10 PM
 * @decs
 */
class MainFragment : Fragment() {
    companion object {
        private const val TAG = "MainFragment"
    }

    private lateinit var unBinder: Unbinder

    @BindView(R.id.textView)
    @JvmField
    var textView: TextView? = null

    @OnClick(R.id.textView, R.id.textView2)
    fun onClick() {
//        val toast = Toast.makeText(this.activity, "", Toast.LENGTH_SHORT)
//        toast.setText("@OnClick")
//        toast.show()

        startActivity(Intent(activity, MainExtendActivity::class.java))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val contentView = layoutInflater.inflate(R.layout.fragment_main, container, false)
        unBinder = ButterKnife.bind(this, contentView)
        Log.d(TAG, "onCreateView: " + (textView == null))
        return contentView
    }

    override fun onDestroyView() {
        unBinder.unbind()
        super.onDestroyView()
    }
}