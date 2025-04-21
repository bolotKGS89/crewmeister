package com.crewmeister.cmcodingchallenge;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.crewmeister.cmcodingchallenge.constant.Constants;
import com.crewmeister.cmcodingchallenge.entity.CurrencyDto;
import com.crewmeister.cmcodingchallenge.entity.RateResponseDto;
import com.crewmeister.cmcodingchallenge.service.CurrencyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceImplTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private CurrencyServiceImpl currencyService;

    @BeforeEach
    void setUp() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
    }

    @Test
    void getCurrencies_Success() {
        Map<String, String> testCurrencies = new HashMap<>();
        testCurrencies.put("USD", "United States Dollar");
        testCurrencies.put("EUR", "Euro");

        when(requestHeadersUriSpec.uri(Constants.CURRENCIES)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(Mono.just(testCurrencies));

        List<CurrencyDto> result = currencyService.getCurrencies();

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(dto -> "USD".equals(dto.getCode()) && "United States Dollar".equals(dto.getName())));
        assertTrue(result.stream().anyMatch(dto -> "EUR".equals(dto.getCode()) && "Euro".equals(dto.getName())));
    }

    @Test
    void getCurrencies_WebClientError_ThrowsException() throws URISyntaxException {

        Throwable cause = new Throwable("Connection refused");
        WebClientRequestException ex = new WebClientRequestException(cause,
                HttpMethod.GET, new URI(""), new HttpHeaders());

        when(requestHeadersUriSpec.uri(Constants.CURRENCIES)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.error(ex));

        assertThrows(WebClientRequestException.class, () -> currencyService.getCurrencies());
    }

    @Test
    void getExchangeRates_Success() {
        String base = "EUR";
        RateResponseDto expectedResponse = new RateResponseDto();
        expectedResponse.setBase(base);

        ArgumentCaptor<Function<UriBuilder, URI>> uriFunctionCaptor = ArgumentCaptor.forClass(Function.class);

        when(requestHeadersUriSpec.uri(uriFunctionCaptor.capture())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(RateResponseDto.class)).thenReturn(Mono.just(expectedResponse));

        RateResponseDto result = currencyService.getExchangeRates(base);

        UriBuilder uriBuilder = UriComponentsBuilder.newInstance();
        URI uri = uriFunctionCaptor.getValue().apply(uriBuilder);
        assertEquals("/latest", uri.getPath());
        assertEquals(base, uri.getQuery().split("=")[1]);

        assertEquals(expectedResponse, result);
    }

    @Test
    void getEurFxRateOnDate_Success() {
        LocalDate date = LocalDate.of(2024, 5, 5);
        String base = "USD";
        RateResponseDto expectedResponse = new RateResponseDto();

        when(requestHeadersUriSpec.uri("/{date}?from={base}", date, base))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(RateResponseDto.class)).thenReturn(Mono.just(expectedResponse));

        RateResponseDto result = currencyService.getEurFxRateOnDate(date, base);

        verify(requestHeadersUriSpec).uri("/{date}?from={base}", date, base);

        assertEquals(expectedResponse, result);
    }

    @Test
    void getEurFxRateOnDateConvertedToEur_Success() {

        LocalDate date = LocalDate.of(2024, 5, 5);
        String from = "USD";
        String to = "EUR";
        Double amount = 100.0;
        RateResponseDto expectedResponse = new RateResponseDto();

        when(requestHeadersUriSpec.uri(
                "/{date}?amount={amount}&from={from}&to={to}",
                date, amount, from, to
        )).thenReturn(requestHeadersSpec);

        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(RateResponseDto.class)).thenReturn(Mono.just(expectedResponse));

        RateResponseDto result = currencyService.getEurFxRateOnDateConvertedToEur(date, from, to, amount);

        verify(requestHeadersUriSpec).uri(
                "/{date}?amount={amount}&from={from}&to={to}",
                date, amount, from, to
        );

        assertEquals(expectedResponse, result);
    }

    @Test
    void getExchangeRates_WebClientError_ThrowsException() throws URISyntaxException {
        String base = "EUR";
        Throwable cause = new Throwable("Connection refused");
        WebClientRequestException ex = new WebClientRequestException(cause,
                HttpMethod.GET, new URI(""), new HttpHeaders());

        ArgumentCaptor<Function<UriBuilder, URI>> uriFunctionCaptor = ArgumentCaptor.forClass(Function.class);
        when(requestHeadersUriSpec.uri(uriFunctionCaptor.capture())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(RateResponseDto.class)).thenReturn(Mono.error(ex));

        assertThrows(WebClientRequestException.class, () -> currencyService.getExchangeRates(base));
    }
}