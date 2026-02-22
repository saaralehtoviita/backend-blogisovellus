package backend25.blogisovellus.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="IMAGES")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="img_id")
    public Long imgId;

    @Column(name="file_name")
    public String fileName;

    @Column(name="content_type")
    public String contentType;

    @Lob //large object
    @Column(name="image_data") 
    public byte[] data;

    @ManyToOne
    @JoinColumn(name = "post_id")
    public Post post;

    public Image() {

    }

    public Image(String fileName, String contentType, byte[] data, Post post) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.data = data;
        this.post = post;
    }

    public Long getImgId() {
        return imgId;
    }

    public void setImgId(Long imgId) {
        this.imgId = imgId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    



    

}
