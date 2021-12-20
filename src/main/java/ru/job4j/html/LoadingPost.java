package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import ru.job4j.post.Post;

import java.io.IOException;

public class LoadingPost {

    public static String parsePage(String page) throws IOException {
        Document doc = Jsoup.connect(page).get();
        String header = doc.select(".messageHeader").get(0).ownText();
        String content = doc.select(".msgBody").get(1).text();
        String valueData = doc.select(".msgFooter").get(0).text();
        String[] data = valueData.split(" \\[");
        Post post = new Post();
        post.setLink(page);

        return "Вакансия: " + header + "\n"
                + "Описание: " + content + "\n"
                + "Дата публикации вакансии: " + data[0] + "\n"
                + "Ссылка на вакансию: " + post.getLink();

    }

    public static void main(String[] args) throws IOException {
        String page = "https://www.sql.ru/forum/1325330/lidy-be-fe-senior-cistemnye-analitiki-qa-i-devops-moskva-do-200t";
        System.out.println(parsePage(page));
    }
}