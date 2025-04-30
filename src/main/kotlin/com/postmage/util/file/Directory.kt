package com.postmage.util.file

import com.postmage.util.extensions.desktopHomeFolder
import com.postmage.util.extensions.makeFolder
import java.io.File

object Directory {
    var userDesktopDir: File? = null
    var usersFile = "users"
    var generalPhotoPath = "/postmage_photos"
    var tempFiles = "/temp_files"


    /**
     * Eğer sistemde bu klasörler yoksa oluşturuyor.
     */
    fun initializeDirectory() {
        userDesktopDir = makeFolder(desktopHomeFolder() + generalPhotoPath)
        makeFolder(userDesktopDir!!.path + tempFiles)
    }
}