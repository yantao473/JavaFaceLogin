package cn.yctech.facelogin.controller;


import cn.yctech.facelogin.dto.ImageDTO;
import cn.yctech.facelogin.service.Constants;
import cn.yctech.facelogin.service.PredictRlt;
import cn.yctech.facelogin.service.RecognizeService;
import cn.yctech.facelogin.service.TrainData;
import cn.yctech.facelogin.utils.MatBufferedImage;
import cn.yctech.facelogin.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.bytedeco.opencv.opencv_core.Mat;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_GRAYSCALE;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;

@RestController
@RequestMapping("/test")
@Tag(name = "测试")
public class TestController {

    @PostMapping("/compare")
    @Operation(description = "测试")
    public Result compare(@RequestBody ImageDTO imageDTO) throws IOException {
        String imgBase64Str = imageDTO.getImgBase64();
        if (imgBase64Str.indexOf(",") > 0) {
            imgBase64Str = imgBase64Str.split(",")[1];
        }

        // 无中间文件
        long start = System.currentTimeMillis();
        Map<Integer, String> dataMap = new HashMap<>();
        dataMap.put(0, "xuehua");
        dataMap.put(1, "yuhao");
        dataMap.put(2, "yutong");
        dataMap.put(3, "yanqing");

        // 有中间文件
        String imgPath = MatBufferedImage.Base64ToImage(imgBase64Str);
        if (StringUtils.hasLength(imgPath)) {
            System.out.println(imgPath);
            RecognizeService recognizeService = new RecognizeService(TrainData.TRAIN_XML_PATH);
            Mat mat = imread(imgPath, IMREAD_GRAYSCALE);
            PredictRlt predict = recognizeService.predict(mat);
            // for test
            long end = System.currentTimeMillis();
            System.out.println("use Time: " + (end - start));
            System.out.println(predict);

            // 删除临时图片
//            File tmpImage = new File(imgPath);
//            if(tmpImage.exists()){
//                tmpImage.delete();
//            }

            if (Double.doubleToLongBits(predict.getConfidence()) <= Double.doubleToLongBits(Constants.MAX_CONFIDENCE)) {
                String name = dataMap.getOrDefault(predict.getLable(), "");
                return Result.ok().put("result", name);
            } else {
                return Result.ok().put("result", "unknown");
            }
        } else {
            return Result.error("failed");
        }
    }

    @GetMapping("/faceCheck")
    @Operation(description = "人脸识别")
    @Parameters({@Parameter(in = ParameterIn.QUERY, name = "imgPath", description = "图片路径")})
    public Result faceCheck(@RequestParam("imgPath") String imgPath) {
        String recognizeModelFilePath = "E:\\data\\faceRecognizer.xml";
        RecognizeService recognizeService = new RecognizeService(recognizeModelFilePath);
        org.bytedeco.opencv.opencv_core.Mat mat = imread(imgPath, IMREAD_GRAYSCALE);

        PredictRlt predict = recognizeService.predict(mat);
        System.out.println(predict);
        return Result.ok().put("result", predict);
    }

    @PostMapping("/upload")
    @Operation(description = "图片上传")
    @Parameters({@Parameter(in = ParameterIn.QUERY, name = "file", description = "图片文件")})
    public Result upload(@RequestParam("file") MultipartFile imgFile) {
        //System.getProperty("java.io.tmpdir") + File.separator;
        String tempPath = "E:\\data\\temp\\";
        String originalFilename = imgFile.getOriginalFilename();
        String destFile = tempPath + File.separator + originalFilename;
        File dest = new File(destFile);

        try {
            imgFile.transferTo(dest);
            if (dest.exists()) {
                return Result.ok().put("imgPath", destFile);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return Result.ok().put("imgPath", "");
    }
}
