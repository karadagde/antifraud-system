package antifraud.service;

import antifraud.model.entity.IpAddress;
import antifraud.model.records.DeleteIpAddressResponse;
import antifraud.repository.IpAddressRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class IpAddressService {

    private final IpAddressRepository ipAddressRepository;


    public IpAddressService(IpAddressRepository ipAddressRepository) {
        this.ipAddressRepository = ipAddressRepository;
    }

    public boolean isIpAddressValid(String ipAddress) {
        Pattern ipPattern = Pattern.compile("^\\d{1,3}(\\.\\d{1,3}){3}$");
        Matcher matcher = ipPattern.matcher(ipAddress);
        boolean matchFound = matcher.find();

        if (!matchFound) {
            return false;
        }

        String[] octets = ipAddress.split("\\.");

        for (String octet : octets) {
            int intPart = Integer.parseInt(octet);
            if (intPart < 0 || intPart > 255) {
                return false;
            }
        }

        return true;
    }

    @Transactional
    public IpAddress registerSuspiciousIp(String ip) {
        IpAddress newAddress = new IpAddress();
        newAddress.setIp(ip);

        return ipAddressRepository.save(newAddress);
    }

    @Transactional
    public DeleteIpAddressResponse deleteIpAddress(String ipAddress) {
        Optional<IpAddress> findIp = ipAddressRepository.findIpAddressByIp(ipAddress);


        findIp.ifPresent(ipAddressRepository::delete);

        return new DeleteIpAddressResponse("IP " + ipAddress + " successfully removed!");

    }

    public List<IpAddress> getAllIps() {
        Iterable<IpAddress> allIps = ipAddressRepository.findAll();
        List<IpAddress> ipList = new ArrayList<>();
        allIps.forEach(ipList::add);
        return ipList;
    }

    public boolean isIpAddressBlacklisted(String ipAddress) {
        return ipAddressRepository.findIpAddressByIp(ipAddress).isPresent();
    }
}
