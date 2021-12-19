package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class LoadingPost {

    public static String parsePage(String page) throws IOException {
        Document doc = Jsoup.connect(page).get();
        Elements header = doc.select(".messageHeader");
        String[] name = header.text().split("\\.");
        Elements content = doc.select(".msgBody");
        Element description = content.get(1);
        Elements data = doc.select(".msgFooter");
        String[] date = data.text().split(",");
        String[] time = date[1].split(" ");
        return "Вакансия: " + name[0] + "\n"
                + "Описание: " + description.text() + "\n"
                + "Дата публикации вакансии: " + date[0] + "," + time[1] + "\n"
                + "Ссылка на вакансию: " + time[2];
    }

    public static void main(String[] args) throws IOException {
        String page = "https://www.sql.ru/forum/1325330/lidy-be-fe-senior-cistemnye-analitiki-qa-i-devops-moskva-do-200t";
        System.out.println(parsePage(page));
    }
}