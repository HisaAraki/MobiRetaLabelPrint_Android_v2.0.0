package com.mobireta.mobiretalabelprint;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;
import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.security.AccessController.getContext;


public class InitializeConfig extends LicenseActivity {

    public static Integer Initialize(Context context){
        //日付作成
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        DateFormat df1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date1 = new Date(System.currentTimeMillis());
        //データベース作成
        MsControl mscontrol = new MsControl();
        MsControlHelper mscontrolhelper = new MsControlHelper();
        MsUser msu = new MsUser();
        mscontrol = mscontrolhelper.finddata(context);
        if (mscontrol.Tenpocd == null)
        {
            MsControl msc = new MsControl();
            msc.Kanriid = df.format(date);
            msc.Kanrikbn = "";
            msc.Tourokuid = "";
            msc.Tenpocd = "000000";
            msc.Mobilecd = "0000";
            msc.Kaishakj = "";
            msc.Zipcd = "";
            msc.Address1 = "";
            msc.Address2 = "";
            msc.Tel = "";
            msc.Fax = "";
            msc.Readerkbn = "0";
            msc.Readernm = "";
            msc.Readeraddress = "";
            msc.Keyboardkbn = "0";
            msc.Printerkbn = "0";
            msc.Printernm = "";
            msc.Printeraddress = "";
            msc.Nefudaformat = "0";
            msc.Shomiformat = "0";
            msc.Nebikiformat = "0";
            msc.Barcode128 = "0";
            msc.Maxmaisu = "30";
            msc.Addymd = df.format(date1);
            msc.Updymd = df1.format(date1);
            MsControlHelper.insert(context, msc);
        }
        //ライセンスファイル作成
        String filepath = context.getFilesDir().getAbsolutePath() + "/";
        String fileName = context.getString(R.string.permitelicense);
        String str = "Your License Permit Date : " + df1.format(date1) + "\r\n";
        File file = new File(fileName);
        saveFile(context, fileName, str);
        //プリンタファイルデータ
        //テストプリント用
        fileName = "S3T01.TXT";
        file = new File(fileName);
        String printdata ="";
        if(!file.exists()) {
            // 存在しない
            printdata = "V20^H50^P0^L0000^K9Dモバリテ テストプリント" + "\r\n";
            printdata = printdata + "#TV50^H70^P2^L0101^K8D" + "\r\n";
            printdata = printdata + "#DV70^H70^P2^L0101^K8D" + "\r\n";
            printdata = printdata + "#AV90^H70^P2^L0101^K8D" + "\r\n";
            printdata = printdata + "V110^H100^BG02075>I0654145566" + "\r\n";
            saveFile(context, fileName, printdata);
        }
        fileName = "S4T01.TXT";
        if(!file.exists()) {
            // 存在しない
            printdata = "V10^H50^P0^L0000^K9Dモバリテ テストプリント" + "\r\n";
            printdata = printdata + "#TV40^H70^P2^L0101^K8D" + "\r\n";
            printdata = printdata + "#DV60^H70^P2^L0101^K8D" + "\r\n";
            printdata = printdata + "#AV80^H70^P2^L0101^K8D" + "\r\n";
            printdata = printdata + "V110^H110^BG02075>I0654145566" + "\r\n";
            saveFile(context, fileName, printdata);
        }
        fileName = "TET01.TXT";
        if(!file.exists()) {
            // 存在しない
            printdata = "D0282,0560,0250" + "\r\n";
            printdata = printdata + "D0300,0580,0300" + "\r\n";
            printdata = printdata + "AX;+000,+000,+00" + "\r\n";
            printdata = printdata + "AY;+00,1" + "\r\n";
            printdata = printdata + "C" + "\r\n";
            printdata = printdata + "PC00;0040,0040,10,1,V,00,B,J0202,P1=モバリテ テストプリント" + "\r\n";
            printdata = printdata + "#TPC01;0060,0075,10,1,V,00,B,J0000,P1=" + "\r\n";
            printdata = printdata + "#DPC02;0060,0105,10,1,V,00,B,J0000,P1=" + "\r\n";
            printdata = printdata + "#APC03;0060,0135,10,1,V,00,B,J0000,P1=" + "\r\n";
            printdata = printdata + "XB03;0100,0145,9,3,02,0,0075,+0000000000,010,0,00=0354145566" + "\r\n";
            saveFile(context, fileName, printdata);
        }
        fileName = "TLT01.TXT";
        if(!file.exists()) {
            // 存在しない
            printdata = "D0282,0560,0250" + "\r\n";
            printdata = printdata + "D0300,0580,0300" + "\r\n";
            printdata = printdata + "AX;+000,+000,+00" + "\r\n";
            printdata = printdata + "AY;+00,1" + "\r\n";
            printdata = printdata + "C" + "\r\n";
            printdata = printdata + "PC00;0040,0040,10,1,V,00,B,J0202,P1=モバリテ テストプリント" + "\r\n";
            printdata = printdata + "#TPC01;0060,0075,10,1,V,00,B,J0000,P1=" + "\r\n";
            printdata = printdata + "#DPC02;0060,0105,10,1,V,00,B,J0000,P1=" + "\r\n";
            printdata = printdata + "#APC03;0060,0135,10,1,V,00,B,J0000,P1=" + "\r\n";
            printdata = printdata + "XB03;0100,0145,9,3,02,0,0075,+0000000000,010,0,00=0354145566" + "\r\n";
            saveFile(context, fileName, printdata);
        }
        //値札プリント用
        fileName = "S3F01.TXT";
        if(!file.exists()) {
            // 存在しない
            printdata = "#HV30^H00^P0^L0002^K9D" + "\r\n";
            printdata = printdata + "V150^H250^P0^L0000^K2B（本体価格）" + "\r\n";
            printdata = printdata + "PS" + "\r\n";
            printdata = printdata + "#PV90^H222^P0^L0304^X21," + "\r\n";
            printdata = printdata + "V110^H350^P0^L0202^K8D円" + "\r\n";
            printdata = printdata + "#BV85^H10^B302070" + "\r\n"; //初期は、JAN13
            printdata = printdata + "#BV163^H25^P2^L0000^X20," + "\r\n";
            saveFile(context, fileName, printdata);
        }
        fileName = "S4F01.TXT";
        if(!file.exists()) {
            // 存在しない
            printdata = "#HV30^H10^P0^L0002^K9D" + "\r\n";
            printdata = printdata + "V150^H250^P0^L0000^K2B（本体価格）" + "\r\n";
            printdata = printdata + "PS" + "\r\n";
            printdata = printdata + "#PV75^H230^P2^$A,65,75,0^$=" + "\r\n";
            printdata = printdata + "V110^H390^P0^L0202^K8D円" + "\r\n";
            printdata = printdata + "#BV85^H10^B302070" + "\r\n"; //初期は、JAN13
            printdata = printdata + "#BV163^H25^P2^L0000^X20," + "\r\n";
            saveFile(context, fileName, printdata);
        }
        fileName = "TEF01.TXT";
        if(!file.exists()) {
            // 存在しない
            printdata = "D0282,0560,0250" + "\r\n";
            printdata = printdata + "D0300,0580,0300" + "\r\n";
            printdata = printdata + "AX;+000,+000,+00" + "\r\n";
            printdata = printdata + "AY;+00,1" + "\r\n";
            printdata = printdata + "C" + "\r\n";
            printdata = printdata + "#HPC00;0005,0070,1,15,V,00,B,J0101,P1=" + "\r\n";
            printdata = printdata + "PC01;0440,0170,20,25,U,00,B,J0202,P1=円" + "\r\n";
            printdata = printdata + "PC02;0350,0200,10,10,U,00,B,J0101,P1=(本体価格)" + "\r\n";
            printdata = printdata + "#PPV00;0275,0170,0050,0070,A,-015,00,B=" + "\r\n";
            printdata = printdata + "#BXB00;0030,0100,5,3,02,0,0090,+0000000000,020,1,00=" + "\r\n"; //初期値JAN13
            saveFile(context, fileName, printdata);
        }
        fileName = "TLF01.TXT";
        if(!file.exists()) {
            // 存在しない
            printdata = "D0282,0560,0250" + "\r\n";
            printdata = printdata + "D0300,0580,0300" + "\r\n";
            printdata = printdata + "AX;+000,+000,+00" + "\r\n";
            printdata = printdata + "AY;+00,1" + "\r\n";
            printdata = printdata + "C" + "\r\n";
            printdata = printdata + "#HPC00;0025,0050,1,15,V,00,B,J0101,P1=" + "\r\n";
            printdata = printdata + "PC01;0460,0150,20,25,U,00,B,J0202,P1=円" + "\r\n";
            printdata = printdata + "PC02;0370,0180,10,10,U,00,B,J0101,P1=(本体価格)" + "\r\n";
            printdata = printdata + "#PPV00;0295,0150,0050,0070,A,-015,00,B=" + "\r\n";
            printdata = printdata + "#BXB00;0050,0080,5,3,02,0,0090,+0000000000,020,1,00=" + "\r\n"; //初期値JAN13
            saveFile(context, fileName, printdata);
        }
        //賞味期限プリント用
        fileName = "S3S01.TXT";
        if(!file.exists()) {
            // 存在しない
            printdata = "#HV30^H10^P0^L0102^K9D" + "\r\n";
            printdata = printdata + "PS" + "\r\n";
            printdata = printdata + "V100^H050^P0^L0001^K8D賞味期限" + "\r\n";
            printdata = printdata + "#MV100^H200^P0^L0102^K8D" + "\r\n";
            printdata = printdata + "#DV150^H050^P0^L0001^K8D製造月日" + "\r\n";
            printdata = printdata + "#ZV150^H200^P0^L0102^K8D" + "\r\n";
            saveFile(context, fileName, printdata);
        }
        fileName = "S4S01.TXT";
        if(!file.exists()) {
            // 存在しない
            printdata = "#HV30^H10^P0^L0102^K9D" + "\r\n";
            printdata = printdata + "PS" + "\r\n";
            printdata = printdata + "V090^H050^P0^L0001^K8D賞味期限" + "\r\n";
            printdata = printdata + "#MV090^H200^P0^L0102^K8D" + "\r\n";
            printdata = printdata + "#DV140^H050^P0^L0001^K8D製造月日" + "\r\n";
            printdata = printdata + "#ZV140^H200^P0^L0102^K8D" + "\r\n";
            saveFile(context, fileName, printdata);
        }
        fileName = "TES01.TXT";
        if(!file.exists()) {
            // 存在しない
            printdata = "D0282,0560,0250" + "\r\n";
            printdata = printdata + "D0300,0580,0300" + "\r\n";
            printdata = printdata + "AX;+000,+000,+00" + "\r\n";
            printdata = printdata + "AY;+00,1" + "\r\n";
            printdata = printdata + "C" + "\r\n";
            printdata = printdata + "#HPC00;0005,0070,1,15,V,00,B,J0101,P1=" + "\r\n";
            printdata = printdata + "PC01;0005,0140,1,10,V,00,B,J0101,P1=賞味期限" + "\r\n";
            printdata = printdata + "#MPC02;0180,0140,1,10,V,00,B,J0101,P1=" + "\r\n";
            printdata = printdata + "#DPC03;0005,0200,1,10,V,00,B,J0101,P1=製造月日" + "\r\n";
            printdata = printdata + "#ZPC04;0180,0200,1,10,V,00,B,J0101,P1=" + "\r\n";
            saveFile(context, fileName, printdata);
        }
        fileName = "TLS01.TXT";
        if(!file.exists()) {
            // 存在しない
            printdata = "D0282,0560,0250" + "\r\n";
            printdata = printdata + "D0300,0580,0300" + "\r\n";
            printdata = printdata + "AX;+000,+000,+00" + "\r\n";
            printdata = printdata + "AY;+00,1" + "\r\n";
            printdata = printdata + "C" + "\r\n";
            printdata = printdata + "#HPC00;0035,0070,1,15,V,00,B,J0101,P1=" + "\r\n";
            printdata = printdata + "PC01;0035,0140,1,10,V,00,B,J0101,P1=賞味期限" + "\r\n";
            printdata = printdata + "#MPC02;0210,0140,1,10,V,00,B,J0101,P1=" + "\r\n";
            printdata = printdata + "#DPC03;0035,0200,1,10,V,00,B,J0101,P1=製造月日" + "\r\n";
            printdata = printdata + "#ZPC04;0210,0200,1,10,V,00,B,J0101,P1=" + "\r\n";
            saveFile(context, fileName, printdata);
        }
        //フォーム０
        //値引プリント用
        fileName = "S3B01.TXT";
        if(!file.exists()) {
            // 存在しない
            printdata = "V20^H40^P0^L0201^K8Dレジにて" + "\r\n";
            printdata = printdata + "PS" + "\r\n";
            printdata = printdata + "#SV30^H45^P0^L0102^X22," + "\r\n";
            //printdata = printdata + "V40^H120^P0^L0102^K8D円を" + "\r\n";
            printdata = printdata + "#TV20^H170^P0^L0203^X22," + "\r\n";
            printdata = printdata + "V50^H305^P0^L0202^K8D円引" + "\r\n";
            printdata = printdata + "#BV100^H55^BG02065>I" + "\r\n";
            printdata = printdata + "#BV170^H65^P2^L0202^X20," + "\r\n";
            saveFile(context, fileName, printdata);
        }
        fileName = "S4B01.TXT";
        if(!file.exists()) {
            // 存在しない
            printdata = "V05^H50^P0^L0201^K8Dレジにて" + "\r\n";
            printdata = printdata + "PS" + "\r\n";
            printdata = printdata + "#SV30^H50^P3^$A,40,40,0^$=" + "\r\n";
            printdata = printdata + "V35^H135^P0^L0102^K8D円を" + "\r\n";
            printdata = printdata + "PS" + "\r\n";
            printdata = printdata + "#TV10^H180^P2^$A,75,85,0^$=" + "\r\n";
            printdata = printdata + "V35^H320^P0^L0203^K8D円引" + "\r\n";
            printdata = printdata + "#BV90^H45^BG02065>I" + "\r\n";
            printdata = printdata + "#BV165^H55^P2^L0202^X20," + "\r\n";
            saveFile(context, fileName, printdata);
        }
        fileName = "TEB01.TXT";
        if(!file.exists()) {
            // 存在しない
            printdata = "D0282,0560,0250" + "\r\n";
            printdata = printdata + "D0300,0580,0300" + "\r\n";
            printdata = printdata + "AX;+000,+000,+00" + "\r\n";
            printdata = printdata + "AY;+00,1" + "\r\n";
            printdata = printdata + "C" + "\r\n";
            printdata = printdata + "PC00;0035,0035,15,1,V,00,B,J0101,P1=レジにて" + "\r\n";
            printdata = printdata + "#SPC01;0035,0070,15,1,V,00,B,J0101,P1=" + "\r\n";
            printdata = printdata + "#TPV00;0205,0090,0060,0090,A,-015,00,B=" + "\r\n";
            printdata = printdata + "PC02;0380,0090,20,25,U,00,B,J0202,P1=円引" + "\r\n";
            printdata = printdata + "#BXB00;0055,0110,9,3,02,0,0080,+0000000000,010,1,00=" + "\r\n";
            saveFile(context, fileName, printdata);
        }
        fileName = "TLB01.TXT";
        if(!file.exists()) {
            // 存在しない
            printdata = "D0282,0560,0250" + "\r\n";
            printdata = printdata + "D0300,0580,0300" + "\r\n";
            printdata = printdata + "AX;+000,+000,+00" + "\r\n";
            printdata = printdata + "AY;+00,1" + "\r\n";
            printdata = printdata + "C" + "\r\n";
            printdata = printdata + "PC00;0055,0035,15,1,V,00,B,J0101,P1=レジにて" + "\r\n";
            printdata = printdata + "#SPC01;0065,0070,15,1,V,00,B,J0101,P1=" + "\r\n";
            printdata = printdata + "#TPV00;0235,0090,0060,0090,A,-015,00,B=" + "\r\n";
            printdata = printdata + "PC02;0405,0090,20,25,U,00,B,J0202,P1=円引" + "\r\n";
            printdata = printdata + "#BXB00;0085,0110,9,3,02,0,0080,+0000000000,010,1,00=" + "\r\n";
            saveFile(context, fileName, printdata);
        }
        //割引プリント用
        fileName = "S3W01.TXT";
        if(!file.exists()) {
            // 存在しない
            printdata = "V20^H40^P0^L0201^K8Dレジにて" + "\r\n";
            printdata = printdata + "PS" + "\r\n";
            printdata = printdata + "#SV30^H45^P0^L0102^X22," + "\r\n";
            //printdata = printdata + "V40^H120^P0^L0102^K8D円を" + "\r\n";
            printdata = printdata + "#TV20^H220^P0^L0203^X22," + "\r\n";
            printdata = printdata + "V50^H300^P0^L0202^K8D％引" + "\r\n";
            printdata = printdata + "#BV100^H55^BG02065>I" + "\r\n";
            printdata = printdata + "#BV170^H65^P2^L0202^X20," + "\r\n";
            saveFile(context, fileName, printdata);
        }
        fileName = "S4W01.TXT";
        if(!file.exists()) {
            // 存在しない
            printdata = "V05^H50^P0^L0201^K8Dレジにて" + "\r\n";
            printdata = printdata + "PS" + "\r\n";
            printdata = printdata + "#SV30^H50^P3^$A,40,40,0^$=" + "\r\n";
            printdata = printdata + "V35^H135^P0^L0102^K8D円を" + "^";
            printdata = printdata + "PS" + "\r\n";
            printdata = printdata + "#TV10^H245^P2^$A,75,85,0^$=" + "\r\n";
            printdata = printdata + "V35^H320^P0^L0203^K8D％引" + "\r\n";
            printdata = printdata + "#BV90^H45^BG02065>I" + "\r\n";
            printdata = printdata + "#BV165^H55^P2^L0202^X20," + "\r\n";
            saveFile(context, fileName, printdata);
        }
        fileName = "TEW01.TXT";
        if(!file.exists()) {
            // 存在しない
            printdata = "D0282,0560,0250" + "\r\n";
            printdata = printdata + "D0300,0580,0300" + "\r\n";
            printdata = printdata + "AX;+000,+000,+00" + "\r\n";
            printdata = printdata + "AY;+00,1" + "\r\n";
            printdata = printdata + "C" + "\r\n";
            printdata = printdata + "PC00;0035,0035,15,1,V,00,B,J0101,P1=レジにて" + "\r\n";
            printdata = printdata + "#SPC01;0035,0070,15,1,V,00,B,J0101,P1=" + "\r\n";
            printdata = printdata + "#TPV00;0270,0090,0060,0090,A,-015,00,B=" + "\r\n";
            printdata = printdata + "PC03;0375,0090,20,25,U,00,B,J0202,P1=％引" + "\r\n";
            printdata = printdata + "#BXB00;0055,0110,9,3,02,0,0080,+0000000000,010,1,00=" + "\r\n";
            saveFile(context, fileName, printdata);
        }
        fileName = "TLW01.TXT";
        if(!file.exists()) {
            // 存在しない
            printdata = "D0282,0560,0250" + "\r\n";
            printdata = printdata + "D0300,0580,0300" + "\r\n";
            printdata = printdata + "AX;+000,+000,+00" + "\r\n";
            printdata = printdata + "AY;+00,1" + "\r\n";
            printdata = printdata + "C" + "\r\n";
            printdata = printdata + "PC00;0055,0035,15,1,V,00,B,J0101,P1=レジにて" + "\r\n";
            printdata = printdata + "#SPC01;0065,0070,15,1,V,00,B,J0101,P1=" + "\r\n";
            printdata = printdata + "#TPV00;0305,0090,0060,0090,A,-015,00,B=" + "\r\n";
            printdata = printdata + "PC03;0395,0090,20,25,U,00,B,J0202,P1=％引" + "\r\n";
            printdata = printdata + "#BXB00;0070,0100,9,3,02,0,0080,+0000000000,010,1,00=" + "\r\n";
            saveFile(context, fileName, printdata);
        }
        //値下シールド
        fileName = "S3G01.TXT";
        if(!file.exists()) {
            // 存在しない
            printdata = "V20^H40^P0^L0201^K8Dレジにて" + "\r\n";
            printdata = printdata + "PS" + "\r\n";
            printdata = printdata + "#SV30^H45^P0^L0102^X22," + "\r\n";
            //printdata = printdata + "V40^H120^P0^L0102^K8D円を" + "\r\n";
            printdata = printdata + "#PV20^H170^P0^L0203^X22," + "\r\n";
            printdata = printdata + "V50^H320^P0^L0202^K8D円" + "\r\n";
            printdata = printdata + "#BV100^H55^BG02065>I" + "\r\n";
            printdata = printdata + "#BV170^H65^P2^L0202^X20," + "\r\n";
            saveFile(context, fileName, printdata);
        }
        fileName = "S4G01.TXT";
        if(!file.exists()) {
            // 存在しない
            printdata = "V05^H50^P0^L0201^K8Dレジにて" + "\r\n";
            printdata = printdata + "PS" + "\r\n";
            printdata = printdata + "#SV30^H50^P3^$A,40,40,0^$=" + "\r\n";
            printdata = printdata + "V35^H135^P0^L0102^K8D円を" + "\r\n";
            printdata = printdata + "PS" + "\r\n";
            printdata = printdata + "#PV10^H190^P2^$A,75,85,0^$=" + "\r\n";
            printdata = printdata + "V35^H330^P0^L0203^K8D円" + "\r\n";
            printdata = printdata + "#BV90^H45^BG02065>I" + "\r\n";
            printdata = printdata + "#BV165^H55^P2^L0202^X20," + "\r\n";
            saveFile(context, fileName, printdata);
        }
        fileName = "TEG01.TXT";
        if(!file.exists()) {
            // 存在しない
            printdata = "D0282,0560,0250" + "\r\n";
            printdata = printdata + "D0300,0580,0300" + "\r\n";
            printdata = printdata + "AX;+000,+000,+00" + "\r\n";
            printdata = printdata + "AY;+00,1" + "\r\n";
            printdata = printdata + "C" + "\r\n";
            printdata = printdata + "PC00;0035,0035,15,1,V,00,B,J0101,P1=レジにて" + "\r\n";
            printdata = printdata + "#SPC01;0035,0070,15,1,V,00,B,J0101,P1=" + "\r\n";
            printdata = printdata + "#PPV00;0210,0090,0060,0090,A,-015,00,B=" + "\r\n";
            printdata = printdata + "PC02;0385,0090,20,25,U,00,B,J0202,P1=円" + "\r\n";
            printdata = printdata + "#BXB00;0055,0110,9,3,02,0,0080,+0000000000,010,1,00=" + "\r\n";
            saveFile(context, fileName, printdata);
        }
        fileName = "TLG01.TXT";
        if(!file.exists()) {
            // 存在しない
            printdata = "D0282,0560,0250" + "\r\n";
            printdata = printdata + "D0300,0580,0300" + "\r\n";
            printdata = printdata + "AX;+000,+000,+00" + "\r\n";
            printdata = printdata + "AY;+00,1" + "\r\n";
            printdata = printdata + "C" + "\r\n";
            printdata = printdata + "PC00;0065,0035,15,1,V,00,B,J0101,P1=レジにて" + "\r\n";
            printdata = printdata + "#SPC01;0065,0070,15,1,V,00,B,J0101,P1=" + "\r\n";
            printdata = printdata + "#PPV00;0250,0090,0060,0090,A,-015,00,B=" + "\r\n";
            printdata = printdata + "PC02;0420,0090,20,25,U,00,B,J0202,P1=円" + "\r\n";
            printdata = printdata + "#BXB00;0085,0110,9,3,02,0,0080,+0000000000,010,1,00=" + "\r\n";
            saveFile(context, fileName, printdata);
        }
        //フォーム１//////////////////////////////////////////////////
        //値引プリント用
        fileName = "S3B02.TXT";
        if(!file.exists()) {
            // 存在しない
            printdata = "V05^H10^P0^L0101^K8B標準価格" + "\r\n";
            printdata = printdata + "PS" + "\r\n";
            printdata = printdata + "#SV20^H010^P0^L0000^X22," + "\r\n";
            printdata = printdata + "V25^H90^P0^L0000^K8D円より" + "\r\n";
            printdata = printdata + "PS" + "\r\n";
            printdata = printdata + "#TV40^H10^P1^L0304^X21," + "\r\n";
            printdata = printdata + "V70^H130^P0^L0102^K8D円引" + "\r\n";
            printdata = printdata + "V05^H200^P0^L0101^K9B値引後価格" + "\r\n";
            printdata = printdata + "PS" + "\r\n";
            printdata = printdata + "#PV30^H200^P1^L0304^X21," + "\r\n";
            printdata = printdata + "V60^H315^P0^L0102^K8D円" + "\r\n";
            printdata = printdata + "#BV110^H45^BG02065>I" + "\r\n";
            printdata = printdata + "#BV180^H55^P2^L0202^X20," + "\r\n";
            saveFile(context, fileName, printdata);
        }
        fileName = "S4B02.TXT";
        if(!file.exists()) {
            // 存在しない
            printdata = "V00^H00^P0^L0101^K8B標準価格" + "\r\n";
            printdata = printdata + "PS" + "\r\n";
            printdata = printdata + "#SV10^H15^P3^$A,30,40,0^$=" + "\r\n";
            printdata = printdata + "V25^H080^P0^L0101^K8D円より" + "\r\n";
            printdata = printdata + "PS" + "\r\n";
            printdata = printdata + "#TV35^H10^P2^$A,65,75,0^$=" + "\r\n";
            printdata = printdata + "V65^H135^P0^L0102^K8D円引" + "\r\n";
            printdata = printdata + "V00^H190^P0^L0101^K9B値引後価格" + "\r\n";
            printdata = printdata + "PS" + "\r\n";
            printdata = printdata + "#PV20^H195^P2^$A,65,75,0^$=" + "\r\n";
            printdata = printdata + "V50^H320^P0^L0102^K8D円" + "\r\n";
            printdata = printdata + "#BV105^H20^BG02065>I" + "\r\n";
            printdata = printdata + "#BV175^H30^P2^L0202^X20," + "\r\n";
            saveFile(context, fileName, printdata);
        }
        fileName = "TEB02.TXT";
        if(!file.exists()) {
            // 存在しない
            printdata = "D0282,0560,0250" + "\r\n";
            printdata = printdata + "D0300,0580,0300" + "\r\n";
            printdata = printdata + "AX;+000,+000,+00" + "\r\n";
            printdata = printdata + "AY;+00,1" + "\r\n";
            printdata = printdata + "C" + "\r\n";
            printdata = printdata + "PC00;0000,0030,10,10,V,-03,00,B,J0000,P1=表示価格" + "\r\n";
            printdata = printdata + "#SPV00;0035,0060,0030,0035,A,-010,00,B=" + "\r\n";
            printdata = printdata + "PC01;0110,0065,10,10,V,-05,00,B,J0000,P1=円より" + "\r\n";
            printdata = printdata + "#TPV01;0000,0115,0050,0060,A,-015,00,B=" + "\r\n";
            printdata = printdata + "PC03;0135,0115,10,10,V,-03,00,B,J0000,P1=円引" + "\r\n";
            printdata = printdata + "PC04;0230,0040,10,10,V,00,B,J0000,P1=値引後価格" + "\r\n";
            printdata = printdata + "#PPV02;0210,0100,0055,0065,A,-015,00,B=" + "\r\n";
            printdata = printdata + "PC05;0360,0100,10,10,V,00,B,J0101,P1=円" + "\r\n";
            printdata = printdata + "#BPV03;0030,0240,0023,0025,A,-005,00,B=" + "\r\n";
            printdata = printdata + "#BXB00;0015,0140,9,3,02,0,0075,+0000000000,010,0,00=" + "\r\n";
            saveFile(context, fileName, printdata);
        }
        fileName = "TLB02.TXT";
        if(!file.exists()) {
            // 存在しない
            printdata = "D0282,0560,0250" + "\r\n";
            printdata = printdata + "D0300,0580,0300" + "\r\n";
            printdata = printdata + "AX;+000,+000,+00" + "\r\n";
            printdata = printdata + "AY;+00,1" + "\r\n";
            printdata = printdata + "C" + "\r\n";
            printdata = printdata + "PC00;0025,0040,10,10,V,-03,00,B,J0000,P1=表示価格" + "\r\n";
            printdata = printdata + "#SPV00;0035,0070,0030,0035,A,-010,00,B=" + "\r\n";
            printdata = printdata + "PC01;0130,0075,10,10,V,-05,00,B,J0000,P1=円より" + "\r\n";
            printdata = printdata + "#TPV01;0025,0125,0050,0060,A,-015,00,B=" + "\r\n";
            printdata = printdata + "PC03;0160,0125,10,10,V,-03,00,B,J0000,P1=円引" + "\r\n";
            printdata = printdata + "PC04;0250,0040,10,10,V,00,B,J0000,P1=値引後価格" + "\r\n";
            printdata = printdata + "#PPV02;0230,0100,0055,0065,A,-015,00,B=" + "\r\n";
            printdata = printdata + "PC02;0390,0100,10,10,V,00,B,J0101,P1=円" + "\r\n";
            printdata = printdata + "#BPV03;0060,0235,0023,0025,A,-005,00,B=" + "\r\n";
            printdata = printdata + "#BXB00;0045,0135,9,3,02,0,0075,+0000000000,010,0,00=" + "\r\n";
            saveFile(context, fileName, printdata);
        }
        //割引プリント用
        fileName = "S3W02.TXT";
        if(!file.exists()) {
            // 存在しない
            printdata = "V05^H10^P0^L0101^K8B標準価格" + "\r\n";
            printdata = printdata + "PS" + "\r\n";
            printdata = printdata + "#SV25^H05^P0^L0000^X22," + "\r\n";
            printdata = printdata + "V30^H75^P0^L0000^K8D円より" + "\r\n";
            printdata = printdata + "PS" + "\r\n";
            printdata = printdata + "#TV40^H50^P1^L0304^X21," + "\r\n";
            printdata = printdata + "V70^H110^P0^L0102^K8D％引" + "\r\n";
            printdata = printdata + "V10^H190^P0^L0101^K9B割引後価格" + "\r\n";
            printdata = printdata + "PS" + "\r\n";
            printdata = printdata + "#PV30^H200^P1^L0304^X21," + "\r\n";
            printdata = printdata + "V60^H315^P0^L0102^K8D円" + "\r\n";
            printdata = printdata + "#BV110^H45^BG02065>I" + "\r\n";
            printdata = printdata + "#BV180^H55^P2^L0202^X20," + "\r\n";
            saveFile(context, fileName, printdata);
        }
        fileName = "S4W02.TXT";
        if(!file.exists()) {
            // 存在しない
            printdata = "V00^H00^P0^L0101^K8B標準価格" + "\r\n";
            printdata = printdata + "PS" + "\r\n";
            printdata = printdata + "#SV10^H15^P3^$A,30,40,0^$=" + "\r\n";
            printdata = printdata + "V25^H080^P0^L0101^K8D円より" + "\r\n";
            printdata = printdata + "PS" + "\r\n";
            printdata = printdata + "#TV35^H70^P2^$A,65,75,0^$=" + "\r\n";
            printdata = printdata + "V65^H135^P0^L0102^K8D％引" + "\r\n";
            printdata = printdata + "V00^H190^P0^L0101^K9B割引後価格" + "\r\n";
            printdata = printdata + "PS" + "\r\n";
            printdata = printdata + "#PV20^H195^P2^$A,65,75,0^$=" + "\r\n";
            printdata = printdata + "V50^H320^P0^L0102^K8D円" + "\r\n";
            printdata = printdata + "#BV105^H20^BG02065>I" + "\r\n";
            printdata = printdata + "#BV175^H30^P2^L0202^X20," + "\r\n";
            saveFile(context, fileName, printdata);
        }
        fileName = "TEW02.TXT";
        if(!file.exists()) {
            // 存在しない
            printdata = "D0282,0560,0250" + "\r\n";
            printdata = printdata + "D0300,0580,0300" + "\r\n";
            printdata = printdata + "AX;+000,+000,+00" + "\r\n";
            printdata = printdata + "AY;+00,1" + "\r\n";
            printdata = printdata + "C" + "\r\n";
            printdata = printdata + "PC00;0000,0030,10,10,V,-03,00,B,J0000,P1=表示価格" + "\r\n";
            printdata = printdata + "#SPV00;0035,0060,0030,0035,A,-010,00,B=" + "\r\n";
            printdata = printdata + "PC01;0110,0065,10,10,V,-05,00,B,J0000,P1=円より" + "\r\n";
            printdata = printdata + "#TPV01;0050,0115,0050,0060,A,-015,00,B=" + "\r\n";
            printdata = printdata + "PC03;0135,0115,10,10,V,-03,00,B,J0000,P1=％引" + "\r\n";
            printdata = printdata + "PC04;0230,0040,10,10,V,00,B,J0000,P1=割引後価格" + "\r\n";
            printdata = printdata + "#PPV02;0210,0100,0055,0065,A,-015,00,B=" + "\r\n";
            printdata = printdata + "PC05;0365,0100,10,10,V,00,B,J0101,P1=円" + "\r\n";
            printdata = printdata + "#BPV03;0030,0240,0023,0025,A,-005,00,B=" + "\r\n";
            printdata = printdata + "#BXB00;0015,0140,9,3,02,0,0075,+0000000000,010,0,00=" + "\r\n";
            saveFile(context, fileName, printdata);
        }
        fileName = "TLW02.TXT";
        if(!file.exists()) {
            // 存在しない
            printdata = "D0282,0560,0250" + "\r\n";
            printdata = printdata + "D0300,0580,0300" + "\r\n";
            printdata = printdata + "AX;+000,+000,+00" + "\r\n";
            printdata = printdata + "AY;+00,1" + "\r\n";
            printdata = printdata + "C" + "\r\n";
            printdata = printdata + "PC00;0025,0040,10,10,V,-03,00,B,J0000,P1=表示価格" + "\r\n";
            printdata = printdata + "#SPV00;0030,0070,0030,0035,A,-010,00,B=" + "\r\n";
            printdata = printdata + "PC01;0130,0075,10,10,V,-05,00,B,J0000,P1=円より" + "\r\n";
            printdata = printdata + "#TPV01;0050,0125,0050,0060,A,-015,00,B=" + "\r\n";
            printdata = printdata + "PC03;0135,0125,10,10,V,-03,00,B,J0000,P1=％引" + "\r\n";
            printdata = printdata + "PC04;0255,0040,10,10,V,00,B,J0000,P1=割引後価格" + "\r\n";
            printdata = printdata + "#PPV02;0235,0100,0055,0065,A,-015,00,B=" + "\r\n";
            printdata = printdata + "PC05;0390,0100,10,10,V,00,B,J0101,P1=円" + "\r\n";
            printdata = printdata + "#BPV03;0060,0240,0023,0025,A,-005,00,B=" + "\r\n";
            printdata = printdata + "#BXB00;0030,0140,9,3,02,0,0075,+0000000000,010,0,00=" + "\r\n";
            saveFile(context, fileName, printdata);
        }
        //値下シールド
        fileName = "S3G02.TXT";
        if(!file.exists()) {
            // 存在しない
            printdata = "V05^H10^P0^L0101^K8B標準価格" + "\r\n";
            printdata = printdata + "PS" + "\r\n";
            printdata = printdata + "PS" + "\r\n";
            printdata = printdata + "#SV30^H20^P1^L0304^X21," + "\r\n";
            printdata = printdata + "V65^H130^P0^L0102^K8D円" + "\r\n";
            printdata = printdata + "V05^H200^P0^L0101^K9B値下後価格" + "\r\n";
            printdata = printdata + "PS" + "\r\n";
            printdata = printdata + "#PV30^H200^P1^L0304^X21," + "\r\n";
            printdata = printdata + "V60^H315^P0^L0102^K8D円" + "\r\n";
            printdata = printdata + "#BV110^H45^BG02065>I" + "\r\n";
            printdata = printdata + "#BV180^H55^P2^L0202^X20," + "\r\n";
            saveFile(context, fileName, printdata);
        }
        fileName = "S4G02.TXT";
        if(!file.exists()) {
            // 存在しない
            printdata = "V10^H2^P0^L0101^K8B標準価格" + "\r\n";
            printdata = printdata + "PS" + "\r\n";
            printdata = printdata + "#SV35^H15^P3^$A,65,75,0^$=" + "\r\n";
            printdata = printdata + "V40^H135^P0^L0102^K8D円" + "\r\n";
            printdata = printdata + "V00^H190^P0^L0101^K9B値下後価格" + "\r\n";
            printdata = printdata + "PS" + "\r\n";
            printdata = printdata + "#PV20^H195^P2^$A,65,75,0^$=" + "\r\n";
            printdata = printdata + "V50^H328^P0^L0102^K8D円" + "\r\n";
            printdata = printdata + "#BV105^H20^BG02065>I" + "\r\n";
            printdata = printdata + "#BV175^H30^P2^L0202^X20," + "\r\n";
            saveFile(context, fileName, printdata);
        }
        fileName = "TEG02.TXT";
        if(!file.exists()) {
            // 存在しない
            printdata = "D0282,0560,0250" + "\r\n";
            printdata = printdata + "D0300,0580,0300" + "\r\n";
            printdata = printdata + "AX;+000,+000,+00" + "\r\n";
            printdata = printdata + "AY;+00,1" + "\r\n";
            printdata = printdata + "C" + "\r\n";
            printdata = printdata + "PC00;0000,0030,10,10,V,-03,00,B,J0000,P1=表示価格" + "\r\n";
            printdata = printdata + "#SPV01;0000,0115,0050,0060,A,-015,00,B=" + "\r\n";
            printdata = printdata + "PC01;0135,0115,10,10,V,00,B,J0101,P1=円" + "\r\n";
            printdata = printdata + "PC01;0230,0040,10,10,V,00,B,J0000,P1=値下後価格" + "\r\n";
            printdata = printdata + "#PPV02;0210,0100,0055,0065,A,-015,00,B=" + "\r\n";
            printdata = printdata + "PC02;0360,0100,10,10,V,00,B,J0101,P1=円" + "\r\n";
            printdata = printdata + "#BPV03;0030,0240,0023,0025,A,-005,00,B=" + "\r\n";
            printdata = printdata + "#BXB00;0015,0140,9,3,02,0,0075,+0000000000,010,0,00=" + "\r\n";
            saveFile(context, fileName, printdata);
        }
        fileName = "TLG02.TXT";
        if(!file.exists()) {
            // 存在しない
            printdata = "D0282,0560,0250" + "\r\n";
            printdata = printdata + "D0300,0580,0300" + "\r\n";
            printdata = printdata + "AX;+000,+000,+00" + "\r\n";
            printdata = printdata + "AY;+00,1" + "\r\n";
            printdata = printdata + "C" + "\r\n";
            printdata = printdata + "PC00;0025,0040,10,10,V,-03,00,B,J0000,P1=表示価格" + "\r\n";
            printdata = printdata + "#SPV01;0030,0115,0050,0060,A,-015,00,B=" + "\r\n";
            printdata = printdata + "PC01;0165,0115,10,10,V,00,B,J0101,P1=円" + "\r\n";
            printdata = printdata + "PC01;0255,0040,10,10,V,00,B,J0000,P1=値下後価格" + "\r\n";
            printdata = printdata + "#PPV02;0240,0100,0055,0065,A,-015,00,B=" + "\r\n";
            printdata = printdata + "PC02;0390,0100,10,10,V,00,B,J0101,P1=円" + "\r\n";
            printdata = printdata + "#BPV03;0060,0240,0023,0025,A,-005,00,B=" + "\r\n";
            printdata = printdata + "#BXB00;0045,0140,9,3,02,0,0075,+0000000000,010,0,00=" + "\r\n";
            saveFile(context, fileName, printdata);
        }
        return 0;
    }

    // ファイルを保存
    public static String saveFile(Context context, String file, String str) {
        // try-with-resources
        try (
                FileOutputStream fileOutputstream = context.openFileOutput(file, Context.MODE_PRIVATE);){
                fileOutputstream.write(str.getBytes());
                return "";
        } catch (IOException e) {
            e.printStackTrace();
            return e.toString();
        }
    }

}
