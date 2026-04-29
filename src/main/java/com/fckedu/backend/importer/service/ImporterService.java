package com.fckedu.backend.importer.service;

import com.fckedu.backend.importer.dto.parsed.ParsedDataOutput;
import com.fckedu.backend.importer.infrastructure.pandoc.PandocConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImporterService {
    private final PandocConverter fileParser;

    public ImporterService() {
        this.fileParser = new PandocConverter();
    }

    @Transactional
    public ParsedDataOutput execute(byte[] fileBuffer, String subject) throws Exception {
        if (subject == null || subject.trim().isEmpty()) {
            throw new IllegalArgumentException("Không tồn tại môn học");
        }
        if (fileBuffer == null || fileBuffer.length == 0) {
            throw new IllegalArgumentException("File không tồn tại");
        }

        ParsedDataOutput parsedData = fileParser.parse(fileBuffer);

        return parsedData;
    }
}
