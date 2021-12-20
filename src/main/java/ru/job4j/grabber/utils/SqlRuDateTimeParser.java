package ru.job4j.grabber.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class SqlRuDateTimeParser implements DateTimeParser {

    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("d M yy");
    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

    private static final Map<String, String> MONTHS = new HashMap<>() {
        {
            put("янв", "1");
            put("фев", "2");
            put("мар", "3");
            put("апр", "4");
            put("май", "5");
            put("июн", "6");
            put("июл", "7");
            put("авг", "8");
            put("сен", "9");
            put("окт", "10");
            put("ноя", "11");
            put("дек", "12");
        }
    };

    @Override
    public LocalDateTime parse(String parse) {
        String[] fullDate = parse.split(", ");
        String[] date = fullDate[0].split(" ");
        String value;
        LocalDate localDate = LocalDate.now();
        if (parse.toLowerCase(Locale.ROOT).equals("сегодня")) {
            value = localDate.format(dateFormat);
        } else if (parse.toLowerCase(Locale.ROOT).equals("вчера")) {
            value = localDate.minusDays(1).format(dateFormat);
        } else {
            String year = String.valueOf(Integer.parseInt(date[2]));
            value = String.format("%s %s %s", date[0], MONTHS.get(date[1]), year);
        }
        LocalDate day = LocalDate.parse(value, dateFormat);
        LocalTime time = LocalTime.parse(fullDate[1], timeFormat);
        return LocalDateTime.of(day, time);
    }

    public static void main(String[] args) throws IOException {
        Document doc = Jsoup.connect("https://www.sql.ru/forum/job-offers").get();
        Elements row = doc.select(".postslisttopic");
        for (Element td : row) {
            Element parent = td.parent();
            String s = parent.children().get(5).text();
            SqlRuDateTimeParser parser = new SqlRuDateTimeParser();
            System.out.println(parser.parse(s));
        }
    }
}