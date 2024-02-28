package antifraud.repository;

import antifraud.model.entity.IpAddress;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface IpAddressRepository extends CrudRepository<IpAddress, Integer> {
    Optional<IpAddress> findIpAddressByIp(String ipAddress);
}
