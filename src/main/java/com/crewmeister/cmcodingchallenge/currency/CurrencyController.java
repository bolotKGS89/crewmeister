package com.crewmeister.cmcodingchallenge.currency;

import com.crewmeister.cmcodingchallenge.entity.CurrencyDto;
import com.crewmeister.cmcodingchallenge.entity.RateResponseDto;
import com.crewmeister.cmcodingchallenge.service.CurrencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

import static com.crewmeister.cmcodingchallenge.constant.Constants.EUR;

@Slf4j
@RestController()
@RequestMapping("/api/v1")
@AllArgsConstructor
@Tag(name = "Currency API", description = "Operations related to currency exchange rates")
public class CurrencyController {

    private final CurrencyService currencyService;

    @Operation(summary = "Get list of all currencies")
    @GetMapping("/currencies")
    public ResponseEntity<List<CurrencyDto>> getCurrencies()  {
        log.info("CurrencyController.getCurrencies invoked");
        return new ResponseEntity<>(currencyService.getCurrencies(), HttpStatus.OK);
    }

    @Operation(summary = "Get exchange rates for a specific base currency")
    @GetMapping("/exchange‑rates")
    public ResponseEntity<RateResponseDto> getExchangeRates(@RequestParam("base") String base) {
        log.info("CurrencyController.getExchangeRates invoked ");
        return new ResponseEntity<>(currencyService.getExchangeRates(base), HttpStatus.OK);
    }

    @Operation(summary = "Get exchange rates for a specific base currency")
    @GetMapping("/exchange‑rates-date")
    public ResponseEntity<RateResponseDto> getExchangeRatesForOneDate(@RequestParam("date")
                                                                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                                      LocalDate date,
                                                                      @RequestParam("base") String base) {
        log.info("CurrencyController.getExchangeRatesForOneDate invoked ");
        return new ResponseEntity<>(currencyService.getEurFxRateOnDate(date, base), HttpStatus.OK);
    }

    @Operation(summary = "Get exchange rates for a specific base currency")
    @GetMapping("/exchange‑rates-date-to-eur")
    public ResponseEntity<RateResponseDto> getEurFxRateOnDateConvertedToEur(@RequestParam("date")
                                                                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                                      LocalDate date,
                                                                      @RequestParam("amount") Double amount,
                                                                      @RequestParam("from")
                                                                                String from) {
        log.info("CurrencyController.getEurFxRateOnDateConvertedToEur invoked");
        return new ResponseEntity<>(currencyService
                .getEurFxRateOnDateConvertedToEur(date, from, EUR, amount), HttpStatus.OK);
    }

}
