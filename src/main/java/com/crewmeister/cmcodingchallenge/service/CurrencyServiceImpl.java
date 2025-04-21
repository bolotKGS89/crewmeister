package com.crewmeister.cmcodingchallenge.service;

import com.crewmeister.cmcodingchallenge.entity.CurrencyDto;
import com.crewmeister.cmcodingchallenge.entity.RateResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.crewmeister.cmcodingchallenge.constant.Constants.*;

@Slf4j
@Service
@AllArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private final WebClient webClient;

    @Override
    public List<CurrencyDto> getCurrencies() {
        return webClient.get()
                .uri(CURRENCIES)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {})
                .onErrorResume(WebClientRequestException.class, ex -> {
                    log.error("client error {}", ex.getMessage());
                    return Mono.error(ex);
                })
                .map(currencyMap -> currencyMap.entrySet().stream()
                        .map(entry -> CurrencyDto.builder()
                                .code(entry.getKey())
                                .name(entry.getValue())
                                .build())
                        .collect(Collectors.toList())
                ).block();
    }

    @Override
    public RateResponseDto getExchangeRates(String base) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(LATEST)
                        .queryParam(FROM, base)
                        .build())
                .retrieve()
                .bodyToMono(RateResponseDto.class)
                .onErrorResume(WebClientRequestException.class, ex -> {
                    log.error("client error {}", ex.getMessage());
                    return Mono.error(ex);
                })
                .block();
    }

    @Override
    public RateResponseDto getEurFxRateOnDate(LocalDate date, String base) {
        return webClient.get()
                .uri("/{date}?from={base}", date, base)
                .retrieve()
                .bodyToMono(RateResponseDto.class)
                .onErrorResume(WebClientRequestException.class, ex -> {
                    log.error("client error {}", ex.getMessage());
                    return Mono.error(ex);
                })
                .block();
    }

    @Override
    public RateResponseDto getEurFxRateOnDateConvertedToEur(LocalDate date, String from, String to, Double amount) {
        return webClient.get()
                .uri("/{date}?amount={amount}&from={from}&to={to}", date, amount, from, to)
                .retrieve()
                .bodyToMono(RateResponseDto.class)
                .onErrorResume(WebClientRequestException.class, ex -> {
                    log.error("client error {}", ex.getMessage());
                    return Mono.error(ex);
                })
                .block();
    }

}
