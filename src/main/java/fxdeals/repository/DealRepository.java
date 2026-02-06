package fxdeals.repository;

import fxdeals.entity.Deal;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DealRepository extends JpaRepository<Deal, Long> {
    boolean existsByDealId(String dealId);
    Optional<Deal> findByDealId(String dealId);

}
