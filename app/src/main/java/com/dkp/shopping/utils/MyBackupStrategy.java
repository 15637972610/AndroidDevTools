package com.dkp.shopping.utils;

import com.elvishew.xlog.printer.file.backup.FileSizeBackupStrategy;


/**
 * Created by Administrator on 2018/12/7.
 */

public class MyBackupStrategy extends FileSizeBackupStrategy {


    /**
     * Constructor.
     *
     * @param maxSize the max size the file can reach
     */
    public MyBackupStrategy(long maxSize) {
        super(maxSize);
    }
}
