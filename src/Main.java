import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        try {
            String percorsoFileInput = "src/Diffusione.csv";
            List<Dato> dati = caricaDati(percorsoFileInput);
            System.out.println("Dati caricati: " + dati.size() + " record");

            List<Report> reportRegioni = generaReport(dati);
            System.out.println("Report generato per " + reportRegioni.size() + " regioni");

            String percorsoFileOutput = "report.csv";
            salvaReport(reportRegioni, percorsoFileOutput);
            System.out.println("Report generato con successo: " + percorsoFileOutput);
        } catch (IOException e) {
            System.err.println("Errore: " + e.getMessage());
        }
    }

    static class Dato {
        private int anno;
        private String regione;
        private double valore;

        public Dato(int anno, String regione, double valore) {
            this.anno = anno;
            this.regione = regione;
            this.valore = valore;
        }

        public int getAnno() {
            return anno;
        }

        public String getRegione() {
            return regione;
        }

        public double getValore() {
            return valore;
        }
    }

    static class Report {
        private String regione;
        private double totale;
        private Map<Integer, Double> valoriPerAnno;

        public Report(String regione) {
            this.regione = regione;
            this.totale = 0.0;
            this.valoriPerAnno = new HashMap<>();
        }

        public void aggiungi(int anno, double valore) {
            this.totale += valore;
            this.valoriPerAnno.put(anno, valore);
        }

        public String getRegione() {
            return regione;
        }

        public double getTotale() {
            return totale;
        }

        public double getValore(int anno) {
            return valoriPerAnno.getOrDefault(anno, 0.0);
        }

        public double getMedia() {
            return valoriPerAnno.size() > 0 ? totale / valoriPerAnno.size() : 0;
        }
    }

    private static List<Dato> caricaDati(String percorsoFile) throws IOException {
        List<Dato> dati = new ArrayList<>();

        try (BufferedReader lettore = new BufferedReader(new FileReader(percorsoFile))) {
            String riga;
            boolean intestazione = true;

            while ((riga = lettore.readLine()) != null) {
                if (intestazione) {
                    intestazione = false;
                    continue;
                }

                String[] valori = riga.split(";");
                if (valori.length >= 3) {
                    int anno = Integer.parseInt(valori[0]);
                    String regione = valori[1];
                    String valoreStr = valori[2].replace(',', '.');
                    double valore = Double.parseDouble(valoreStr);

                    dati.add(new Dato(anno, regione, valore));
                }
            }
        }

        return dati;
    }

    private static List<Report> generaReport(List<Dato> dati) {
        List<Report> reportRegioni = new ArrayList<>();
        List<String> regioni = new ArrayList<>();

        for (Dato dato : dati) {
            if (!regioni.contains(dato.getRegione())) {
                regioni.add(dato.getRegione());
            }
        }

        for (String regione : regioni) {
            Report report = new Report(regione);

            for (Dato dato : dati) {
                if (dato.getRegione().equals(regione)) {
                    report.aggiungi(dato.getAnno(), dato.getValore());
                }
            }

            reportRegioni.add(report);
        }

        Collections.sort(reportRegioni, (r1, r2) -> r1.getRegione().compareTo(r2.getRegione()));

        return reportRegioni;
    }

    private static void salvaReport(List<Report> reportRegioni, String percorsoFile) throws IOException {
        try (FileWriter scrittore = new FileWriter(percorsoFile)) {
            // Intestazione del file CSV
            scrittore.write("Regione;Totale;2003;2004;2005;2006;2007;Media\n");

            // Scrivi una riga per ogni regione
            for (Report report : reportRegioni) {
                StringBuilder riga = new StringBuilder();

                // Aggiungi regione
                riga.append(report.getRegione()).append(";");

                // Aggiungi totale
                riga.append(formattaNumero(report.getTotale())).append(";");

                // Aggiungi valori per ogni anno
                riga.append(formattaNumero(report.getValore(2003))).append(";");
                riga.append(formattaNumero(report.getValore(2004))).append(";");
                riga.append(formattaNumero(report.getValore(2005))).append(";");
                riga.append(formattaNumero(report.getValore(2006))).append(";");
                riga.append(formattaNumero(report.getValore(2007))).append(";");

                // Aggiungi media
                riga.append(formattaNumero(report.getMedia()));

                // Scrivi la riga
                scrittore.write(riga.toString() + "\n");
            }
        }
    }

    // Metodo di supporto per formattare i numeri
    private static String formattaNumero(double valore) {
        return String.format("%.2f", valore).replace('.', ',');
    }
}