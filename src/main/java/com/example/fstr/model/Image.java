package com.example.fstr.model;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mount_pass_images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;
    @Column(name = "date_added")
    private LocalDateTime date_added;
    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "img")
    private byte[] image;

    public Image(LocalDateTime date_added, byte[] image) {
        this.date_added = date_added;
        this.image = image;
    }

    public Image() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDate_added() {
        return date_added;
    }

    public void setDate_added(LocalDateTime date_added) {
        this.date_added = date_added;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
