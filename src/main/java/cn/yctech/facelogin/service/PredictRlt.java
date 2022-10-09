package cn.yctech.facelogin.service;

import lombok.Data;

/**
 * 推理结果
 */
@Data
public class PredictRlt {
    private int lable;
    private double confidence;
}
