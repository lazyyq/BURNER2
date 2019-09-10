package kyklab.burner2.selectpicture

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.android.material.floatingactionbutton.FloatingActionButton

import kyklab.burner2.R
import kyklab.burner2.fm.FMActivity
import kyklab.burner2.picture.PictureManager
import kyklab.burner2.utils.PrefManager

class SelectPictureActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    private var mAdapter: PicturePreviewListAdapter? = null
    private var mNeedsRefresh = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_picture)

        setupRecyclerView()
        updateThumbnails()

        val mFab = findViewById<FloatingActionButton>(R.id.fab_search)
        mFab.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                            this@SelectPictureActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                launchFM()
            } else {
                ActivityCompat.requestPermissions(this@SelectPictureActivity,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        REQ_CODE_PERM)
            }
        }

        PrefManager.registerPrefChangeListener(this)
    }

    override fun onResume() {
        super.onResume()

        if (mNeedsRefresh) {
            updateThumbnails()
            mNeedsRefresh = false
        }
    }

    private fun setupRecyclerView() {
        mAdapter = PicturePreviewListAdapter(this,
                PictureManager.instance.pictureList)
        val mRecyclerView = findViewById<RecyclerView>(R.id.picturesListView)
        mRecyclerView.adapter = mAdapter
        val layoutManager = GridLayoutManager(
                this, PICTURE_LIST_SPAN_COUNT)
        mRecyclerView.layoutManager = layoutManager
        val margin = resources.getDimensionPixelSize(R.dimen.picture_preview_list_margin)
        val decoration = GridLayoutItemDecoration(margin, margin, margin, margin * 4)
        mRecyclerView.addItemDecoration(decoration)
    }

    private fun updateThumbnails() {
        PictureManager.instance.updatePictureList()
        mAdapter!!.notifyDataSetChanged()
    }

    private fun launchFM() {
        val intent = Intent(this@SelectPictureActivity, FMActivity::class.java)
        startActivityForResult(intent, REQ_CODE_ACTIVITY_FM)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            REQ_CODE_PERM -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchFM()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, s: String) {
        when (s) {
            PrefManager.Key.SELECTED_PICTURE_INDEX -> mNeedsRefresh = true
        }
    }

    internal inner class GridLayoutItemDecoration(private val leftMargin: Int, private val topMargin: Int, private val rightMargin: Int, private val bottomMargin: Int) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect, view: View,
                                    parent: RecyclerView, state: RecyclerView.State) {
            outRect.left = leftMargin
            outRect.right = rightMargin
            outRect.bottom = bottomMargin
            outRect.top = topMargin
        }
    }

    companion object {
        private val TAG = "SelectPictureActivity"
        private val PICTURE_LIST_SPAN_COUNT = 2
        private val REQ_CODE_PERM = 100
        private val REQ_CODE_ACTIVITY_FM = 100
    }
}
