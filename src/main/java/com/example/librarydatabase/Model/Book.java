package com.example.librarydatabase.Model;

import java.text.DecimalFormat;

public class Book {
    private int bookID;
    private String title;
    private String author;
    private double rating;
    private int num_pages;
    private int year;
    private boolean availability;

    public Book(int bookID, String title, String author, double rating,
                int num_pages, int year, boolean availability) {
        this.bookID = bookID;
        this.title = title;
        this.author = author;
        this.rating = rating;
        this.num_pages = num_pages;
        this.year = year;
        this.availability = availability;

    }

    public int getBookID() {
        return bookID;
    }

    public String getAuthor() {
        return author;
    }
    public String getTitle() {
        return title;
    }
//    public void setTitle(String newTitle){
//        title = newTitle;
//    }
    public double getRating(){
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String rounded = decimalFormat.format(rating);
        return Double.parseDouble(rounded);
    }

    public int getNum_pages() {
        return num_pages;
    }

    public int getYear(){
        return year;
    }
    public boolean getAvailability(){
        return availability;
    }
    public void setAvailability(boolean status){
        this.availability = status;
    }





}
