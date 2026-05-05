package org.yellowflash;

import org.yellowflash.catalog.Catalog;
import org.yellowflash.domain.Movie;
import org.yellowflash.domain.enums.Category;

import java.util.Optional;

public class App 
{
    public static void main( String[] args )
    {
        Catalog<Movie> movies = new Catalog<>();
        movies.add(1, new Movie(1, "Shrek", "Adamson", 2001, Category.FAMILY));
        Optional<Movie> hit  = movies.find(1);    // present
        Optional<Movie> miss = movies.find(999);  // empty
        System.out.println(movies.size());                            // 1
    }
}
