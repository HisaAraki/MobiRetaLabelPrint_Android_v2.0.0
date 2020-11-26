package com.mobireta.mobiretalabelprint;

public class JancodeCheck {

    private String data1;
    private String data2;
    private String data3;
    private String data4;
    private String data5;
    private String data6;
    private String data7;
    private String data8;
    private String data9;
    private String data10;
    private String data11;
    private String data12;
    private String data13;

    public JancodeCheck(){ }

    public Boolean checkjan13(String barcode)
    {
        for(int i = 0 ; i < 13 ;i++){
            switch(i) {
                case 0:
                    data1 = barcode.substring(i, i + 1);
                    break;
                case 1:
                    data2 = barcode.substring( i, i + 1);
                    break;
                case 2:
                    data3 = barcode.substring( i, i + 1);
                    break;
                case 3:
                    data4 = barcode.substring( i, i + 1);
                    break;
                case 4:
                    data5 = barcode.substring( i, i + 1);
                    break;
                case 5:
                    data6 = barcode.substring( i, i + 1);
                    break;
                case 6:
                    data7 = barcode.substring( i, i + 1);
                    break;
                case 7:
                    data8 = barcode.substring( i, i + 1);
                    break;
                case 8:
                    data9 = barcode.substring( i, i + 1);
                    break;
                case 9:
                    data10 = barcode.substring( i, i + 1);
                    break;
                case 10:
                    data11 = barcode.substring( i, i + 1);
                    break;
                case 11:
                    data12 = barcode.substring( i, i + 1);
                    break;
                case 12:
                    data13 = barcode.substring( i, i + 1);
                    break;
            }
        }
        int kisu = Integer.parseInt(data1) + Integer.parseInt(data3) + Integer.parseInt(data5)
                + Integer.parseInt(data7) + Integer.parseInt(data9) + Integer.parseInt(data11);
        int gusu = (Integer.parseInt(data2) + Integer.parseInt(data4) + Integer.parseInt(data6)
                + Integer.parseInt(data8) + Integer.parseInt(data10) + Integer.parseInt(data12)) * 3;
        int gokei = kisu + gusu;
        int ketasu = String.valueOf(gokei).toString().length();
        String taishocd = String.valueOf(gokei).toString().substring(ketasu - 1, ketasu);
        String cd = String.valueOf(10 - Integer.parseInt(taishocd));
        if (cd.equals("10")){
            cd = "0";
        }
        if (data13.equals(cd)){
            return true;
        }
        else {
            return false;
        }
    }

    public Boolean checkjan8(String barcode)
    {
        data1 = "0";
        data2 = "0";
        data3 = "0";
        data4 = "0";
        data5 = "0";
        for(int i = 0 ; i < 8 ; i++){
            switch(i) {
                case 0:
                    data6 = barcode.substring(i, i + 1);
                    break;
                case 1:
                    data7 = barcode.substring( i, i + 1);
                    break;
                case 2:
                    data8 = barcode.substring( i, i + 1);
                    break;
                case 3:
                    data9 = barcode.substring( i, i + 1);
                    break;
                case 4:
                    data10 = barcode.substring( i, i + 1);
                    break;
                case 5:
                    data11 = barcode.substring( i, i + 1);
                    break;
                case 6:
                    data12 = barcode.substring( i, i + 1);
                    break;
                case 7:
                    data13 = barcode.substring( i, i + 1);
                    break;
            }
        }
        int kisu = Integer.parseInt(data1) + Integer.parseInt(data3) + Integer.parseInt(data5)
                + Integer.parseInt(data7) + Integer.parseInt(data9) + Integer.parseInt(data11);
        int gusu = (Integer.parseInt(data2) + Integer.parseInt(data4) + Integer.parseInt(data6)
                + Integer.parseInt(data8) + Integer.parseInt(data10) + Integer.parseInt(data12)) * 3;
        int gokei = kisu + gusu;
        int ketasu = String.valueOf(gokei).toString().length();
        String taishocd = String.valueOf(gokei).toString().substring(ketasu - 1, ketasu);
        String cd = String.valueOf(10 - Integer.parseInt(taishocd));
        if (cd.equals("10")){
            cd = "0";
        }
        if (data13.equals(cd)){
            return true;
        }
        else {
            return false;
        }
    }

    public Boolean checkupca(String barcode)
    {
        for(int i = 1 ; i < 13 ;i++){
            switch(i) {
                case 1:
                    data1 = barcode.substring(i, i + 1);
                    break;
                case 2:
                    data2 = barcode.substring( i, i + 1);
                    break;
                case 3:
                    data3 = barcode.substring( i, i + 1);
                    break;
                case 4:
                    data4 = barcode.substring( i, i + 1);
                    break;
                case 5:
                    data5 = barcode.substring( i, i + 1);
                    break;
                case 6:
                    data6 = barcode.substring( i, i + 1);
                    break;
                case 7:
                    data7 = barcode.substring( i, i + 1);
                    break;
                case 8:
                    data8 = barcode.substring( i, i + 1);
                    break;
                case 9:
                    data9 = barcode.substring( i, i + 1);
                    break;
                case 10:
                    data10 = barcode.substring( i, i + 1);
                    break;
                case 11:
                    data11 = barcode.substring( i, i + 1);
                    break;
                case 12:
                    data12 = barcode.substring( i, i + 1);
                    break;
            }
        }
        int kisu = (Integer.parseInt(data1) + Integer.parseInt(data3) + Integer.parseInt(data5)
                + Integer.parseInt(data7) + Integer.parseInt(data9) + Integer.parseInt(data11)) * 3;
        int gusu = (Integer.parseInt(data2) + Integer.parseInt(data4) + Integer.parseInt(data6)
                + Integer.parseInt(data8) + Integer.parseInt(data10));
        int gokei = kisu + gusu;
        int ketasu = String.valueOf(gokei).toString().length();
        String taishocd = String.valueOf(gokei).toString().substring(ketasu - 1, ketasu);
        String cd = String.valueOf(10 - Integer.parseInt(taishocd));
        if (cd.equals("10")){
            cd = "0";
        }
        if (data12.equals(cd)){
            return true;
        }
        else {
            return false;
        }
    }

    public String  makeEAN128(String jancode, String shorikbn, String Shoridata)
    {
        String CODE128 = "";
        if (jancode.length() == 8){
            data1 = "0";
            data2 = "0";
            data3 = "0";
            data4 = "0";
            data5 = "0";
            for(int i = 0 ; i < 8 ; i++){
                switch(i) {
                    case 0:
                        data6 = jancode.substring(i, i + 1);
                        break;
                    case 1:
                        data7 = jancode.substring( i, i + 1);
                        break;
                    case 2:
                        data8 = jancode.substring( i, i + 1);
                        break;
                    case 3:
                        data9 = jancode.substring( i, i + 1);
                        break;
                    case 4:
                        data10 = jancode.substring( i, i + 1);
                        break;
                    case 5:
                        data11 = jancode.substring( i, i + 1);
                        break;
                    case 6:
                        data12 = jancode.substring( i, i + 1);
                        break;
                    case 7:
                        data13 = jancode.substring( i, i + 1);
                        break;
                }
            }
            CODE128 = "00000" + jancode + shorikbn + String.format("%05d" , Integer.parseInt(Shoridata));
        }else{
            for(int i = 0 ; i < 13 ;i++){
                switch(i) {
                    case 0:
                        data1 = jancode.substring(i, i + 1);
                        break;
                    case 1:
                        data2 = jancode.substring( i, i + 1);
                        break;
                    case 2:
                        data3 = jancode.substring( i, i + 1);
                        break;
                    case 3:
                        data4 = jancode.substring( i, i + 1);
                        break;
                    case 4:
                        data5 = jancode.substring( i, i + 1);
                        break;
                    case 5:
                        data6 = jancode.substring( i, i + 1);
                        break;
                    case 6:
                        data7 = jancode.substring( i, i + 1);
                        break;
                    case 7:
                        data8 = jancode.substring( i, i + 1);
                        break;
                    case 8:
                        data9 = jancode.substring( i, i + 1);
                        break;
                    case 9:
                        data10 = jancode.substring( i, i + 1);
                        break;
                    case 10:
                        data11 = jancode.substring( i, i + 1);
                        break;
                    case 11:
                        data12 = jancode.substring( i, i + 1);
                        break;
                    case 12:
                        data13 = jancode.substring( i, i + 1);
                        break;
                }
            }
            CODE128 = jancode + shorikbn + String.format("%05d" , Integer.parseInt(Shoridata));
        }
        int kisu = Integer.parseInt(data1) + Integer.parseInt(data3) + Integer.parseInt(data5)
                + Integer.parseInt(data7) + Integer.parseInt(data9) + Integer.parseInt(data11);
        int gusu = (Integer.parseInt(data2) + Integer.parseInt(data4) + Integer.parseInt(data6)
                + Integer.parseInt(data8) + Integer.parseInt(data10) + Integer.parseInt(data12)) * 3;
        int gokei = kisu + gusu;
        int ketasu = String.valueOf(gokei).toString().length();
        String taishocd = String.valueOf(gokei).toString().substring(ketasu - 1, ketasu);
        String cd = String.valueOf(10 - Integer.parseInt(taishocd));
        if (cd.equals("10")){
            cd = "0";
        }
        CODE128= CODE128 + cd;
        return CODE128;
    }
}
