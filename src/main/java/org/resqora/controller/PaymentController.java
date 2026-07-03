package org.resqora.controller;

import org.resqora.dto.request.OrderRequest;
import org.resqora.entity.ServiceRequest;
import org.resqora.enums.PaymentStatus;
import org.resqora.repository.ServiceRequestRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Value("${cashfree.client.id}")
    private String clientId;

    @Value("${cashfree.client.secret}")
    private String clientSecret;

    @Value("${cashfree.base.url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ServiceRequestRepository serviceRequestRepository;

    // constructor injection - repository chahiye DB update ke liye
    public PaymentController(ServiceRequestRepository serviceRequestRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
    }

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest request) {

        ServiceRequest serviceRequest = serviceRequestRepository
                .findById(request.getServiceRequestId())
                .orElseThrow(() -> new RuntimeException("Service request not found"));

        // Phone/email ab DB se seedha User entity se nikalenge - frontend se nahi
        String userPhone = serviceRequest.getUser().getPhone();
        String userEmail = serviceRequest.getUser().getEmail();
        Long userId = serviceRequest.getUser().getId();

        String orderId = "resqora_" + request.getServiceRequestId() + "_" + System.currentTimeMillis();

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-client-id", clientId);
        headers.set("x-client-secret", clientSecret);
        headers.set("x-api-version", "2023-08-01");
        headers.setContentType(MediaType.APPLICATION_JSON);
        System.out.println("Client ID: " + clientId);
        System.out.println("Client Secret: " + clientSecret);

        Map<String, Object> customerDetails = new HashMap<>();
        customerDetails.put("customer_id", "user_" + userId);
        customerDetails.put("customer_phone", userPhone);
        customerDetails.put("customer_email", userEmail);

        Map<String, Object> orderMeta = new HashMap<>();
        orderMeta.put("return_url",
                "https://resqora-9jtt.vercel.app/payment-status?order_id={order_id}");

        Map<String, Object> body = new HashMap<>();
        body.put("order_id", orderId);
        body.put("order_amount", request.getAmount());
        body.put("order_currency", "INR");
        body.put("customer_details", customerDetails);
        body.put("order_meta", orderMeta);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/orders", entity, Map.class
        );

        Map<String, Object> responseBody = response.getBody();

        serviceRequest.setCashfreeOrderId(orderId);
        serviceRequest.setPaymentSessionId((String) responseBody.get("payment_session_id"));
        serviceRequest.setPaymentStatus(PaymentStatus.PENDING);
        serviceRequestRepository.save(serviceRequest);

        return ResponseEntity.ok(responseBody);
    }

    // ---------- 2. VERIFY PAYMENT ----------
    @GetMapping("/verify/{orderId}")
    public ResponseEntity<?> verifyPayment(@PathVariable String orderId) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-client-id", clientId);
        headers.set("x-client-secret", clientSecret);
        headers.set("x-api-version", "2023-08-01");

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // Cashfree se order ka current status pucho
        ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl + "/orders/" + orderId, HttpMethod.GET, entity, Map.class
        );

        Map<String, Object> responseBody = response.getBody();
        String orderStatus = (String) responseBody.get("order_status"); // "PAID" / "ACTIVE" / "EXPIRED"

        // DB me is order_id wala ServiceRequest dhoondo
        Optional<ServiceRequest> optionalRequest =
                serviceRequestRepository.findByCashfreeOrderId(orderId);

        if (optionalRequest.isEmpty()) {
            return ResponseEntity.status(404).body("Service request not found for this order");
        }

        ServiceRequest serviceRequest = optionalRequest.get();

        if ("PAID".equals(orderStatus)) {
            // Payment confirm - status update karo
            serviceRequest.setPaymentStatus(PaymentStatus.PAID);
            serviceRequest.setPaidAmount(new BigDecimal(responseBody.get("order_amount").toString()));
            serviceRequest.setPaidAt(LocalDateTime.now());
            serviceRequestRepository.save(serviceRequest);

            // YAHIN pe tumhara existing mechanic-notification logic call hoga
            // e.g. notificationService.notifyMechanic(serviceRequest);

        } else if ("EXPIRED".equals(orderStatus)) {
            serviceRequest.setPaymentStatus(PaymentStatus.FAILED);
            serviceRequestRepository.save(serviceRequest);
        }
        // agar "ACTIVE" hai to matlab abhi tak payment pending hai, kuch mat karo

        return ResponseEntity.ok(Map.of(
                "orderStatus", orderStatus,
                "paymentStatus", serviceRequest.getPaymentStatus()
        ));
    }

    // ---------- 3. WEBHOOK (Step 15 me signature verify add karenge) ----------
    @PostMapping("/webhook")
    public ResponseEntity<?> handleWebhook(@RequestBody String payload) {
        // Abhi ke liye simple log - signature verification baad me add karenge
        System.out.println("Webhook received: " + payload);
        return ResponseEntity.ok().build();
    }
}