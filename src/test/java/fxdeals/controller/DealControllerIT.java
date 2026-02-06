package fxdeals.controller;

import fxdeals.entity.Deal;
import fxdeals.repository.DealRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test") // utilise application-test.properties
@Transactional // rollback après chaque test
public class DealControllerIT {

    @Autowired
    private DealRepository dealRepository;

    @Test
    void testSaveDeal() {
        Deal deal = new Deal();
        deal.setDealId("D100");
        deal.setFromCurrency("USD");
        deal.setToCurrency("EUR");
        deal.setAmount(BigDecimal.valueOf(100));

        dealRepository.save(deal);

        Deal saved = dealRepository.findById(deal.getId()).orElse(null);
        assertThat(saved).isNotNull();
        assertThat(saved.getDealId()).isEqualTo("D100");
    }
}
