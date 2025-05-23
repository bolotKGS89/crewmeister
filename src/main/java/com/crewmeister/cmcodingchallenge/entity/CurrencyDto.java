package com.crewmeister.cmcodingchallenge.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrencyDto {
    private String code;
    private String name;
}
