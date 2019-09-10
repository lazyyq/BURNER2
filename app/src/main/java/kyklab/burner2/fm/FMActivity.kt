package kyklab.burner2.fm

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomappbar.BottomAppBar
import kyklab.burner2.R
import kyklab.burner2.picture.PictureManager
import java.io.File
import java.lang.ref.WeakReference
import java.util.*

class FMActivity : AppCompatActivity(), FMAdapterCallback {
    private var mCurrentPath: String? = null
    private var mFileList: MutableList<File>? = null
    private var mToolbarText: TextView? = null
    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    private var mProgressBar: ProgressBar? = null
    private var mAdapter: FMAdapter? = null
    private var mRefreshTask: RefreshTask? = null
    private var mCurrentDepth: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fm)
        val mBottomAppBar = findViewById<BottomAppBar>(R.id.fmBottomAppBar)
        setSupportActionBar(mBottomAppBar)

        val mActionBar = supportActionBar
        if (mActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        initFM()
        refreshFileList()
    }

    override fun onPause() {
        super.onPause()

        mRefreshTask!!.cancel(true)
    }

    override fun onBackPressed() {
        if (mCurrentDepth == 0) {
            super.onBackPressed()
        } else {
            gotoUpperDirectory()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_fm, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.fm_action_home -> {
                gotoHomeDirectory()
                return true
            }
            R.id.fm_action_gallery -> {
                launchGallery()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun initFM() {
        mToolbarText = findViewById(R.id.fmToolbarText)
        val mRecyclerView = findViewById<RecyclerView>(R.id.fmRecyclerView)
        mProgressBar = findViewById(R.id.fmProgressBar)
        mFileList = ArrayList()
        val layoutManager = LinearLayoutManager(this)
        mRecyclerView.layoutManager = layoutManager
        mAdapter = FMAdapter(this, mFileList)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.addItemDecoration(DividerItemDecoration(mRecyclerView.context,
                layoutManager.orientation))
        mSwipeRefreshLayout = findViewById(R.id.fmSwipeRefreshLayout)
        mSwipeRefreshLayout!!.setOnRefreshListener {
            refreshFileList()
            mSwipeRefreshLayout!!.isRefreshing = false
        }

        gotoHomeDirectory()
    }

    private fun refreshFileList() {
        mRefreshTask = RefreshTask(this)
        mRefreshTask!!.execute()
    }

    override fun gotoUpperDirectory() {
        val path: String
        if (mCurrentDepth == 0) {
            // Root
            return
        } else if (mCurrentDepth == 1) {
            // Right under root
            path = "/"
        } else {
            path = mCurrentPath!!.substring(0, mCurrentPath!!.lastIndexOf(File.separator))
        }
        switchToDirectory(path)
        --mCurrentDepth
    }

    override fun enterDirectory(dir: String) {
        val path: String
        if (mCurrentDepth == 0) {
            path = mCurrentPath!! + dir
        } else {
            path = mCurrentPath + File.separator + dir
        }
        switchToDirectory(path)
        ++mCurrentDepth
    }

    private fun gotoHomeDirectory() {
        val path: String
        path = Environment.getExternalStorageDirectory().absolutePath
        mCurrentDepth = path.length - path.replace("/", "").length
        switchToDirectory(path)
    }

    private fun switchToDirectory(path: String) {
        mToolbarText!!.text = path
        mCurrentPath = path
        refreshFileList()
    }

    override fun selectAsCustomPicture(obj: Any?) {
        PictureManager.instance.setAsCustomPicture(obj)
        finish()
    }

    private fun launchGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        startActivityForResult(intent, REQ_CODE_ACTIVITY_DOCUMENTS_UI)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQ_CODE_ACTIVITY_DOCUMENTS_UI -> if (resultCode == Activity.RESULT_OK && data != null) {
                val uri = data.data
                selectAsCustomPicture(uri)
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    internal class RefreshTask(activity: FMActivity) : AsyncTask<Void, Void, Void>() {
        private val activityWeakReference: WeakReference<FMActivity>

        init {
            this.activityWeakReference = WeakReference(activity)
        }

        override fun onPreExecute() {
            val activity = activityWeakReference.get()
            if (activity == null || activity.isFinishing) {
                return
            }

            activity.mProgressBar!!.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg voids: Void): Void? {
            val activity = activityWeakReference.get()
            if (activity == null || activity.isFinishing) {
                return null
            }

            val tempFileList = ArrayList<File>()
            val tempDirList = ArrayList<File>()

            val current = File(activity.mCurrentPath!!)
            val files = current.list()
            if (files != null) {
                var path: String
                var file: File

                Arrays.sort(files)
                for (name in files) {
                    path = activity.mCurrentPath + File.separator + name
                    file = File(path)
                    if (file.isDirectory) {
                        tempDirList.add(file)
                    } else if (name.toLowerCase().endsWith(".jpg")
                            || name.toLowerCase().endsWith(".jpeg")
                            || name.toLowerCase().endsWith(".png")
                            || name.toLowerCase().endsWith(".gif")
                            || name.toLowerCase().endsWith(".webp")) {
                        tempFileList.add(file)
                    }
                }
            }

            activity.mFileList!!.clear()
            activity.mFileList!!.addAll(tempDirList)
            activity.mFileList!!.addAll(tempFileList)

            return null
        }

        override fun onPostExecute(aVoid: Void) {
            val activity = activityWeakReference.get()
            if (activity == null || activity.isFinishing) {
                return
            }

            activity.mAdapter!!.notifyDataSetChanged()
            activity.mProgressBar!!.visibility = View.GONE
        }
    }

    companion object {
        private val REQ_CODE_ACTIVITY_DOCUMENTS_UI = 100
    }
}
