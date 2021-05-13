package com.tecmov.acmeexplorer;

import com.tecmov.acmeexplorer.entity.Trip;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class Constants {

    public static final Trip[] trips = {
            new Trip("AAAA056", "Sevilla", "Paseo en el Guadalquivir", 32.00, new GregorianCalendar(2021, 6, 1).getTime(), new GregorianCalendar(2021, 6, 5).getTime(), "https://iconape.com/wp-content/png_logo_vector/beach-tour-logo.png", false),
            new Trip("BBBB066", "Cádiz", "Bautizo Playa La Caleta", 20.00, new GregorianCalendar(2021, 7, 1).getTime(), new GregorianCalendar(2021, 7, 10).getTime(), "https://iconape.com/wp-content/png_logo_vector/berjaya-redang-beach-resort.png", false),
            new Trip("CCCC086", "Madrid", "Museo del Prado", 50.00, new GregorianCalendar(2021, 8, 1).getTime(), new GregorianCalendar(2021, 8, 7).getTime(), "https://www.designevo.com/res/templates/thumb_small/mountain-and-banner-icon.png", false),
            new Trip("DDDD086", "París", "Visita completa Torre Eiffel y Museo del Louvre. Vuelo incluido", 200.00, new GregorianCalendar(2021, 9, 1).getTime(), new GregorianCalendar(2021, 9, 15).getTime(), "https://image.freepik.com/vector-gratis/fondo-logo-aventura-vintage_23-2148137112.jpg", false),
            new Trip("EEEE086", "New York", "Visita New York, vuelo y alojamietno incluido", 700.00, new GregorianCalendar(2021, 10, 1).getTime(), new GregorianCalendar(2021, 10, 30).getTime(), "https://seeklogo.com/images/I/international-association-of-educating-cities-logo-A7A76FB348-seeklogo.com.png", false)
    };

    public final static String filterPreferences = "Filter", startedDate = "StartDate", finishedDate = "FinishDate", priceMin = "PriceMin", priceMax = "PriceMax";

    public static List<Trip> chargedTrips = null;
}

