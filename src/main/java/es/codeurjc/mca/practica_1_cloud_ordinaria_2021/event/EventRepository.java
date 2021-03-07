package es.codeurjc.mca.practica_1_cloud_ordinaria_2021.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE Event e SET e.current_capacity = e.current_capacity + 1 WHERE e.id = :eventId AND e.current_capacity + 1  <= e.max_capacity")
    public int incrementCurrentCapacity(@Param("eventId") long eventId);
    
}
