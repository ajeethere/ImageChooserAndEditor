package com.example.imagechooser;

public class UploadImg {
    String name;
    String imgUrl;
    public UploadImg(){
//Empty constructor needed
    }

    public UploadImg(String name, String imgUrl) {
        if (name.equals("")){
            name="No name";
        }
        this.name = name;
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
