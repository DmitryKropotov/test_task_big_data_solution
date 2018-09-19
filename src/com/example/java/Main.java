package com.example.java;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

public class Main {

    public static void main(String[] args) {
        try (BufferedReader br = new BufferedReader(new FileReader("src/com/example/java/TRD.csv"))) {

            TreeMap<LocalTime, Integer> sharesInTimeMoment = new TreeMap<>();
            HashMap<String, Integer> sharesPerExchange = new HashMap<>();
            HashMap<String, LocalTime> timeMomentOfFirstExchangeShare = new HashMap<>();
            HashMap<String, LocalTime> timeMomentOfLastExchangeShare = new HashMap<>();

            br.lines().map(string -> string.split(",")).forEach(strings -> {
                LocalTime timeMoment = LocalTime.parse(strings[0]);
                int amountOfShares = Integer.valueOf(strings[2]);
                String exchange = strings[3];
                sharesInTimeMoment.put(timeMoment, sharesInTimeMoment.getOrDefault(timeMoment, 0) + amountOfShares);
                sharesPerExchange.put(exchange, sharesPerExchange.getOrDefault(exchange, 0) + amountOfShares);
                timeMomentOfFirstExchangeShare.putIfAbsent(exchange, timeMoment);
                timeMomentOfLastExchangeShare.put(exchange, timeMoment);
            });

            LocalTime beginningOfSecond = sharesInTimeMoment.firstKey();
            int sharesInCurrentSecond = sharesInTimeMoment.get(beginningOfSecond);
            int maxShares = sharesInTimeMoment.get(beginningOfSecond);
            LocalTime[] secondOfMaxShares = {beginningOfSecond, beginningOfSecond.plusNanos(999000000L)};
            LocalTime currentMoment = sharesInTimeMoment.higherKey(beginningOfSecond);
            while (currentMoment != null) {
                if (currentMoment.compareTo(beginningOfSecond.plusSeconds(1)) >= 0) {
                    if (maxShares < sharesInCurrentSecond) {
                        maxShares = sharesInCurrentSecond;
                        secondOfMaxShares[0] = beginningOfSecond;
                        secondOfMaxShares[1] = beginningOfSecond.plusNanos(999000000L);
                    }
                    do {
                        sharesInCurrentSecond -= sharesInTimeMoment.get(beginningOfSecond);
                        beginningOfSecond = sharesInTimeMoment.higherKey(beginningOfSecond);
                    } while (currentMoment.compareTo(beginningOfSecond.plusSeconds(1)) >= 0);
                }
                sharesInCurrentSecond += sharesInTimeMoment.get(currentMoment);
                if (currentMoment == sharesInTimeMoment.lastKey() && maxShares < sharesInCurrentSecond) {
                    maxShares = sharesInCurrentSecond;
                    secondOfMaxShares[0] = beginningOfSecond;
                    secondOfMaxShares[1] = beginningOfSecond.plusNanos(999000000L);
                }
                currentMoment = sharesInTimeMoment.higherKey(currentMoment);
            }

            System.out.println(
                    "maximal amount of shares in 1 second was made between " + secondOfMaxShares[0] + " and " + secondOfMaxShares[1] +
                            ". In this interval " + maxShares + " shares were made");

            Set<String> exchanges = sharesPerExchange.keySet();
            exchanges.forEach(exchange ->
                    System.out.println(sharesPerExchange.get(exchange) + " shares were made since " +
                            timeMomentOfFirstExchangeShare.get(exchange) +
                            " till " + timeMomentOfLastExchangeShare.get(exchange) + " on exchange " + exchange));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
