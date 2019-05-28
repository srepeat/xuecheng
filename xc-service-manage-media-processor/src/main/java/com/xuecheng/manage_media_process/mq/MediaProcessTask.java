package com.xuecheng.manage_media_process.mq;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.MediaFileProcess_m3u8;
import com.xuecheng.framework.utils.HlsVideoUtil;
import com.xuecheng.framework.utils.Mp4VideoUtil;
import com.xuecheng.manage_media_process.dao.MediaFileRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author 鲜磊 on 2019/5/27
 **/
@Component
public class MediaProcessTask {

    //注入上传文件路径
    @Value("${xc-service-manage-media.video-location}")
    String serverPath;

    //注入文件ffmpeg解析器
    @Value("${xc-service-manage-media.ffmpeg-path}")
    String ffmpeg_path;

    @Autowired
    private MediaFileRepository mediaFileRepository;

    //使用多并发处理视频
    @RabbitListener(queues = "${xc-service-manage-media.mq.queue-media-video-processor}",containerFactory = "customContainerFactory")
    public void receiveMediaProcessTask(String msg){

        //1、解析消息内容，获取mediaId
        Map msgMap = JSON.parseObject(msg, Map.class);
        //获取这个mediaId信息
        String mediaId = (String) msgMap.get("mediaId");

        //2、拿mediaId数据库查询内容
        Optional<MediaFile> optional = mediaFileRepository.findById(mediaId);
        //判断数据是否
        if(!optional.isPresent()){
            return;
        }

        //获取全部数据
        MediaFile mediaFile = optional.get();
        //煤资文件类型
        String fileType = mediaFile.getFileType();
        //判断是否为avi后缀的文件
        if(!fileType.equals("avi") || fileType == null){
            mediaFile.setProcessStatus("303004");//处理状态为无需处理
            mediaFileRepository.save(mediaFile);
            return ;
        }else {
            mediaFile.setProcessStatus("303001");//处理状态为未处理
            mediaFileRepository.save(mediaFile);
        }
        //3、使用工具类将Avi转换为Mp4
        //文件路径
        String video_path = serverPath + mediaFile.getFilePath() + mediaFile.getFileName();
        //文件类型
        String mp4_name = mediaFile.getFileId() + ".mp4";
        //文件存储路径
        String mp4folder_path = serverPath + mediaFile.getFilePath();
        //使用工具类进行组合
        Mp4VideoUtil mp4VideoUtil = new Mp4VideoUtil(ffmpeg_path,video_path,mp4_name,mp4folder_path);
        //获取结果集
        String result = mp4VideoUtil.generateMp4();
        //判断文件
        if(result == null || !result.equals("success")){
            //操作失败写入处理日志
            mediaFile.setProcessStatus("303003");//处理状态为处理失败
            MediaFileProcess_m3u8 mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
            //错误结果
            mediaFileProcess_m3u8.setErrormsg(result);
            mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
            //保存到数据库中
            mediaFileRepository.save(mediaFile);
            return ;
        }

        //4、将Mp4生成m3u8和ts文件
        //Mpa4文件路径
        String map4_video_path = serverPath + mediaFile.getFilePath() + mp4_name;
        //m3u8文件生成路径
        String m3u8_name = mediaFile.getFileId() + ".m3u8";
        //m3u8文件所在目录
        String m3u8Folder_path = serverPath + mediaFile.getFilePath() + "hls/";
        HlsVideoUtil hlsVideoUtil = new HlsVideoUtil(ffmpeg_path,map4_video_path,m3u8_name,m3u8Folder_path);
        //获取结果集
        String tsResult = hlsVideoUtil.generateM3u8();
        //判断结果集
        if(tsResult == null || !result.equals("success")){
            //操作失败写入处理日志
            mediaFile.setProcessStatus("303003");//处理状态为处理失败
            MediaFileProcess_m3u8 mediaFileProcess_m3u8 = new MediaFileProcess_m3u8();
            //错误结果
            mediaFileProcess_m3u8.setErrormsg(result);
            mediaFile.setMediaFileProcess_m3u8(mediaFileProcess_m3u8);
            //保存到数据库中
            mediaFileRepository.save(mediaFile);
            return ;
        }

        //获取M3U8文件
        List<String> ts_list = hlsVideoUtil.get_ts_list();

        //更新处理状态为成功
        mediaFile.setProcessStatus("303002");
        MediaFileProcess_m3u8 process_m3u8 = new MediaFileProcess_m3u8();
        process_m3u8.setTslist(ts_list);
        mediaFile.setMediaFileProcess_m3u8(process_m3u8);

        //m3u8文件路径
        String m3u8_path = mediaFile.getFilePath() + "hls/" + m3u8_name;
        mediaFile.setFileUrl(m3u8_path);
        mediaFileRepository.save(mediaFile);
    }
}
