package fxdeals.service;

import org.springframework.stereotype.Service;
import fxdeals.entity.Deal;
import fxdeals.repository.DealRepository;

@Service
public class DealService {

    private final DealRepository repository;

    public DealService(DealRepository repository) {
        this.repository = repository;
    }

    public String saveDeal(Deal deal) {
        if (repository.existsByDealId(deal.getDealId())) {
            return "Deal already exists";
        }

        repository.save(deal);
        return "Deal saved successfully";
    }
}
