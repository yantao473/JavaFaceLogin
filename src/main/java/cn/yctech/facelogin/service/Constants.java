package cn.yctech.facelogin.service;

public interface Constants {
    /**
     * 调整后的文件宽度
     */
    int RESIZE_WIDTH = 164;
    /**
     * 调整后的文件高度
     */
    int RESIZE_HEIGHT = 164;

    /**
     * 超过这个置信度就明显有问题了
     */
//    double MAX_CONFIDENCE = 50d;
    double MAX_CONFIDENCE = 66d;

    /**
     * radius - The radius used for building the Circular Local Binary Pattern. The greater the radius, the smoother the image but more spatial information you can get.
     * neighbors - The number of sample points to build a Circular Local Binary Pattern from. An appropriate value is to use 8 sample points. Keep in mind: the more sample points you include, the higher the computational cost.
     * grid_x - The number of cells in the horizontal direction, 8 is a common value used in publications. The more cells, the finer the grid, the higher the dimensionality of the resulting feature vector.
     * grid_y - The number of cells in the vertical direction, 8 is a common value used in publications. The more cells, the finer the grid, the higher the dimensionality of the resulting feature vector.
     * threshold - The threshold applied in the prediction. If the distance to the nearest neighbor is larger than the threshold, this method returns -1. ### Notes:
     * The Circular Local Binary Patterns (used in training and prediction) expect the data given as grayscale images, use cvtColor to convert between the color spaces.
     * This model supports updating.
     */
    int LBPH_RADIUS = 1;
    int LBPH_NEIGHBORS = 8;
    int LBPH_GRID_X = 8;
    int LBPH_GRID_Y = 8;
    double LBPH_THRESHOLD = 50d;

    /**
     * 卷积神经网络推理使用的图片宽度
     */
    int CNN_PREIDICT_IMG_WIDTH = 256;

    /**
     * 卷积神经网络推理使用的图片高度
     */
    int CNN_PREIDICT_IMG_HEIGHT = 256;


    /**
     * 卷积神经网络推理使用的图片宽度
     */
    int CAFFE_MASK_MODEL_IMG_WIDTH = 160;

    /**
     * 卷积神经网络推理使用的图片高度
     */
    int CAFFE_MASK_MODEL_IMG_HEIGHT = 160;

    String IMG_TYPE = "jpg";
}
