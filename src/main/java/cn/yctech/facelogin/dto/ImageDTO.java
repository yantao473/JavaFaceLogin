package cn.yctech.facelogin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "图片Base64数据")
public class ImageDTO {
    private static final long serialVersionUID = 1L;
    private String imgBase64;
}