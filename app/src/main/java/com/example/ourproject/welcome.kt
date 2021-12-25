package com.example.ourproject

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_welcome.*

class welcome : AppCompatActivity() {
    private var layoutDot: LinearLayout?= null
    private  var viewPager: ViewPager?=null
    private lateinit var dots: Array<TextView?>
    private var layouts: IntArray? = null
    private var pagerAdapter: MyPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        if (!isFirstTimeStartApp()) {
            startLoginActivity()
            finish()
        }


        setStatusBarTransparent()

        setContentView(R.layout.activity_welcome)
        viewPager = findViewById(R.id.view_pager)
        layoutDot = findViewById(R.id.dotLayout)


        layouts = intArrayOf(R.layout.slider1, R.layout.slider2, R.layout.slider3)
        pagerAdapter = MyPagerAdapter(layouts, applicationContext)
        viewPager!!.adapter = pagerAdapter
        viewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }
            override fun onPageSelected(position: Int) {
                if (position == layouts!!.size - 1) {
                    //LAST PAGE
                    btn_next.text = "Let's GO "
                    btn_skip.visibility = View.GONE
                } else {
                    btn_next.text = "NEXT"
                    btn_skip.visibility = View.VISIBLE
                }
                setDotStatus(position)
            }

        })
        setDotStatus(0)
        btn_skip.setOnClickListener {
            startLoginActivity()
        }
        btn_next.setOnClickListener {
            val currentPage = viewPager!!.currentItem + 1
            if (currentPage < layouts!!.size) {
                //move to next page
                viewPager!!.currentItem = currentPage
            } else {
                startLoginActivity()
            }
        }
    }


    private fun setDotStatus(page: Int) {

        dots = arrayOfNulls<TextView>(layouts!!.size)
        layoutDot!!.removeAllViews()
        for (i in dots.indices) {
            dots[i] = TextView(this)
            dots[i]!!.text = Html.fromHtml("&#8226;")
            dots[i]!!.textSize = 35f
            dots[i]!!.setTextColor(Color.parseColor("#ed756c"))
            layoutDot!!.addView( dots[i])
        }
        //Set current dot active
        if (dots.isNotEmpty()) {
            dots[page]!!.setTextColor(Color.parseColor("#7B52AB"))
        }
    }



    private fun isFirstTimeStartApp(): Boolean {
        val ref = applicationContext.getSharedPreferences("IntroSliderApp", Context.MODE_PRIVATE)
        return ref.getBoolean("FirstTimeStartFlag", true)
    }

    private fun setFirstTimeStartStatus(stt: Boolean) {
        val ref = applicationContext.getSharedPreferences("IntroSliderApp", Context.MODE_PRIVATE)
        val editor = ref.edit()
        editor.putBoolean("FirstTimeStartFlag", stt)
        editor.commit()
    }



    private fun startLoginActivity() {
        setFirstTimeStartStatus(false)
        startActivity(Intent(this, Choice::class.java))
        finish()
    }

    private fun setStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= 21) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
    }


}
