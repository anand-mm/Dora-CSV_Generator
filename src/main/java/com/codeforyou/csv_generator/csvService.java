package com.codeforyou.csv_generator;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class csvService {

    public void csv_generator_listofMap(HttpServletResponse response,MultipartFile file) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();

        // Convert MultipartFile to byte[]
        byte[] filebytes = file.getBytes();

        try {

            List<Map<String, Object>> interData = objectMapper.readValue(new String(filebytes), new TypeReference<List<Map<String, Object>>>() {});

            boolean firstObjectProcessed = false;

            StringBuilder csvStringBuilder = new StringBuilder();

            Set<String> headerList = new LinkedHashSet<>();

            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=\"csvOutput.csv\"");

            for (Map<String, Object> map : interData) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    firstObjectProcessed = false;
                    Object value = entry.getValue();
                    csvStringBuilder.append("\n");
                    csvStringBuilder.append(entry.getKey() + ",");
                    if (value != null && value instanceof ArrayList<?>) {
                        List<?> valueList = (ArrayList<?>) value;
                        for (Object tempObject : (ArrayList<?>) value) {
                            if (valueList.size() > 1 && firstObjectProcessed) {
                                csvStringBuilder.append("\n");
                                csvStringBuilder.append(",");
                            }
                            Map<?, ?> mObject = (Map<?, ?>) tempObject;
                            for (Map.Entry<?, ?> mEntry : mObject.entrySet()) {
                                headerList.add(mEntry.getKey().toString());
                                csvStringBuilder.append(mEntry.getValue().toString() + ",");
                                firstObjectProcessed = true;
                            }
                        }
                    }
                }
            }

            csvStringBuilder.insert(0, "Domains," + String.join(",", headerList));
            byte[] bytes = csvStringBuilder.toString().getBytes("UTF-8");
            response.getOutputStream().write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void single_csv(HttpServletResponse response,MultipartFile file){
        try{

            ObjectMapper objectMapper = new ObjectMapper();

            byte[] filebytes = file.getBytes();

            List<Map<String, Object>> interData = objectMapper.readValue(new String(filebytes), new TypeReference<List<Map<String, Object>>>() {});

            String json = objectMapper.writeValueAsString(interData);

            JsonNode jsonNode = objectMapper.readTree(json);

            CsvSchema.Builder builder = CsvSchema.builder();
            jsonNode.elements().next().fieldNames().forEachRemaining(builder::addColumn);

            CsvSchema csvSchema = builder.build().withHeader();

            CsvMapper csvMapper = new CsvMapper();
            csvMapper.writerFor(JsonNode.class)
                    .with(csvSchema)
                    .writeValue(response.getOutputStream(), jsonNode);
        } catch (Exception e) {
            e.printStackTrace();
        }        }
    }

