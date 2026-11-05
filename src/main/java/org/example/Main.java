package org.example;

import java.util.Scanner;

public class Main {
    static void main() {

        while (true) {

            Scanner scanner = new Scanner(System.in);

            System.out.println("=== Taschenrechner ===");

            System.out.print("Erste Zahl: ");
            double zahl1 = scanner.nextDouble();

            System.out.print("Rechenzeichen: ");
            String operator = scanner.next();

            System.out.print("Zweite Zahl: ");
            double zahl2 = scanner.nextDouble();

            if (operator.equals("+")) {

                System.out.println("Ergebnis: " + (zahl1 + zahl2));

            } else if (operator.equals("-")) {

                System.out.println("Ergebnis: " + (zahl1 - zahl2));

            } else if (operator.equals("/")) {

                if (zahl2 == 0) {

                    System.out.println("Durch 0 kann man nicht teilen!");

                } else {

                    System.out.println("Ergebnis: " + (zahl1 / zahl2));

                }

            } else if (operator.equals("*")) {

                System.out.println("Ergebnis: " + (zahl1 * zahl2));

            } else {

                System.out.println("Ungültiger Operator!");

            }

            System.out.print("Weitere Rechnung? (ja/nein): ");
            String weiter = scanner.next();
        }
    } // nein ausgabe bei weitere rechnung geht nicht
}