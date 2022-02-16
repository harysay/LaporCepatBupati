package id.go.kebumenkab.laporcepatbupati.ui.statistik.jsontable.utils;

import id.go.kebumenkab.laporcepatbupati.ui.statistik.jsontable.model.Aduan;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortUtil implements Comparator<Aduan> {
    private static int SORT_FACTOR = -1;
    private static boolean sort_asc = true;

    public static void sortByKategori(List<Aduan> data) {
        if (SORT_FACTOR == 0 && sort_asc == false) {
            Collections.sort(data, Collections.reverseOrder(new SortUtil()));
            sort_asc = true;
        } else {
            SORT_FACTOR = 0;
            Collections.sort(data, new SortUtil());
            sort_asc = false;
        }
    }

    public static void sortBySkpd(List<Aduan> data) {
        if (SORT_FACTOR == 1 && sort_asc == false) {
            Collections.sort(data, Collections.reverseOrder(new SortUtil()));
            sort_asc = true;
        } else {
            SORT_FACTOR = 1;
            Collections.sort(data, new SortUtil());
            sort_asc = false;
        }
    }

    public static void sortByJmlBelum(List<Aduan> data) {
        if (SORT_FACTOR == 2 && sort_asc == false) {
            Collections.sort(data, Collections.reverseOrder(new SortUtil()));
            sort_asc = true;
        } else {
            SORT_FACTOR = 2;
            Collections.sort(data, new SortUtil());
            sort_asc = false;
        }
    }

    public static void sortByJmlProses(List<Aduan> data) {
        if (SORT_FACTOR == 3 && sort_asc == false) {
            Collections.sort(data, Collections.reverseOrder(new SortUtil()));
            sort_asc = true;
        } else {
            SORT_FACTOR = 3;
            Collections.sort(data, new SortUtil());
            sort_asc = false;
        }
    }
    public static void sortByJmlSelesai(List<Aduan> data) {
        if (SORT_FACTOR == 4 && sort_asc == false) {
            Collections.sort(data, Collections.reverseOrder(new SortUtil()));
            sort_asc = true;
        } else {
            SORT_FACTOR = 4;
            Collections.sort(data, new SortUtil());
            sort_asc = false;
        }
    }
//    public static void sortByPersenBlm(List<Aduan> data) {
//        if (SORT_FACTOR == 5 && sort_asc == false) {
//            Collections.sort(data, Collections.reverseOrder(new SortUtil()));
//            sort_asc = true;
//        } else {
//            SORT_FACTOR = 5;
//            Collections.sort(data, new SortUtil());
//            sort_asc = false;
//        }
//    }
//    public static void sortByPersenProses(List<Aduan> data) {
//        if (SORT_FACTOR == 6 && sort_asc == false) {
//            Collections.sort(data, Collections.reverseOrder(new SortUtil()));
//            sort_asc = true;
//        } else {
//            SORT_FACTOR = 6;
//            Collections.sort(data, new SortUtil());
//            sort_asc = false;
//        }
//    }
//    public static void sortByPersenSelesai(List<Aduan> data) {
//        if (SORT_FACTOR == 7 && sort_asc == false) {
//            Collections.sort(data, Collections.reverseOrder(new SortUtil()));
//            sort_asc = true;
//        } else {
//            SORT_FACTOR = 7;
//            Collections.sort(data, new SortUtil());
//            sort_asc = false;
//        }
//    }

    public static void sortByTotal(List<Aduan> data) {
        if (SORT_FACTOR == 5 && sort_asc == false) {
            Collections.sort(data, Collections.reverseOrder(new SortUtil()));
            sort_asc = true;
        } else {
            SORT_FACTOR = 5;
            Collections.sort(data, new SortUtil());
            sort_asc = false;
        }
    }

    @Override
    public int compare(Aduan a1, Aduan a2) {
        switch (SORT_FACTOR) {
            case 0:
            default:
                return a1.getnamaKategori().compareToIgnoreCase(a2.getnamaKategori());
            case 1:
                return a1.getnamaSkpd().compareToIgnoreCase(a2.getnamaSkpd());
            case 2:
                return a1.getjumlAduanBelum().compareToIgnoreCase(a2.getjumlAduanBelum());
            case 3:
                return a1.getjumlAduanProses().compareToIgnoreCase(a2.getjumlAduanProses());
            case 4:
                return a1.getJumlAduanSelesai().compareToIgnoreCase(a2.getJumlAduanSelesai());
//            case 5:
////                return a1.getPersenBelum().compareToIgnoreCase(a2.getPersenBelum());
//                return Float.compare(a1.getPersentaseBelum(),a2.getPersentaseBelum());
//            case 6:
////                return a1.getPersenProses().compareToIgnoreCase(a2.getPersenProses());
//                return Float.compare(a1.getPersentaseProses(),a2.getPersentaseProses());
//            case 7:
////                return a1.getPersenSelesai().compareToIgnoreCase(a2.getPersenSelesai());
//                return Float.compare(a1.getPersentaseSelesai(),a2.getPersentaseSelesai());
            case 5:
                return Float.compare(a1.getTotalfloat(),a2.getTotalfloat());
        }
    }
}
