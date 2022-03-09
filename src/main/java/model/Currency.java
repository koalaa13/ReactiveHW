package model;

public enum Currency {
    DOL(30.0), RUB(1.0), EU(40.0);

    /**
     * 1 currency = coef rubles
     */
    private final double coef;

    Currency(double coef) {
        this.coef = coef;
    }

    public double getCoef() {
        return coef;
    }
}
