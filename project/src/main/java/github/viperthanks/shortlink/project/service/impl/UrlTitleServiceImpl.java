package github.viperthanks.shortlink.project.service.impl;

import github.viperthanks.shortlink.project.dto.resp.UrlTitleRespDTO;
import github.viperthanks.shortlink.project.service.UrlTitleService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * desc: url标题实现层
 *
 * @author Viper Thanks
 * @since 1/3/2024
 */
@Slf4j
@Service
public class UrlTitleServiceImpl implements UrlTitleService {

    @SneakyThrows
    @Override
    public UrlTitleRespDTO getUrlTitleByUrl(String url) {
        URL tagetUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) tagetUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            Document document = Jsoup.connect(url).get();
            return new UrlTitleRespDTO(document.title());
        }
        return new UrlTitleRespDTO("Error while fetching title");
    }
}
