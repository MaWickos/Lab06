package pollub.ism.lab06;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MagazynBazaDanych extends SQLiteOpenHelper {

    // Derklaracja zmiennych
    private static final String NAZWA_BAZY = "Stoisko z warzywami.";
    static final int WERSJA = 1;
    public static final String NAZWA_TABELI = "Warzywniak";
    public static final String NAZWA_KOLUMNY_1 = "NAME";
    public static final String NAZWA_KOLUMNY_2 = "QUANTITY";

    private final Context kontekst;

    // Konstrukor
    MagazynBazaDanych(Context context){
        super(context,NAZWA_BAZY,null,WERSJA);
        kontekst = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqlLiteDatabase){

        // Tworzenie bazy danych przy pierwszym uruchomieniu aplikacji
        String sqlString = "CREATE TABLE " + NAZWA_TABELI + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + NAZWA_KOLUMNY_1 + " TEXT, " + NAZWA_KOLUMNY_2 +" INTEGER)";
        sqlLiteDatabase.execSQL(sqlString);

        ContentValues krotka = new ContentValues();

        String[] asortyment = kontekst.getResources().getStringArray(R.array.Asortyment);

        for(String pozycja : asortyment){
            krotka.clear();
            krotka.put(NAZWA_KOLUMNY_1, pozycja);
            krotka.put(NAZWA_KOLUMNY_2, 0);
            sqlLiteDatabase.insert(NAZWA_TABELI, null, krotka);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1){
        // Wymaga nadpisania, nawet gdy nie używamy
    }

    // Metoda do odczytywania ilości danego produktu
    public Integer podajIlosc(String wybraneWarzywo){

        Integer ilosc = null;
        SQLiteDatabase bazaDoOdczytu = null;
        Cursor kursor = null;

        try{
            bazaDoOdczytu = getReadableDatabase();

            // Zapytanie
            kursor = bazaDoOdczytu.query(
                    MagazynBazaDanych.NAZWA_TABELI,
                    new String[]{MagazynBazaDanych.NAZWA_KOLUMNY_2},
                    MagazynBazaDanych.NAZWA_KOLUMNY_1 + "=?", new String[]{wybraneWarzywo},
                    null, null, null);

            kursor.moveToFirst();

            // Odczyt ilosci wybranego produktu
            ilosc = kursor.getInt(0);

        } catch (SQLException ex){
            // Należy obsłużyć wyjątek

        } finally {

            if(kursor != null)
                kursor.close();

            if(bazaDoOdczytu != null)
                bazaDoOdczytu.close();
        }

        // Zwrócenie ilości produktu
        return ilosc;
    }

    // Metoda do zmiany zpisanej ilości na podstawie nazwy
    public void zmienStanMagazynu(String wybraneWarzywo, int nowaIlosc){

        SQLiteDatabase bazaDoZapisu = null;

        try{
            bazaDoZapisu = getWritableDatabase();

            // Zapytanie
            ContentValues krotka = new ContentValues();
            krotka.put(NAZWA_KOLUMNY_2, Integer.toString(nowaIlosc));

            // Aktualizacja bazy danych
            bazaDoZapisu.update(NAZWA_TABELI, krotka, NAZWA_KOLUMNY_1 + "=?", new String[]{wybraneWarzywo});

        } catch (SQLException ex){

            // Nalezy obsłużyć ewentualny wyjątek
        } finally {

            if(bazaDoZapisu != null)
                bazaDoZapisu.close();
        }

    }
}
