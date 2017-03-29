package com.fyp.n3015509.Util;

import com.fyp.n3015509.goodreadsDAO.GoodreadsAuthor;
import com.fyp.n3015509.goodreadsDAO.GoodreadsBook;
import com.fyp.n3015509.goodreadsDAO.GoodreadsShelf;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                if (el.getNodeName().contains("book")) {
                    books.add(xmlToGoodreadsBook(el));
                }
            }
        }

        return books;
    }


    public GoodreadsBook xmlToGoodreadsBook(Element el)
    {
        try {
            int id = Integer.parseInt(el.getElementsByTagName("id").item(0).getTextContent());
            String isbn = el.getElementsByTagName("isbn").item(0).getTextContent();
            String isbn13 = el.getElementsByTagName("isbn13").item(0).getTextContent();
            int text_reviews_count = Integer.parseInt(el.getElementsByTagName("id").item(0).getTextContent());
            String title = el.getElementsByTagName("title").item(0).getTextContent();
            String title_without_series = el.getElementsByTagName("title_without_series").item(0).getTextContent();
            String image_url = el.getElementsByTagName("image_url").item(0).getTextContent();
            String small_image_url = el.getElementsByTagName("small_image_url").item(0).getTextContent();
            String large_image_url = el.getElementsByTagName("large_image_url").item(0).getTextContent();
            String link = el.getElementsByTagName("link").item(0).getTextContent();
            int num_pages = Integer.parseInt(el.getElementsByTagName("num_pages").item(0).getTextContent());
            String format = el.getElementsByTagName("format").item(0).getTextContent();
            String edition_information = el.getElementsByTagName("edition_information").item(0).getTextContent();
            String publisher = el.getElementsByTagName("publisher").item(0).getTextContent();
            int publication_day = Integer.parseInt(el.getElementsByTagName("publication_day").item(0).getTextContent());
            int publication_year = Integer.parseInt(el.getElementsByTagName("publication_year").item(0).getTextContent());
            int publication_month = Integer.parseInt(el.getElementsByTagName("publication_month").item(0).getTextContent());
            double average_rating = getDoubleFromElement(el.getElementsByTagName("average_rating").item(0).getTextContent());
            int ratings_count = Integer.parseInt(el.getElementsByTagName("ratings_count").item(0).getTextContent());
            String description = el.getElementsByTagName("description").item(0).getTextContent();
            int yearPublished = Integer.parseInt(el.getElementsByTagName("published").item(0).getTextContent());

            NodeList valueList = el.getElementsByTagName("authors");
            ArrayList<GoodreadsAuthor> authors= new ArrayList<GoodreadsAuthor>();

            for (int j = 0; j < valueList.getLength(); ++j) {
                Element value = (Element) valueList.item(j);

                int authorId = Integer.parseInt(value.getElementsByTagName("id").item(0).getTextContent());
                String authorName = value.getElementsByTagName("name").item(0).getTextContent();
                String authorImageURL = value.getElementsByTagName("image_url").item(0).getTextContent();
                String authorSmallImageURL = value.getElementsByTagName("small_image_url").item(0).getTextContent();
                String authorLink = value.getElementsByTagName("link").item(0).getTextContent();
                double authorAverageRating = getDoubleFromElement(value.getElementsByTagName("average_rating").item(0).getTextContent());
                int authorRatingsCount = Integer.parseInt(value.getElementsByTagName("ratings_count").item(0).getTextContent());
                int authorTextReviewsCount = Integer.parseInt(value.getElementsByTagName("text_reviews_count").item(0).getTextContent());
                GoodreadsAuthor author = new GoodreadsAuthor();
                author.createGoodreadsAuthor(authorId, authorName, authorImageURL, authorSmallImageURL, authorLink, authorAverageRating, authorRatingsCount, authorTextReviewsCount);
                authors.add(author);
            }
            //author information
            //needs to be in bested loop


            return GoodreadsUtil.CreateGoodreadsBook(id, isbn, isbn13, text_reviews_count, title, title_without_series, image_url, small_image_url, large_image_url, link, num_pages,
                    format, edition_information, publisher, publication_day, publication_year, publication_month, average_rating, ratings_count, description, yearPublished, authors);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public double getDoubleFromElement(String el)
    {
        double d = 0;
        Pattern p = Pattern.compile("(\\d+(?:\\.\\d+))");
        Matcher m = p.matcher(el);
        while(m.find()) {
            d = Double.parseDouble(m.group(1));
        }
        return d;
    }

    public GoodreadsAuthor xmlToGoodreadsAuthor(String response)
    {
        GoodreadsAuthor authors = new GoodreadsAuthor();

        return authors;
    }
}
