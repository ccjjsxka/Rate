package com.example.javaprj;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class MyClass {
    public static void main(String[] args) throws InterruptedException {
        while (true) {
            System.out.println("hello");
            String url = "https://www.usd-cny.com/bankofchina.htm";
            try {
                Document doc = Jsoup.connect(url).get();
                System.out.println(doc.title());
                Element table = doc.getElementsByTag("table").first();
                Elements trs = table.getElementsByTag("tr");
                for (Element tr : trs) {
                    Elements tds = tr.getElementsByTag("td");
                    if (tds.size() > 0) {
                        Element td1 = tds.get(0);
                        Element td2 = tds.get(5);
                        System.out.println(td1.text() + "==>" + td2.text());
                    }
                }
            } catch (IOException e) {

            }
            Thread.sleep(10000);
        }
//        while (true){
//            System.out.println("65666");
//            Thread.sleep(1234);
//        }

    }
}