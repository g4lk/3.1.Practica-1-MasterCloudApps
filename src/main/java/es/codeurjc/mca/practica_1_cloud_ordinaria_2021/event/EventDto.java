package es.codeurjc.mca.practica_1_cloud_ordinaria_2021.event;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter 
public class EventDto{

    private MultipartFile multiparImage;

    private String name;

    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Date date;

    private Double price;

    private int max_capacity;
    
}
