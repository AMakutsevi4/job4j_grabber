package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;
import ru.job4j.post.Post;

import java.io.IOException;

public class LoadingPost {

    public static Post parsePage(String page) throws IOException {
        Post post = new Post();
        post.setLink(page);
        Document doc = Jsoup.connect(page).get();
        post.setTitle(doc.select(".messageHeader").get(0).ownText());
        post.setDescription(doc.select(".msgBody").get(1).text());
        String valueData = doc.select(".msgFooter").get(0).text();
        String[] data = valueData.split(" \\[");
        SqlRuDateTimeParser parser = new SqlRuDateTimeParser();
        post.setCreated(parser.parse(data[0]));
        return post;
    }

    public static void main(String[] args) throws IOException {
        String page = "https://www.sql.ru/forum/1325330/lidy-be-fe-senior-cistemnye-analitiki-qa-i-devops-moskva-do-200t";
        System.out.println(parsePage(page));
    }
}