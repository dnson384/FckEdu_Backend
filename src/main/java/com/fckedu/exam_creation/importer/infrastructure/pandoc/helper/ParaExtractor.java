package com.fckedu.exam_creation.importer.infrastructure.pandoc.helper;

import com.fckedu.exam_creation.importer.dto.parsed.NewQuestionImporterDTO;
import org.jspecify.annotations.NonNull;
import tools.jackson.databind.JsonNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

public class ParaExtractor {
    public static void execute(JsonNode content, NewQuestionImporterDTO resBase, String uploadDir) {
        if (content == null || !content.isArray() || content.isEmpty()) return;


        JsonNode firstElement = content.get(0);
        String type = firstElement.has("t") ? firstElement.get("t").asString() : "";

        // Xử lý ảnh
        if ("Image".equals(type)) {
            Map<String, String> imageMap = resBase.getQuestion().getVariables().getImage();
            int curVarIndex = imageMap.size();

            String currentTemplate = resBase.getQuestion().getTemplate();
            resBase.getQuestion().setTemplate(currentTemplate + " \n<img_" + curVarIndex + ">\n ");

            String rawSrc = firstElement.get("c").get(2).get(0).asString();
            Path sourcePath = Paths.get(rawSrc).toAbsolutePath();

            // Lấy đuôi tệp
            String extension = "";
            int extIndex = rawSrc.lastIndexOf(".");
            if (extIndex > 0) {
                extension = rawSrc.substring(extIndex);
            }

            // Tạo file với tên mới là UUID
            String newFileName = UUID.randomUUID() + extension;
            Path destPath = Paths.get(uploadDir, newFileName);

            try {
                // Di chuyển file ảnh tới thư mục chính thức
                Files.move(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);

                String publicUrl = getString(destPath);

                imageMap.put("img_" + curVarIndex, publicUrl);

            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                System.err.println("Lỗi di chuyển ảnh " + rawSrc + ": " + e.getMessage());
            }
        }
        // Xử lý text & công thức
        else {
            StringBuilder templateBuilder = new StringBuilder(resBase.getQuestion().getTemplate());
            Map<String, String> mathMap = resBase.getQuestion().getVariables().getMath();

            for (JsonNode raw : content) {
                String t = raw.has("t") ? raw.get("t").asString() : "";

                if ("Str".equals(t)) {
                    templateBuilder.append(raw.get("c").asString());
                } else if ("Space".equals(t)) {
                    templateBuilder.append(" ");
                } else if ("LineBreak".equals(t)) {
                    templateBuilder.append(" \n");
                } else if ("Math".equals(t)) {
                    int curVarIndex = mathMap.size();
                    templateBuilder.append("<math_").append(curVarIndex).append(">");

                    String mathContext = raw.get("c").get(1).asString();
                    mathMap.put("math_" + curVarIndex, mathContext);
                }
            }

            resBase.getQuestion().setTemplate(templateBuilder.toString());
        }
    }

    private static @NonNull String getString(Path destPath) {
        String destPathStr = destPath.toString();
        int staticIndex = destPathStr.indexOf("static");
        String relativePart = "";

        if (staticIndex != -1) {
            relativePart = destPathStr.substring(staticIndex + "static".length() + 1);
        }

        // Chuẩn hóa gạch chéo
        String normalizedPath = relativePart.replace(File.separator, "/");

        return "/static/" + normalizedPath;
    }
}
