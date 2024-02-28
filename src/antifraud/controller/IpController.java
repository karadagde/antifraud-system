package antifraud.controller;

import antifraud.model.entity.IpAddress;
import antifraud.model.records.DeleteIpAddressResponse;
import antifraud.model.records.SuspiciousIpRegisterRequest;
import antifraud.service.IpAddressService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/antifraud/suspicious-ip")
@Validated
public class IpController {


    private final IpAddressService ipAddressService;

    public IpController(IpAddressService ipAddressService) {
        this.ipAddressService = ipAddressService;
    }

    @PostMapping
    public ResponseEntity<IpAddress> registerIpAddress(@RequestBody SuspiciousIpRegisterRequest request) {
        boolean isValidIp = ipAddressService.isIpAddressValid(request.ip());
        if (!isValidIp) {
            return ResponseEntity.badRequest().build();
        }
        boolean isIpBlacklisted = ipAddressService.isIpAddressBlacklisted(request.ip());
        if (isIpBlacklisted) {
            return ResponseEntity.status(409).build();
        }

        return ResponseEntity.ok(ipAddressService.registerSuspiciousIp(request.ip()));
    }

    @GetMapping
    public ResponseEntity<List<IpAddress>> getAllIpAddresses() {
        return ResponseEntity.ok(ipAddressService.getAllIps());
    }

    @DeleteMapping("/{ipAddress}")

    public ResponseEntity<DeleteIpAddressResponse> deleteIpAddress(@PathVariable String ipAddress) {
        boolean isValidIp = ipAddressService.isIpAddressValid(ipAddress);

        if (!isValidIp) {
            return ResponseEntity.badRequest().build();
        }
        boolean isIpBlacklisted = ipAddressService.isIpAddressBlacklisted(ipAddress);

        if (!isIpBlacklisted) {
            return ResponseEntity.status(404).build();
        }

        return ResponseEntity.ok(ipAddressService.deleteIpAddress(ipAddress));
    }

}
