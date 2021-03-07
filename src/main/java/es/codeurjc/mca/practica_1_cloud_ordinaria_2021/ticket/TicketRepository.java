package es.codeurjc.mca.practica_1_cloud_ordinaria_2021.ticket;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
}
