package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.Parse;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;
import ru.job4j.post.Post;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SqlRuParse implements Parse {

    private final DateTimeParser dateTimeParser;

    public SqlRuParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    @Override
    public List<String> list(String link) throws IOException {
        List<String> list = new ArrayList<>();
        int i = 1;
        while (i <= 5) {
            String page = link + i++;
            Document doc = Jsoup.connect(page).get();
            Elements row = doc.select(".postslisttopic");
            for (Element td : row) {
                list.add(detail(td.child(0).attr("href")));
            }
        }
        return list;
    }

    @Override
    public String detail(String link) throws IOException {
        Document doc = Jsoup.connect(link).get();
        String header = doc.select(".messageHeader").get(0).ownText();
        String content = doc.select(".msgBody").get(1).text();
        String valueData = doc.select(".msgFooter").get(0).text();
        String[] data = valueData.split(" \\[");
        Post post = new Post();
        post.setLink(link);

        return "Вакансия: " + header + "\n"
                + "Описание: " + content + "\n"
                + "Дата публикации вакансии: " + data[0] + "\n"
                + "Ссылка на вакансию: " + post.getLink();
    }

    public static void main(String[] args) throws IOException {
        String page = "https://www.sql.ru/forum/job-offers/";
        SqlRuParse sqlRuParse = new SqlRuParse(new SqlRuDateTimeParser());
        List<String> list = sqlRuParse.list(page);
        for (String post: list) {
            System.out.println(post);
        }
    }
}