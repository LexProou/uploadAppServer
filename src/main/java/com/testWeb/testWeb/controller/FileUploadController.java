import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "${frontend.url}") // Разрешаем доступ с фронтенда
public class FileUploadController {

    @Value("${frontend.url}")
    private String frontendUrl;
    // Разрешенные типы файлов
    private static final String[] ALLOWED_TYPES = {"text/plain", "application/json", "text/csv"};

    // Эндпоинт для загрузки файла
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("name") String name,
            @RequestParam("file") MultipartFile file) {

        // Проверка на пустое имя
        if (name == null || name.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Имя не может быть пустым"));
        }

        // Проверка типа файла
        String fileType = file.getContentType();
        if (fileType == null || !isValidFileType(fileType)) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Неверный формат файла. Разрешены только .txt, .json и .csv."));
        }

        // Проверка размера файла
        if (file.getSize() > 1024) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Размер файла превышает 1KB."));
        }

        // Генерация имени файла (можно использовать уникальные имена для предотвращения конфликтов)
        String filename = System.currentTimeMillis() + "_" + StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Путь для сохранения файла
            String uploadDir = System.getProperty("user.dir") + "/uploads/";
            File uploadDirFile = new File(uploadDir);
            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs(); // Создаем папку, если её нет
            }

            // Сохраняем файл на сервере
            file.transferTo(new File(uploadDir + filename));

            // Ответ от сервера с подтверждением успешной загрузки
            return ResponseEntity.ok().body(new UploadResponse("File uploaded successfully", filename, name));
        } catch (IOException e) {
            e.printStackTrace(); // Логируем ошибку
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Ошибка при загрузке файла."));
        }
    }

    // Метод для проверки допустимых типов файлов
    private boolean isValidFileType(String fileType) {
        for (String allowedType : ALLOWED_TYPES) {
            if (allowedType.equals(fileType)) {
                return true;
            }
        }
        return false;
    }

    // Ответ от сервера с подтверждением успешной загрузки
    public static class UploadResponse {
        private String message;
        private String filename;
        private String nameField;

        public UploadResponse(String message, String filename, String nameField) {
            this.message = message;
            this.filename = filename;
            this.nameField = nameField;
        }

        public String getMessage() {
            return message;
        }

        public String getFilename() {
            return filename;
        }

        public String getNameField() {
            return nameField;
        }
    }

    // Ответ для ошибок
    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }
}
