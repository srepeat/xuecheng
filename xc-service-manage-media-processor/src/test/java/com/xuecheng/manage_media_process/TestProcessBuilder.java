package com.xuecheng.manage_media_process;

import com.xuecheng.framework.utils.HlsVideoUtil;
import com.xuecheng.framework.utils.Mp4VideoUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 * @create 2018-07-12 9:11
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestProcessBuilder {

    @Test
    public void testProcessBuilder() throws IOException {

        //创建ProcessBuilder对象
        ProcessBuilder processBuilder =new ProcessBuilder();
        //设置执行的第三方程序(命令)
        //processBuilder.command("ping","127.0.0.1");
        //将标准输入流和错误输入流合并，通过标准输入流读取信息就可以拿到第三方程序输出的错误信息、正常信息
        processBuilder.command("ipconfig");
        //启动一个进程
        Process process = processBuilder.start();
        //将标准输入流和错误输入流合并，通过标准输入流读取信息
        processBuilder.redirectErrorStream(true);
        //由于前边将错误和正常信息合并在输入流，只读取输入流
        InputStream inputStream = process.getInputStream();
        //将字节流转成字符流
        InputStreamReader reader = new InputStreamReader(inputStream,"gbk");
       //字符缓冲区
        char[] chars = new char[1024];

        //循环读取
        int len = -1;
        while((len=reader.read(chars))!=-1){
            String string = new String(chars,0,len);
            System.out.println(string);
        }

        inputStream.close();
        reader.close();
    }

    //测试视频转换
    @Test
    public void testProcessBuilderVoide() throws IOException{
        //构建调用第三方工具类
        ProcessBuilder processBuilder = new ProcessBuilder();
        //传入一个集合类的方式
        List<String> command = new ArrayList<>();
        command.add("D:\\developer_xc\\ffmpeg-20180227-fa0c9d6-win64-static\\bin\\ffmpeg.exe");
        command.add("-i");
        command.add("D:\\ffmpage_video\\Java.avi");
        command.add("-y");//覆盖输出文件
        command.add("-c:v");
        command.add("libx264");
        command.add("-s");
        command.add("1280x720");
        command.add("-pix_fmt");
        command.add("yuv420p");
        command.add("-b:a");
        command.add("63k");
        command.add("-b:v");
        command.add("753k");
        command.add("-r");
        command.add("18");
        command.add("D:\\ffmpage_video\\Java.mp4");
        processBuilder.command(command);
        //将标准输入流和错误输入流合并，通过标准输入流读取信息
        processBuilder.redirectErrorStream(true);
        //启动一个进程
        Process process = processBuilder.start();

        //读取错误流和标准输入流合并为一
        InputStream inputStream = process.getInputStream();
        //使用字符流包装一下
        InputStreamReader reader = new InputStreamReader(inputStream,"gbk");
        //定义字符数组
        char [] chars = new char[1024];
        //循环读取
        int len = -1;
        StringBuffer stringBuffer = new StringBuffer();
        while ((len=reader.read(chars))!=-1){
            String string = new String(chars,0,len);
            stringBuffer.append(string);
            System.out.println(string);
        }

        inputStream.close();
        reader.close();
    }





    //测试使用工具类将avi转成mp4
    @Test
    public void testProcessMp4(){
        //String ffmpeg_path, String video_path, String mp4_name, String mp4folder_path
        //ffmpeg的路径
        String ffmpeg = "D:\\developer_xc\\ffmpeg-20180227-fa0c9d6-win64-static\\bin\\ffmpeg.exe";
        //video_path视频地址
        String video_path = "D:\\ffmpage_video\\Java.avi";
        //mp4_name mp4文件名称
        String map4 = "Java1.mp4";
        //mp4folder_path mp4文件目录路径
        String mp4folder_path = "D:\\ffmpage_video\\";
        //开始编码,如果成功返回success，否则返回输出的日志
        Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpeg,video_path,map4,mp4folder_path);
        String generateMp4 = videoUtil.generateMp4();
        System.out.println(generateMp4);
    }

    @Test
    public void testProcessHLS(){
        //String ffmpeg_path, String video_path, String mp4_name, String mp4folder_path
        //ffmpeg的路径
        String ffmpeg = "D:\\developer_xc\\ffmpeg-20180227-fa0c9d6-win64-static\\bin\\ffmpeg.exe";
        //video_path视频地址
        String video_path = "D:\\ffmpage_video\\Java.avi";
        //mp4_name mp4文件名称
        String map4 = "1.m3u8";
        //mp4folder_path mp4文件目录路径
        String mp4folder_path = "D:\\ffmpage_video\\chunk\\";
        //开始编码,如果成功返回success，否则返回输出的日志
        HlsVideoUtil hlsVideoUtil = new HlsVideoUtil(ffmpeg,video_path,map4,mp4folder_path);
        String generateM3u8 = hlsVideoUtil.generateM3u8();
        System.out.println(generateM3u8);
        System.out.println(hlsVideoUtil.get_ts_list());

    }

}
