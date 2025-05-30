package com.api.central.modele;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SalesPoint {
    private int id;
    private String name;
    private String baseUrl;
}
