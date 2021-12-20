package ru.job4j.grabber;

import java.io.IOException;
import java.util.List;

public interface Parse {
    List<String> list(String link) throws IOException;

    String detail(String link) throws IOException;
}
