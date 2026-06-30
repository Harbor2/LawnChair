package com.example

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.wyz.emlibrary.em.EMLibrary

class NgInitializer: ContentProvider() {
    override fun onCreate(): Boolean {
        val app = context?.applicationContext as? Application
        app?.let {
            NegativeContext.init(it)
            EMLibrary.init(it)
        }
        return true
    }

    override fun delete(p0: Uri, p1: String?, p2: Array<out String?>?): Int {
        return 0
    }

    override fun getType(p0: Uri): String? {
        return null
    }

    override fun insert(p0: Uri, p1: ContentValues?): Uri? {
        return null
    }

    override fun query(
        p0: Uri,
        p1: Array<out String?>?,
        p2: String?,
        p3: Array<out String?>?,
        p4: String?,
    ): Cursor? {
        return null
    }

    override fun update(
        p0: Uri,
        p1: ContentValues?,
        p2: String?,
        p3: Array<out String?>?,
    ): Int {
        return 0
    }
}
