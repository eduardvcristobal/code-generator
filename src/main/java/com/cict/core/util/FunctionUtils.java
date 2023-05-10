package com.cict.core.util;

import org.jasypt.encryption.pbe.PBEStringCleanablePasswordEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Component
public class FunctionUtils {

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");

    public static boolean isNumber(String number) {
        try {
            Integer.parseInt(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public Boolean isValidFile(MultipartFile file, String type) {
        List<String> contentTypes;
        if (type.equals("file") || type.equals("files")) {
            contentTypes = Arrays.asList("image/png", "image/jpeg", "image/gif", "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/pdf",
                    "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation",
                    "application/vnd.oasis.opendocument.presentation","application/vnd.oasis.opendocument.spreadsheet","text/plain",
                    "video/mpeg","video/3gpp","audio/3gpp","video/x-msvideo","image/vnd.microsoft.icon","image/bmp","image/svg+xml",
                    "video/x-ms-wmv", "video/x-msvideo","video/quicktime", "video/MP2T", "application/x-mpegURL","video/mp4", "video/x-flv");
        } else {
            contentTypes = Arrays.asList("image/png","image/jpeg","image/gif","image/vnd.microsoft.icon","image/bmp","image/svg+xml");
        }
        String fileContentType = file.getContentType();
        if (contentTypes.contains(fileContentType)) {
            return true;
        }
        return false;
    }

    public String uploadFiles(MultipartFile file, String logoPath, String folder, String pathToDirectory) throws IOException {
        if (file != null) {

            List<String> contentTypes = Arrays.asList("image/png", "image/jpeg", "image/gif", "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/pdf",
                    "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation",
                    "application/vnd.oasis.opendocument.presentation","application/vnd.oasis.opendocument.spreadsheet","text/plain",
                    "video/mpeg","video/3gpp","audio/3gpp","video/x-msvideo","image/vnd.microsoft.icon","image/bmp","image/svg+xml",
                    "video/x-ms-wmv", "video/x-msvideo","video/quicktime", "video/MP2T", "application/x-mpegURL","video/mp4", "video/x-flv");
            String fileContentType = file.getContentType();
            if (contentTypes.contains(fileContentType)) {
                String directoryName = pathToDirectory.concat(folder);
                File directory = new File(directoryName);
                if (!directory.exists()){
                    directory.mkdirs();
                }
                UUID uuid = UUID.randomUUID();
                String filename = uuid+"."+file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
                File convertFile = new File(directoryName + File.separator + filename);
                convertFile.createNewFile();
                FileOutputStream fout = new FileOutputStream(convertFile);
                fout.write(file.getBytes());
                fout.close();
                return  folder.replace("/","") + "/" + filename;
            }
            return "";
        }
        return logoPath;
    }

    public String controlNumber() {
        Date dateNow = new Date();
        return formatter.format(dateNow);
    }

//    public Supplier<Stream<Row>> getStreamSupplier(Iterable<Row> rows) {
//        return () -> getStream(rows);
//    }

    public <T> Stream<T> getStream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    public String getFirstLetters(String text) {
        StringBuilder firstLetters = new StringBuilder();
        text = text.replaceAll("[_]", " ");
        for(String s : text.split(" "))
        {
            firstLetters.append(s.charAt(0));
        }
        if (firstLetters.toString().length() >= 3) {
            return firstLetters.toString().substring(0,2);
        } else {
            return firstLetters.toString().substring(0,1);
        }
    }

    public PBEStringCleanablePasswordEncryptor basicTextEncryptor() {
        StandardPBEStringEncryptor encryptor;
        encryptor = new StandardPBEStringEncryptor();
        encryptor.setAlgorithm("PBEWITHMD5ANDDES");
        encryptor.setPassword("forgotPasswordEncrypt");
        return encryptor;
    }

    public LocalDateTime convertDateToLocalDateTime(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public int[] StringArrToIntArr(String[] s) {
        int[] result = new int[s.length];
        for (int i = 0; i < s.length; i++) {
            result[i] = Integer.parseInt(s[i]);
        }
        return result;
    }

    public static int dayName(String inputDate){
        Calendar calendar = Calendar.getInstance();
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            formatter.setLenient(false);
            Date date = formatter.parse(inputDate);
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public static float roundFloat(float f, int places) {
        BigDecimal bigDecimal = new BigDecimal(Float.toString(f));
        bigDecimal = bigDecimal.setScale(places, RoundingMode.HALF_UP);
        return bigDecimal.floatValue();
    }

    public String uploadAvatar(MultipartFile file, String logoPath, String folder, String pathToDirectory) throws IOException {
        if (file != null) {
            List<String> contentTypes = Arrays.asList("image/png","image/jpeg","image/gif","image/vnd.microsoft.icon","image/bmp","image/svg+xml");
            String fileContentType = file.getContentType();
            if (contentTypes.contains(fileContentType)) {
                String directoryName = pathToDirectory.concat(folder);
                File directory = new File(directoryName);
                if (!directory.exists()){
                    directory.mkdirs();
                }
                UUID uuid = UUID.randomUUID();
                String filename = uuid+"."+file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
                File convertFile = new File(directoryName + File.separator + filename);
                convertFile.createNewFile();
                FileOutputStream fout = new FileOutputStream(convertFile);
                fout.write(file.getBytes());
                fout.close();
                return folder.replace("/","") + "/" + filename;
            }
            return "Error invalid file type";
        }
        return logoPath;
    }

    public static <T> List<T> toList(Optional<T> opt) {
        return opt
                .map(Collections::singletonList)
                .orElseGet(Collections::emptyList);
    }
}
