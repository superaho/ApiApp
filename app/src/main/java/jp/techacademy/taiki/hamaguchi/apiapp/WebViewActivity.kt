package jp.techacademy.taiki.hamaguchi.apiapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_web_view.*

class WebViewActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        val stateShop = intent.extras?.getString(KEY_SHOP)
        // Gsonで String → Object
        val state = Gson().fromJson<Shop>(stateShop, Shop::class.java)
        val WebViewUrl = if (state.couponUrls.sp.isNotEmpty()) state.couponUrls.sp else state.couponUrls.pc
        webView.loadUrl(WebViewUrl)


        val isFavorite = FavoriteShop.findBy(state.id) != null
        fab.apply {
            setImageResource(if (isFavorite) R.drawable.ic_star else R.drawable.ic_star_border)
        }
        fab.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                //TODO("Not yet implemented")
                val FavoriteState = FavoriteShop.findBy(state.id) != null
                if (FavoriteState) {
                    AlertDialog.Builder(this@WebViewActivity)
                        .setTitle(R.string.delete_favorite_dialog_title)
                        .setMessage(R.string.delete_favorite_dialog_message)
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            FavoriteShop.delete(state.id)
                            fab.apply {
                                setImageResource(R.drawable.ic_star_border)
                            }
                        }
                        .setNegativeButton(android.R.string.cancel) { _, _ ->}
                        .create()
                        .show()
                } else {
                    FavoriteShop.insert(FavoriteShop().apply {
                        id = state.id
                        name = state.name
                        imageUrl = state.logoImage
                        url = if (state.couponUrls.sp.isNotEmpty()) state.couponUrls.sp else state.couponUrls.pc
                    })
                    fab.apply {
                        setImageResource(R.drawable.ic_star)
                    }
                    //(viewPagerAdapter.fragments[MainActivity.VIEW_PAGER_POSITION_FAVORITE] as FavoriteFragment).updateData()
                }
            }

        })
    }

    companion object {
        private const val KEY_SHOP = "key_shop"
        fun start(activity: Activity, shop: Shop) {
            val intent = Intent(activity, WebViewActivity::class.java)
            intent.putExtra(KEY_SHOP, Gson().toJson(shop))
            activity.startActivity(intent)
        }
    }
}

