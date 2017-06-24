package com.shuangyulin.domain;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

public class Book10 {
    /*手机号码*/
	@NotEmpty(message="手机号码不能为空")  
    private String phoneNumber;
    public String getphoneNumber() {
        return phoneNumber;
    }
    public void setphoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

 
    
    /*日期*/
    @NotEmpty(message="必须输入日期")  
    private String date;
    public String getdate() {
        return date;
    }
    public void setdate(String date) {
        this.date = date;
    }

    
    /*照片*/
    @NotNull(message="必须输入照片路径")  
    private String imagePath;
    public String getimagePath() {
        return imagePath;
    }
    public void setimagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    
    /*音频*/
    @NotEmpty(message="音频路径") 
    private String voicePath;
    public String getvoicePath() {
        return voicePath;
    }
    public void setvoicePath(String voicePath) {
        this.voicePath = voicePath;
    }

    /*视频*/
    private String videoPath;
    public String getvideoPath() {
        return videoPath;
    }
    public void setvideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

}