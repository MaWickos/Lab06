package pollub.ism.lab06;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import pollub.ism.lab06.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ArrayAdapter<CharSequence> adapter;

    // Pola związane z bazą danych
    MagazynBazaDanych bazaDanych;
    String wybraneWarzywo = null;
    Integer wybraneWarzywoIlosc = null;

    public enum OperacjaMagazynowa {SKLADUJ, WYDAJ};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Zapis nieakutalny
        // setContentView(R.layout.activity_main);

        // Uchywt do wszystkich widoków w aplikacji
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Utworzenie adaptera i podłączenie spinnera
        adapter = ArrayAdapter.createFromResource(this, R.array.Asortyment, android.R.layout.simple_dropdown_item_1line);
        binding.spinner.setAdapter(adapter);

        // Utworzenie uchwytu do bazy danych
        bazaDanych = new MagazynBazaDanych(this);

        // Dodanie listenerów do widoku
        binding.przyciskSkladuj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                // Inkrementacja ilości produktów
                zmienStan(OperacjaMagazynowa.SKLADUJ);
            }
        });

        binding.przyciskWydaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Dekrementacja liczby produktów
                zmienStan(OperacjaMagazynowa.WYDAJ);

            }
        });

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                wybraneWarzywo = adapter.getItem(i).toString();
                aktualizuj();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Nie będziemy implementować, ale musi być
            }
        });
    }

    // Operacje na bazie danych - reakcje na przyciski
    private void aktualizuj() {
        wybraneWarzywoIlosc = bazaDanych.podajIlosc(wybraneWarzywo);

        if (wybraneWarzywoIlosc != 0){
            binding.tekstStanMagazynu.setText("Stan magazynu dla " + wybraneWarzywo + " wynosi " + wybraneWarzywoIlosc);
        } else {
            binding.tekstStanMagazynu.setText("Aktualnie nie mamy " + wybraneWarzywo + " :(");
        }
    }

    // Zmiana stanu (SKLADUJ - dodajemy, WYDAJ - odejmujemy)
    private void zmienStan(OperacjaMagazynowa operacja){

        Integer zmianaIlosci = null;
        Integer nowaIlosc = null;

        try{
            zmianaIlosci = Integer.parseInt(binding.edycjaIlosc.getText().toString());
        } catch (NumberFormatException ex){
            return;
        } finally {
            binding.edycjaIlosc.setText("");
        }

        // Rozróżnienie operacji do wykonania
        switch (operacja){
            case SKLADUJ:
                        nowaIlosc = wybraneWarzywoIlosc + zmianaIlosci;
                        break;

            case WYDAJ:
                        nowaIlosc = wybraneWarzywoIlosc - zmianaIlosci;
                        break;
        }

        // Jeżeli ilość wychodzi ujemna to nic nie rób
        if(nowaIlosc < 0){

            // Komunikat
            Toast.makeText(this, "Brak wystarczającej ilości produktów", Toast.LENGTH_LONG).show();

        } else {
            // Wprowadzenie zmian
            bazaDanych.zmienStanMagazynu(wybraneWarzywo, nowaIlosc);
        }

        aktualizuj();
    }
}