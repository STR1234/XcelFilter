package sperrlistenpruefung;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class Sperrlistenpruefer {
    Sperrlistenleser mailListenLeser;
    Sperrlistenleser sperrListenLeser;
    ArrayList zuSperrendeAdressZeilen = new ArrayList();
    String mailListenPfad;
    //Windows
    String speicherPfad;

    //MacOS
    //String speicherPfad = "/";

    public Sperrlistenpruefer(String ListenPfad) {
        this.mailListenPfad = ListenPfad;
        this.mailListenLeser = new Sperrlistenleser(mailListenPfad, false);
        this.sperrListenLeser =
                new Sperrlistenleser(mailListenPfad, true);

        sperrListenLeser.initialisiereWorkbook();
        mailListenLeser.initialisiereWorkbook();
    }

    public void pruefe() {
        mailAdressenFilter();
        modifiziere();
    }

    public void mailAdressenFilter() {

        int sperrListenLaenge = sperrListenLeser.gibLaenge();

        int mailListenLaenge = mailListenLeser.gibLaenge();

        for (int i = 1; i <= sperrListenLaenge; i++) {
            for (int j = 1; j <= mailListenLaenge; j++) {
                if (mailListenLeser.gibZeilenWert(j, 1).equals(
                        sperrListenLeser.gibZeilenWert(i, 1))) {
                        this.zuSperrendeAdressZeilen.add(j);
                }
            }
        }
    }

    public void modifiziere() {
        Iterator<Integer> sperrAdressIterator =
                zuSperrendeAdressZeilen.iterator();
        while (sperrAdressIterator.hasNext()) {
            int zeile = sperrAdressIterator.next();
            mailListenLeser.loescheZeile(zeile);
        }
        schiebeZellen();
        schreibe();
    }

    public void schiebeZellen() {
        int zeilenAnzahl = mailListenLeser.gibZeilenAnzahl();
        int ersteStelleZelleLeer = 0;
        int aktuelleStelle = 0;
        boolean keineZellenLeer = true;
        boolean komplettDurchlaufen = false;

        for (int i = 1; keineZellenLeer && i < zeilenAnzahl; i++) {
            Row row = mailListenLeser.sheet.getRow(i);
            if (row == null ||
                    row.getCell(1).getCellType() == CellType.BLANK) {
                keineZellenLeer = false;
                ersteStelleZelleLeer = i;

            }
        }

        for (int i = ersteStelleZelleLeer; i < zeilenAnzahl
                && (mailListenLeser.sheet.getRow(i) == null ||
                mailListenLeser.sheet.getRow(i).
                        getCell(1).getCellType() ==
                        CellType.BLANK); i++) {
            if (i != zeilenAnzahl - 1) {
                aktuelleStelle = i + 1;
            } else {
                komplettDurchlaufen = true;
            }
        }

        this.schiebeUndEntferne(ersteStelleZelleLeer, aktuelleStelle);

        if (mailListenLeser.sheet.
                getRow(ersteStelleZelleLeer + 1) == null) {
            ersteStelleZelleLeer++;
        }
        while (!komplettDurchlaufen) {
            for (int i = aktuelleStelle; i < zeilenAnzahl &&
                    mailListenLeser.sheet.getRow(i) == null; i++) {
                aktuelleStelle = i + 1;
            }

            this.schiebeUndEntferne(ersteStelleZelleLeer, aktuelleStelle);

            if (aktuelleStelle == zeilenAnzahl) {
                komplettDurchlaufen = true;
            }

            if (mailListenLeser.sheet.
                    getRow(ersteStelleZelleLeer + 1) == null) {
                ersteStelleZelleLeer++;
            }
        }

    }

    public void schreibe() {
        try {
            neuerListenPfad();
            FileOutputStream fileOutputStream =
                    new FileOutputStream(
                            new File(this.speicherPfad));

            mailListenLeser.workbook.write(fileOutputStream);

            System.out.println("MaillisteNeu erstellt");
            fileOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void schiebeUndEntferne(int ersteStelleZelleLeer,
                                  int aktuelleStelle) {
        Cell cell = mailListenLeser.sheet.getRow(aktuelleStelle).
                getCell(1);

        mailListenLeser.sheet.createRow(ersteStelleZelleLeer);
        mailListenLeser.sheet.getRow(ersteStelleZelleLeer).createCell(1);

        mailListenLeser.sheet.getRow(ersteStelleZelleLeer).getCell(1).
                setCellValue(cell.getStringCellValue());

       mailListenLeser.sheet.
               removeRow(mailListenLeser.sheet.getRow(aktuelleStelle));
    }

    public String neuerListenPfad() {
        String aktuellerListenPfad = this.mailListenPfad;
        System.out.println("AktuellerListenPfad:");
        System.out.println(aktuellerListenPfad);

        //Unter MacOS werden / diese Schrägstriche genutzt
        //String[] neuerListenPfad = aktuellerListenPfad.split("/");

        //Unter Windows werden \ diese Schrägstriche genutzt
        String[] neuerListenPfad = aktuellerListenPfad.split("\\\\");

        //Windows
        neuerListenPfad[neuerListenPfad.length - 1] = "\\MaillisteNeu.xlsx";

        //MacOS
        //neuerListenPfad[neuerListenPfad.length - 1] = "MaillisteNeu.xlsx";

        for (int i = 0; i < neuerListenPfad.length; i++) {
            if (i == 0) {
                //Windows
                this.speicherPfad =  neuerListenPfad[i];

                //MacOS
                //this.speicherPfad = this.speicherPfad + neuerListenPfad[i];
                System.out.println(neuerListenPfad[i]);

            } else if (i < neuerListenPfad.length - 1) {
                //Windows
                this.speicherPfad =
                        this.speicherPfad + "\\" + neuerListenPfad[i];

                //MacOS
                //this.speicherPfad = this.speicherPfad + neuerListenPfad[i] + "/";
                System.out.println(neuerListenPfad[i]);
            } else {
                this.speicherPfad = this.speicherPfad + neuerListenPfad[i];
                System.out.println(neuerListenPfad[i]);
            }
        }

        System.out.println(this.speicherPfad);
        return aktuellerListenPfad;
    }
}
