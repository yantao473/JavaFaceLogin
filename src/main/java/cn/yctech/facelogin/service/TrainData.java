package cn.yctech.facelogin.service;

import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;

import java.io.File;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_GRAYSCALE;
import static org.bytedeco.opencv.global.opencv_imgproc.resize;
import static org.opencv.core.CvType.CV_32SC1;

/**
 * @author willzhao
 * @version 1.0
 * @description 训练
 * @date 2021/12/12 18:26
 */
public class TrainData {
    private static final String BASE_PATH = "E:\\data\\yb\\";
    public static final String TRAIN_XML_PATH = BASE_PATH + "faceRecognizer.xml";

    /**
     * 从指定目录下
     *
     * @param dataMap
     */
    public static void train(Map<Integer, String> dataMap) {
        Map<String, Object> resultMap = getImgAndLabels(dataMap);
        MatVector imgMatMap = (MatVector) resultMap.getOrDefault("images", null);
        Mat lableMat = (Mat) resultMap.getOrDefault("labels", null);

        // 实例化人脸识别类
        LBPHFaceRecognizer faceRecognizer = LBPHFaceRecognizer.create(Constants.LBPH_RADIUS, Constants.LBPH_NEIGHBORS, Constants.LBPH_GRID_X, Constants.LBPH_GRID_Y, Constants.LBPH_THRESHOLD);
        // 训练，入参就是图片集合和分类集合
        faceRecognizer.train(imgMatMap, lableMat);
        // 训练完成后，模型保存在指定位置
        faceRecognizer.save(TRAIN_XML_PATH);
        //释放资源
        faceRecognizer.close();
    }

    /**
     * 更新某个标签的训练数据集
     *
     * @param dataMap 标签名
     */
    public static void update(Map<Integer, String> dataMap) {
        Map<String, Object> resultMap = getImgAndLabels(dataMap);
        MatVector imgMatMap = (MatVector) resultMap.getOrDefault("images", null);
        Mat lableMat = (Mat) resultMap.getOrDefault("labels", null);

        LBPHFaceRecognizer faceRecognizer = LBPHFaceRecognizer.create(Constants.LBPH_RADIUS, Constants.LBPH_NEIGHBORS, Constants.LBPH_GRID_X, Constants.LBPH_GRID_Y, Constants.LBPH_THRESHOLD);
        faceRecognizer.update(imgMatMap, lableMat);
        // 训练完成后，模型保存在指定位置
        faceRecognizer.save(TRAIN_XML_PATH);
        //释放资源
        faceRecognizer.close();
    }

    /**
     * 把指定路径下所有文件的绝对路径放入list集合中返回
     *
     * @param path
     * @return
     */
    public static List<String> getAllFilePath(String path) {
        List<String> paths = new LinkedList<>();
        File file = new File(path);
        if (file.exists()) {
            // 列出该目录下的所有文件
            File[] files = file.listFiles();

            for (File f : files) {
                if (!f.isDirectory()) {
                    // 把每个文件的绝对路径都放在list中
                    paths.add(f.getAbsolutePath());
                }
            }
        }

        return paths;
    }

    /**
     * 读取指定图片的灰度图，调整为指定大小
     *
     * @param path
     * @return
     */
    private static Mat read(String path) {
        Mat faceMat = opencv_imgcodecs.imread(path, IMREAD_GRAYSCALE);
        resize(faceMat, faceMat, new Size(Constants.RESIZE_WIDTH, Constants.RESIZE_HEIGHT));
        return faceMat;
    }

    /**
     * 获取图片和标签
     *
     * @param dataMap
     * @return
     */
    private static Map<String, Object> getImgAndLabels(Map<Integer, String> dataMap) {
        // k为label序号， v为label名称
        int[] totalImageNums = {0};
        Map<Integer, List<String>> fileMap = new HashMap<>();
        dataMap.forEach((k, v) -> {
            String imgDir = BASE_PATH + v;
            List<String> files = getAllFilePath(imgDir);
            totalImageNums[0] += files.size();
            fileMap.put(k, files);
        });

        System.out.println("total images : " + totalImageNums[0]);

        // 这里用来保存每一张照片的序号，和照片的Mat对象
        MatVector imgMatMap = new MatVector(totalImageNums[0]);

        // labels
        Mat lableMat = new Mat(totalImageNums[0], 1, CV_32SC1);

        // 这里用来保存每一张照片的序号，和照片的类别
        IntBuffer lablesBuf = lableMat.createBuffer();

        int[] imageIndex = {0};
        fileMap.forEach((kindIndex, fileList) -> {
            // 处理一个目录下的每张照片，它们的序号不同，类别相同
            for (String fileName : fileList) {
                // imageIndexMatMap放的是照片的序号和Mat对象
                imgMatMap.put(imageIndex[0], read(fileName));
                // bablesBuf放的是照片序号和类别
                lablesBuf.put(imageIndex[0], kindIndex);
                imageIndex[0]++;
            }
        });

        Map<String, Object> resultMap = new HashMap<>(2);
        resultMap.put("images", imgMatMap);
        resultMap.put("labels", lableMat);

        return resultMap;
    }

    public static void main(String[] args) {
        Map<Integer, String> dataMap = new HashMap<>();
        dataMap.put(0, "xuehua");
        dataMap.put(1, "yuhao");
        dataMap.put(2, "yutong");
        dataMap.put(3, "yanqing");

        // 开始训练，并指定模型输出位置
        train(dataMap);

        // 更新数据集
        Map<Integer, String> updateMap = new HashMap<>();
        updateMap.put(3, "yanqing");
//        update(updateMap);
    }
}
