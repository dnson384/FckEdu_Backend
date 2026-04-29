package com.fckedu.backend.importer.infrastructure.pandoc.helper;

import com.fckedu.backend.importer.dto.parsed.NewQuestionImporterDTO;
import org.jspecify.annotations.NonNull;
import tools.jackson.databind.JsonNode;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

public class BulletListExtractor {
    public static void execute(JsonNode content, NewQuestionImporterDTO resBase, int curOptionIndex, String uploadDir) {
        if (content == null || !content.isArray() || content.isEmpty()) return;

        JsonNode firstElement = content.get(0);
        String type = firstElement.has("t") ? firstElement.get("t").asString() : "";

        // Xử lý ảnh
        if ("Image".equals(type)) {
            Map<String, String> imageMap = resBase.getOptions().get(curOptionIndex).getVariables().getImage();
            int curVarIndex = imageMap.size();

            String currentTemplate = resBase.getOptions().get(curOptionIndex).getTemplate();
            resBase.getOptions().get(curOptionIndex).setTemplate(currentTemplate + "<img_" + curVarIndex + ">");

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

            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                System.err.println("Lỗi di chuyển ảnh " + rawSrc + ": " + e.getMessage());
            }
        }
        // Xử lý text & công thức
        else {
            StringBuilder templateBuilder = new StringBuilder(resBase.getOptions().get(curOptionIndex).getTemplate());
            Map<String, String> mathMap = resBase.getOptions().get(curOptionIndex).getVariables().getMath();

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

            resBase.getOptions().get(curOptionIndex).setTemplate(templateBuilder.toString());
        }
    }

    private static @NonNull String getString(Path destPath) throws URISyntaxException {
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
