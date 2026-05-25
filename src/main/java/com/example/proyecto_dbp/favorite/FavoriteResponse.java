// FavoriteResponse.java
package com.example.proyecto_dbp.Favorite;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
public class FavoriteResponse {
    private Long id;
    private Long productId;
    private String productTitle;
    private Double productPrice;
    private LocalDateTime fechaAgregado;
}