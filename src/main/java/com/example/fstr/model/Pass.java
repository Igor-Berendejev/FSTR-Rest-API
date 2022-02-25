package com.example.fstr.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pereval_added")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Pass {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "date_added")
    private LocalDateTime date_added;
    @Type(type = "jsonb")
    @Column(name = "raw_data", columnDefinition = "json")
    private String raw_data;
    @Type(type = "jsonb")
    @Column(name = "images", columnDefinition = "json")
    private String images;
    @Column(name = "status")
    private String status;

    public Pass(LocalDateTime date_added, String raw_data, String images, String status) {
        this.date_added = date_added;
        this.raw_data = raw_data;
        this.images = images;
        this.status = status;
    }

    public Pass() {

    }

    @Override
    public String toString() {
        return "Pass{" +
                "id=" + id +
                ", date_added=" + date_added +
                ", raw_data=" + raw_data +
                ", images=" + images +
                ", status=" + status +
                '}';
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

    public String getRaw_data() {
        return raw_data;
    }

    public void setRaw_data(String raw_data) {
        this.raw_data = raw_data;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
