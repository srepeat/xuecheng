package com.xuecheng.manage_media;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author 鲜磊 on 2019/5/25
 * 文件分块合并测试
 **/
public class FileTest {


    @Test
    public  void testChunk() throws IOException {
        //源文件
        File file_Source = new File("D:\\ffmpage_video\\lucene.avi");
        //分块文件
        String chunkFile = "D:\\ffmpage_video\\chunks\\";
        File chunkPath = new File(chunkFile);

        //判断是否有文件夹
        if(!chunkPath.exists()){
            chunkPath.mkdirs();
        }
        //分块大小
        long chunkSize = 1*1024*1024; //1M

        //分块数量，向上取整
        long chunkNum = (long) Math.ceil(file_Source.length()*1.0/chunkSize);
        if(chunkNum <= 0){
            chunkNum = 1;
        }

        //建立缓冲区
        byte[] bytes = new byte[1024];
        //使用RandomAccessFile访问文件、只读
        RandomAccessFile raf_read = new RandomAccessFile(file_Source,"r");
        //分块
        for(int i=0;i<chunkNum;i++){
            //创建分区文件,把文件拆分开
            File file = new File(chunkFile+i);
            // 判断文件是否存在，不存在就创建
            boolean newFile = file.createNewFile();
            if(newFile){
                //向分块文件写数据、可读可写
                RandomAccessFile raf_write = new RandomAccessFile(file,"rw");
                int len = -1;
                //开始读取分块
                while((len=raf_read.read(bytes))!=-1){
                    raf_write.write(bytes,0,len);
                    //读取文件的长度大于给定的大小就跳出继续读取
                    if(file.length()>chunkSize){
                        break;
                    }
                }
                raf_write.close();
            }
        }
        raf_read.close();
    }


    //文件合并
    @Test
    public void testMerge() throws IOException {
        //分块文件
        String chunkFile = "D:\\ffmpage_video\\chunks\\";
        File chunkPath = new File(chunkFile);

        //分块列表
        File[] files = chunkPath.listFiles();

        //排序
        List<File> fileList = Arrays.asList(files);
        //排序、按照姓名排序  升序排序-1 降序1
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                //比较name的大小的、进行排序
                if(Integer.parseInt(o1.getName())>Integer.parseInt(o2.getName())){
                    return 1;
                }
                return -1;
            }
        });

        //合并目录
        File mergeFile = new File("D:\\ffmpage_video\\lucene_merge.avi");
        if(mergeFile.exists()){
            mergeFile.delete();
        }
        //创建新的合并文件
        mergeFile.createNewFile();

        //读写文件
        RandomAccessFile raf_write = new RandomAccessFile(mergeFile,"rw");
        //文件顶端
        raf_write.seek(0);
        //缓冲区
        byte [] bytes = new byte[1024];
        //遍历文件列表
        for(File file : fileList){
            //读取文件
            RandomAccessFile raf_read = new RandomAccessFile(file,"r");
            //下标
            int len = -1;
            //循环读取
            while ((len=raf_read.read(bytes))!=-1){
                //写入
                raf_write.write(bytes,0,len);
            }
            //关闭读取资源
            raf_read.close();
        }
        //关闭写资源
        raf_write.close();
    }

}
