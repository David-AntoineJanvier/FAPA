package ca.ulaval.glo2004;


import ca.ulaval.glo2004.gui.FenetrePrincipale;

import javax.swing.*;


public class App {
    //Exemple de creation d'une fenetre et d'un bouton avec swing. Lorsque vous allez creer votre propre GUI
    // Vous n'aurez pas besoin d'ecrire tout ce code, il sera genere automatiquement par intellij ou netbeans
    // Par contre vous aurez a creer les actions listener pour vos boutons et etc.
    public static void main(String[] args) {
        JFrame frame = new FenetrePrincipale();
        frame.setSize(1920,1080);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }
}

