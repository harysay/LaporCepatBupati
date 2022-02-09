package id.go.kebumenkab.laporcepatbupati.ui.statistik.jsontable.model;

public class Aduan {
    private String namaKategori;
    private String namaSkpd;
    private String jumlAduanBelum;
    private String jumlAduanProses;
    private String jumlAduanSelesai;
    private String persenBelum;
    private String persenProses;
    private String persenSelesai;
    private String total;

    public Aduan() {
    }

    public Aduan(String kategori, String skpd, String jmlbelum, String jmlproses, String jmlselesai, String persenblm, String persenproses, String persenselesai, String totaladuan) {
        this.namaKategori = kategori;
        this.namaSkpd = skpd;
        this.jumlAduanBelum = jmlbelum;
        this.jumlAduanProses = jmlproses;
        this.jumlAduanSelesai = jmlselesai;
        this.persenBelum = persenblm;
        this.persenProses = persenproses;
        this.persenSelesai = persenselesai;
        this.total = totaladuan;
    }

    public String getnamaKategori() {
        return namaKategori;
    }

    public String getnamaSkpd() {
        return namaSkpd;
    }

    public String getjumlAduanBelum() {
        return jumlAduanBelum;
    }

    public String getjumlAduanProses() {
        return jumlAduanProses;
    }

    public String getJumlAduanSelesai() {
        return jumlAduanSelesai;
    }

    public String getPersenBelum() {
        return persenBelum;
    }

    public String getPersenProses() {
        return persenProses;
    }

    public String getPersenSelesai() {
        return persenSelesai;
    }

    public String getTotal() {
        return total;
    }

/*--------------------------di bawah khusus untuk urut angka----------------------------------*/

    public float getPersentaseBelum() {
        return Float.parseFloat(persenBelum);
    }
    public float getPersentaseProses() {
        return Float.parseFloat(persenProses);
    }
    public float getPersentaseSelesai() {
        return Float.parseFloat(persenSelesai);
    }

    public float getTotalfloat() {
        return Float.parseFloat(total);
    }

}
