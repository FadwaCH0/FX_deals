package fxdeals.controller;

import fxdeals.entity.Deal;
import fxdeals.service.DealService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/deals")

public class DealController {

    private final DealService dealService;

    public DealController(DealService dealService) {
        this.dealService = dealService;
    }

    @PostMapping
    public ResponseEntity<String> createDeal(@Valid @RequestBody Deal deal) {
        String result = dealService.saveDeal(deal);

        if ("Deal already exists".equals(result)) {  // <-- correspond au texte du service
            return ResponseEntity.status(409).body(result);
        }

        return ResponseEntity.status(201).body(result);
    }
}
