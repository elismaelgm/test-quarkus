package org.desafio.enums;

public enum Processadora {
    VISA(1),
    MASTERCARD(2),
    AMEX(3),
    ELO(4);

    private final int codigo;

    Processadora(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }
}