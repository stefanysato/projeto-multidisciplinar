package br.com.raizesdonordeste.api.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PagamentoMockService {

    public boolean autorizar(BigDecimal valor) {

        BigDecimal limiteAprovacao = new BigDecimal("500.00");

        return valor.compareTo(limiteAprovacao) <= 0;
    }
}
