package com.example.echo;

public class AlbumIndivdual {
    String album;
    String image;
    String itemId;
    String where;
    String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    AlbumIndivdual(){}

    public AlbumIndivdual(String album, String image, String itemId, String where, String imageUrl) {
        this.album = album;
        this.image = image;
        this.itemId = itemId;
        this.where = where;
        this.imageUrl = imageUrl;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getImage() {
        return image;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
}
