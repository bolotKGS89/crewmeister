package com.crewmeister.cmcodingchallenge.service;

import com.crewmeister.cmcodingchallenge.entity.CurrencyDto;
import com.crewmeister.cmcodingchallenge.entity.RateResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface CurrencyService {
    List<CurrencyDto> getCurrencies();
    RateResponseDto getExchangeRates(String base);
    RateResponseDto getEurFxRateOnDate(LocalDate date, String base);
    RateResponseDto getEurFxRateOnDateConvertedToEur(LocalDate date, String from, String to, Double amount);
}
