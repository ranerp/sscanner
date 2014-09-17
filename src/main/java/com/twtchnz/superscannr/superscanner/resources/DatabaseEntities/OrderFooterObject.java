package com.twtchnz.superscannr.superscanner.resources.DatabaseEntities;

public class OrderFooterObject {

   String scrapCopper;
   String emptyReels;
   String totalPallets;
   String totalProducts;

    public OrderFooterObject(String scrapCopper, String emptyReels, String totalPallets, String totalProducts) {
        this.scrapCopper = scrapCopper;
        this.emptyReels = emptyReels;
        this.totalPallets = totalPallets;
        this.totalProducts = totalProducts;
    }

    public String getScrapCopper() {
        return scrapCopper;
    }

    public void setScrapCopper(String scrapCopper) {
        this.scrapCopper = scrapCopper;
    }

    public String getEmptyReels() {
        return emptyReels;
    }

    public void setEmptyReels(String emptyReels) {
        this.emptyReels = emptyReels;
    }

    public String getTotalPallets() {
        return totalPallets;
    }

    public void setTotalPallets(String totalPallets) {
        this.totalPallets = totalPallets;
    }

    public String getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(String totalProducts) {
        this.totalProducts = totalProducts;
    }
}
