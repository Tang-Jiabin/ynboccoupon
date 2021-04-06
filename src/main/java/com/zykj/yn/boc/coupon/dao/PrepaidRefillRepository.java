package com.zykj.yn.boc.coupon.dao;

import com.zykj.yn.boc.coupon.pojo.PrepaidRefill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrepaidRefillRepository extends JpaRepository<PrepaidRefill,Integer> {
}
