package org.desafio.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class CartaoGenerator {

    public static String gerarNumeroCartao() {
        Random random = new Random();
        StringBuilder numeroCartao = new StringBuilder();

        for (int i = 0; i < 16; i++) {
            int digito = random.nextInt(10);
            numeroCartao.append(digito);
        }

        return numeroCartao.toString();
    }

    public static String gerarNumeroConta() {
        Random random = new Random();
        StringBuilder numeroConta = new StringBuilder();

        for (int i = 0; i < 5; i++) {
            int digito = random.nextInt(10);
            numeroConta.append(digito);
        }

        return numeroConta.toString();
    }

    public static String gerarDataValidade() {
        Random random = new Random();
        Calendar calendar = Calendar.getInstance();

        int anos = 1 + random.nextInt(5);
        calendar.add(Calendar.YEAR, anos);

        int meses = random.nextInt(12);
        calendar.add(Calendar.MONTH, meses);

        Date validade = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        return sdf.format(validade);
    }

    public static String gerarCVV() {
        Random random = new Random();
        StringBuilder cvv = new StringBuilder();

        for (int i = 0; i < 3; i++) {
            int digito = random.nextInt(10);
            cvv.append(digito);
        }

        return cvv.toString();
    }
}