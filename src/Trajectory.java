/**
 * Created by ljh on 2019/8/17.
 */

import com.google.common.geometry.*;

import java.io.*;
import java.lang.*;
import java.nio.file.Files;
import java.util.ArrayList;

import org.apache.poi.*;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import javax.swing.filechooser.FileSystemView;

public class Trajectory {

    public static ArrayList<S2CellId> s2CellIds = new ArrayList<>();

    public static void main(String args[]) throws IOException{
         /*  test
            lat&lng transfer to cellid */
        S2LatLng llDeg = S2LatLng.fromDegrees(29.32567, 107.75974);//transfer lat&lng to Degree
        S2CellId cellId = S2CellId.fromLatLng(llDeg);//transfer Degree to cellID
        System.out.print(llDeg+"\n");
        System.out.print(cellId+"\n"+cellId.toToken()+"\n");
        System.out.print(cellId.parent(3).toToken()+"\n");

        /*  test
            cellid transfers to lat&lng */
        S2LatLng test = cellId.toLatLng();
        double testlag = test.lat().degrees();
        double testlng = test.lng().degrees();
        System.out.print(test+"\n");
        System.out.print(testlag+"\n");
        System.out.print(testlng+"\n");

        //code
        ReadFile();
        CreateExcel();
    }

    private static void ReadFile() throws IOException {
        FileSystemView fsv = FileSystemView.getFileSystemView();
        String desktop = fsv.getHomeDirectory().getPath();
        String filePath = desktop + "/Desktop/3wdata.xls";

        FileInputStream fileInputStream = new FileInputStream(filePath);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        POIFSFileSystem fileSystem = new POIFSFileSystem(bufferedInputStream);
        HSSFWorkbook workbook = new HSSFWorkbook(fileSystem);
        HSSFSheet sheet = workbook.getSheet("sheet1");

        int lastRowIndex = sheet.getLastRowNum();
        System.out.println(lastRowIndex);

        for (int i = 0; i <= lastRowIndex; i++) {
            HSSFRow row = sheet.getRow(i);

            if (row == null) { break; }

            double latValue = row.getCell(0).getNumericCellValue();
            double lngValue = row.getCell(1).getNumericCellValue();
            S2LatLng latlngValue = S2LatLng.fromDegrees(latValue, lngValue);
            S2CellId cellIdValue = S2CellId.fromLatLng(latlngValue);
            System.out.print(cellIdValue);
            s2CellIds.add(cellIdValue);
        }
        System.out.println(s2CellIds);
        workbook.close();
    }

    public static void CreateExcel() throws IOException{
        File file = new File("/users/ljh/Desktop/newtest.xls");
        OutputStream outputStream = new FileOutputStream(file);
        HSSFWorkbook outWorkbook = new HSSFWorkbook();
        HSSFSheet outSheet = outWorkbook.createSheet("newtest");
        HSSFRow outRow = outSheet.createRow(0);
        outRow.createCell(0).setCellValue("level");
        outRow.createCell(1).setCellValue("token");

        int size = s2CellIds.size();

        for(int i = 1; i <= size; i++){
            int index = i-1;
            HSSFRow createRow = outSheet.createRow(i);
            createRow.createCell(0).setCellValue(s2CellIds.get(index).level());
            createRow.createCell(1).setCellValue(s2CellIds.get(index).parent(5).toToken());
        }

        outWorkbook.write(outputStream);
        outputStream.close();
    }
}
