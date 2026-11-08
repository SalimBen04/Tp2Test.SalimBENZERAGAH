package ht.mbds.salimbenzeragah.tp2test_salimbenzeragah;

public class TestMeteo {
    public static void main(String[] args) {
        MeteoTool tool = new MeteoTool();

        System.out.println("=== TEST UNITAIRE DE METEO TOOL ===");

        double[] coords = tool.getCoordonnees("Paris");
        System.out.println("Coordonnées de Paris : Lat = " + coords[0] + ", Lon = " + coords[1]);

        boolean pluie = tool.isPluiePrevue(48.85, 2.35);
        System.out.println("Pluie prévue à Paris pour les 3 prochains jours ? " + (pluie ? "OUI" : "NON"));
    }
}