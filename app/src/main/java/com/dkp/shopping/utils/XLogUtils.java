package com.dkp.shopping.utils;

import android.os.Environment;
import com.elvishew.xlog.Logger;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.flattener.ClassicFlattener;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator;

import java.io.File;

import static com.dkp.shopping.utils.FileUtil.LOG_DIR_LOGIN;
import static com.dkp.shopping.utils.FileUtil.LOG_DIR_GENERAL;
import static com.dkp.shopping.utils.FileUtil.LOG_DIR_XLOG;
import static com.dkp.shopping.utils.FileUtil.LOG_DIR_CRASH;

/**
 * Created by dkp on 2018/8/23.
 * XLog局部使用工具类
 *
 */

public class XLogUtils {

    public static int mMaxSize =1024*1024*10;//单个日志文件最大为10M


    /**
     *
     * @param tag 不同类型的标记，方便区分是哪部分的日志
     * @return Logger
     */

    public static Logger createLoggerWithTag(String tag){
        String path = new File(LOG_DIR_LOGIN).getPath();
        FilePrinter filePrinter = new FilePrinter.Builder(path ).
                fileNameGenerator(new DateFileNameGenerator()).
                logFlattener(new ClassicFlattener()).
                backupStrategy(new MyBackupStrategy(mMaxSize)).build();
        Logger mLog = XLog.printers(filePrinter).tag(tag).build();

        return mLog;
    }

}
