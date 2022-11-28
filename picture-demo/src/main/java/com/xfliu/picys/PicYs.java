package com.xfliu.picys;

import net.coobird.thumbnailator.Thumbnails;

import java.io.File;
import java.io.IOException;

/**
 * 图片压缩
 */

public class PicYs {


    private void imgScale(String source, String destPath) {


        File file = new File(destPath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            Thumbnails.of(source)
                    //
                    .scale(0.3).outputQuality(1)
                    .toFile(destPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
