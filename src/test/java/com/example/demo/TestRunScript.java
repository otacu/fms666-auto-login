package com.example.demo;

import com.example.demo.util.BinaryImageUtil;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;

import java.io.File;
import java.net.URL;

public class TestRunScript {
    static final String ORIGIN_CODE_FILE_PATH = "E:\\originCode.png";

    static final String TRANSFERED_CODE_FILE_PATH = "E:\\transferedCode.png";

    public static void main(String[] args) throws Exception {
        // 图片二值化
        BinaryImageUtil.binaryImage(ORIGIN_CODE_FILE_PATH, TRANSFERED_CODE_FILE_PATH);
        // 图片转字符串
        String code = transferImageToString(TRANSFERED_CODE_FILE_PATH);

        System.out.println(code);
    }

    /**
     * 图片转字符串
     *
     * @param transferedFilePath 经过二值化的图片地址
     * @return 字符串
     */
    public static String transferImageToString(String transferedFilePath) {
        String code = null;
        File transferedFile = new File(transferedFilePath);
        ITesseract instance = new Tesseract();//调用Tesseract
        URL url = ClassLoader.getSystemResource("tessdata");//获得Tesseract的文字库
        String tesspath = url.getPath().substring(1);
        instance.setDatapath(tesspath);//进行读取，默认是英文，如果要使用中文包，加上instance.setLanguage("chi_sim");
        instance.setLanguage("num");
        try {
            code = instance.doOCR(transferedFile);
            System.out.println("识别结果：" + code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }
}
