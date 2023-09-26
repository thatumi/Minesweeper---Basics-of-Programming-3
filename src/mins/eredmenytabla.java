package mins;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/** Eredménytábla módosítása és kiírása txt fileba */
public class eredmenytabla {
    /** Ez a változó tárolja el az eredménytáblát HTML formázásban */
    String szoveg = "<html><body>";

    public eredmenytabla() {
        szoveg = "<html><body>";
    }

    /** Az eredménytáblát módosítja (ami egy txt fájl) az új adatok alapján
     * @param ujNev A felhasználó által megadott nickname
     * @param ujPontszam Pontszám (ami a hátramaradt idő)
     */
    public String[] eredmenyKi(String ujNev, int ujPontszam) {
        Scanner sc;
        String beNev;
        int bePont;

        String[] nevTomb = new String[10];
        int[] pontTomb = new int[10];
        int sor = 0;

        try {
            sc = new Scanner(new FileReader("src/eredmenytabla.txt"));
            while (sc.hasNextLine() && sor < 10) {
                System.out.println(sor);
                beNev = sc.next();
                bePont = sc.nextInt();
                if (bePont <= ujPontszam) {
                    nevTomb[sor] = ujNev;
                    pontTomb[sor] = ujPontszam;
                    ujNev = beNev;
                    ujPontszam = bePont;
                } else {
                    nevTomb[sor] = beNev;
                    pontTomb[sor] = bePont;
                }
                sor++;
            }
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        File file = new File("src/eredmenytabla.txt");
        file.delete();

        try {
            file.createNewFile();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            for (int i = 0; i < 10; i++) {
                bw.write(nevTomb[i]);
                bw.write("\t");
                bw.write(String.valueOf(pontTomb[i]));
                bw.write("\n");
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nevTomb;
    }

    /** Az eredmenytabla.txt-ből olvas be a szoveg változóba html formázásban. */
    public String eredmenyBe() {
        int sor = 1;

        try {
            Scanner scanner = new Scanner(new File("src/eredmenytabla.txt"));

            while (scanner.hasNextLine()) {
                if (sor == 10) {
                    szoveg += "</body></html>";
                    scanner.close();
                    return szoveg;
                } else {
                    szoveg += scanner.nextLine() + "<br>";
                }
                sor++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return szoveg;
    }
}