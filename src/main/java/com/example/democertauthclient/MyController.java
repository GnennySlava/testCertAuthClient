package com.example.democertauthclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/client")
public class MyController {

    /**
     * This property bean is created in {@link SslConfig#webClient()}
     **/
    @Autowired
    WebClient webClient;

    /**
     * This controller method invokes the server using the wired bean
     * {@link this#webClient} and returns back the secured data from the server
     *
     * @return
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     */
    @GetMapping
    public String gatherDataFromServer0() {
        Mono<String> dateFromServer = webClient.get()
                .uri("https://localhost:8082/server")
                .retrieve().bodyToMono(String.class);
        return dateFromServer.block();
    }

    @GetMapping("/test1")
    public String gatherDataFromServer1() {
        Mono<String> dateFromServer = webClient.get()
                .uri("https://localhost:8082/server/search/12345")
                .retrieve().bodyToMono(String.class);
        return dateFromServer.block();
    }

    @GetMapping("/test2")
    public String gatherDataFromServer2() {
        Mono<String> dateFromServer = webClient.get()
                .uri("https://localhost:8082/server/search/12345678")
                .retrieve().bodyToMono(String.class);
        return dateFromServer.block();
    }


}