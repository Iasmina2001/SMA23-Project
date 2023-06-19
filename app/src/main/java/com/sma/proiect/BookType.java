package com.sma.proiect;

import android.graphics.Color;


public class BookType {

    public static int getColorFromBookGenre(String genre) {
        genre = genre.toLowerCase();
        switch (genre) {
            case "literatura universala":
                return Color.rgb(86, 130, 3);
            case "analiza matematica":
                return Color.rgb(123, 182, 97);
            case "geometrie":
                return Color.rgb(0, 171, 102);
            case "informatica":
                return Color.rgb(77, 140, 87);
            default:
                return Color.rgb(0, 155, 125);
        }
    }

    public static String[] getBookGenres() {
        return new String[]{"literatura universala", "analiza matematica", "geometrie", "informatica", "other"};
    }

    public static String[] getBookTypes() {
        return new String[]{"eBook", "paper book"};
    }
}
