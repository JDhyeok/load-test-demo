package load.test.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
public class LoadTestController {
    private final Random random = new Random();

    // API 1: 다양한 응답 상태 API
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatusResponse() {
        int randomValue = random.nextInt(100);

        // 5% 확률로 지연 응답
        if (randomValue < 5) {
            try {
                // 3-5초 지연
                TimeUnit.MILLISECONDS.sleep(3000 + random.nextInt(2000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            Map<String, Object> response = new HashMap<>();
            response.put("status", "delayed");
            response.put("message", "This response was intentionally delayed");
            return ResponseEntity.ok(response);
        }

        // 15% 확률로 504 Gateway Timeout
        else if (randomValue < 20) {
            throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, "Simulated gateway timeout");
        }

        // 80% 확률로 정상 응답
        else {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Normal response");
            return ResponseEntity.ok(response);
        }
    }

    // API 2: 리소스 집약적 작업 API
    @GetMapping("/resource")
    public ResponseEntity<Map<String, Object>> getResourceIntensiveResponse() {
        int randomValue = random.nextInt(100);

        // 5% 확률로 429 Too Many Requests
        if (randomValue < 5) {
            try {
                // 1-2초 대기 후 429 응답
                TimeUnit.MILLISECONDS.sleep(1000 + random.nextInt(1000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded");
        }

        // 10% 확률로 503 Service Unavailable
        else if (randomValue < 15) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Service temporarily unavailable");
        }

        // 25% 확률로 CPU 부하가 큰 작업
        else if (randomValue < 40) {
            // CPU 및 메모리 부하 시뮬레이션
            List<byte[]> memoryConsumer = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                memoryConsumer.add(new byte[1024 * 1024]); // 약 1MB씩 할당
            }

            // CPU 부하 작업
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < 500) {
                for (int i = 0; i < 1000000; i++) {
                    Math.sin(random.nextDouble());
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("status", "heavy processing");
            response.put("message", "Heavy CPU/memory usage task completed");
            return ResponseEntity.ok(response);
        }

        // 60% 확률로 빠른 정상 응답
        else {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Light processing completed");
            return ResponseEntity.ok(response);
        }
    }

    // API 3: 데이터 처리 API
    @GetMapping("/data")
    public ResponseEntity<Object> getDataProcessingResponse() {
        int randomValue = random.nextInt(100);

        // 3% 확률로 500 Internal Server Error
        if (randomValue < 3) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Simulated internal error");
        }

        // 7% 확률로 400 Bad Request
        else if (randomValue < 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Simulated invalid parameters");
        }

        // 20% 확률로 대량 데이터 반환
        else if (randomValue < 30) {
            Map<String, Object> largeResponse = new HashMap<>();
            largeResponse.put("status", "large data");

            // 대용량 데이터 생성
            List<Map<String, Object>> items = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", i);
                item.put("name", "Item " + i);
                item.put("description", "This is a detailed description for item " + i + " with additional text to increase payload size.");
                item.put("timestamp", System.currentTimeMillis());
                item.put("randomData", random.nextDouble());
                items.add(item);
            }

            largeResponse.put("data", items);
            return ResponseEntity.ok(largeResponse);
        }

        // 70% 확률로 소량 데이터 반환
        else {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Data processed successfully");

            // 소량 데이터 생성
            List<Map<String, Object>> items = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", i);
                item.put("name", "Simple Item " + i);
                items.add(item);
            }

            response.put("data", items);
            return ResponseEntity.ok(response);
        }
    }
}
