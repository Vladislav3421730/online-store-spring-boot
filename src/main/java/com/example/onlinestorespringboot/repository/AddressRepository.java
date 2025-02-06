package com.example.onlinestorespringboot.repository;


import com.example.onlinestorespringboot.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface AddressRepository extends JpaRepository<Address,Long> {
}
