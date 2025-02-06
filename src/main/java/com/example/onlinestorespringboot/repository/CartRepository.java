package com.example.onlinestorespringboot.repository;


import com.example.onlinestorespringboot.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface CartRepository extends JpaRepository<Cart,Long> {

    @Modifying
    @Query("delete from Cart c where c.id=:id")
    public void deleteById(@Param("id") Long id);
}
