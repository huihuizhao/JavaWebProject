package com.shuangyulin.domain;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

public class Book10 {
    /*�ֻ�����*/
	@NotEmpty(message="�ֻ����벻��Ϊ��")  
    private String phoneNumber;
    public String getphoneNumber() {
        return phoneNumber;
    }
    public void setphoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

 
    
    /*����*/
    @NotEmpty(message="������������")  
    private String date;
    public String getdate() {
        return date;
    }
    public void setdate(String date) {
        this.date = date;
    }

    
    /*��Ƭ*/
    @NotNull(message="����������Ƭ·��")  
    private String imagePath;
    public String getimagePath() {
        return imagePath;
    }
    public void setimagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    
    /*��Ƶ*/
    @NotEmpty(message="��Ƶ·��") 
    private String voicePath;
    public String getvoicePath() {
        return voicePath;
    }
    public void setvoicePath(String voicePath) {
        this.voicePath = voicePath;
    }

    /*��Ƶ*/
    private String videoPath;
    public String getvideoPath() {
        return videoPath;
    }
    public void setvideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

}