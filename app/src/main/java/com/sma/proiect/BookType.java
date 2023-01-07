package com.sma.proiect;

import android.graphics.Color;

public class BookType {
    public static int getColorFromBookGenre(String genre) {
        genre = genre.toLowerCase();
        switch (genre) {
            case "literatura universala":
                return Color.rgb(236, 131, 3);
            case "analiza matematica":
                return Color.rgb(236, 197, 3);
            case "geometrie":
                return Color.rgb(143, 228, 22);
            case "informatica":
                return Color.rgb(223, 42, 126);
            default:
                return Color.rgb(133, 153, 255);
        }
    }

    public static String[] getBookGenres() {
        return new String[]{"literatura universala", "analiza matematica", "geometrie", "informatica", "other"};
    }
}
