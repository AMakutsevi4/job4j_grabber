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
    public List<Post> list(String link) throws IOException {
        List<Post> list = new ArrayList<>();
        int i = 1;
        while (i <= 5) {
            String page = link + i++;
            Document doc = Jsoup.connect(page).get();
            Elements row = doc.select(".messageHeader");
            for (Element td : row) {
                if (td.text().contains("javascript")) {
                    continue;
                }
                list.add(detail(td.children().attr("href")));
            }
        }
        return list;
    }

    @Override
    public Post detail(String link) throws IOException {
        Post post = new Post();
        post.setLink(link);
        Document doc = Jsoup.connect(link).get();
        post.setTitle(doc.select(".messageHeader").get(0).ownText());
        post.setDescription(doc.select(".msgBody").get(1).text());
        String valueData = doc.select(".msgFooter").get(0).text();
        String[] data = valueData.split(" \\[");
        post.setCreated(dateTimeParser.parse(data[0]));
        return post;
    }

    public static void main(String[] args) throws IOException {
        String page = "https://www.sql.ru/forum/job-offers/";
        SqlRuParse sqlRuParse = new SqlRuParse(new SqlRuDateTimeParser());
        List<Post> list = sqlRuParse.list(page);
        for (Post post : list) {
            System.out.println(post);
        }
    }
}