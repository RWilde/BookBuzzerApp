package com.fyp.n3015509.Util;

import com.fyp.n3015509.APIs.GoodreadsAPI;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsAuthor;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsBook;
import com.fyp.n3015509.dao.goodreadsDAO.GoodreadsShelf;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jsoup.Jsoup;
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
import org.apache.commons.lang.StringEscapeUtils;

/**
 * Created by tomha on 23-Mar-17.
 */

public class XMLUtil {
    //GoodreadsAPI gUtil = new GoodreadsAPI();

    public Document getXMLDocument(String xml) {
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

    public ArrayList<GoodreadsShelf> xmlToGoodreadsShelves(String response) {
        Document doc = getXMLDocument(response);
        NodeList nodeList = doc.getElementsByTagName("*");
        ArrayList<GoodreadsShelf> shelves = new ArrayList<GoodreadsShelf>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element) nodeList.item(i);
                if (el.getNodeName().contains("user_shelf")) {
                    int id = getInt(el.getElementsByTagName("id").item(0).getTextContent());
                    String name = el.getElementsByTagName("name").item(0).getTextContent();
                    int count = getInt(el.getElementsByTagName("book_count").item(0).getTextContent());
                    GoodreadsShelf shelf = com.fyp.n3015509.APIs.GoodreadsAPI.createGoodreadsShelf(id, name, count);
                    shelves.add(shelf);
                }
            }
        }
        return shelves;
    }

    public ArrayList<GoodreadsBook> xmlToGoodreadsBooks(String response)
    {
        try {
            int count = 0;
            Document doc = getXMLDocument(response);
            NodeList nodeList = doc.getElementsByTagName("*");
            ArrayList<GoodreadsBook> books = new ArrayList<GoodreadsBook>();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) nodeList.item(i);
                    if (el.getNodeName().contains("book")) {
                        books.add(xmlToGoodreadsBook(el));
                        count++;
                    }
                }
            }

            return books;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public GoodreadsBook xmlToGoodreadsBook(Element el)
    {
        try {
            int id = getIntFromElement(el.getElementsByTagName("id").item(0).getTextContent());
            String isbn = unescapeXML(el.getElementsByTagName("isbn").item(0).getTextContent());
            String isbn13 = unescapeXML(el.getElementsByTagName("isbn13").item(0).getTextContent());
            int text_reviews_count = getIntFromElement(el.getElementsByTagName("text_reviews_count").item(0).getTextContent());
            String title = unescapeXML(el.getElementsByTagName("title").item(0).getTextContent());
            String title_without_series = unescapeXML(el.getElementsByTagName("title_without_series").item(0).getTextContent());
            String image_url = unescapeXML(el.getElementsByTagName("image_url").item(0).getTextContent());
            String small_image_url = unescapeXML(el.getElementsByTagName("small_image_url").item(0).getTextContent());
            String large_image_url = unescapeXML(el.getElementsByTagName("large_image_url").item(0).getTextContent());
            String link = unescapeXML(el.getElementsByTagName("link").item(0).getTextContent());
            int num_pages = getIntFromElement(el.getElementsByTagName("num_pages").item(0).getTextContent());
            String format = unescapeXML(el.getElementsByTagName("format").item(0).getTextContent());
            String edition_information = unescapeXML(el.getElementsByTagName("edition_information").item(0).getTextContent());
            String publisher = unescapeXML(el.getElementsByTagName("publisher").item(0).getTextContent());
            int publication_day = getIntFromElement(el.getElementsByTagName("publication_day").item(0).getTextContent());
            int publication_year = getIntFromElement(el.getElementsByTagName("publication_year").item(0).getTextContent());
            int publication_month = getIntFromElement(el.getElementsByTagName("publication_month").item(0).getTextContent());
            double average_rating = getDoubleFromElement(el.getElementsByTagName("average_rating").item(0).getTextContent());
            int ratings_count =getIntFromElement(el.getElementsByTagName("ratings_count").item(0).getTextContent());
            String description = unescapeXML(el.getElementsByTagName("description").item(0).getTextContent());
            int yearPublished = getIntFromElement(el.getElementsByTagName("published").item(0).getTextContent());

            NodeList valueList = el.getElementsByTagName("authors");
            ArrayList<GoodreadsAuthor> authors= new ArrayList<GoodreadsAuthor>();

            for (int j = 0; j < valueList.getLength(); ++j) {
                Element value = (Element) valueList.item(j);

                int authorId = getIntFromElement(value.getElementsByTagName("id").item(0).getTextContent());
                String authorName = unescapeXML(value.getElementsByTagName("name").item(0).getTextContent());
                String authorImageURL = unescapeXML(value.getElementsByTagName("image_url").item(0).getTextContent());
                String authorSmallImageURL = unescapeXML(value.getElementsByTagName("small_image_url").item(0).getTextContent());
                String authorLink = unescapeXML(value.getElementsByTagName("link").item(0).getTextContent());
                double authorAverageRating = getDoubleFromElement(value.getElementsByTagName("average_rating").item(0).getTextContent());
                int authorRatingsCount = getIntFromElement(value.getElementsByTagName("ratings_count").item(0).getTextContent());
                int authorTextReviewsCount = getIntFromElement(value.getElementsByTagName("text_reviews_count").item(0).getTextContent());
                GoodreadsAuthor author = new GoodreadsAuthor();
                author.createGoodreadsAuthor(authorId, authorName, authorImageURL, authorSmallImageURL, authorLink, authorAverageRating, authorRatingsCount, authorTextReviewsCount);
                author.setImgLink(authorImageURL);
                authors.add(author);
            }
            //author information
            //needs to be in bested loop


            return GoodreadsAPI.CreateGoodreadsBook(id, isbn, isbn13, text_reviews_count, title, title_without_series, image_url, small_image_url, large_image_url, link, num_pages,
                    format, edition_information, publisher, publication_day, publication_year, publication_month, average_rating, ratings_count, description, yearPublished, authors);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public String unescapeXML(String el)
    {
        if(el.contentEquals(""))
        {
            el = null;
        }
        if (el != null)
        {return Jsoup.parse(StringEscapeUtils.unescapeJava(el)).text();}
        return null;
    }

    public int getIntFromElement(String el)
    {
        if(el.contentEquals(""))
        {
            el = null;
        }
        if (el != null)
        {return Integer.parseInt(el);}
        return 0;
    }

    public static int getInt(String el)
    {
        if(el.contentEquals(""))
        {
            el = null;
        }
        if (el != null)
        {return Integer.parseInt(el);}
        return 0;
    }

    public double getDoubleFromElement(String el)
    {
        if(el.contentEquals(""))
        {
            el = null;
        }
        if (el != null)
        {
            double d = 0;
            Pattern p = Pattern.compile("(\\d+(?:\\.\\d+))");
            Matcher m = p.matcher(el);
            while (m.find()) {
                d = Double.parseDouble(m.group(1));
            }
            return d;
        }
        return 0;
    }

    public GoodreadsAuthor xmlToGoodreadsAuthor(String response)
    {
        GoodreadsAuthor authors = new GoodreadsAuthor();

        return authors;
    }
}
