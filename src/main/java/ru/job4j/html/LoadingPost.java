package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoadingPost {
    public static void main(String[] args) throws IOException {
        String page = "https://www.sql.ru/forum/1325330/lidy-be-fe-senior-cistemnye-analitiki-qa-i-devops-moskva-do-200t";
        Document doc = Jsoup.connect(page).get();
        Elements row = doc.select(".msgBody");
        Element date = row.get(1);
        int maxLength = 150;
        Pattern p = Pattern.compile("\\G\\s*(.{1," + maxLength + "})(?=\\s|$)", Pattern.DOTALL);
        Matcher m = p.matcher(date.text());
        while (m.find()) {
            System.out.println(m.group(1));
        }
    }
}