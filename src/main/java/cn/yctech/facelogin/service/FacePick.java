package cn.yctech.facelogin.service;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;

import static org.bytedeco.javacpp.opencv_objdetect.CASCADE_DO_CANNY_PRUNING;
import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;
import static org.opencv.imgproc.Imgproc.resize;


public class FacePick {
    // OpenCv人脸识别分类器
    private static CascadeClassifier cascadeClassifier;

    static {
        // 只要是用opencv 就得用
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        cascadeClassifier = new CascadeClassifier("D:\\opencv\\sources\\data\\haarcascades\\haarcascade_frontalface_alt.xml");
    }


    /**
     * 人脸裁剪
     * @param imagePath
     */
    public static void faceCrop(String imagePath) {
        File imgFile = new File(imagePath);
        if (imgFile.exists()) {
            Mat srcImg = Imgcodecs.imread(imagePath);
            // 目标灰色图像
            Mat dstGrayImg = new Mat();
            // 转换灰色
            Imgproc.cvtColor(srcImg, dstGrayImg, Imgproc.COLOR_BGR2GRAY);

            // 用来存放人脸矩形
            MatOfRect faceRect = new MatOfRect();
            // 特征检测点的最小尺寸
            Size minSize = new Size(60, 60);
            // 图像缩放比例,可以理解为相机的X倍镜
            double scaleFactor = 1.2;
            // 对特征检测点周边多少有效检测点同时检测,这样可以避免选取的特征检测点大小而导致遗漏
            int minNeighbors = 3;
            // 执行人脸检测
            cascadeClassifier.detectMultiScale(dstGrayImg, faceRect, scaleFactor, minNeighbors, CASCADE_DO_CANNY_PRUNING, minSize);

            // 待保存图片的mat
            Mat faceMat;

            //遍历矩形,画到原图上面
            // 定义绘制颜色
//            Scalar color = new Scalar(0, 0, 255);
            for (Rect rect : faceRect.toArray()) {
//                int x = rect.x;
//                int y = rect.y;
//                int w = rect.width;
//                int h = rect.height;
                // 单独框出每一张人脸
//                Imgproc.rectangle(srcImg, new Point(x, y), new Point(x + w, y + h), color, 2);

                faceMat = new Mat(srcImg, rect);
                Size size = new Size(Constants.RESIZE_WIDTH, Constants.RESIZE_HEIGHT);
                resize(faceMat, faceMat, size);
                imwrite(imagePath, faceMat);
            }
        }
    }


    /**
     * 根据传入的MAT构造相同尺寸的MAT，存放灰度图片用于以后的检测
     *
     * @param src 原始图片的MAT对象
     * @return 相同尺寸的灰度图片的MAT对象
     */
    public static Mat buildGrayImage(Mat src) {
        return new Mat(src.rows(), src.cols(), CV_8UC1);
    }

    public static void showImage(Mat mat) {
        HighGui.imshow("image", mat);
        // 阻塞进程
        HighGui.waitKey();
        HighGui.destroyAllWindows();
    }


    public static void main(String[] args) {
        String filepath = "E:\\data\\yb\\xuehua\\a.jpg";
        faceCrop(filepath);
    }
}
