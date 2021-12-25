package com.example.ourproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

class MyPagerAdapter( ) : PagerAdapter() {


    private var inflater: LayoutInflater? = null
    private var layouts: IntArray? = null
    private var context: Context? = null

    constructor(layouts: IntArray?, context: Context?) : this() {
        this.layouts = layouts
        this.context = context
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int {
        return layouts?.size!!
    }


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflater!!.inflate(layouts?.get(position)!!, container, false)
        container.addView(v)
        return v
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val v = `object` as View
        container.removeView(v)
    }

}