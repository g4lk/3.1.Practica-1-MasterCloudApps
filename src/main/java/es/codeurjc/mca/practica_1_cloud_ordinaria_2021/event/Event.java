package es.codeurjc.mca.practica_1_cloud_ordinaria_2021.event;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonView;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import es.codeurjc.mca.practica_1_cloud_ordinaria_2021.ticket.Ticket;
import es.codeurjc.mca.practica_1_cloud_ordinaria_2021.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Event {

    public interface BasicAtt {}

    public interface OrganizerAtt extends BasicAtt{}

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(BasicAtt.class)
    private Long id;

    @JsonView(BasicAtt.class)
    private String name;

    @Column(length = 10000)
    @JsonView(BasicAtt.class)
    private String description;

    @JsonView(BasicAtt.class)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @JsonView(BasicAtt.class)
    private Double price;

    @JsonView(BasicAtt.class)
    private int max_capacity;

    @JsonView(BasicAtt.class)
    private int current_capacity;

    @CreationTimestamp
    @JsonView(BasicAtt.class)
    private LocalDateTime createDateTime;

    @UpdateTimestamp
    @JsonView(BasicAtt.class)
    private LocalDateTime updateDateTime;

    @ManyToOne
	@JsonView(OrganizerAtt.class)
    private User creator;
    
    @JsonView(BasicAtt.class)
    private String image;

    //@JsonView(OrganizerAtt.class)
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Ticket> tickets;

    public Event(String name, String description, Date date, Double price, int max_capacity) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.price = price;
        this.max_capacity = max_capacity;
        this.current_capacity = 0;
    }

}
