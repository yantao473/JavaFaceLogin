package cn.yctech.facelogin.utils;

import cn.yctech.facelogin.service.FacePick;
import org.apache.commons.codec.binary.Base64;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.opencv.core.CvType;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class MatBufferedImage {
    /**
     * Mat转换成BufferedImage
     *
     * @param matrix        要转换的Mat
     * @param fileExtension 格式为 ".jpg", ".png", etc
     * @return
     */
    public static BufferedImage Mat2BufImg(org.opencv.core.Mat matrix, String fileExtension) {
        // convert the matrix into a matrix of bytes appropriate for
        // this file extension
        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(fileExtension, matrix, mob);
        // convert the "matrix of bytes" into a byte array
        byte[] byteArray = mob.toArray();
        BufferedImage bufImage = null;
        try {
            InputStream in = new ByteArrayInputStream(byteArray);
            bufImage = ImageIO.read(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bufImage;
    }

    /**
     * BufferedImage转换成Mat
     *
     * @param original 要转换的BufferedImage
     * @param imgType  bufferedImage的类型 如 BufferedImage.TYPE_3BYTE_BGR(RGB)
     * @param matType  转换成mat的type 如 CvType.CV_8UC3
     */
    public static org.opencv.core.Mat BufImg2Mat(BufferedImage original, int imgType, int matType) {
        if (original == null) {
            throw new IllegalArgumentException("original == null");
        }

        // Don't convert if it already has correct type
        if (original.getType() != imgType) {

            // Create a buffered image
            BufferedImage image = new BufferedImage(original.getWidth(), original.getHeight(), imgType);

            // Draw the image onto the new buffer
            Graphics2D g = image.createGraphics();
            try {
                g.setComposite(AlphaComposite.Src);
                g.drawImage(original, 0, 0, null);
            } finally {
                g.dispose();
            }
        }
        DataBufferByte dbi = (DataBufferByte) original.getRaster().getDataBuffer();
        byte[] pixels = dbi.getData();
        org.opencv.core.Mat mat = org.opencv.core.Mat.eye(original.getHeight(), original.getWidth(), matType);
        mat.put(0, 0, pixels);
        return mat;
    }

    public static BufferedImage Base64ToBufferedImage(String base64Str) {
        BufferedImage bufferedImage = null;
        if (base64Str == null) {
            return null;
        }

        byte[] imageBytes = Base64.decodeBase64(base64Str);
        try (ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);) {
            bufferedImage = ImageIO.read(bis);
        } catch (Exception e) {
        }

        return bufferedImage;
    }

    public static org.opencv.core.Mat Base64ToMat(String base64Str) throws IOException {
        BufferedImage bufferedImage = Base64ToBufferedImage(base64Str);
//        File tmpImg = File.createTempFile("tmp", ".jpg");
//        System.out.println(tmpImg);
//        try {
//            ImageIO.write(bufferedImage, "jpg", tmpImg);
//        }catch (Exception e){
//            System.out.println(e.getMessage());
//        }
        org.opencv.core.Mat mat = BufImg2Mat(bufferedImage, bufferedImage.getType(), CvType.CV_8UC3);
        return mat;
    }


    public static Mat bufferedImageToMat(BufferedImage bufferedImage) {
        OpenCVFrameConverter.ToMat cv = new OpenCVFrameConverter.ToMat();
        Mat mat = cv.convertToMat(new Java2DFrameConverter().convert(bufferedImage));
        // 转换为灰度的mat
        opencv_imgproc.cvtColor(mat, mat, opencv_imgproc.COLOR_BGR2GRAY);
        return mat;
    }

    public static String Base64ToImage(String base64Str) {
        BufferedImage bufferedImage = Base64ToBufferedImage(base64Str);

        try {
            File tmpImg = File.createTempFile("tmp_face_", ".jpg");
            ImageIO.write(bufferedImage, "jpg", tmpImg);

            if (tmpImg.exists()) {
                String imgPath = tmpImg.getAbsolutePath();
                FacePick.faceCrop(imgPath);
                return imgPath;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }
}
