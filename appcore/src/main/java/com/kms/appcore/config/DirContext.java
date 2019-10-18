package com.kms.appcore.config;

import android.content.Context;

import com.kms.appcore.fs.DirectoryManager;
import com.kms.appcore.fs.DirectroyContext;


public class DirContext extends SysContext {

    DirectoryManager mDirectoryManager = null;

    public DirContext(Context context) {
        super(context);
    }

    public void init(DirectroyContext dir) {
        if (dir == null) {
            dir = new DirectroyContext(getApplicationContext());
        }
        DirectoryManager dm = new DirectoryManager(dir);
        boolean ret = dm.buildAndClean();
        if (!ret) {
            return;
        }
        mDirectoryManager = dm;
    }

    public void init() {
        init(null);
    }

    public DirectoryManager getDirManager() {
        return mDirectoryManager;
    }

}
