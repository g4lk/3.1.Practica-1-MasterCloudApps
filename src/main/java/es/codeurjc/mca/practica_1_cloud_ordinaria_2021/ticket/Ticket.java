package es.codeurjc.mca.practica_1_cloud_ordinaria_2021.ticket;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonView;

import org.hibernate.annotations.CreationTimestamp;

import es.codeurjc.mca.practica_1_cloud_ordinaria_2021.event.Event;
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
public class Ticket {

    public interface BasicAtt {}

    public interface CustomerAtt{}

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView(BasicAtt.class)
    private Long id;

    @JsonView(BasicAtt.class)
    private Double purchasePrice;

    @CreationTimestamp
    @JsonView(BasicAtt.class)
    private LocalDateTime createDateTime;

    @ManyToOne
    @JsonView(CustomerAtt.class)
    private User customer;

    @ManyToOne
    @JsonView(BasicAtt.class)
    private Event event;

    public Ticket(User customer, Event event) {
        this.customer = customer;
        this.event = event;
        this.purchasePrice = event.getPrice();
    }
 
}
