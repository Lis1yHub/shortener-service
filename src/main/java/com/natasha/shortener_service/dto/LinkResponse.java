package com.natasha.shortener_service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LinkResponse {

    private String shortCode;

    private String shortUrl;
}
