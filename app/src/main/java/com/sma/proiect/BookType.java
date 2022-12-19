package com.sma.proiect;

import android.graphics.Color;

public class BookType {
    public static int getColorFromBookGenre(String genre) {
        genre = genre.toLowerCase();
        switch (genre) {
            case "literatura universala":
                return Color.rgb(200, 50, 50);
            case "analiza matematica":
                return Color.rgb(50, 150, 50);
            case "geometrie":
                return Color.rgb(20, 20, 150);
            case "informatica":
                return Color.rgb(230, 140, 0);
            default:
                return Color.rgb(100, 100, 100);
        }
    }

    public static String[] getBookGenres() {
        return new String[]{"literatura universala", "analiza matematica", "geometrie", "informatica", "other"};
    }
}
