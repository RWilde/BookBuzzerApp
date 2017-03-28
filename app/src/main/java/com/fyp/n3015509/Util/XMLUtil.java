package com.fyp.n3015509.Util;

import android.provider.DocumentsContract;

import com.fyp.n3015509.goodreads.GoodreadsAuthor;
import com.fyp.n3015509.goodreads.GoodreadsBook;
import com.fyp.n3015509.goodreads.GoodreadsShelf;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by tomha on 23-Mar-17.
 */

public class XMLUtil {
    //GoodreadsUtil gUtil = new GoodreadsUtil();

    public static Document getXMLDocument(String xml) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        InputSource is;
        try {
            builder = factory.newDocumentBuilder();
            is = new InputSource(new StringReader(xml));
            Document doc = builder.parse(is);
            return doc;
        } catch (ParserConfigurationException e) {
        } catch (SAXException e) {
        } catch (IOException e) {
        }
        return null;
    }

    public static ArrayList<GoodreadsShelf> xmlToGoodreadsShelves(String response) {
        Document doc = XMLUtil.getXMLDocument(response);
        NodeList nodeList = doc.getElementsByTagName("*");
        ArrayList<GoodreadsShelf> shelves = new ArrayList<GoodreadsShelf>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element) nodeList.item(i);
                if (el.getNodeName().contains("user_shelf")) {
                    int id = Integer.parseInt(el.getElementsByTagName("id").item(0).getTextContent());
                    String name = el.getElementsByTagName("name").item(0).getTextContent();
                    int count = Integer.parseInt(el.getElementsByTagName("book_count").item(0).getTextContent());
                    GoodreadsShelf shelf = GoodreadsUtil.createGoodreadsShelf(id, name, count);
                    shelves.add(shelf);
                }
            }
        }
        return shelves;
    }

    public ArrayList<GoodreadsBook> xmlToGoodreadsBooks(String response)
    {
        Document doc = XMLUtil.getXMLDocument(response);
        NodeList nodeList = doc.getElementsByTagName("*");
        ArrayList<GoodreadsBook> books = new ArrayList<GoodreadsBook>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element) nodeList.item(i);
                if (el.getNodeName().contains("user_shelf")) {
                    books.add(xmlToGoodreadsBook(el));
                }
            }
        }

        return books;
    }


    public GoodreadsBook xmlToGoodreadsBook(Element el)
    {
        int id =Integer.parseInt(el.getElementsByTagName("id").item(0).getTextContent());
        String isbn =el.getElementsByTagName("isbn").item(0).getTextContent();
        String isbn13 = el.getElementsByTagName("isbn13").item(0).getTextContent();
        int text_reviews_count =Integer.parseInt(el.getElementsByTagName("id").item(0).getTextContent());
        String title =el.getElementsByTagName("title").item(0).getTextContent();
        String title_without_series = el.getElementsByTagName("title_without_series").item(0).getTextContent();
        String image_url = el.getElementsByTagName("image_url").item(0).getTextContent();
        String small_image_url = el.getElementsByTagName("small_image_url").item(0).getTextContent();
        String large_image_url = el.getElementsByTagName("large_image_url").item(0).getTextContent();
        String link = el.getElementsByTagName("link").item(0).getTextContent();
        int num_pages = Integer.parseInt(el.getElementsByTagName("num_pages").item(0).getTextContent());
        String format = el.getElementsByTagName("format").item(0).getTextContent();
        String edition_information = el.getElementsByTagName("edition_information").item(0).getTextContent();
        String publisher = el.getElementsByTagName("publisher").item(0).getTextContent();
        int publication_day =Integer.parseInt(el.getElementsByTagName("publication_day").item(0).getTextContent());
        int publication_year=Integer.parseInt(el.getElementsByTagName("publication_year").item(0).getTextContent());
        int publication_month=Integer.parseInt(el.getElementsByTagName("publication_month").item(0).getTextContent());
        double average_rating=Integer.parseInt(el.getElementsByTagName("average_rating").item(0).getTextContent());
        int ratings_count=Integer.parseInt(el.getElementsByTagName("ratings_count").item(0).getTextContent());
        String description  = el.getElementsByTagName("description").item(0).getTextContent();
        int yearPublished=Integer.parseInt(el.getElementsByTagName("year_published").item(0).getTextContent());

        //author information
        //needs to be in bested loop
        int authorId=Integer.parseInt(el.getElementsByTagName("id").item(0).getTextContent());
        String authorName  = el.getElementsByTagName("name").item(0).getTextContent();
        String authorImageURL = el.getElementsByTagName("image_url").item(0).getTextContent();
        String authorSmallImageURL = el.getElementsByTagName("small_image_url").item(0).getTextContent();
        String authorLink = el.getElementsByTagName("link").item(0).getTextContent();
        double authorAverageRating=Integer.parseInt(el.getElementsByTagName("id").item(0).getTextContent());
        int authorRatingsCount=Integer.parseInt(el.getElementsByTagName("id").item(0).getTextContent());
        int authorTextReviewsCount = Integer.parseInt(el.getElementsByTagName("id").item(0).getTextContent());


        return GoodreadsUtil.CreateGoodreadsBook(id, isbn, isbn13, text_reviews_count, title, title_without_series, image_url, small_image_url, large_image_url, link, num_pages,
                format, edition_information, publisher, publication_day, publication_year, publication_month, average_rating, ratings_count, description, yearPublished, authorId,
                authorName, authorImageURL, authorSmallImageURL, authorLink, authorAverageRating, authorRatingsCount, authorTextReviewsCount);
    }

    public GoodreadsAuthor xmlToGoodreadsAuthor(String response)
    {
        GoodreadsAuthor authors = new GoodreadsAuthor();

        return authors;
    }
}
