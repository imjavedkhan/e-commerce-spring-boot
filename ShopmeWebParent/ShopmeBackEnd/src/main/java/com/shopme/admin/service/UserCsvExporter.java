package com.shopme.admin.service;

import com.shopme.common.entity.User;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UserCsvExporter {

    public void export(List<User> listUsers, HttpServletResponse response) throws IOException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String timeStamp = dateFormat.format(new Date());
        String fileName = "users_" + timeStamp + ".csv";

        response.setContentType("text/csv");

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + fileName;
        response.setHeader(headerKey,headerValue);

        ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

        String[] csvHeader = {"User ID","E-mail","First Name","Last Name","Roles","Enabled"};
        String[] fieldMapping = {"id","email","firstName","lastName","roles","enabled"};

        csvBeanWriter.writeHeader(csvHeader);

        for(User user : listUsers){
            csvBeanWriter.write(user,fieldMapping);
        }
        csvBeanWriter.close();
    }
}
