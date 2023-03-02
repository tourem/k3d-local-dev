package demo.kafka.controller;

import demo.kafka.api.SendPaymentRequest;
import demo.kafka.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Slf4j
@Tag(name = "Payment", description = "Manage payments")
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/payments")
public class PaymentController {

    @Autowired
    private final PaymentService paymentService;

    @Operation(summary = "Send a new payment")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(schema = @Schema(implementation = String.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", content = {
                    @Content(schema = @Schema(implementation = String.class), mediaType = "application/json")
            }),
    })
    @PostMapping("/send")
    public ResponseEntity<String> sendPayment(@RequestBody SendPaymentRequest request) {
        try {
            paymentService.process(request.getPaymentId(), request);
            return ResponseEntity.ok(request.getPaymentId());
        } catch(Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.internalServerError().body(request.getPaymentId());
        }
    }
}
