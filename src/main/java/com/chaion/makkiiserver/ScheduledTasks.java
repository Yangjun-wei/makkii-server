package com.chaion.makkiiserver;

import com.chaion.makkiiserver.services.exchange.CurrencyService;
import com.chaion.makkiiserver.services.exchange.ExchangePool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class ScheduledTasks {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private ExchangePool pool;

//    @Scheduled(fixedRate = 30 * 60 * 1000)
    public void fetchCurrencyRate() {
        logger.info("fetch currency rate");

        for (String crypto : pool.getCryptoList()) {
            try{
                Map<String,BigDecimal> prices = currencyService.fetchCurrency(crypto, pool.getFiatList());
                for(String currency: prices.keySet()){
                    pool.updatePrice(crypto, currency, prices.get(currency));
                }
            }catch (ServiceException e) {
                logger.error("fetch [" + crypto +  "] failed: " + e.getMessage());
            }
        }
        pool.dump();
    }
}
