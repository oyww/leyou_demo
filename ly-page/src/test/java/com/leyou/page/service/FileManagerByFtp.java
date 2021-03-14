package com.leyou.page.service;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileOutputStream;

/**
 * JAVA FTP上传下载 助手类
 *
 * commons-net-1.4.1.jar PFTClinet jar包
 *
 * @author : JenMinZhang
 */
public class FileManagerByFtp {

    /**
     * FTP上传单个文件测试
     */
    public static void fileUploadByFtp() {
        FTPClient ftpClient = new FTPClient();
        FileInputStream fis = null;

        try {
            ftpClient.connect("192.85.1.128");
            ftpClient.login("root", "root");

            File srcFile = new File("D:/Users/OYWW/IdeaProjects/leyou/ly-page/src/main/resources/upload/141.html");
            fis = new FileInputStream(srcFile);
            // 设置上传目录
            ftpClient.changeWorkingDirectory("/home/leyou/testUpload");
            ftpClient.setBufferSize(1024);
            ftpClient.setControlEncoding("GBK");
            // 设置文件类型（二进制）
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.storeFile("141.html", fis);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("FTP客户端出错！", e);
        } finally {
            IOUtils.closeQuietly(fis);
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("关闭FTP连接发生异常！", e);
            }
        }
    }

    /**
     * FTP下载单个文件测试
     */
    public static void fileDownloadByFtp() {
        FTPClient ftpClient = new FTPClient();
        FileOutputStream fos = null;

        try {
            ftpClient.connect("192.85.1.9");
            ftpClient.login("zhangzhenmin", "62672000");

            String remoteFileName = "/home/zhangzhenmin/test_back_081901.sql";
            // fos = new FileOutputStream("E:/test/test_back_081901.sql");
            fos = new FileOutputStream("H:/test/test_back_081901.sql");
            ftpClient.setBufferSize(1024);
            // 设置文件类型（二进制）
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.retrieveFile(remoteFileName, fos);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("FTP客户端出错！", e);
        } finally {
            IOUtils.closeQuietly(fos);
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("关闭FTP连接发生异常！", e);
            }
        }
    }

    public static void main(String[] args) {
        fileUploadByFtp();
//        fileDownloadByFtp();
    }
}